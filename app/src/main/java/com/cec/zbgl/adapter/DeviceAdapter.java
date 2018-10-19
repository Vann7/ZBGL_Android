package com.cec.zbgl.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter  extends ArrayAdapter<DeviceInfo> {

    private int mResourcedId;
    private Context mContext;
    private List<DeviceInfo> list;
    private int count;

    public DeviceAdapter(@NonNull Context context, int resource, List<DeviceInfo> list) {
        super(context, resource);
        this.mResourcedId = resource;
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DeviceInfo device = list.get(position);

//        if (null == convertView) {
//            convertView = LayoutInflater.from(mContext).inflate(mResourcedId,parent,false);
//        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(mResourcedId, null);

        TextView nameText = (TextView) convertView.findViewById(R.id.id_device_name);
        TextView typeText = (TextView) convertView.findViewById(R.id.id_device_type);
        TextView locText = (TextView) convertView.findViewById(R.id.id_device_location);

        nameText.setText(device.getName());
        typeText.setText(String.valueOf(device.getType()));
        locText.setText(device.getLocation());

        return convertView;
    }

    public void onDateChange(List<DeviceInfo> devices) {
        this.list = devices;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
       count = list.size();
        return count;
    }

    @Override
    public DeviceInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
