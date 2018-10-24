package com.cec.zbgl.holder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceCourse;

import org.w3c.dom.Text;


public class TypeOneHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView name;
    private TextView type;
    private TextView desc;


    public TypeOneHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.item01_img);
        name = (TextView) itemView.findViewById(R.id.item01_tv_name);
        type = (TextView) itemView.findViewById(R.id.item01_tv_type);
        desc = (TextView) itemView.findViewById(R.id.item01_tv_desc);
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    public void  bindHolder(DeviceCourse course) {
        name.setText(course.getName());
        type.setText(String.valueOf(course.getDeviceType()));
        desc.setText(course.getDescription());
    }
}
