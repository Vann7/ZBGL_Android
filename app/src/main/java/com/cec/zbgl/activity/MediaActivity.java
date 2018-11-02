package com.cec.zbgl.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cec.zbgl.R;

import java.io.IOException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MediaActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private ProgressBar progressBar;

    private TextView masker_tv;
    private TextView back_tv;
    private ImageView play_iv;
    private TextView fill_tv;

    private boolean isFilled;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        initView();
        initEvent();
        disappear();
        play_iv.setVisibility(VISIBLE);
    }

    private void initEvent() {
        masker_tv.setOnClickListener(this);
        back_tv.setOnClickListener(this);
        play_iv.setOnClickListener(this);
        fill_tv.setOnClickListener(this);
        surfaceView.setOnClickListener(this);
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        masker_tv = (TextView) findViewById(R.id.surface_masker);
        back_tv = (TextView) findViewById(R.id.surface_back);
        play_iv = (ImageView) findViewById(R.id.surface_play);
        fill_tv = (TextView) findViewById(R.id.surface_fill);

        String uri= "android.resource://" + getPackageName()+ "/" + R.raw.demo;
        player=new MediaPlayer();
        try {
            player.setDataSource(this, Uri.parse(uri));
            holder=surfaceView.getHolder();
            holder.addCallback(new MyCallBack());
            player.prepare();
            player.setOnPreparedListener(mp -> {
                progressBar.setVisibility(View.INVISIBLE);
//                player.start();
                player.setLooping(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surface_masker :
                disappear();
                break;
            case R.id.surface_play :
                if (isPlaying == false) {
                    player.start();
                    isPlaying = true;
                }else {
                    player.pause();
                    isPlaying = false;
                }

                break;
            case R.id.surface_back :
                finish();
                break;
            case R.id.surface_fill :

                break;
            case R.id.surfaceView :
                appear();
        }
    }

    private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            System.out.println("surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            System.out.println("surfaceDestroyed");
            player.stop();
        }
    }


    private void disappear() {
        masker_tv.setVisibility(GONE);
        back_tv.setVisibility(GONE);
        play_iv.setVisibility(GONE);
        fill_tv.setVisibility(GONE);
    }

    private void appear() {
        masker_tv.setVisibility(VISIBLE);
        back_tv.setVisibility(VISIBLE);
        play_iv.setVisibility(VISIBLE);
        fill_tv.setVisibility(VISIBLE);
    }


}
