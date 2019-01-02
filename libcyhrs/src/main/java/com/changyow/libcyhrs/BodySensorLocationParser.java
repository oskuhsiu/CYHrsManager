package com.changyow.libcyhrs;

import no.nordicsemi.android.ble.data.Data;

/**
 * Created by Osku on 2018/12/27.
 */

public class BodySensorLocationParser
{
	public static String parse(final Data data)
	{
		final int value = data.getIntValue(Data.FORMAT_UINT8, 0);

		switch (value)
		{
			case 1:
				return "Chest";
			case 2:
				return "Wrist";
			case 3:
				return "Finger";
			case 4:
				return "Hand";
			case 5:
				return "Ear Lobe";
			case 6:
				return "Foot";
			case 0:
			default:
				return "Other";
		}
	}
}