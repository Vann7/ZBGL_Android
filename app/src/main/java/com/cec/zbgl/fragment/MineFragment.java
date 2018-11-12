package com.cec.zbgl.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cec.zbgl.R;
import com.cec.zbgl.activity.LoginActivity;
import com.cec.zbgl.activity.UserActivity;
import com.cec.zbgl.utils.CacheUtil;

import java.lang.reflect.Field;


public class MineFragment extends Fragment implements View.OnClickListener {

    private TextView logout_tv;
    private RelativeLayout password_rl;
    private RelativeLayout clearCache_rl;
    private TextView cacheSize_tv;
    private AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
    }

    /**
     * 绑定事件
     */
    private void initEvent() {
        password_rl.setOnClickListener(this);
        clearCache_rl.setOnClickListener(this);
        logout_tv.setOnClickListener(this);
    }

    /**
     * 初始化界面view
     */
    private void initView() {
        logout_tv = (TextView) getActivity().findViewById(R.id.mine_out_tv);
        password_rl = (RelativeLayout) getActivity().findViewById(R.id.mine_user_rl);
        clearCache_rl = (RelativeLayout) getActivity().findViewById(R.id.mine_clear_cache_rl);
        cacheSize_tv = (TextView) getActivity().findViewById(R.id.cache_size);
        try {
            cacheSize_tv.setText(CacheUtil.getTotalCacheSize(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_out_tv :
                logout();
                break;
            case R.id.mine_user_rl :
                Intent intent = new Intent(getActivity(), UserActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.mine_clear_cache_rl :
                clean();
                break;
        }
    }

    /**
     *
     */
    private void clean() {
        CacheUtil.clearAllCache(getContext());
        cacheSize_tv.setText("0MB");
    }


    /**
     * 注销当前登录用户
     */
    public void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext(),R.style.appalertdialog)
                .setTitle("退出")
                .setMessage("退出后不会删除当前账户信息")
                .setPositiveButton("确定", (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
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
}
