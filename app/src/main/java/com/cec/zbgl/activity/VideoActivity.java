package com.cec.zbgl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.cec.zbgl.R;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.utils.ToastUtils;

public class VideoActivity extends AppCompatActivity {

    private VideoView video;
    private String[] permission_write_storage = { Manifest.permission.READ_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
//        onRequestPermissionsResult();
        authority();
        play();
    }


    @Override
    protected void onResume() {
        /* 设置为横屏  */
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    //权限判断
    private void authority() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //先判断有没有权限 ，没有就在这里进行权限的申请
            ActivityCompat.requestPermissions(this,
                    permission_write_storage,333);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    ) {
                // 开始提交摄存储请求权限
                ActivityCompat.requestPermissions(this, permission_write_storage, 333);
            }
        }
    }


    private void play() {
        video = (VideoView) findViewById(R.id.video);
        MediaController mc =new MediaController(VideoActivity.this);
        video.setMediaController(mc);

        video.setVideoPath("android.resource://" + getPackageName()
                + "/" + R.raw.demo);
        video.start();
        video.requestFocus();
        video.setOnCompletionListener(mp -> {
            ToastUtils.showShort("视频播放完毕");
            finish();
        });
    }
}
