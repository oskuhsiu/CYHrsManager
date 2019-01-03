package com.changyow.cyhrsmanagerprj;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2017/11/3.
 */

public class DeviceAdapter extends BaseAdapter
{
	private Context mContext;
	private ArrayList<String> mDeviceNames = new ArrayList();

	private int mSelectedIndex = -1;

	public DeviceAdapter(Context context)
	{
		this.mContext = context;
	}

	public int getSelectedIndex()
	{
		return mSelectedIndex;
	}

	public void setSelectedIndex(int selectedIndex)
	{
		mSelectedIndex = selectedIndex;
	}

	public void setDeviceNames(List<String> names)
	{
		mDeviceNames.clear();
		mDeviceNames.addAll(names);
	}

	@Override
	public int getCount()
	{
		return mDeviceNames.size();
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.layout_device_item, null);
		}

		TextView txtTitle = (TextView) convertView.findViewById(R.id.txvName);
		TextView txvConnected = (TextView) convertView.findViewById(R.id.txvConnected);

		if (position == mSelectedIndex)
			txvConnected.setVisibility(View.VISIBLE);
		else
			txvConnected.setVisibility(View.INVISIBLE);

		txtTitle.setText(mDeviceNames.get(position));

		return convertView;
	}
}
