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
import com.cec.zbgl.activity.SyncDataActivity;
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
    private LinearLayout rl_sync;
    public static final int RESULT_OK  = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sync, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK :
                //EventBus 发送事件
                EventBus.getDefault().post(new MessageEvent("UI"));
        }
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sync_data :
                Intent intent = new Intent(getActivity(), SyncDataActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.sync_upload_file :
                Intent intent1 = new Intent(getActivity(), VideoUploadActivity.class);
                getActivity().startActivity(intent1);
                break;
            case R.id.sync_download_file :
                Intent intent2 = new Intent(getActivity(), VideoDownloadActivity.class);
                getActivity().startActivity(intent2);
                break;
        }

    }


}
