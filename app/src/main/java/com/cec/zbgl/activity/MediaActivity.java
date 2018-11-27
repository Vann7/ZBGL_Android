package com.cec.zbgl.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import java.io.IOException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MediaActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener ,MediaPlayer.OnVideoSizeChangedListener {

    private SurfaceView surfaceView;

    private SurfaceHolder holder;

    private TextView tvSound, tvCurrentT, tvDuration;

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

    private RelativeLayout media_rl;
    private RelativeLayout play_rl;

    private TextView masker_tv;

    private CourseService courseService;

    private DeviceCourse course;

    private long id;

    private int surfaceWidth;

    private int surfaceHeight;

    private int currentPosition;

//    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media);
        //TODO　将屏幕设置为横屏()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //TODO 将屏幕设置为竖屏()
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
        initData();
        initEvent();

    }


    private void initView() {
        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        holder = surfaceView.getHolder();
        tvSound = (TextView) findViewById(R.id.tv_sound);
        tvCurrentT = (TextView) findViewById(R.id.tv_current);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar_filled = (ProgressBar) findViewById(R.id.progress_filled);
        media_rl = (RelativeLayout) findViewById(R.id.rl_media);
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
    protected void onResume() {
        super.onResume();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initEvent() {
        surfaceView.setOnTouchListener((v, event) -> {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // TODO 音量
                    float distanceX = event.getX() - downX;
                    float distanceY = event.getY() - downY;
                    if (downX > screenWidth - 200
                            && Math.abs(distanceX) < 50
                            && distanceY > FACTOR)
                    {
                        // TODO 减小音量
                        setVolume(false);
                    }
                    else if (downX > screenWidth - 200
                            && Math.abs(distanceX) < 50
                            && distanceY < -FACTOR)
                    {
                        // TODO 增加音量
                        setVolume(true);

                    }
                    // TODO 播放进度调节
                    if (Math.abs(distanceY) < 50 && distanceX > FACTOR)
                    {
                        // TODO 快进
                        int currentT = mediaPlayer.getCurrentPosition();//播放的位置
                        mediaPlayer.seekTo(currentT + 15000);
                        downX = event.getX();
                        downY = event.getY();
                        LogUtil.i("info", "distanceX快进=" + distanceX);
                    }
                    else if (Math.abs(distanceY) < 50
                            && distanceX < -FACTOR)
                    {
                        // TODO 快退
                        int currentT = mediaPlayer.getCurrentPosition();
                        mediaPlayer.seekTo(currentT - 15000);
                        downX = event.getX();
                        downY = event.getY();
                        LogUtil.i("info", "distanceX=" + distanceX);
                    }
                    break;
            }
            return true;
        });

        holder.addCallback(this);


        // TODO　给videoview设置播放源(通过本地存储卡来设置)
        // uri = Uri.fromFile(new File("/sdcard/download/video1.mp4"));
        uri = Uri.fromFile(new File(course.getLocation()));
        //TODO 给viedeoview设置播放源（通过资源文件来设置），PS 在调用资源文件时，在协议头后加上如“://”
//        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                + getPackageName() + "/" + R.raw.demo);//TODO 在raw下添加video1视频（）

        mediaPlayer.setOnPreparedListener(mp -> {
            // 开始播放视频
//            mediaPlayer.start();
            // 设置总时长
            tvDuration.setText(TimeUtils.stringForTime(mp.getDuration()));
//            tvCurrentT.setText(mp.getCurrentPosition() / 1000 + "");
            tvCurrentT.setText("00:00");
            progressBar.setMax(mp.getDuration());
            progressBar_filled.setMax(mp.getDuration());
            updateView();
        });

//        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());

        back_iv.setOnClickListener(this);
        play_iv.setOnClickListener(this);
        masker_tv.setOnClickListener(this);
        media_rl.setOnClickListener(this);
        pause_iv.setOnClickListener(this);

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
        tvSound.setVisibility(View.VISIBLE);
        tvSound.setText("音量:" + curretnV);
        handler.postDelayed(() -> tvSound.setVisibility(View.GONE), 1000);
        /**
         * 1.AudioManager.STREAM_MUSIC 多媒体 2.AudioManager.STREAM_ALARM 闹钟
         * 3.AudioManager.STREAM_NOTIFICATION 通知 4.AudioManager.STREAM_RING 铃音
         * 5.AudioManager.STREAM_SYSTEM 系统提示音 6.AudioManager.STREAM_VOICE_CALL
         * 电话
         *
         * AudioManager.FLAG_SHOW_UI:显示音量控件
         */
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // 当surfaceView被创建完成之前才能绘制画布,所以只能在此回调方法之后开始播放
        try
        {
            // 1.指定播放源
            mediaPlayer.setDataSource(this, uri);
            // 2.将mediaplayer和surfaceView时行绑定
            mediaPlayer.setDisplay(holder);
            // 3.准备进行异步播放(当prepareAsync被调用后会执行mediaPlayer的onPrepared回调方法)
            mediaPlayer.prepareAsync();

            setVideoSize();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    /**
     * 更新播放进度的递归
     */
    private void updateView()
    {
        handler.postDelayed(() -> {
            // TODO 设置进度控件
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
//                play_iv.setVisibility(GONE);
//                pause_iv.setVisibility(VISIBLE);
                mediaPlayer.start();
//                handler.postDelayed(() -> {
//                    disappear();
//                },2000);
                break;
            case R.id.surface_back :
                finish();
                break;
            case R.id.rl_media :
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
        tvSound.setVisibility(GONE);
        play_rl.setVisibility(GONE);
        progressBar_filled.setVisibility(VISIBLE);
    }

    private void appear() {
        masker_tv.setVisibility(VISIBLE);
        if (mediaPlayer.isPlaying()){
            pause_iv.setVisibility(VISIBLE);
        }else {
            play_iv.setVisibility(VISIBLE);
        }
        back_iv.setVisibility(VISIBLE);
        tvSound.setVisibility(VISIBLE);
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
//            max = Math.max((float) videoWidth / (float) surfaceWidth,(float) videoHeight / (float) surfaceHeight);
        } else{
            //横屏模式下按视频高度计算放大倍数值
//            max = Math.max(((float) videoWidth/(float) surfaceHeight),(float) videoHeight/(float) surfaceWidth);
            width = (int)(scale * surfaceHeight);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
//        videoWidth = (int) Math.ceil((float) videoWidth / max);
//        videoHeight = (int) Math.ceil((float) videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
         RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(params);

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        setVideoSize();
    }
}
