package com.cec.zbgl.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.model.User;
import com.cec.zbgl.service.UserService;
import com.cec.zbgl.thirdLibs.zxing.activity.CaptureActivity;
import com.cec.zbgl.utils.EditTextClearUtil;
import com.cec.zbgl.utils.ToastUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

   private DatabaseHelper helper;
   private EditText e1,e2;
   private ImageView m1,m2;
   private Button btn,db_btn;
   private UserService userService;
    // 要申请的权限
    private String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private String[] permission_camera = {Manifest.permission.CAMERA};
    private String[] permission_write_storage = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        authority();
        init();
    }

    private void init() {
        btn = (Button)findViewById(R.id.loginButton);
        btn.setOnClickListener(this);
        db_btn = (Button) findViewById(R.id.createDb_btn);
        db_btn.setOnClickListener(this);
        e1=(EditText)findViewById(R.id.phonenumber);
        e2=(EditText)findViewById(R.id.password);
        m1=(ImageView)findViewById(R.id.del_phonenumber);
        m2=(ImageView)findViewById(R.id.del_password);
        EditTextClearUtil.addclerListener(e1, m1);
        EditTextClearUtil.addclerListener(e2, m2);
    }

    @Override
    public void onClick(View v) {
        userService = new UserService(this);
        User user = new User(e1.getText().toString(), e2.getText().toString());
        switch (v.getId()) {
            case R.id.loginButton:
                if (user.getName().equals("")  || user.getPassword().equals("")) {
                    ToastUtils.showShort("用户名和密码不能为空");
                    return;
                }
                int flag = userService.checkUser(user);
                if (flag == 1){
                    ToastUtils.showShort("已登录");
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("user", user);
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent, 1);
                    this.finish();
                } else {
                    ToastUtils.showShort("当前用户名或密码错误");
                }
                break;
            case R.id.createDb_btn:
                userService.insert(user);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
    }

    //权限判断
    private void authority() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //先判断有没有权限 ，没有就在这里进行权限的申请
            ActivityCompat.requestPermissions(this,
                    permissions,321);
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    ) {
                // 开始提交摄像头、存储请求权限
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }


}
