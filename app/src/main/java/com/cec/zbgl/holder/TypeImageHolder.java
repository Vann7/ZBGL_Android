package com.cec.zbgl.holder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceCourse;


public class TypeImageHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView name;
    private TextView type;
    private TextView desc;


    public TypeImageHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.course_image_img);

    }

    public void  bindHolder(DeviceCourse course) {

    }
}
