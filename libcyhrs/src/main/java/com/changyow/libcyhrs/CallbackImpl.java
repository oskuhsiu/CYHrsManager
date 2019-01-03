package com.changyow.libcyhrs;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Osku on 2019/1/3.
 */
class CallbackImpl implements HrsManagerCallbacks
{
	@Override
	public void onScanResult(int callbackType, DiscoveredBluetoothDevice device)
	{

	}

	@Override
	public void onBatchScanResults(List<DiscoveredBluetoothDevice> results)
	{

	}

	@Override
	public void onScanFailed(int errorCode)
	{

	}

	@Override
	public void onDeviceConnecting(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onDeviceConnected(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onDeviceDisconnecting(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onDeviceDisconnected(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onLinkLossOccurred(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound)
	{

	}

	@Override
	public void onDeviceReady(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onBondingRequired(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onBonded(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onBondingFailed(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode)
	{

	}

	@Override
	public void onDeviceNotSupported(@NonNull BluetoothDevice device)
	{

	}

	@Override
	public void onBatteryLevelChanged(@NonNull BluetoothDevice device, int batteryLevel)
	{

	}

	@Override
	public void onBodySensorLocationReceived(@NonNull BluetoothDevice device, int sensorLocation)
	{

	}

	@Override
	public void onHeartRateMeasurementReceived(@NonNull BluetoothDevice device, int heartRate, @Nullable Boolean contactDetected, @Nullable Integer energyExpanded, @Nullable List<Integer> rrIntervals)
	{

	}
}
