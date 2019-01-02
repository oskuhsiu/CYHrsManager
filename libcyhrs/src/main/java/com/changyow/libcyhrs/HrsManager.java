package com.changyow.libcyhrs;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.ParcelUuid;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.common.callback.battery.BatteryLevelDataCallback;
import no.nordicsemi.android.ble.common.callback.hr.BodySensorLocationDataCallback;
import no.nordicsemi.android.ble.common.callback.hr.HeartRateMeasurementDataCallback;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * Created by Osku on 2018/12/25.
 */

public class HrsManager extends BleManager<HrsManagerCallbacks>
{
	protected final String TAG = "HrsManager";

	static final UUID HR_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
	private static final UUID BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID = UUID.fromString("00002A38-0000-1000-8000-00805f9b34fb");
	private static final UUID HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");


	private final static UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
	private final static UUID BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

	private BluetoothGattCharacteristic mHeartRateCharacteristic, mBodySensorLocationCharacteristic;
	private BluetoothGattCharacteristic mBatteryLevelCharacteristic;
	private Integer mBatteryLevel;

	private boolean mScanning = false;
	private BluetoothDevice mDevice;
	private DiscoveredBluetoothDevice mDiscoveredBluetoothDevice;
	private int mLastestHr = 0;

	private static HrsManager managerInstance = null;

	/**
	 * Singleton implementation of HRSManager class.
	 */
	public static synchronized HrsManager getInstance(Context context)
	{
		if (managerInstance == null)
		{
			managerInstance = new HrsManager(context);
		}
		return managerInstance;
	}

	private HrsManager(final Context context)
	{
		super(context);
	}

	public DiscoveredBluetoothDevice getConnectedDevice()
	{
		return mDiscoveredBluetoothDevice;
	}

	public int getLastestHr()
	{
		if (isConnected())
			return mLastestHr;
		return 0;
	}

	@NonNull
	@Override
	protected BatteryManagerGattCallback getGattCallback()
	{
		return mGattCallback;
	}

	public void readBatteryLevelCharacteristic()
	{
		if (isConnected())
		{
			readCharacteristic(mBatteryLevelCharacteristic)
					  .with(mBatteryLevelDataCallback)
					  .fail(new FailCallback()
					  {
						  @Override
						  public void onRequestFailed(@NonNull BluetoothDevice device, int status)
						  {
							  Log.w(TAG, "Battery Level characteristic not found");
						  }
					  })
					  .enqueue();
		}
	}

	public void enableBatteryLevelCharacteristicNotifications()
	{
		if (isConnected())
		{
			// If the Battery Level characteristic is null, the request will be ignored
			setNotificationCallback(mBatteryLevelCharacteristic)
					  .with(mBatteryLevelDataCallback);
			enableNotifications(mBatteryLevelCharacteristic)
					  .done(new SuccessCallback()
					  {
						  @Override
						  public void onRequestCompleted(@NonNull BluetoothDevice device)
						  {
							  Log.i(TAG, "Battery Level notifications enabled");
						  }
					  })
					  .fail(new FailCallback()
					  {
						  @Override
						  public void onRequestFailed(@NonNull BluetoothDevice device, int status)
						  {
							  Log.w(TAG, "Battery Level characteristic not found");
						  }
					  })
					  .enqueue();
		}
	}

	/**
	 * Disables Battery Level notifications on the Server.
	 */
	public void disableBatteryLevelCharacteristicNotifications()
	{
		if (isConnected())
		{
			disableNotifications(mBatteryLevelCharacteristic)
					  .done(new SuccessCallback()
					  {
						  @Override
						  public void onRequestCompleted(@NonNull BluetoothDevice device)
						  {
							  Log.i(TAG, "Battery Level notifications disabled");
						  }
					  })
					  .enqueue();
		}
	}

	/**
	 * Returns the last received Battery Level value.
	 * The value is set to null when the device disconnects.
	 *
	 * @return Battery Level value, in percent.
	 */
	public Integer getBatteryLevel()
	{
		return mBatteryLevel;
	}

	/**
	 * Get all discovered devices.
	 */
	public List<DiscoveredBluetoothDevice> getDiscoveredBluetoothDevices()
	{
		return new ArrayList<>(mDevices);
	}

