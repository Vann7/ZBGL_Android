package com.cec.zbgl.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.cec.zbgl.R;
import com.cec.zbgl.fragment.MineFragment;
import com.cec.zbgl.fragment.ZbglFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login_btn;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private RadioButton rb_zbgl, rb_mine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        transaction.add(R.id.content_layout, new ZbglFragment());
        transaction.commit();


    }

    //初始化view视图
    public void initView() {
        rb_zbgl = (RadioButton) findViewById(R.id.rb_zbgl);
        rb_mine = (RadioButton) findViewById(R.id.rb_mine);

        rb_zbgl.setOnClickListener(this);
        rb_mine.setOnClickListener(this);

    }


    /**
     * 点击radioButton时触发事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        transaction = manager.beginTransaction();
        switch (v.getId()) {
            case R.id.rb_zbgl:
                transaction.replace(R.id.content_layout, new ZbglFragment());
                break;
            case R.id.rb_mine:
                transaction.replace(R.id.content_layout, new MineFragment());
                break;

        }
        transaction.commit();
    }
}
