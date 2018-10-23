package com.cec.zbgl.holder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView name;
    private TextView type;
    private TextView desc;

    public DeviceViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.item01_img);
        name = (TextView) itemView.findViewById(R.id.item01_tv_name);
        type = (TextView) itemView.findViewById(R.id.item01_tv_type);
        desc = (TextView) itemView.findViewById(R.id.item01_tv_desc);
        itemView.setBackgroundColor(Color.GRAY);
    }

    public void bindHolder(DeviceInfo device) {
        name.setText(device.getName());
        type.setText(device.getBelongSys());
        desc.setText(device.getLocation());
    }
}
