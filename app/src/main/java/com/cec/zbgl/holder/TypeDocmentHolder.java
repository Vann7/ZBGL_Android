package com.cec.zbgl.holder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceCourse;


public class TypeDocmentHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView name;
    private TextView desc;


    public TypeDocmentHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.course_document_img);
        name = (TextView) itemView.findViewById(R.id.course_document_name);
        desc = (TextView) itemView.findViewById(R.id.course_document_desc);
    }

    public void  bindHolder(DeviceCourse course) {
        name.setText(course.getName());
        desc.setText(course.getDescription());
    }
}
