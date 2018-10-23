package com.cec.zbgl.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.activity.LoginActivity;


public class MyFragment extends Fragment implements View.OnClickListener {

    private TextView logout_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab04, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

        initEvent();
    }

    /**
     * 绑定事件
     */
    private void initEvent() {
        logout_tv.setOnClickListener(this);
    }

    /**
     * 初始化界面view
     */
    private void initViews() {
        logout_tv = (TextView) getActivity().findViewById(R.id.mine_out_tv);
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
        }
    }



    /**
     * 注销当前登录用户
     */
    public void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("退出当前账号")
                .setMessage("退出后不会删除任何当前账户信息")
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
}