	/**
	 * Connect to peripheral.
	 */
	public void connectDevice(@NonNull final DiscoveredBluetoothDevice device)
	{
		if (mDevice == null)
		{
			mDevice = device.getDevice();
			mDiscoveredBluetoothDevice = device;
			mLastestHr = 0;
			reconnect();
		}
	}

	/**
	 * Reconnects to previously connected device.
	 * If this device was not supported, its services were cleared on disconnection, so
	 * reconnection may help.
	 */
	public void reconnect()
	{
		if (mDevice != null)
		{
			this.connect(mDevice)
					  .retry(3, 100)
					  .useAutoConnect(false)
					  .enqueue();
		}
	}

	/**
	 * Disconnect from peripheral.
	 */
	public void disconnectDevice()
	{
		mDevice = null;
		mDiscoveredBluetoothDevice = null;
		mLastestHr = 0;
		mDevices.clear();
		super.disconnect().enqueue();
	}

	public void startScan()
	{
		if (mScanning)
		{
			return;
		}

		mDevices.clear();

		final ScanSettings settings = new ScanSettings.Builder()
														  .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
														  .setReportDelay(500)
														  .setUseHardwareBatchingIfSupported(false)
														  // Hardware filtering has some issues on selected devices
														  .setUseHardwareFilteringIfSupported(false)
														  .build();

		final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
		scanner.startScan(null, settings, scanCallback);
		mScanning = true;
	}

	/**
	 * Stop scanning for bluetooth devices.
	 */
	public void stopScan()
	{
		final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
		scanner.stopScan(scanCallback);
		mScanning = false;
		mDevices.clear();
	}

	private final ScanCallback scanCallback = new ScanCallback()
	{
		@Override
		public void onScanResult(final int callbackType, final ScanResult result)
		{
			if (Utils.isLocationRequired(getContext()) && !Utils.isLocationEnabled(getContext()))
				Utils.markLocationNotRequired(getContext());
			if (!matchesUuidFilter(result))
				return;
			DiscoveredBluetoothDevice device = newDeviceDiscovered(result);

			if (device != null)
				mCallbacks.onScanResult(callbackType, device);
		}

		@Override
		public void onBatchScanResults(final List<ScanResult> results)
		{
			if (Utils.isLocationRequired(getContext()) && !Utils.isLocationEnabled(getContext()))
				Utils.markLocationNotRequired(getContext());

			ArrayList<DiscoveredBluetoothDevice> devices = new ArrayList<>();
			DiscoveredBluetoothDevice device;
			for (ScanResult sr : results)
			{
				boolean hasHRService = matchesUuidFilter(sr);
				if (!hasHRService)
					continue;
				device = newDeviceDiscovered(sr);
				if (device != null)
					devices.add(device);
			}

			if (devices.size() > 0)
				mCallbacks.onBatchScanResults(devices);
		}

		@Override
		public void onScanFailed(final int errorCode)
		{
			mScanning = false;
			mCallbacks.onScanFailed(errorCode);
		}
	};

