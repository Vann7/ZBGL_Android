package com.cec.zbgl.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.ImageAdapter;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.service.CourseService;
import com.cec.zbgl.transformer.ScalePageTransformer;
import com.cec.zbgl.view.ScaleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private TextView pagerCount_tv;
    private ScaleView imageView;
    private ImageView mCancle_iv;
    private ImageAdapter mImageAdapter;
    private ProgressBar mBar;
    private List<DeviceCourse> mData =new ArrayList<>();
    private CourseService courseService;
    private int position;
    private String deviceId;
    private String sysId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transition();
        setContentView(R.layout.activity_photo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 0, 0));
        }
        initData();
        initView();

    }

    private void transition() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        Fade fade = new Fade();
//
//        fade.setDuration(200);
//        getWindow().setEnterTransition(fade);
    }

    private void initData() {
        courseService = new CourseService();
        deviceId = getIntent().getStringExtra("deviceId");
        sysId = getIntent().getStringExtra("sysId");
        position = getIntent().getIntExtra("position",0);
        if (sysId != null) {
            mData = courseService.loadBySysid(sysId, Constant.COURSE_IMAGE);
        }
        if (deviceId != null) {
            mData = courseService.loadByDid(deviceId, Constant.COURSE_IMAGE);
        }

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.image_pager);
        mViewPager.setBackgroundColor(Color.rgb(0,0,0));
        pagerCount_tv = (TextView) findViewById(R.id.image_num_tv);
        mCancle_iv = (ImageView) findViewById(R.id.image_cancle_iv);
        mCancle_iv.setOnClickListener(this);
        pagerCount_tv.setBackgroundColor(Color.argb(55, 0, 0,0));
        pagerCount_tv.setText(position + " / " + mData.size());
        mImageAdapter = new ImageAdapter(getPages());
        mViewPager.setAdapter(mImageAdapter);
        mViewPager.setPageTransformer(true, new ScalePageTransformer());
        mViewPager.setCurrentItem(position -1);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerCount_tv.setText( (position + 1) + " / " + mData.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_cancle_iv :
                this.finish();
                overridePendingTransition(R.anim.dd_mask_in, R.anim.dd_mask_out);
        }
    }

    /**
     * 获取pagerView 照片List
     * @return
     */
    private List<View> getPages() {
        List<View> pages = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            imageView = new ScaleView(this);
            DeviceCourse course = mData.get(i);
            if (course.getImage_full() != null && course.getLocation() != null) {
                File file = new File(course.getLocation());
                if (file.exists()){
                    Uri uri = Uri.parse(course.getLocation());
                    imageView.setImageURI(uri);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(course.getImage_full(),0,course.getImage_full().length);
                    imageView.setImageBitmap(bitmap);
                }

            } else {
                imageView.setImageResource(R.mipmap.default_image);
            }
            pages.add(imageView);
        }
        return pages;
    }
}
