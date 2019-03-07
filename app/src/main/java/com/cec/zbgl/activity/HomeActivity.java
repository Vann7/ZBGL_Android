package com.cec.zbgl.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.adapter.MainAdapter;
import com.cec.zbgl.event.MessageEvent;
import com.cec.zbgl.fragment.DeviceFragment;
import com.cec.zbgl.fragment.MineFragment;
import com.cec.zbgl.fragment.SyncFragment;
import com.cec.zbgl.model.User;
import com.cec.zbgl.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页活动页
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private MainAdapter mAdapter;
    private List<Fragment> mDatas;

    private TextView tv_device;
    private TextView tv_sync;
    private TextView tv_mine;

    private ImageView iv_device;
    private ImageView iv_sync;
    private ImageView iv_mine;


    private RelativeLayout mDevice_rl;
    private RelativeLayout mSync_rl;
    private RelativeLayout mMine_rl;

    private ImageView mTabline;
    private int mScreen1_3;

    private int mCurrentPageIndex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        transition();

        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(255, 0, 113, 188));
        }
        initTabLine();
        initView();
        initEvent();
        int flag = getIntent().getIntExtra("server_back",0);
        setSelect(flag);
    }

//    private void transition() {
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        Fade fade = new Fade();
//        fade.setDuration(200);
//        getWindow().setEnterTransition(fade);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化下划线
     */
    private void initTabLine() {
        mTabline = (ImageView) findViewById(R.id.id_iv_tabline);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mScreen1_3 = outMetrics.widthPixels / 3;
        ViewGroup.LayoutParams lp = mTabline.getLayoutParams();
        lp.width = mScreen1_3;
        mTabline.setLayoutParams(lp);
    }

    /**
     * 绑定点击事件
     */
    private void initEvent() {
        mMine_rl.setOnClickListener(this);
        mDevice_rl.setOnClickListener(this);
        mSync_rl.setOnClickListener(this);
    }

    /**
     * 初始化各View控件
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        tv_device = (TextView) findViewById(R.id.id_tv_device);
        tv_sync = (TextView) findViewById(R.id.id_tv_sync);
        tv_mine = (TextView) findViewById(R.id.id_tv_mine);

        iv_device = (ImageView) findViewById(R.id.id_iv_device);
        iv_sync = (ImageView) findViewById(R.id.id_iv_sync);
        iv_mine = (ImageView) findViewById(R.id.id_iv_mine);

        mDevice_rl = (RelativeLayout) findViewById(R.id.id_ll_device);
        mSync_rl = (RelativeLayout) findViewById(R.id.id_ll_sync);
        mMine_rl = (RelativeLayout) findViewById(R.id.id_ll_mine);

        mDatas = new ArrayList<>();

        DeviceFragment tab01 = new DeviceFragment();
        SyncFragment tab02 = new SyncFragment();
        MineFragment tab03 = new MineFragment();

        mDatas.add(tab01);
        mDatas.add(tab02);
        mDatas.add(tab03);

        mAdapter = new MainAdapter(getSupportFragmentManager(),mDatas);

        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPx) {

                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabline
                        .getLayoutParams();

                if (mCurrentPageIndex == 0 && position == 0){// 0->1
                    lp.leftMargin = (int) (positionOffset * mScreen1_3 + mCurrentPageIndex
                            * mScreen1_3);
                } else if (mCurrentPageIndex == 1 && position == 0){// 1->0

                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positionOffset - 1)
                            * mScreen1_3);
                } else if (mCurrentPageIndex == 1 && position == 1){ // 1->2
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + positionOffset
                            * mScreen1_3);
                } else if (mCurrentPageIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + ( positionOffset-1)
                            * mScreen1_3);
                }
                mTabline.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 切换界面后字体颜色修改
     */
    protected void resetTextView() {
        iv_device.setImageResource(R.mipmap.home);
        iv_mine.setImageResource(R.mipmap.my);
        iv_sync.setImageResource(R.mipmap.round_transfer);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.id_ll_device :
                setSelect(0);
                break;
            case R.id.id_ll_sync:
                setSelect(1);
                break;
            case R.id.id_ll_mine:
                setSelect(2);
                break;
        }
    }

    private void setSelect(int i)
    {
        setTab(i);
//        mCurrentPageIndex = i;
        mViewPager.setCurrentItem(i);
    }

    private void setTab(int position) {
        resetTextView();
        switch (position) {
            case 0:
                iv_device.setImageResource(R.mipmap.homefill);
                break;
            case 1:
                iv_sync.setImageResource(R.mipmap.round_transfer_fill);
                break;
            case 2:
                iv_mine.setImageResource(R.mipmap.myfill);
                break;

        }
        mCurrentPageIndex = position;
    }

    /**
     * 数据同步完成后刷新界面UI
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        if (messageEvent.getMessage().equals("刷新DeviceFragment")) {
            mAdapter.notifyDataSetChanged();
        }
    }


}
