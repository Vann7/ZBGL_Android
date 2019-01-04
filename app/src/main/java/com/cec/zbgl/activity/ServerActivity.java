package com.cec.zbgl.activity;

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
import com.cec.zbgl.model.ServerConfig;
import com.cec.zbgl.service.ServerService;

import java.util.List;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_iv;
    private TextView head_tv;
    private EditText ip_et;
    private EditText port_et;
    private EditText ftp_et;
    private TextView submit_tv;
    private String ip;
    private String port;
    private String ftp;
    private ServerService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
        }
        service = new ServerService();
        initView();
        initData();
        initEvent();
    }



    private void initView() {
        back_iv = (ImageView) findViewById(R.id.bar_back_iv);
        head_tv = (TextView) findViewById(R.id.bar_back_tv);
        ip_et = (EditText) findViewById(R.id.server_ip_et);
        port_et = (EditText) findViewById(R.id.server_port_et);
        ftp_et = (EditText) findViewById(R.id.ftp_ip_et);
        submit_tv = (TextView) findViewById(R.id.server_submit_tv);
        head_tv.setText("服务器信息设置");
    }



    private void initData() {
//        SharedPreferences setting = getSharedPreferences("server", 0);
//        ip = setting.getString("ip","");
//        port = setting.getString("port","");
//        ftp = setting.getString("ftp", "");

        ServerConfig config = service.load();
        if (config != null) {
            ip_et.setText(config.getIp());
            if (config.getPort() == 0) {
                port_et.setText("");
            }else {
                port_et.setText(String.valueOf(config.getPort()));
            }
            ftp_et.setText(config.getHostName());
        }

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
                ftp = ftp_et.getText().toString();
                setConfig(ip, port, ftp);
                finish();
                break;
        }
    }

    private void setConfig(String ip, String port, String ftp) {
//        SharedPreferences setting = getSharedPreferences("server", 0);
//        SharedPreferences.Editor editor = setting.edit();
//        editor.putString("ip", ip);
//        editor.putString("port", port);
//        editor.putString("ftp", ftp);
//        editor.commit();
        ServerConfig config;
        if (!port.equals("")){
            config = new ServerConfig(ip, Integer.valueOf(port), ftp);
        } else {
            config = new ServerConfig(ip, ftp);
        }

        if (service.load() != null) {
            service.update(config);
        } else {
            service.insert(config);
        }


    }


}
