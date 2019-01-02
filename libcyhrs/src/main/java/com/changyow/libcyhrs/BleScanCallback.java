package com.changyow.libcyhrs;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * Created by Osku on 2018/12/28.
 */

public interface BleScanCallback
{
	void onScanResult(final int callbackType, final DiscoveredBluetoothDevice device);

	void onBatchScanResults(final List<DiscoveredBluetoothDevice> results);

	void onScanFailed(final int errorCode);
}
