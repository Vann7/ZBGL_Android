package com.cec.zbgl.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cec.zbgl.R;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_iv;
    private TextView head_tv;
    private EditText ip_et;
    private EditText port_et;
    private TextView submit_tv;
    private String ip;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        initView();
        initData();
        initEvent();
    }



    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        ip_et = (EditText) findViewById(R.id.server_ip_et);
        port_et = (EditText) findViewById(R.id.server_port_et);
        submit_tv = (TextView) findViewById(R.id.server_submit_tv);
        head_tv.setText("服务器信息设置");
    }



    private void initData() {
        SharedPreferences setting = getSharedPreferences("server", 0);
        ip = setting.getString("ip","");
        port = setting.getString("port","");
        ip_et.setText(ip);
        port_et.setText(port);
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
            case R.id.server_submit_tv :
                ip = ip_et.getText().toString();
                port = port_et.getText().toString();
                setConfig(ip, port);
                finish();
                break;
        }
    }

    private void setConfig(String ip, String port) {
        SharedPreferences setting = getSharedPreferences("server", 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("ip", ip);
        editor.putString("port", port);
        editor.commit();

    }


}
