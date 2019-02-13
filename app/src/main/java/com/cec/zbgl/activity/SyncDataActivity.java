package com.cec.zbgl.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.SyncAdapter;
import com.cec.zbgl.adapter.UploadAdapter;
import com.cec.zbgl.event.MessageEvent;
import com.cec.zbgl.service.SyncService;
import com.cec.zbgl.utils.TimeUtils;
import com.cec.zbgl.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nereo.multi_image_selector.bean.Video;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SyncDataActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView back_iv;
    private TextView head_tv;
    private TextView checked_tv;
    private TextView unchecked_tv;
    private TextView sync_btn;
    private ListView sync_lv;;
    private RelativeLayout masker_rl;
//    private View masker;
    private ProgressBar sync_bar;
    private TextView sync_value_tv;
    private List<String> mSyncList = new ArrayList<>();
    private List<String> mList = new ArrayList<>();
    private SyncAdapter mAdapter;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
    private static final int WRITE_PERMISSION = 0x01;
    private SyncService syncService;
    private boolean flag = false;  //数据同步判定标识
    private boolean has_synced = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
        }

        requestWritePermission();
        initView();
        initEvent();
    }

    private void requestWritePermission() {
    }


    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        String str = loadTime();
        if (str != null) {
            head_tv.setText("数据同步( " + str +" )");
        } else {
            head_tv.setText("数据同步");
        }

        checked_tv = (TextView) findViewById(R.id.select_tv);
        unchecked_tv = (TextView) findViewById(R.id.unselect_tv);
        checked_tv.setVisibility(VISIBLE);
        sync_lv = (ListView) findViewById(R.id.sync_lv);
        sync_bar = (ProgressBar) findViewById(R.id.sync_bar);
        sync_btn = (TextView) findViewById(R.id.sync_btn);
//        masker = findViewById(R.id.sync_masker);
//        masker.setBackgroundColor(Color.alpha(0));
        masker_rl = (RelativeLayout) findViewById(R.id.sync_masker_rl);
        masker_rl.setBackgroundColor(Color.alpha(10));
        sync_value_tv = (TextView) findViewById(R.id.sync_ProgressBar_tv);

        mList.add("orgnization"); //系统信息
        mList.add("device");      //设备信息
        mList.add("course");      //教程信息
        mList.add("user");        //人员信息
        mAdapter = new SyncAdapter(mList, this,sync_lv);
    }

    private void initEvent() {
        syncService = new SyncService(this);

        back_iv.setOnClickListener(this);
        checked_tv.setOnClickListener(this);
        unchecked_tv.setOnClickListener(this);
        sync_btn.setOnClickListener(this);
//        masker.setOnClickListener(this);
        //绑定ListView事件
        sync_lv.setAdapter(mAdapter);
        mAdapter.setOnListClickListener((name, position) -> {

            if (flag == true) return;
            if (!mSyncList.contains(name)) {
                mSyncList.add(name);
            }else {
                mSyncList.remove(name);
            }
            uploadSum(2);
        });
    }


    private void uploadSum(int flag) {
        if (flag == 0) {
            checked_tv.setVisibility(GONE);
            unchecked_tv.setVisibility(VISIBLE);
            mSyncList.clear();
            mSyncList.addAll(mList);
        } else if (flag == 1) {
            checked_tv.setVisibility(VISIBLE);
            unchecked_tv.setVisibility(GONE);
            mSyncList.clear();
        } else if (flag == 3) {
            mSyncList.clear();
            return;
        }

        if (mSyncList.size() > 0) {
            sync_btn.setBackgroundColor(Color.rgb(12,91,174));
        } else {
            sync_btn.setBackgroundColor(Color.rgb(108,108,108));
        }
        sync_btn.setText("同步");
        mAdapter.syncData(mSyncList);
    }

    @Override
    public void onClick(View v) {
        if (flag == true) return;
        switch (v.getId()) {
            case R.id.bar_back_iv :
                if (has_synced) {
                    this.setResult(RESULT_OK);
                }else {
                    this.setResult(0);
                }
                this.finish();
                break;
            case R.id.select_tv :
                uploadSum(0);
                break;
            case R.id.unselect_tv :
                uploadSum(1);
                break;
            case R.id.sync_btn :
                if (mSyncList.size() == 0) return;
                syncData();
                break;
            case R.id.upload_masker :
                break;
        }
    }


    public void syncData() {

        if (mSyncList.contains("device")) {

        }

        AlertDialog alertDialog = new AlertDialog.Builder(this,R.style.appalertdialog)
                .setMessage("开始同步数据后，请勿进行任何操作")
                .setPositiveButton("确定", (dialog, which) -> {
                    new SyncTask().execute();
                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .create();

        alertDialog.show();
        //修改Message字体颜色
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextColor(Color.BLACK);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    class SyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            masker_rl.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            syncService.syncData();
            flag = true;
            syncService.socketSync(mSyncList);
            //EventBus 发送事件
            EventBus.getDefault().post(new MessageEvent("刷新UI"));

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     * 数据同步完成后刷新界面UI
     * POSTING (默认) 表示事件处理函数的线程跟发布事件的线程在同一个线程。
     * MAIN 表示事件处理函数的线程在主线程(UI)线程，因此在这里不能进行耗时操作。
     * BACKGROUND 表示事件处理函数的线程在后台线程，因此不能进行UI操作。如果发布事件的线程是主线程(UI线程)，那么事件处理函数将会开启一个后台线程，如果果发布事件的线程是在后台线程，那么事件处理函数就使用该线程。
     * ASYNC 表示无论事件发布的线程是哪一个，事件处理函数始终会新建一个子线程运行，同样不能进行UI操作。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        masker_rl.setVisibility(View.GONE);
        if (messageEvent.getMessage().equals("succeed")) {
            ToastUtils.showShort("同步数据已完成!");
            saveTime();
            has_synced = true;
        } else if (messageEvent.getMessage().equals("failed")){
            ToastUtils.showShort("同步数据发生异常，请联系管理员!");
            has_synced = false;
        }
        flag = false;  //数据同步完毕, 标识为false
    }


    /**
     * 绑定EventBus
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * 解除绑定EventBus
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 保存最新更新时间
     */
    public void saveTime() {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            out = openFileOutput("timeData", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            long time = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = new Date(time);
            String dateTime = sdf.format(d1);
            writer.write(dateTime);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 加载最新更新时间
     * @return 更新时间
     */
    public String loadTime() {
        FileInputStream in;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            in = openFileInput("timeData");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
