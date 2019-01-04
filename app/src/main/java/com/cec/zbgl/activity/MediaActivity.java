package com.cec.zbgl.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.service.CourseService;
import com.cec.zbgl.utils.FileUtils;
import com.cec.zbgl.utils.LogUtil;
import com.cec.zbgl.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.media.MediaPlayer.*;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MediaActivity extends AppCompatActivity implements View.OnClickListener ,OnVideoSizeChangedListener {

    private RelativeLayout media_rl;

    private SurfaceView surfaceView;

    private SurfaceHolder holder;

    private TextView tvCurrentT, tvDuration;

    private ProgressBar progressBar;

    private ProgressBar progressBar_filled;

    private MediaPlayer mediaPlayer;

    private Uri uri;

    private Handler handler;

    private float downX, downY;

    private int screenWidth;

    private int FACTOR = 100;

    private ImageView back_iv;

    private ImageView play_iv;

    private ImageView pause_iv;

    private ImageView image_iv;

    private RelativeLayout play_rl;

    private TextView masker_tv;

    private CourseService courseService;

    private DeviceCourse course;

    private long id;

    private int surfaceWidth;

    private int surfaceHeight;

    private int currentPosition;

    private String path;

    private FileInputStream fis;

    private GestureDetector mGestureDetector;

    private AudioManager mAudioManager;

//    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        transition();
        // 隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
        }
        //TODO　将屏幕设置为横屏()
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //TODO 将屏幕设置为竖屏()
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initData();
        initEvent();

    }

    private void transition() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Fade fade = new Fade();
        fade.setDuration(200);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //TODO　将屏幕设置为横屏()
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //TODO 将屏幕设置为竖屏()
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.seekTo(currentPosition);
        }
