package com.cec.zbgl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.DownloadAdapter;
import com.cec.zbgl.adapter.DownloadDocAdapter;
import com.cec.zbgl.adapter.UploadAdapter;
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.model.FileFtp;
import com.cec.zbgl.service.FTPService;
import com.cec.zbgl.utils.LogUtil;
import com.cec.zbgl.utils.ToastUtils;
import com.google.gson.Gson;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.nereo.multi_image_selector.bean.Video;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cec.zbgl.activity.VideoUploadActivity.FTP_DOWN_LOADING;
import static com.cec.zbgl.activity.VideoUploadActivity.FTP_DOWN_SUCCESS;
import static com.cec.zbgl.activity.VideoUploadActivity.FTP_FILE_NOTEXISTS;
import static com.cec.zbgl.activity.VideoUploadActivity.FTP_UPLOAD_LOADING;

public class VideoDownloadActivity extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout video_rl;
    private RelativeLayout doc_rl;
    private ImageView back_iv;
    private TextView head_tv;
    private ImageView more_iv;

    private ProgressBar download_bar;
    private ProgressBar download_doc_bar;
    private List<FileFtp> mList; //当前视频List
    private List<FileFtp> mDocList; // 当前文档List
    private List<FileFtp> mDownList; // 下载视频List
    private List<FileFtp> mDocDownList; // 下载文档List
    private List<FileFtp> mFinish; // 下载完成视频List
    private List<FileFtp> mDocFinish; //下载完成文档List
    private List<String> vList; //本地视频List
    private List<String> dList; //本地文档List
    private ListView down_lv;
    private ListView downDoc_lv;
    private TextView checked_doc_tv;
    private TextView unchecked_doc_tv;
    private TextView checked_tv;
    private TextView unchecked_tv;
    private TextView download_btn;
    private TextView download_doc_btn;
    private TextView video_tv;
    private TextView doc_tv;
    private TextView download_value_tv;
    private DownloadAdapter mAdapter;
    private DownloadDocAdapter mDocAdapter;
    private RelativeLayout masker_rl;
    private View masker;
    private String hostName;
    private int serverPort;
    private String userName;
    private String password;
    private FTPClient client;
    private final String videoPath = "/ftp/video/";
    private final String docPath = "/ftp/document/";
    private int flag = 1;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
    private static final int WRITE_PERMISSION = 0x01;
    private static final int FTP_DOWNLOADING = 0;
    private static final int FTP_SUCCESS = 1;
    private int flag_success = 0;

    private final String[] MEDIA_COLUMNS ={
            MediaStore.Files.FileColumns._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Thumbnails.DATA
    };

    private final String[] DOCUMENT_COLUMNS ={
            MediaStore.Files.FileColumns._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Thumbnails.DATA
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        requestWritePermission();
        initiData();
        initView();
        initEvent();

        FtpTask ftpTask = new FtpTask();
        ftpTask.execute();

    }


    private void initView() {
        video_rl = (RelativeLayout) findViewById(R.id.video_rl);
        doc_rl = (RelativeLayout) findViewById(R.id.document_rl);
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        more_iv = (ImageView) findViewById(R.id.upload_more);
        more_iv.setVisibility(GONE);
        checked_tv = (TextView) findViewById(R.id.select_tv);
        unchecked_tv = (TextView) findViewById(R.id.unselect_tv);
        checked_doc_tv = (TextView) findViewById(R.id.select_tv);
        unchecked_doc_tv = (TextView) findViewById(R.id.unselect_tv);
        download_bar = (ProgressBar) findViewById(R.id.video_download_bar);
        download_doc_bar = (ProgressBar) findViewById(R.id.document_download_bar);
        head_tv.setText("文件下载");
        mList = new ArrayList<>();
        mDocList = new ArrayList<>();

        down_lv = (ListView) findViewById(R.id.video_download_lv);
        mAdapter = new DownloadAdapter(mList,this,down_lv);
        down_lv.setAdapter(mAdapter);

        downDoc_lv = (ListView) findViewById(R.id.document_download_lv);
        mDocAdapter = new DownloadDocAdapter(mDocList,this,downDoc_lv);
        downDoc_lv.setAdapter(mDocAdapter);

        video_tv = (TextView) findViewById(R.id.download_video_tv);
        doc_tv = (TextView) findViewById(R.id.download_doc_tv);

        download_btn = (TextView) findViewById(R.id.download_video_btn);
        download_doc_btn = (TextView) findViewById(R.id.download_document_btn);

        masker_rl = (RelativeLayout) findViewById(R.id.download_masker_rl);
        masker = findViewById(R.id.download_masker);
        download_value_tv = (TextView) findViewById(R.id.download_ProgressBar_tv);
    }

    private void initEvent() {
        back_iv.setOnClickListener(this);
        video_tv.setOnClickListener(this);
        doc_tv.setOnClickListener(this);
        download_btn.setOnClickListener(this);
        download_doc_btn.setOnClickListener(this);
        masker.setOnClickListener(this);
        mAdapter.setOnListClickListener((ftpFile, position) -> {
            if (ftpFile.isDownload()) return;
            if (mDownList.size() > 0) {
                int a =0;
                for (FileFtp file : mDownList) {
                    if (ftpFile.getName().equals(file.getName())
                            && ftpFile.getSize() == file.getSize()){
                        mDownList.remove(file);
                        a = 1;
                        break;
                    }
                }
                if (a == 0) {
                    mDownList.add(ftpFile);
                }
            }else {
                mDownList.add(ftpFile);
            }
//            if (!mDownList.contains(ftpFile)){
//                mDownList.add(ftpFile);
//            }else {
//                mDownList.remove(ftpFile);
//            }
            downloadSum(2);
        });

        mDocAdapter.setOnListClickListener((ftpFile, position) -> {
            if (ftpFile.isDownload()) return;
            if (mDocDownList.size() > 0) {
                int a = 0;
                for (FileFtp file : mDocDownList) {
                    if (ftpFile.getName().equals(file.getName())
                            && ftpFile.getSize() == file.getSize()){
                        mDocDownList.remove(file);
                        a = 1;
                        break;
                    }
                }
                if (a == 0) {
                    mDocDownList.add(ftpFile);
                }
            }else {
                mDocDownList.add(ftpFile);
            }

            downloadSum(2);
        });
    }

    private List<FileFtp> loginFtp() {
        List<FileFtp> list = new ArrayList<>();
        try {
            if (client == null) {
                client = new FTPClient();
                client.connect(hostName,serverPort);
                client.login(userName,password);
            }
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                LogUtil.d("ftp", "无法连接到ftp服务器, 错误码为: " + reply);
                return list;
            }
            list = loadFile(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }



    private List<FileFtp> loadFile(String path){
        List<FileFtp> list = new ArrayList<>();
        try {
            FTPFile[] files = client.listFiles(path);
            for (FTPFile file : files) {
                Gson gson = new Gson();
                String ob = gson.toJson(file);
                FileFtp fileFtp = gson.fromJson(ob,FileFtp.class);
                fileFtp.setName(new String(file.getName().getBytes("iso-8859-1"),"GBK"));
                list.add(fileFtp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (flag == 1) {
            if (mList.size() == 0) {
                if (vList.size() > 0) {
                    for (FileFtp file : list) {
                        for(String name : vList) {
                            if (file.getName().equals(name)) {
                                mFinish.add(file);
                                file.setDownload(true);
                            }
                        }
                    }
                }
                mList = list;
            }
        }
        return list;
    }

    private void initiData() {
        hostName = "172.18.3.101";
        serverPort = 21;
        userName = "ftpadmin";
        password = "Rjs123456";
        mList = new ArrayList<>();
        mDownList = new ArrayList<>();
        mDocList = new ArrayList<>();
        mDocDownList = new ArrayList<>();
        vList = new ArrayList<>();
        dList = new ArrayList<>();
        mFinish = new ArrayList<>();
        mDocFinish = new ArrayList<>();

        Cursor cursor = this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MEDIA_COLUMNS, null, null, null);

        cursor.moveToFirst();
        do{
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[0]));
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[1]));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[2]));
//                long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[3]));
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(this.getContentResolver(),
                    id, MediaStore.Video.Thumbnails.MINI_KIND, null);

            String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MEDIA_COLUMNS[4]));

            if(!fileExist(path)){continue;}
            if (!TextUtils.isEmpty(name)) {
                vList.add(name);
            }
        }while(cursor.moveToNext());

    }

    private boolean fileExist(String path){
        if(!TextUtils.isEmpty(path)){
            return new File(path).exists();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_back_iv :
                finish();
                break;
            case R.id.download_video_tv : //切换到下载视频
                if (flag == 1) return;
                flag = 1;
                changeTab(flag);
                break;
            case R.id.download_doc_tv :  //切换到下载文档
                if (flag == 2) return;
                flag = 2;
               changeTab(flag);
                break;
            case R.id.download_video_btn : //下载视频按钮
                if (mDownList.size() == 0) return;
                new DownloadTask().execute(mDownList);
                break;
            case R.id.download_document_btn : //下载文档按钮
                if (mDocDownList.size() == 0) return;
                new DownloadTask().execute(mDocDownList);
                break;

        }
    }

    private void changeTab(int b) {
        if (b == 1) {
            video_rl.setVisibility(VISIBLE);
            doc_rl.setVisibility(GONE);
            video_tv.setTextColor(Color.rgb(12,91,174));
            doc_tv.setTextColor(Color.rgb(38,38,38));
            new ChangeTypeTask().execute(Constant.FILE_VIDEO);
//            mAdapter.onDateChange(mList);
//            mAdapter.down(mDownList);
        } else {
            video_rl.setVisibility(GONE);
            doc_rl.setVisibility(VISIBLE);
            video_tv.setTextColor(Color.rgb(38,38,38));
            doc_tv.setTextColor(Color.rgb(12,91,174));
            new ChangeTypeTask().execute(Constant.FILE_DOCUMENT);
//            mDocAdapter.onDateChange(mDocList);
//            mDocAdapter.down(mDocDownList);
        }
    }


    private void downloadSum(int b) {
        if (flag == 1) {
            if (b == 0) {
                checked_tv.setVisibility(GONE);
                unchecked_tv.setVisibility(VISIBLE);
                mDownList.clear();
                for (FileFtp file : mList) {
                    if (!file.isDownload()) mDownList.add(file);
                }
            } else if (b == 1) {
                checked_tv.setVisibility(VISIBLE);
                unchecked_tv.setVisibility(GONE);
                mDownList.clear();
            } else if (b == 3) {
                mDownList.clear();
                download_btn.setText("下载");
                download_btn.setBackgroundColor(Color.rgb(108,108,108));
            }

            if (mDownList.size() > 0) {
                download_btn.setText("下载 (" + mDownList.size()+")");
                download_btn.setBackgroundColor(Color.rgb(12,91,174));
            }else {
                download_btn.setText("下载");
                download_btn.setBackgroundColor(Color.rgb(108,108,108));
            }

            mAdapter.down(mDownList);
        }else {
            if (b == 0) {
                checked_doc_tv.setVisibility(GONE);
                unchecked_doc_tv.setVisibility(VISIBLE);
                mDocDownList.clear();
                for (FileFtp file : mDocList) {
                    if (!file.isDownload()) mDocDownList.add(file);
                }
            } else if (b == 1) {
                checked_doc_tv.setVisibility(VISIBLE);
                unchecked_doc_tv.setVisibility(GONE);
                mDocDownList.clear();
            } else if (b == 3) {
                mDocDownList.clear();
                download_doc_btn.setText("下载");
                download_doc_btn.setBackgroundColor(Color.rgb(108,108,108));
            }

            if (mDocDownList.size() > 0) {
                download_doc_btn.setText("下载 (" + mDocDownList.size()+")");
                download_doc_btn.setBackgroundColor(Color.rgb(12,91,174));
            }else {
                download_doc_btn.setText("下载");
                download_doc_btn.setBackgroundColor(Color.rgb(108,108,108));
            }
            mDocAdapter.down(mDocDownList);
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 扫描本地文本文件
     * @return
     */
    public void loadFile() {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/Documents/";
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileArray = file.listFiles();
                for (File f : fileArray) {
                    if (f.isDirectory()) {
                        loadFile(f.getPath());
                    } else {
                        if (f.getName().endsWith(".pdf") || f.getName().endsWith(".doc") ||
                                f.getName().endsWith(".wps") || f.getName().endsWith(".docx")) {
                            dList.add(f.getName());
                        }
                    }
                }
            }
        }

    }

    class FtpTask extends AsyncTask<Integer, Void, List<FileFtp>> {
        @Override
        protected List<FileFtp> doInBackground(Integer... integers) {
            List<FileFtp> list = loginFtp();
            loadFile();
            return list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            download_bar.setVisibility(VISIBLE);
            down_lv.setVisibility(GONE);
        }

        @Override
        protected void onPostExecute(List<FileFtp> fileFtps) {
            super.onPostExecute(fileFtps);
            download_bar.setVisibility(GONE);
            down_lv.setVisibility(VISIBLE);
            mAdapter.downFinish(mList, mFinish);
        }


    }

    class ChangeTypeTask extends AsyncTask<Integer, Void, List<FileFtp>> {

        @Override
        protected void onPostExecute(List<FileFtp> fileFtps) {
            super.onPostExecute(fileFtps);
            if (flag == 1) {
                download_bar.setVisibility(GONE);
                down_lv.setVisibility(VISIBLE);
                if (mList.size() > 0) {
                    for (FileFtp f1 : fileFtps) {
                        for (FileFtp f2 : mList) {
                            if (f1.getName().equals(f2.getName()) && f2.isDownload() ) {
                                f1.setDownload(f2.isDownload());
                            }
                        }
                    }
                }
                mList.clear();
                mList.addAll(fileFtps);
                mAdapter.onDateChange(fileFtps);
            } else {
                download_doc_bar.setVisibility(GONE);
                downDoc_lv.setVisibility(VISIBLE);

                if (mDocList.size() > 0) {
                    for (FileFtp f1 : fileFtps) {
                        for (FileFtp f2 : mDocList) {
                            if (f1.getName().equals(f2.getName()) && f2.isDownload()) {
                                f1.setDownload(f2.isDownload());
                            }
                        }
                    }
                }
                mDocList.clear();
                mDocList.addAll(fileFtps);
                if (dList.size() > 0 && mDocList.size() > 0) {
                    for (FileFtp file : mDocList) {
                        for (String name : dList) {
                            if (file.getName().equals(name)) {
                                mDocFinish.add(file);
                                file.setDownload(true);
                            }
                        }
                    }
                }
                mDocAdapter.downFinish(fileFtps, mDocFinish);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (flag == 1) {
                download_bar.setVisibility(VISIBLE);
                down_lv.setVisibility(GONE);
            } else {
                download_doc_bar.setVisibility(VISIBLE);
                downDoc_lv.setVisibility(GONE);
            }

        }

        @Override
        protected List<FileFtp> doInBackground(Integer... integers) {
            List<FileFtp> list = new ArrayList<>();
            switch (integers[0]) {
                case Constant.FILE_VIDEO :
                    try {
                        client.changeWorkingDirectory(videoPath);
                        list = loadFile(videoPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constant.FILE_DOCUMENT :
                    try {
                        client.changeWorkingDirectory(docPath);
                        list = loadFile(docPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return list;
        }
    }


    class DownloadTask extends AsyncTask<List<FileFtp>, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            download_value_tv.setText("0%");
            masker_rl.setVisibility(VISIBLE);
            
        }



        @Override
        protected Integer doInBackground(List<FileFtp>... lists) {
            List<FileFtp> list = lists[0];

            String localPath;
            String path;
            FTPService ftpService = new FTPService();
            for (FileFtp fileFtp : list) {
                if (flag == 1) {
                    localPath = Environment.getExternalStorageDirectory().getPath() + "/Movies/";
                    File file = new File(localPath);
                    if (!file.exists()) file.mkdir();
                    path = videoPath;
                } else {
                    localPath = Environment.getExternalStorageDirectory().getPath() + "/Documents/";
                    path = docPath;
                }
                    try {
                        ftpService.downloadSingleFile(path + fileFtp.getName(), localPath, fileFtp.getName(),
                                (currentStep, downProcess, size) -> {
                            if (currentStep.equals(FTP_DOWN_SUCCESS)) {
                                if (flag == 1) {
                                    for (FileFtp f : mList) {
                                        if (f.getName() == fileFtp.getName()){
                                            f.setDownload(true);
                                        }
                                    }
                                }else {
                                    for (FileFtp f : mDocList) {
                                        if (f.getName() == fileFtp.getName()){
                                            f.setDownload(true);
                                        }
                                    }
                                }
                                publishProgress(FTP_SUCCESS, 0);
                                flag_success = 1;
                                LogUtil.d("upload", "-----upload success-----");
                            }else if (currentStep.equals(FTP_DOWN_LOADING)) {
                                publishProgress(FTP_DOWNLOADING, (int)downProcess );
                                LogUtil.d("upload", " upload: " + downProcess + "%");
                            }else if ((currentStep.equals(FTP_FILE_NOTEXISTS))) {
                                if (flag == 1) {
                                    System.out.println(mFinish);
                                } else {
                                    mDocDownList.removeAll( mDocDownList.stream().filter(f ->
                                            f.getName().equals(fileFtp.getName())).collect(Collectors.toList()));
                                }
                            }
                        });
                    } catch (Exception e) {
                        flag_success = 2;
                        e.printStackTrace();
                    }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case FTP_SUCCESS :
                    download_value_tv.setText("100%");
                    break;
                case FTP_DOWNLOADING :
                   String value = values[1] + "%";
                    download_value_tv.setText(value);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            masker_rl.setVisibility(GONE);
            if (flag_success == 1) {
                ToastUtils.showShort("下载完毕");
            }else {
                ToastUtils.showShort("文件无法下载");
            }

            switch (flag) {
                case 1 :
                    mFinish.addAll(mDownList);
                    mAdapter.downFinish(mList, mFinish);
                    break;
                case 2 :
                    mDocAdapter.downFinish(mDocList, mDocDownList);
            }
            downloadSum(3);
        }
    }

}
