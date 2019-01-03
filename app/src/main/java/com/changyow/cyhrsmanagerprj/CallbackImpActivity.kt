package com.changyow.cyhrsmanagerprj

import android.bluetooth.BluetoothDevice
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.changyow.libcyhrs.DiscoveredBluetoothDevice
import com.changyow.libcyhrs.HrsManagerCallbacks

/**
 * Created by Osku on 2019/1/2.
 */
open class CallbackImpActivity : AppCompatActivity(), HrsManagerCallbacks {

    override fun onScanResult(callbackType: Int, device: DiscoveredBluetoothDevice) {

    }

    override fun onBatchScanResults(results: List<DiscoveredBluetoothDevice>) {

    }

    override fun onScanFailed(errorCode: Int) {

    }

    override fun onDeviceConnecting(@NonNull device: BluetoothDevice) {

    }

    override fun onDeviceConnected(@NonNull device: BluetoothDevice) {

    }

    override fun onDeviceDisconnecting(@NonNull device: BluetoothDevice) {

    }

    override fun onDeviceDisconnected(@NonNull device: BluetoothDevice) {

    }

    override fun onLinkLossOccurred(@NonNull device: BluetoothDevice) {

    }

    override fun onServicesDiscovered(@NonNull device: BluetoothDevice, optionalServicesFound: Boolean) {

    }

    override fun onDeviceReady(@NonNull device: BluetoothDevice) {

    }

    override fun onBondingRequired(@NonNull device: BluetoothDevice) {

    }

    override fun onBonded(@NonNull device: BluetoothDevice) {

    }

    override fun onBondingFailed(@NonNull device: BluetoothDevice) {

    }

    override fun onError(@NonNull device: BluetoothDevice, @NonNull message: String, errorCode: Int) {

    }

    override fun onDeviceNotSupported(@NonNull device: BluetoothDevice) {

    }

    override fun onBatteryLevelChanged(@NonNull device: BluetoothDevice, batteryLevel: Int) {

    }

    override fun onBodySensorLocationReceived(@NonNull device: BluetoothDevice, sensorLocation: Int) {

    }

    override fun onHeartRateMeasurementReceived(device: BluetoothDevice, heartRate: Int,
                                                @Nullable contactDetected: Boolean?,
                                                @Nullable energyExpanded: Int?,
                                                @Nullable rrIntervals: List<Int>?) {
    }
}
