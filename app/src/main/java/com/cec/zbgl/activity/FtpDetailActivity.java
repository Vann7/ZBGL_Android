package com.cec.zbgl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.UploadAdapter;
import com.cec.zbgl.common.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiVideoSelectorActivity;
import me.nereo.multi_image_selector.bean.Video;

import static android.view.View.VISIBLE;

public class FtpDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView back_iv;
    private TextView head_tv;
    private ImageView more_iv;

    private ListView upload_lv;;
    private UploadAdapter mAdapter;
    private List<Video> mList;
    private List<Video> mUpList;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
    private static final int WRITE_PERMISSION = 0x01;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
//        requestWritePermission();
        initiData();
        initView();
        initEvent();

    }

    private void initiData() {
        mList = new ArrayList<>();
        mUpList = new ArrayList<>();
    }


    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        head_tv.setText("文件上传");
        mAdapter = new UploadAdapter(mList, this,upload_lv);


    }

    private void initEvent() {
        back_iv.setOnClickListener(this);
        //绑定ListView事件
        upload_lv.setAdapter(mAdapter);
        mAdapter.setOnListClickListener((video, position) -> {
            if (!mUpList.contains(video)) {
                mUpList.add(video);
                mAdapter.upload(mUpList);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_back_iv :
                finish();
                break;
            case R.id.upload_more :
                pickVideo();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constant.CODE_PICK_VIDEO_REQUEST: //相册获取视频 回调存储
                List<String> paths = data.getStringArrayListExtra(MultiVideoSelectorActivity.EXTRA_RESULT);
                System.out.println(paths);
                break;
        }
    }

    private boolean fileExist(String path){
        if(!TextUtils.isEmpty(path)){
            return new File(path).exists();
        }
        return false;
    }

    /**
     * 相册选取视频
     */
    private void pickVideo() {
        ArrayList<String> defaultDataArray = new ArrayList<>();
        Intent intent = new Intent(this, MultiVideoSelectorActivity.class);
        intent.putExtra(MultiVideoSelectorActivity.EXTRA_SELECT_COUNT, 9);
        intent.putExtra(MultiVideoSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        intent.putStringArrayListExtra(MultiVideoSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
        startActivityForResult(intent, Constant.CODE_PICK_VIDEO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                showCameraAction();
            }
        } else if( requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestWritePermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
}
