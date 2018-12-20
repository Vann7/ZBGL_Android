package com.cec.zbgl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.UploadAdapter;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.service.FTPService;
import com.cec.zbgl.utils.LogUtil;
import com.cec.zbgl.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiVideoSelectorActivity;
import me.nereo.multi_image_selector.bean.Video;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VideoUploadActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView back_iv;
    private TextView head_tv;
    private TextView checked_tv;
    private TextView unchecked_tv;
    private TextView upload_btn;
    private ListView upload_lv;;
    private RelativeLayout masker_rl;
    private View masker;
    private ProgressBar upload_bar;
    private TextView upload_value_tv;
    private UploadAdapter mAdapter;
    private List<Video> mList = new ArrayList<>();
    private List<Video> mUpList = new ArrayList<>();
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
    private static final int WRITE_PERMISSION = 0x01;
    private int width;
    private int height;


    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
    public static final int FTP_UPLOADING = 0;
    public static final int FTP_SUCCESS = 1;



    private final String[] MEDIA_COLUMNS ={
            MediaStore.Files.FileColumns._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Thumbnails.DATA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;

        requestWritePermission();
        initView();
        initEvent();
        new UploadTask().execute();
    }



    private List<Video> initData() {

        List<Video> list = new ArrayList<>();
        Cursor cursor = this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MEDIA_COLUMNS, null, null, null);

        cursor.moveToFirst();
        do{
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[0]));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[1]));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[2]));
                long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[3]));
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(this.getContentResolver(),
                    id, MediaStore.Video.Thumbnails.MINI_KIND, null);
            String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[4]));
            if(!fileExist(path)){continue;}
            Video video = null;
            if (!TextUtils.isEmpty(name)) {
//                    video = new Video(id,path, name);
                video = new Video(id,path, name, bitmap);
                video.setThumbPath(thumbPath);
                video.setDateTime(dateTime);
                list.add(video);
            }
        }while(cursor.moveToNext());
        return list.stream().sorted(Video::compareTo).collect(Collectors.toList());
    }


    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        head_tv.setText("文件上传");
        checked_tv = (TextView) findViewById(R.id.select_tv);
        unchecked_tv = (TextView) findViewById(R.id.unselect_tv);
        checked_tv.setVisibility(VISIBLE);
        upload_lv = (ListView) findViewById(R.id.upload_lv);
        mAdapter = new UploadAdapter(mList, this,upload_lv);
        upload_bar = (ProgressBar) findViewById(R.id.upload_bar);
        upload_btn = (TextView) findViewById(R.id.upload_btn);
        masker = findViewById(R.id.upload_masker);
        masker.setBackgroundColor(Color.alpha(0));
        masker_rl = (RelativeLayout) findViewById(R.id.upload_masker_rl);
        masker_rl.setBackgroundColor(Color.alpha(55));
        upload_value_tv = (TextView) findViewById(R.id.upload_ProgressBar_tv);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) upload_lv.getLayoutParams();
//        System.out.println(height);
//        int h = upload_btn.getLineHeight();
//        System.out.println(h);
//        params.height = height/2;
//        upload_lv.setLayoutParams(params);
    }

    private void initEvent() {
        back_iv.setOnClickListener(this);
        checked_tv.setOnClickListener(this);
        unchecked_tv.setOnClickListener(this);
        upload_btn.setOnClickListener(this);
        masker.setOnClickListener(this);
        //绑定ListView事件
        upload_lv.setAdapter(mAdapter);
        mAdapter.setOnListClickListener((video, position) -> {
            if (video.isUpload()) return;
            if (!mUpList.contains(video)) {
                mUpList.add(video);
            }else {
                mUpList.remove(video);
            }
        uploadSum(2);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_back_iv :
                finish();
                break;
            case R.id.select_tv :
               uploadSum(0);

                break;

            case R.id.unselect_tv :
                uploadSum(1);
                break;
            case R.id.upload_btn :
                if (mUpList.size() == 0) return;
                new UploadVideo().execute(mUpList);
                break;
            case R.id.upload_masker :
                break;
        }
    }

    private void uploadSum(int flag) {


        if (flag == 0) {
            checked_tv.setVisibility(GONE);
            unchecked_tv.setVisibility(VISIBLE);
            mUpList.clear();
            for (Video video : mList) {
                if (!video.isUpload()) mUpList.add(video);
            }
        } else if (flag == 1) {
            checked_tv.setVisibility(VISIBLE);
            unchecked_tv.setVisibility(GONE);
            mUpList.clear();
        } else if (flag == 3) {
            mUpList.clear();
            upload_btn.setText("上传");
            upload_btn.setBackgroundColor(Color.rgb(108,108,108));
            return;
        }

        if (mUpList.size() > 0) {
            upload_btn.setText("上传 (" + mUpList.size()+")");
            upload_btn.setBackgroundColor(Color.rgb(12,91,174));
        }else {
            upload_btn.setText("上传");
            upload_btn.setBackgroundColor(Color.rgb(108,108,108));
        }

        mAdapter.upload(mUpList);
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

    class UploadTask extends AsyncTask<Void,Void,List<Video>> {

        @Override
        protected void onPostExecute(List<Video> videos) {
            super.onPostExecute(videos);
            upload_bar.setVisibility(GONE);
            upload_lv.setVisibility(VISIBLE);
            mList = videos;
            mAdapter.onDateChange(videos);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            upload_bar.setVisibility(VISIBLE);
            upload_lv.setVisibility(GONE);

        }

        @Override
        protected List<Video> doInBackground(Void... voids) {
            List<Video> list = initData();
            return list;
        }
    }


    class UploadVideo extends AsyncTask<List<Video>, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            upload_bar.getProgress();
            masker_rl.setVisibility(VISIBLE);
        }

        @Override
        protected Integer doInBackground(List<Video>... lists) {
            List<Video> list = lists[0];
            FTPService ftpService = new FTPService();
            for (Video video : list) {
                File file = new File(video.getPath());
                try {
                    ftpService.uploadSingleFile(file, "/ftp/video", (currentStep, uploadSize, file1) -> {
                        if (currentStep.equals(FTP_UPLOAD_SUCCESS)) {
                            video.setUpload(true);
                            for (Video v : mList) {
                                if (v.getId() == video.getId()) {
                                    v = video;
                                }
                            }
                            publishProgress(FTP_SUCCESS, 0);
                            LogUtil.d("upload", "-----upload success-----");
                        }else if (currentStep.equals(FTP_UPLOAD_LOADING)) {
                            long size = file1.length();
                            float num = (float) uploadSize / (float) size;
                            int result = (int)(num * 100);
                            publishProgress(FTP_UPLOADING, result);
                            LogUtil.d("upload", " upload: " + result + "%");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            masker_rl.setVisibility(GONE);
            ToastUtils.showShort("上传完毕");
            mAdapter.uploadFinish(mList,mUpList);
            uploadSum(3);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case FTP_SUCCESS :
                    upload_value_tv.setText("100%");

                    break;
                case FTP_UPLOADING :
                    upload_value_tv.setText(values[1] + "%");
                    break;
            }
        }
    }




}
