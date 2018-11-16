package com.cec.zbgl.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;
import com.squareup.picasso.Picasso;

import java.io.File;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;

public class TypeDeviceHolder extends RecyclerView.ViewHolder{

    private Context mContext;
    private ImageView imageView;
    private TextView name;
    private TextView type;
    private TextView desc;
//    final int mGridWidth;

    public TypeDeviceHolder(View itemView) {
        super(itemView);
        itemView.setBackgroundColor(Color.LTGRAY);
//        imageView = (ImageView) itemView.findViewById(R.id.item01_img);
        name = (TextView) itemView.findViewById(R.id.item01_tv_name);
        type = (TextView) itemView.findViewById(R.id.item01_tv_type);
        desc = (TextView) itemView.findViewById(R.id.item01_tv_desc);

    }

    public void bindHolder(DeviceInfo device) {
        name.setText(device.getName());
        type.setText(device.getBelongSys());
        desc.setText(device.getLocation());

    }
}
