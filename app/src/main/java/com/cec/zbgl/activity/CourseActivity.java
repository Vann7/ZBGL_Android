package com.cec.zbgl.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cec.zbgl.R;

public class CourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        String name = getIntent().getStringExtra("name");
        TextView tv = (TextView) findViewById(R.id.course_name_tv);
        tv.setText(name);
    }
}
