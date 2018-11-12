package com.cec.zbgl.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.utils.ToastUtils;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_iv;
    private TextView head_tv;
    private EditText pd_tv;
    private EditText chech_pd_tv;
    private TextView submit_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        pd_tv = (EditText) findViewById(R.id.user_pd_et);
        chech_pd_tv = (EditText) findViewById(R.id.user_pd_et2);
        submit_tv = (TextView) findViewById(R.id.user_submit_tv);
    }

    private void initData() {
        head_tv.setText("修改密码");
    }

    private void initEvent() {
        back_iv.setOnClickListener(this);
        submit_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_back_iv :
                finish();
                break;
            case R.id.user_submit_tv :
               changePassword();
                break;
        }
    }

    public void changePassword() {
        String pd1 = pd_tv.getText().toString();
        String pd2 = chech_pd_tv.getText().toString();
        if (pd1.length() <=6 || pd2.length() <=6) {
            ToastUtils.showShort("密码必须6位以上");
            return;
        }
        if (!pd1.equals(pd2)) {
            ToastUtils.showShort("两次输入的密码必须一致!");
        } else {
            ToastUtils.showShort("修改密码成功");
            finish();
        }
    }
}
