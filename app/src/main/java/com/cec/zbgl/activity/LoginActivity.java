package com.cec.zbgl.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cec.zbgl.R;
import com.cec.zbgl.model.User;
import com.cec.zbgl.util.EditTextClearUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
   private EditText e1,e2;
   private ImageView m1,m2;
   private Button btn;

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
        e1=(EditText)findViewById(R.id.phonenumber);
        e2=(EditText)findViewById(R.id.password);
        m1=(ImageView)findViewById(R.id.del_phonenumber);
        m2=(ImageView)findViewById(R.id.del_password);
        EditTextClearUtil.addclerListener(e1, m1);
        EditTextClearUtil.addclerListener(e2, m2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                User user = new User(e1.getText().toString(), e2.getText().toString());
                Toast.makeText((LoginActivity.this), "已登录,"+user.toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                setResult(RESULT_OK, intent);
                startActivityForResult(intent, 1);
                break;
        }

    }
}
