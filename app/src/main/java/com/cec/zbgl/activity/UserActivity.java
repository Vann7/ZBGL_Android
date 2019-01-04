package com.cec.zbgl.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.model.User;
import com.cec.zbgl.service.UserService;
import com.cec.zbgl.utils.ToastUtils;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_iv;
    private TextView head_tv;
    private EditText original_pd_tv;
    private EditText pd_tv;
    private EditText chech_pd_tv;
    private TextView submit_tv;
    private User user;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
        }
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        original_pd_tv = (EditText) findViewById(R.id.user_pd_et0);
        pd_tv = (EditText) findViewById(R.id.user_pd_et);
        chech_pd_tv = (EditText) findViewById(R.id.user_pd_et2);
        submit_tv = (TextView) findViewById(R.id.user_submit_tv);
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        userService = new UserService(this);
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
        String pd0 = original_pd_tv.getText().toString();
        String pd1 = pd_tv.getText().toString();
        String pd2 = chech_pd_tv.getText().toString();
        if (!pd0.equals(user.getPassword())){
            ToastUtils.showShort("当前用户密码错误");
            return;
        }
        if (pd1.length() <6 || pd2.length() <6) {
            ToastUtils.showShort("密码必须6位或6位以上");
            return;
        }
        if (!pd1.equals(pd2)) {
            ToastUtils.showShort("两次输入的密码必须一致!");
        } else {
            user.setPassword(pd2);
           int flag = userService.updatePassword(user);
           if (flag == 1){
               saveSession(user);
               ToastUtils.showShort("修改密码成功");
               finish();
           } else {
               ToastUtils.showShort("修改密码失败");
           }
        }
    }

    private void saveSession(User user) {
        SharedPreferences setting = getSharedPreferences("User", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("id",String.valueOf(user.getId()));
        editor.putString("name", user.getName());
        editor.putString("password", user.getPassword());
        editor.commit();
    }


}
