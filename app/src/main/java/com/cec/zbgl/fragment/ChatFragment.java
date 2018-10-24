package com.cec.zbgl.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.cec.zbgl.R;
import com.cec.zbgl.activity.ContentActivity;
import com.cec.zbgl.activity.LoginActivity;
import com.cec.zbgl.adapter.DeviceAdapter;
import com.cec.zbgl.adapter.OrgsAdapter;
import com.cec.zbgl.listener.ILoadListener;
import com.cec.zbgl.listener.IReflashListener;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.thirdLibs.zxing.activity.CaptureActivity;
import com.cec.zbgl.utils.ToastUtils;
import com.cec.zbgl.view.DeviceListView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class ChatFragment extends Fragment implements IReflashListener,ILoadListener ,View.OnClickListener {

    private ListView mTreeView;
    private DeviceListView dListView;
    private List<SpOrgnization> orgs;
    private List<DeviceInfo> devices;
    private DeviceInfo device;
    private OrgsAdapter<SpOrgnization> mAdapter;
    private DeviceAdapter dAdapter;
    private ImageView qc_iv;
    private ImageView quit_iv;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab01, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTreeView = (ListView) getView().findViewById(R.id.id_lv_tree);
        dListView = (DeviceListView) getView().findViewById(R.id.id_lv_content);
        qc_iv = (ImageView) getView().findViewById(R.id.top_qc_btn);
        qc_iv.setOnClickListener(this);
        quit_iv = (ImageView) getView().findViewById(R.id.top_quit_btn);
        quit_iv.setOnClickListener(this);
        initDatas();
        try
        {
            mAdapter = new OrgsAdapter<>(mTreeView, getContext(),
                    orgs, 0);
            mTreeView.setAdapter(mAdapter);
           showList(devices);
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        initEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            ToastUtils.showShort(result);
        }
    }



    /**
     * 点击左侧树节点，刷新右侧tableView
     */
    private void initEvent() {
        mAdapter.setOnTreeNodeClickListener((node, position) -> {
            if (node.isLeaf())
            {
//                ToastUtils.showShort(node.getName());

                reloadData(node.getName());

            }
        });

        dAdapter.setOnListClickListener((node, position) -> {
//            ToastUtils.showShort(node.getName());
            Intent intent = new Intent(getActivity(), ContentActivity.class);
            intent.putExtra("device",node);
            startActivity(intent);
        });

    }

    /**
     * 初始化模拟数据
     */
    private void initDatas() {
        orgs = new ArrayList<>();
        SpOrgnization org = new SpOrgnization("1","0","根目录1");
        orgs.add(org);
        org = new SpOrgnization("2","0","根目录2");
        orgs.add(org);
        org = new SpOrgnization("3","0","根目录3");
        orgs.add(org);
        org = new SpOrgnization("4","1","根目录1-1");
        orgs.add(org);
        org = new SpOrgnization("5","1","根目录1-2");
        orgs.add(org);

        devices = new ArrayList<>();
        for (int i=0; i< 20; i++) {
            device = new DeviceInfo("tony"+i,i,"Beijing"+i);
            devices.add(device);
        }

    }

    private void showList(List<DeviceInfo> devices) {
        if (dAdapter == null) {
            dListView = (DeviceListView) getView().findViewById(R.id.id_lv_content);
            dAdapter = new DeviceAdapter(dListView, getContext(),devices,R.layout.content_wx);
            dListView.setFreshInterface(this);
            dListView.setLoadInterface(this);
            dListView.setAdapter(dAdapter);
        }else {
            dAdapter.onDateChange(devices);
        }
    }

    /**
     * 模拟刷新数据
     */
    private void setReflashData() {
        devices.removeAll(devices);
        for (int i=100; i< 120; i++) {
            device = new DeviceInfo("GiGi "+i,i,"Ice Land"+i);
            devices.add(device);
        }
    }

    /**
     * 模拟加载更多数据
     */
    private void getLoadData() {
        for (int i=120; i< 140; i++) {
            device = new DeviceInfo("Mix "+i,i,"Green Land"+i);
            devices.add(device);
        }
        dAdapter.onDateChange(devices);
    }

    /**
     * 模拟重载数据
     */
    private void reloadData(String name) {
        devices = new ArrayList<>();
        for (int i=0; i< 40; i++) {
            device = new DeviceInfo("Mac OS " + i, i, "USA-"+ name);
            devices.add(device);
        }
        showList(devices);
//        dAdapter.onDateChange(devices);
    }

    /**
     * 刷新数据
     */
    @Override
    public void onReflash() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            setReflashData();
            showList(devices);
            dListView.reflashComplete();
        }, 500);
    }

    /**
     * 加载更多数据
     */
     @Override
    public void onload() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //获取更多数据
            getLoadData();
            //更新listview显示；
            showList(devices);
            dAdapter.onDateChange(devices);
            //通知listview加载完毕
            dListView.loadComplete();
        }, 2000);
    }

    /**
     * 绑定当前页面的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_qc_btn :
                scanQc();
                break;
            case R.id.top_quit_btn :
                logout();
                break;
        }

    }

    /**
     * 注销当前登录用户
     */
    public void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("退出登录")
                .setMessage("退出登录后不会删除任何个人信息")
                .setPositiveButton("确定", (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .create();
        alertDialog.show();

    }

    /**
     * 开启二维码扫描
     */
    public void scanQc() {
        if (authority()) {
            startActivityForResult(new Intent(getActivity(),
                    CaptureActivity.class), 1);
        } else {
            showDialogTipUserGoToAppSettting();
        }
    }

    //权限判断
    public boolean authority() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                return false;
            }
        }
        return true;
    }


    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSettting(){
        AlertDialog  dialog = new AlertDialog.Builder(getContext())
                .setTitle("存储权限不可用").setMessage("请在-应用设置-权限-中，允许应用使用摄像头权限")
                .setPositiveButton("立即开启",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 123);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).show();

    }




}