	/**
	 * BluetoothGatt callbacks for connection/disconnection, service discovery,
	 * receiving notification, etc.
	 */
	private final BatteryManagerGattCallback mGattCallback = new BatteryManagerGattCallback()
	{

		@Override
		protected void initialize()
		{
			super.initialize();
			readCharacteristic(mBodySensorLocationCharacteristic)
					  .with(new BodySensorLocationDataCallback()
					  {
						  @Override
						  public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data)
						  {
							  Log.i(TAG, "\"" + BodySensorLocationParser.parse(data) + "\" received");
							  super.onDataReceived(device, data);
						  }

						  @Override
						  public void onBodySensorLocationReceived(@NonNull final BluetoothDevice device,
																				 final int sensorLocation)
						  {
							  mCallbacks.onBodySensorLocationReceived(device, sensorLocation);
						  }
					  })
					  .fail(new FailCallback()
					  {
						  @Override
						  public void onRequestFailed(@NonNull BluetoothDevice device, int status)
						  {
							  Log.w(TAG, "Body Sensor Location characteristic not found");
						  }
					  })
					  .enqueue();

			setNotificationCallback(mHeartRateCharacteristic)
					  .with(new HeartRateMeasurementDataCallback()
					  {
						  @Override
						  public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data)
						  {
							  Log.i(TAG, "\"" + HeartRateMeasurementParser.parse(data) + "\" received");
							  super.onDataReceived(device, data);
						  }

						  @Override
						  public void onHeartRateMeasurementReceived(@NonNull final BluetoothDevice device,
																					final int heartRate,
																					@Nullable final Boolean contactDetected,
																					@Nullable final Integer energyExpanded,
																					@Nullable final List<Integer> rrIntervals)
						  {
							  mLastestHr = heartRate;
							  mCallbacks.onHeartRateMeasurementReceived(device, heartRate, contactDetected, energyExpanded, rrIntervals);
						  }
					  });
			enableNotifications(mHeartRateCharacteristic).enqueue();
		}

		@Override
		protected boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt)
		{
			final BluetoothGattService service = gatt.getService(HR_SERVICE_UUID);
			if (service != null)
			{
				mHeartRateCharacteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID);
			}
			return mHeartRateCharacteristic != null;
		}

		@Override
		protected boolean isOptionalServiceSupported(@NonNull final BluetoothGatt gatt)
		{
			super.isOptionalServiceSupported(gatt);
			final BluetoothGattService service = gatt.getService(HR_SERVICE_UUID);
			if (service != null)
			{
				mBodySensorLocationCharacteristic = service.getCharacteristic(BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID);
			}
			return mBodySensorLocationCharacteristic != null;
		}

		@Override
		protected void onDeviceDisconnected()
		{
			super.onDeviceDisconnected();
			mBodySensorLocationCharacteristic = null;
			mHeartRateCharacteristic = null;
			mLastestHr = 0;
		}
	};

	private DataReceivedCallback mBatteryLevelDataCallback = new BatteryLevelDataCallback()
	{
		@Override
		public void onBatteryLevelChanged(@NonNull final BluetoothDevice device, final int batteryLevel)
		{
			Log.i(TAG, "Battery Level received: " + batteryLevel + "%");
			mBatteryLevel = batteryLevel;
			mCallbacks.onBatteryLevelChanged(device, batteryLevel);
		}

		@Override
		public void onInvalidDataReceived(@NonNull final BluetoothDevice device, final @NonNull Data data)
		{
			log(Log.WARN, "Invalid Battery Level data received: " + data);
		}
	};


	protected abstract class BatteryManagerGattCallback extends BleManagerGattCallback
	{

		@Override
		protected void initialize()
		{
			readBatteryLevelCharacteristic();
			enableBatteryLevelCharacteristicNotifications();
		}

		@Override
		protected boolean isOptionalServiceSupported(@NonNull final BluetoothGatt gatt)
		{
			final BluetoothGattService service = gatt.getService(BATTERY_SERVICE_UUID);
			if (service != null)
			{
				mBatteryLevelCharacteristic = service.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC_UUID);
			}
			return mBatteryLevelCharacteristic != null;
		}

		@Override
		protected void onDeviceDisconnected()
		{
			mBatteryLevelCharacteristic = null;
			mBatteryLevel = null;
		}
	}

	private final List<DiscoveredBluetoothDevice> mDevices = new ArrayList<>();

	private int indexOf(final ScanResult result)
	{
		int i = 0;
		for (final DiscoveredBluetoothDevice device : mDevices)
		{
			if (device.matches(result))
				return i;
			i++;
		}
		return -1;
	}

	synchronized DiscoveredBluetoothDevice newDeviceDiscovered(final ScanResult result)
	{
		DiscoveredBluetoothDevice device;

		final int index = indexOf(result);
		if (index == -1)
		{
			device = new DiscoveredBluetoothDevice(result);
			mDevices.add(device);
			device.update(result);
			return device;
		}
		else
		{
			device = mDevices.get(index);
			device.update(result);
			return null;
		}
	}

	private boolean matchesUuidFilter(final ScanResult result)
	{
		final ScanRecord record = result.getScanRecord();
		if (record == null)
			return false;

		final List<ParcelUuid> uuids = record.getServiceUuids();
		if (uuids == null)
			return false;

		for (ParcelUuid uuid : uuids)
			if (uuid.getUuid().toString().toLowerCase().equals(HR_SERVICE_UUID.toString().toLowerCase()))
				return true;
		return false;
	}
}
