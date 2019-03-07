package com.cec.zbgl.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.cec.zbgl.common.Constant;
import com.cec.zbgl.db.DatabaseHelper;
import com.cec.zbgl.model.DeviceCourse;
import com.cec.zbgl.model.DeviceInfo;
import com.cec.zbgl.model.SpOrgnization;
import com.cec.zbgl.model.User;
import com.cec.zbgl.service.CourseService;
import com.cec.zbgl.service.DeviceService;
import com.cec.zbgl.service.OrgsService;
import com.cec.zbgl.service.UserService;
import com.cec.zbgl.thirdLibs.zxing.activity.CaptureActivity;
import com.cec.zbgl.utils.EditTextClearUtil;
import com.cec.zbgl.utils.ImageUtil;
import com.cec.zbgl.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 登录活动页
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

   private SharedPreferences setting;
   private Boolean is_first;
   private DatabaseHelper helper;
   private EditText e1,e2;
   private ImageView m1,m2;
   private Button btn;
   private UserService userService;
    // 要申请的权限
    private String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private String[] permission_camera = {Manifest.permission.CAMERA};
    private String[] permission_write_storage = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private  byte[] images;
    private User sessionUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
//            getWindow().setStatusBarColor(Color.BLACK);
        }


        saveConfig();
        getSession();
        authority();
        init();
    }

    private void init() {
        btn = (Button)findViewById(R.id.loginButton);
        btn.setOnClickListener(this);
        e1=(EditText)findViewById(R.id.phonenumber);
        e2=(EditText)findViewById(R.id.password);
        m1=(ImageView)findViewById(R.id.del_phonenumber);
        m2=(ImageView)findViewById(R.id.del_password);
        EditTextClearUtil.addclerListener(e1, m1);
        EditTextClearUtil.addclerListener(e2, m2);
        if (sessionUser.getName() == "") {
            e1.setText("root");
        } else {
            e1.setText(sessionUser.getName());
        }

        e2.setText("123456");
//        e2.setText(sessionUser.getPassword());


//        ImageUtil imageUtil = new ImageUtil(this);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.default_image);
//        images = imageUtil.imageToByte(bitmap);
    }

    @Override
    public void onClick(View v) {
        userService = new UserService(this);

        User user = new User(e1.getText().toString(), e2.getText().toString());
        user.setmId(UUID.randomUUID().toString());
        switch (v.getId()) {
            case R.id.loginButton:
                if (user.getName().equals("")  || user.getPassword().equals("")) {
                    ToastUtils.showShort("用户名和密码不能为空");
                    return;
                }
                List<User> list = userService.checkUser(user);
                if (list.size() > 0){
                    saveSession(list.get(0));
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("user", user);
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent, 1);
//                    startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    this.finish();
                } else {
                    ToastUtils.showShort("当前用户名或密码错误");
                }
                break;
        }

    }


    /**
     * 第一次登录系统 保存root用户信息
     */
    private void saveConfig(){
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        is_first = setting.getBoolean("FIRST", true);
        if (is_first) {
            editor.putString("name", "root");
            editor.putString("password", "123456");
            editor.putBoolean("FIRST", false);
            boolean flag = editor.commit();
            if (flag) {
                userService = new UserService(this);
                userService.insert(new User("root","123456"));
            }
        }
    }

    /**
     * 保存当前用户session用于"我的"界面显示用户信息
     * @param user
     */
    private void saveSession(User user) {
        SharedPreferences setting = getSharedPreferences("User", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("id",String.valueOf(user.getId()));
        editor.putString("name", user.getName());
        editor.putString("mid",user.getmId());
        editor.putString("password", user.getPassword());
        editor.putBoolean("appUpdate", user.isAppUpdate());
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * 获取当前用户session信息
     */
    private void getSession() {
        SharedPreferences setting = this.getSharedPreferences("User", 0);
        sessionUser = new User(setting.getString("name",""),setting.getString("password",""));
        sessionUser.setId(Integer.valueOf(setting.getString("id","0")));
        sessionUser.setAppUpdate(Boolean.valueOf(setting.getBoolean("appUpdate", false)));
    }

}
