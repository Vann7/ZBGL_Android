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

    private  byte[] images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
//            getWindow().setStatusBarColor(Color.BLACK);
        }
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
                if (list.size() == 1){
                    user.setId(list.get(0).getId());
                    user.setmId(list.get(0).getmId());
                    saveSession(user);
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
            case R.id.createDb_btn:
                new MyAsyncTask().execute(user);
                break;
        }

    }

    private void saveSession(User user) {
        SharedPreferences setting = getSharedPreferences("User", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("id",String.valueOf(user.getId()));
        editor.putString("name", user.getName());
        editor.putString("mid",user.getmId());
        editor.putString("password", user.getPassword());
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

    private void initData(User user) {
        OrgsService orgsService = new OrgsService(this);
        CourseService courseService = new CourseService();
        DeviceService deviceService = new DeviceService();

        //初始化用户
        userService.insert(user);

        //初始化组织机构

       /* List<SpOrgnization> orgs2 = new ArrayList<>();
        SpOrgnization org2 = new SpOrgnization("1","0","根目录1");
        org2.setmId(UUID.randomUUID().toString());
        orgs2.add(org2);
        org2 = new SpOrgnization("2","0","根目录2");
        org2.setmId(UUID.randomUUID().toString());
        orgs2.add(org2);
        org2 = new SpOrgnization("3","0","根目录3");
        org2.setmId(UUID.randomUUID().toString());
        orgs2.add(org2);
        org2 = new SpOrgnization("4","0","根目录4");
        org2.setmId(UUID.randomUUID().toString());
        orgs2.add(org2);
        org2 = new SpOrgnization("4","1","根目录1-1");
        org2.setmId(UUID.randomUUID().toString());
        orgs2.add(org2);
        org2 = new SpOrgnization("5","1","根目录1-2");
        org2.setmId(UUID.randomUUID().toString());
        orgs2.add(org2);
        for (SpOrgnization o : orgs2) {
            orgsService.insert(o);
        }*/

/*

        //初始化装备信息
        Random random = new Random();
        for (int i=0; i< 15; i++) {
            DeviceInfo device;
            String name,typyName;
            name = "装备";
            typyName = "笔记本电脑";
            int r = random.nextInt(100);
            if ( i % 2 == 0){
                device = new DeviceInfo(String.valueOf(r) + name,
                        "根目录2",  "系统装备类别为:"+typyName );
//                device.setImage(images);

            }else {
                device = new DeviceInfo(String.valueOf(r)+ name,
                        "根目录3",  "系统装备类别为:"+typyName );
            }
            device.setValid(true);
            device.setCreateTime(new Date());
            device.setmId(UUID.randomUUID().toString());
            deviceService.insertFromServer(device);
        }

        DeviceInfo device = new DeviceInfo(String.valueOf(123) + " 测试装备01",
                "根目录2",  "系统装备存放位置" );
        device.setmId("2bb9a8d2-08ad-418b-993b-46c02bdf3efe");
        device.setImage(images);
        device.setValid(true);
        device.setCreateTime(new Date());
        deviceService.insertFromServer(device);

        DeviceInfo device2 = new DeviceInfo(String.valueOf(1234) + " 测试装备02",
                "根目录1",  "系统装备存放位置2" );
        device2.setmId("0208d702-a388-480c-9c20-ecf727476103");
        device2.setImage(images);
        device2.setValid(true);
        device2.setCreateTime(new Date());
        deviceService.insertFromServer(device2);


        //初始化教程信息
        for (int i=0; i< 25; i++) {
            String name,typyName;
            int type;
            if (i < 9) {
                type = Constant.COURSE_IMAGE;
                name = "图片教程";
                typyName = "图片";
                DeviceCourse c0 = new DeviceCourse(String.valueOf(i),name,type,
                        "教程类别为:"+typyName,false,"item "+(i+1));
                c0.setmId(UUID.randomUUID().toString());
                c0.setValid(true);
                c0.setImage(images);

                if (i %2 ==0){
                    c0.setDeviceId("2bb9a8d2-08ad-418b-993b-46c02bdf3efe");
                }else {
                    c0.setDeviceId("0208d702-a388-480c-9c20-ecf727476103");
                }

                courseService.insertFromServer(c0);
            }else if (i < 18){
                type = Constant.COURSE_VIDEO;
                name = "视频教程";
                typyName = "视频";
                DeviceCourse c0 = new DeviceCourse(String.valueOf(i),name,type,
                        "教程类别为:"+typyName,false,"item "+(i+1));
                c0.setmId(UUID.randomUUID().toString());
                c0.setValid(true);
                if (i %2 ==0){
                    c0.setDeviceId("2bb9a8d2-08ad-418b-993b-46c02bdf3efe");
                }else {
                    c0.setDeviceId("0208d702-a388-480c-9c20-ecf727476103");
                }
                courseService.insertFromServer(c0);
            } else {
                type = Constant.COURSE_DOCUMENT;
                name = "文档教程";
                typyName = "文档";
                DeviceCourse c0 = new DeviceCourse(String.valueOf(i),name,type,
                        "教程类别为:"+typyName,false,"item "+(i+1));
                c0.setValid(true);
                c0.setmId(UUID.randomUUID().toString());
                c0.setImage(images);
                if (i %2 ==0){
                    c0.setDeviceId("2bb9a8d2-08ad-418b-993b-46c02bdf3efe");
                }else {
                    c0.setDeviceId("0208d702-a388-480c-9c20-ecf727476103");
                }
                courseService.insertFromServer(c0);
            }
        }

*/

    }


    class MyAsyncTask extends AsyncTask<User, Void, Void> {


        @Override
        protected Void doInBackground(User... users) {
            initData(users[0]);
            return null;
        }
    }

}
