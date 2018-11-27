package com.cec.zbgl.holder;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;


public class TypeVideoHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private ImageView imageView;
    private TextView time_tv;
    private TextView size_tv;
    private TextView desc_tv;
    private int mGridWidth;


    public TypeVideoHolder(View itemView, Context context, int gridWidth) {
        super(itemView);
        mContext = context;
        this.mGridWidth = gridWidth;
        imageView = (ImageView) itemView.findViewById(R.id.course_vedio_img);
        time_tv = (TextView) itemView.findViewById(R.id.course_vedio_time);
        size_tv = (TextView) itemView.findViewById(R.id.course_vedio_size);
        desc_tv = (TextView) itemView.findViewById(R.id.course_vedio_desc);
    }

    public void  bindHolder(DeviceCourse course) {
        time_tv.setText("00:12:36");
        size_tv.setText(String.valueOf("2.69 MB"));
        desc_tv.setText("装备使用教程视频01.MP4");
//        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                + mContext.getPackageName() + "/" + R.raw.demo);//TODO 在raw下添加video1视频（）
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(mContext,uri);
//        Bitmap bitmap = mmr.getFrameAtTime();

        if (course.getLocation() != null && course.getImage() != null ) {
            File imageFile = FileUtils.byte2File(course.getImage(), mContext);
            // 显示图片
            Picasso.with(mContext)
                    .load(imageFile)
//                    .placeholder(me.nereo.multi_image_selector.R.drawable.mis_default_error)
                    .tag(MultiImageSelectorFragment.TAG)
                    .resize(mGridWidth, mGridWidth)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
