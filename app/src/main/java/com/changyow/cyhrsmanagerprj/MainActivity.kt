package com.changyow.cyhrsmanagerprj

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import com.changyow.libcyhrs.DiscoveredBluetoothDevice
import com.changyow.libcyhrs.HrsManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : CallbackImpActivity(), AdapterView.OnItemClickListener {

    public val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION)
    public val REQUEST_PERMISSION_CODE = 1985

    private var mDeviceAdapter: DeviceAdapter? = null
    internal var localDeviceList: MutableList<DiscoveredBluetoothDevice> = ArrayList()//replace with LiveData
    internal var localDeviceNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDeviceAdapter = DeviceAdapter(this@MainActivity)
        lvHRCE.setAdapter(mDeviceAdapter)
        lvHRCE.setOnItemClickListener(this@MainActivity)

        btnDisconnect.setOnClickListener { HrsManager.getInstance(this@MainActivity).disconnectDevice() }
        btnDone.setOnClickListener {
            HrsManager.getInstance(this@MainActivity).stopScan()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val missingPermissions = REQUIRED_PERMISSIONS
        if (missingPermissions.isEmpty()) {
            return
        }
        ActivityCompat.requestPermissions(this@MainActivity, missingPermissions, REQUEST_PERMISSION_CODE)
    }

    override fun onResume() {
        super.onResume()

        HrsManager.getInstance(this@MainActivity).setGattCallbacks(this)
        HrsManager.getInstance(this@MainActivity).startScan()
    }

    override fun onPause() {
        super.onPause()
        HrsManager.getInstance(this@MainActivity).removeGattCallbacks()
        HrsManager.getInstance(this@MainActivity).stopScan()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        HrsManager.getInstance(this@MainActivity).connectDevice(localDeviceList.get(position))
    }

    private fun refreshListView() {
        val list = HrsManager.getInstance(this@MainActivity).discoveredBluetoothDevices
        localDeviceList.clear()
        localDeviceNames.clear()

        for (i in list.indices) {
            val device = list.get(i)
            localDeviceList.add(device)
            localDeviceNames.add(device.getName())
        }

        mDeviceAdapter?.setDeviceNames(localDeviceNames)
        mDeviceAdapter?.notifyDataSetChanged()

        swapViews()
    }

    private fun swapViews() {
        if (!HrsManager.getInstance(this@MainActivity).isConnected) {
            layout2.visibility = View.VISIBLE
            layout3.visibility = View.INVISIBLE
        } else {
            layout2.visibility = View.INVISIBLE
            layout3.visibility = View.VISIBLE
            txvConnectedHRCE.text = String.format("%s %s", HrsManager.getInstance(this@MainActivity).bluetoothDevice?.name
                    ?: "Unnamed", "connected")
        }
    }

    override fun onScanResult(callbackType: Int, device: DiscoveredBluetoothDevice) {
        refreshListView()
    }

    override fun onBatchScanResults(results: List<DiscoveredBluetoothDevice>) {
        refreshListView()
    }

    override fun onScanFailed(errorCode: Int) {
    }

    override fun onHeartRateMeasurementReceived(device: BluetoothDevice, heartRate: Int,
                                                @Nullable contactDetected: Boolean?,
                                                @Nullable energyExpanded: Int?,
                                                @Nullable rrIntervals: List<Int>?) {

        txvHrRate.setText(heartRate.toString())
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {

        refreshListView()
        txvHrRate.setText(HrsManager.getInstance(this@MainActivity).lastestHr.toString())
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        refreshListView()
    }
}
