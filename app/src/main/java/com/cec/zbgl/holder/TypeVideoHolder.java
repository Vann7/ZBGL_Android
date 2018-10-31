package com.cec.zbgl.holder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceCourse;


public class TypeVideoHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView time_tv;
    private TextView size_tv;
    private TextView desc_tv;


    public TypeVideoHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.course_vedio_img);
        time_tv = (TextView) itemView.findViewById(R.id.course_vedio_time);
        size_tv = (TextView) itemView.findViewById(R.id.course_vedio_size);
        desc_tv = (TextView) itemView.findViewById(R.id.course_vedio_desc);
    }

    public void  bindHolder(DeviceCourse course) {
        time_tv.setText("00:12:36");
        size_tv.setText(String.valueOf("2.69 MB"));
        desc_tv.setText("装备使用教程视频01.MP4");
    }
}
