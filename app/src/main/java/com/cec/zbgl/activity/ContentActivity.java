package com.cec.zbgl.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cec.zbgl.R;
import com.cec.zbgl.model.DeviceInfo;

/**
 * 备品信息详情活动
 */
public class ContentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name_Et;
    private EditText type_Et;
    private EditText location_Et;
    private EditText count_Et;
    private EditText status_Et;
    private EditText belongSys_Et;
    private EditText createName_Et;
    private EditText createTime_Et;
    private EditText description_Et;
    private Button back_btn,course_btn;
    private DeviceInfo device;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initView();
        initData();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        name_Et = (EditText) findViewById(R.id.device_name_et);
        type_Et = (EditText) findViewById(R.id.device_type_et);
        location_Et = (EditText) findViewById(R.id.device_location_et);
        count_Et = (EditText) findViewById(R.id.device_count_et);
        status_Et = (EditText) findViewById(R.id.device_belongSys_et);
        belongSys_Et = (EditText) findViewById(R.id.device_belongSys_et);
        createName_Et = (EditText) findViewById(R.id.device_createrName_et);
        createTime_Et = (EditText) findViewById(R.id.device_createrTime_et);
        description_Et = (EditText) findViewById(R.id.device_description_et);
        back_btn = (Button)findViewById(R.id.device_back_btn);
        back_btn.setOnClickListener(this);
        course_btn = (Button) findViewById(R.id.check_course_btn);
        course_btn.setOnClickListener(this);

    }


    /**
     * 初始化et数据
     */
    private void initData() {
        device = (DeviceInfo) getIntent().getSerializableExtra("device");
        name_Et.setText(device.getName());
        type_Et.setText(String.valueOf(device.getType()));
        location_Et.setText(device.getLocation());
        count_Et.setText(String.valueOf(device.getCount()));
        status_Et.setText(String.valueOf(device.getStatus()));
        belongSys_Et.setText(device.getBelongSys());
        createName_Et.setText(device.getCreaterName());
//        createTime_Et.setText(device.getCreateTime().toString());
        description_Et.setText(device.getDescription());
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_back_btn :
                this.finish();
                break;
            case R.id.check_course_btn :
                Intent intent = new Intent(this,CourseActivity.class);
                intent.putExtra("name",device.getName());
                startActivity(intent);
                break;
        }
    }
}