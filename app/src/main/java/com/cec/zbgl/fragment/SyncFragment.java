package com.cec.zbgl.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.activity.VideoDownloadActivity;
import com.cec.zbgl.activity.VideoUploadActivity;
import com.cec.zbgl.adapter.OrgsAdapter;
import com.cec.zbgl.dto.OrgnizationDto;
import com.cec.zbgl.event.MessageEvent;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.service.CourseService;
import com.cec.zbgl.service.DeviceService;
import com.cec.zbgl.service.OrgsService;
import com.cec.zbgl.service.SyncService;
import com.cec.zbgl.service.UserService;
import com.cec.zbgl.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.cec.zbgl.R.id.cache_size;
import static com.cec.zbgl.R.id.gone;


public class SyncFragment extends Fragment implements View.OnClickListener {


    private TextView tv_syncData;
    private TextView tv_uFile;
    private TextView tv_dFile;
    private ProgressBar progressBar;
    private LinearLayout rl_sync;
    private SyncService syncService;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sync, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        EventBus.getDefault().register(this);
        initView();
        initEvent();
    }



    private void initView() {
        tv_syncData = (TextView) getActivity().findViewById(R.id.sync_data);
        tv_uFile = (TextView) getActivity().findViewById(R.id.sync_upload_file);
        tv_dFile = (TextView) getActivity().findViewById(R.id.sync_download_file);
        rl_sync = (LinearLayout) getActivity().findViewById(R.id.sync_ll);
        rl_sync.setVisibility(View.GONE);
        rl_sync.bringToFront();
    }

    private void initEvent() {
        tv_syncData.setOnClickListener(this);
        tv_uFile.setOnClickListener(this);
        tv_dFile.setOnClickListener(this);

        syncService = new SyncService(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sync_data :
                new SyncTask().execute();
                break;
            case R.id.sync_upload_file :
                Intent intent = new Intent(getActivity(), VideoUploadActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.sync_download_file :
                Intent intent2 = new Intent(getActivity(), VideoDownloadActivity.class);
                getActivity().startActivity(intent2);
                break;
        }

    }


    class SyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            rl_sync.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            syncService.syncData();
            syncService.socketSync();
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
        rl_sync.setVisibility(View.GONE);
        ToastUtils.showShort("同步数据已完成");
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

}
