package com.cec.zbgl.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import com.cec.zbgl.adapter.PagerAdapter;
import com.cec.zbgl.fragment.ChatFragment;
import com.cec.zbgl.fragment.ContactFragment;
import com.cec.zbgl.fragment.DiscoverFragment;
import com.cec.zbgl.fragment.MineFragment;

import java.util.ArrayList;
import java.util.List;

public class WXActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private List<Fragment> mDatas;

    private TextView mChatTextView;
    private TextView mFriendTextView;
    private TextView mContactTextView;
    private TextView mMineTextView;
    private RelativeLayout mChat_rl;
    private RelativeLayout mContact_rl;
    private RelativeLayout mDiscovery_rl;
    private RelativeLayout mMine_rl;

    private TextView tv_chart;
    private TextView tv_friend;
    private TextView tv_found;
    private TextView tv_mine;


    private ImageView mTabline;
    private int mScreen1_4;

    private int mCurrentPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wx);
        initTabLine();
        initView();
        initEvent();
    }

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

    /**
     * 初始化下划线
     */
    private void initTabLine() {
        mTabline = (ImageView) findViewById(R.id.id_iv_tabline);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mScreen1_4 = outMetrics.widthPixels / 4;
        ViewGroup.LayoutParams lp = mTabline.getLayoutParams();
        lp.width = mScreen1_4;
        mTabline.setLayoutParams(lp);
    }

    /**
     * 绑定点击事件
     */
    private void initEvent() {
        mMine_rl.setOnClickListener(this);
        mDiscovery_rl.setOnClickListener(this);
        mContact_rl.setOnClickListener(this);
        mChat_rl.setOnClickListener(this);
    }

    /**
     * 初始化各View控件
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mChatTextView = (TextView) findViewById(R.id.id_tv_chat);
        mFriendTextView = (TextView) findViewById(R.id.id_tv_friend);
        mContactTextView = (TextView) findViewById(R.id.id_tv_found);
        mMineTextView = (TextView) findViewById(R.id.id_tv_mine);

        mChat_rl = (RelativeLayout) findViewById(R.id.id_ll_chat);
        mContact_rl = (RelativeLayout) findViewById(R.id.id_ll_contact);
        mDiscovery_rl = (RelativeLayout) findViewById(R.id.id_ll_discovery);
        mMine_rl = (RelativeLayout) findViewById(R.id.id_ll_mine);

        tv_chart = (TextView) findViewById(R.id.id_tv_chat);
        tv_friend = (TextView) findViewById(R.id.id_tv_friend);
        tv_found = (TextView) findViewById(R.id.id_tv_found);
        tv_mine = (TextView) findViewById(R.id.id_tv_mine);

        mDatas = new ArrayList<>();

        ChatFragment tab01 = new ChatFragment();
        ContactFragment tab02 = new ContactFragment();
        DiscoverFragment tab03 = new DiscoverFragment();
        MineFragment tab04 = new MineFragment();

        mDatas.add(tab01);
        mDatas.add(tab02);
        mDatas.add(tab03);
        mDatas.add(tab04);

        mAdapter = new PagerAdapter(getSupportFragmentManager(),mDatas);

        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPx) {

                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabline
                        .getLayoutParams();

                if (mCurrentPageIndex == 0 && position == 0){// 0->1
                    lp.leftMargin = (int) (positionOffset * mScreen1_4 + mCurrentPageIndex
                            * mScreen1_4);
                } else if (mCurrentPageIndex == 1 && position == 0){// 1->0
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + (positionOffset - 1)
                            * mScreen1_4);
                } else if (mCurrentPageIndex == 1 && position == 1){ // 1->2
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + positionOffset
                            * mScreen1_4);
                } else if (mCurrentPageIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + ( positionOffset-1)
                            * mScreen1_4);
                }  else if (mCurrentPageIndex == 2 && position == 2) {// 2->3
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + positionOffset
                            * mScreen1_4);
                }  else if (mCurrentPageIndex == 3 && position == 2) {// 3->2
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_4 + ( positionOffset-1)
                            * mScreen1_4);
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
            mChatTextView.setTextColor(Color.BLACK);
            mFriendTextView.setTextColor(Color.BLACK);
            mContactTextView.setTextColor(Color.BLACK);
            mMineTextView.setTextColor(Color.BLACK);
        }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.id_ll_chat :
                setSelect(0);
                break;
            case R.id.id_ll_contact:
                setSelect(1);
                break;
            case R.id.id_ll_discovery:
                setSelect(2);
                break;
            case R.id.id_ll_mine:
                setSelect(3);
                break;
        }
    }

    private void setSelect(int i)
    {
        setTab(i);
//        mCurrentPageIndex = i;
        System.out.println(mViewPager.getChildCount());
        mViewPager.setCurrentItem(i);
    }

    private void setTab(int position) {
        resetTextView();
        switch (position) {
            case 0:
                mChatTextView.setTextColor(Color.parseColor("#008000"));

                break;
            case 1:
                mFriendTextView.setTextColor(Color.parseColor("#008000"));
                break;
            case 2:
                mContactTextView.setTextColor(Color.parseColor("#008000"));
                break;
            case 3:
                mMineTextView.setTextColor(Color.parseColor("#008000"));

        }
        mCurrentPageIndex = position;
    }

}