//        setVideoSize();
    }


    private void initView() {
        media_rl = (RelativeLayout) findViewById(R.id.media_rl);
        media_rl.setBackgroundColor(Color.rgb(0,0,0));
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
//        surfaceView.bringToFront();
//        surfaceView.setBackgroundColor(Color.rgb(0,0,0));

        holder = surfaceView.getHolder();
//        tvSound = (TextView) findViewById(R.id.tv_sound);
        tvCurrentT = (TextView) findViewById(R.id.tv_current);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar_filled = (ProgressBar) findViewById(R.id.progress_filled);
        play_rl = (RelativeLayout) findViewById(R.id.play_rl);
        back_iv = (ImageView) findViewById(R.id.surface_back);
        play_iv = (ImageView) findViewById(R.id.surface_play);
        pause_iv = (ImageView) findViewById(R.id.surface_pause);
        image_iv = (ImageView) findViewById(R.id.media_image);
        masker_tv = (TextView) findViewById(R.id.surface_masker);

    }

    private void initData() {
        courseService = new CourseService();
        id = getIntent().getLongExtra("id",0);
        course = courseService.getDevice(id);

        File file = new File(course.getLocation());
        fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            surfaceWidth = size.x;
            surfaceHeight = size.y;
        }else{
            surfaceWidth = wm.getDefaultDisplay().getWidth();
            surfaceHeight = wm.getDefaultDisplay().getHeight();
        }
        File imageFile = FileUtils.byte2File(course.getImage(),this);
        // 显示图片
        Picasso.with(this)
                .load(imageFile)
                .resize(surfaceWidth, surfaceHeight)
                .centerCrop()
                .into(image_iv);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP :
                break;
        }
        return true;
    }



    private void initEvent() {
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       holder.addCallback(new MyCallBack());

       mediaPlayer.setOnPreparedListener(mp -> {
       // 开始播放视频
//         mediaPlayer.start();
       // 设置总时长
       tvDuration.setText(TimeUtils.stringForTime(mp.getDuration()));
//            tvCurrentT.setText(mp.getCurrentPosition() / 1000 + "");
       tvCurrentT.setText("00:00");
       progressBar.setMax(mp.getDuration());
       progressBar_filled.setMax(mp.getDuration());
       updateView();
           });

       mediaPlayer.setOnCompletionListener(mp -> {
           play_finish();
       });

       mediaPlayer.setOnVideoSizeChangedListener(this::onVideoSizeChanged);

       mediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
           @Override
           public void onSeekComplete(MediaPlayer mp) {
               mp.start();
           }
       });

        back_iv.setOnClickListener(this);
        play_iv.setOnClickListener(this);
        masker_tv.setOnClickListener(this);
        media_rl.setOnClickListener(this);
        pause_iv.setOnClickListener(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());


    }




    private void setVolume(boolean flag)
    {
        // 获取音量管理器
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // 获取当前音量
        int curretnV = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (flag)
        {
            curretnV++;
        }
        else
        {
            curretnV--;
        }
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, curretnV,
                AudioManager.FLAG_SHOW_UI);

        /**
         * 1.AudioManager.STREAM_MUSIC 多媒体 2.AudioManager.STREAM_ALARM 闹钟
         * 3.AudioManager.STREAM_NOTIFICATION 通知 4.AudioManager.STREAM_RING 铃音
         * 5.AudioManager.STREAM_SYSTEM 系统提示音 6.AudioManager.STREAM_VOICE_CALL
         * 电话
         *
         * AudioManager.FLAG_SHOW_UI:显示音量控件
         */
    }


    /**
     * 更新播放进度的递归
     */
    private void updateView()
    {
        handler.postDelayed(() -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying())
            {
                tvCurrentT.setText(TimeUtils.stringForTime(mediaPlayer.getCurrentPosition()));
                progressBar.setProgress(mediaPlayer.getCurrentPosition());
                progressBar_filled.setProgress(mediaPlayer.getCurrentPosition());
            }
            updateView();
        }, 1000);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surface_masker :
                disappear();
                break;
            case R.id.surface_play :
                image_iv.setVisibility(GONE);
                disappear();
                mediaPlayer.start();
                break;
            case R.id.surface_back :
                finish();
                overridePendingTransition(R.anim.dd_mask_in, R.anim.dd_mask_out);
                break;
            case R.id.media_rl :
                appear();
                break;
            case R.id.surface_pause :
                mediaPlayer.pause();
                pause_iv.setVisibility(GONE);
                play_iv.setVisibility(VISIBLE);

        }
    }

    private void disappear() {
        masker_tv.setVisibility(GONE);
        back_iv.setVisibility(GONE);
        play_iv.setVisibility(GONE);
        pause_iv.setVisibility(GONE);
        play_rl.setVisibility(GONE);
        progressBar_filled.setVisibility(VISIBLE);
    }

    private void play_finish() {
        image_iv.setVisibility(VISIBLE);
        masker_tv.setVisibility(GONE);
        back_iv.setVisibility(VISIBLE);
        play_iv.setVisibility(VISIBLE);
        pause_iv.setVisibility(GONE);
        play_rl.setVisibility(GONE);
        progressBar_filled.setVisibility(GONE);
    }

    private void appear() {
        masker_tv.setVisibility(VISIBLE);
        if (mediaPlayer.isPlaying()){
            pause_iv.setVisibility(VISIBLE);
        }else {
            play_iv.setVisibility(VISIBLE);
        }
        back_iv.setVisibility(VISIBLE);
        play_rl.setVisibility(VISIBLE);
        progressBar_filled.setVisibility(GONE);
    }



    /**
     * 重新设置mediaPlayer 长宽比例
     */
    private void setVideoSize() {
        float videoWidth = mediaPlayer.getVideoWidth();
        float videoHeight = mediaPlayer.getVideoHeight();
        int width, height;
        float scale = videoWidth / videoHeight;
        width = 0;
        height = surfaceHeight;
        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;
        if (getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) surfaceWidth,(float) videoHeight / (float) surfaceHeight);
//            width = (int)(scale * surfaceHeight);
        } else{
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth/(float) surfaceHeight),(float) videoHeight/(float) surfaceWidth);
//            width = (int)(scale * surfaceHeight);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (float) Math.ceil(videoWidth / max);
        videoHeight = (float) Math.ceil(videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
//         RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
         RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                 Float.valueOf(videoWidth).intValue(), Float.valueOf(videoHeight).intValue());
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(params);

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        setVideoSize();
    }


    /**
     * SurfaceHolder  内部类
     */
    private class MyCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder)
        {

            // 当surfaceView被创建完成之前才能绘制画布,所以只能在此回调方法之后开始播放
            try {
                // 1.指定播放源
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + getPackageName() + "/" + R.raw.demo);//TODO 在raw下添加video1视频（）
//                mediaPlayer.setDataSource(getApplicationContext(),uri);
                mediaPlayer.setDataSource(fis.getFD());
                // 2.将mediaplayer和surfaceView时行绑定
                mediaPlayer.setDisplay(holder);
                // 3.准备进行异步播放(当prepareAsync被调用后会执行mediaPlayer的onPrepared回调方法)
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setVideoSize();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height)
        {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            try{
                if (mediaPlayer != null) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 双击
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        /**
         * 滑动
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * @return
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

}
