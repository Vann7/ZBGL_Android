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
import com.cec.zbgl.model.DeviceCourse;


public class TypeDocmentHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private ImageView imageView;
    private TextView name;
    private TextView desc;


    public TypeDocmentHolder(View itemView, Context context) {
        super(itemView);
        this.mContext = context;
//        imageView = (ImageView) itemView.findViewById(R.id.course_document_img);
        name = (TextView) itemView.findViewById(R.id.course_document_name);
//        desc = (TextView) itemView.findViewById(R.id.course_document_desc);
    }

    public void  bindHolder(DeviceCourse course) {
//        if (course.getImage() != null) {
//            byte[] bytes = course.getImage();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            imageView.setImageBitmap(bitmap);
//        }else {
//            imageView.setImageResource(R.mipmap.default_image);
//        }
        name.setText(course.getName());
//        desc.setText(course.getDescription());
    }
}
