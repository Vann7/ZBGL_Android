package com.cec.zbgl.holder;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceCourse;
import com.squareup.picasso.Picasso;

import java.io.File;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;


public class TypeImageHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private ImageView imageView;
    final int mGridWidth;


    public TypeImageHolder(View itemView, Context context, int gridWidth) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.course_image_img);
        mContext = context;
        mGridWidth = gridWidth;
    }

    public void  bindHolder(DeviceCourse course) {
        if (course.getLocation() != null ) {
            File imageFile = new File(course.getLocation());
            // 显示图片
            Picasso.with(mContext)
                    .load(imageFile)
                    .placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
                    .tag(MultiImageSelectorFragment.TAG)
                    .resize(mGridWidth, mGridWidth)
                    .centerCrop()
                    .into(imageView);
        }else {
            imageView.setImageResource(R.mipmap.default_image);
        }
    }
}
