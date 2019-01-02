package com.changyow.libcyhrs;

import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.common.profile.battery.BatteryLevelCallback;
import no.nordicsemi.android.ble.common.profile.hr.BodySensorLocationCallback;
import no.nordicsemi.android.ble.common.profile.hr.HeartRateMeasurementCallback;

/**
 * Created by Osku on 2018/12/27.
 */

public interface HrsManagerCallbacks extends BleManagerCallbacks, BatteryLevelCallback, BodySensorLocationCallback, HeartRateMeasurementCallback, BleScanCallback
{
}
