package com.cec.zbgl.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.cec.zbgl.utils.EditTextClearUtil;
import com.cec.zbgl.utils.ToastUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

   private DatabaseHelper helper;
   private EditText e1,e2;
   private ImageView m1,m2;
   private Button btn,db_btn;
   private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

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
                    Intent intent = new Intent(LoginActivity.this, WXActivity.class);
                    intent.putExtra("user", user);
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent, 1);
                } else {
                    ToastUtils.showShort("当前用户名或密码错误");
                }
                break;
            case R.id.createDb_btn:
                userService.insert(user);
                break;


        }

    }
}
