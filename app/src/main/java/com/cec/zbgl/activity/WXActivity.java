package com.cec.zbgl.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cec.zbgl.R;
import com.cec.zbgl.fragment.ChatFragment;
import com.cec.zbgl.fragment.ContactFragment;
import com.cec.zbgl.fragment.DiscoverFragment;
import com.cec.zbgl.fragment.MyFragment;
import com.cec.zbgl.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class WXActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mDatas;

    private TextView mChatTextView;
    private TextView mFriendTextView;
    private TextView mContactTextView;
    private TextView mMineTextView;
    private LinearLayout mChatLinearLayout;

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
        tv_chart.setOnClickListener(this);
        tv_friend.setOnClickListener(this);
        tv_found.setOnClickListener(this);
        tv_mine.setOnClickListener(this);
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
        mChatLinearLayout = (LinearLayout) findViewById(R.id.id_ll_chat);

        tv_chart = (TextView) findViewById(R.id.id_tv_chat);
        tv_friend = (TextView) findViewById(R.id.id_tv_friend);
        tv_found = (TextView) findViewById(R.id.id_tv_found);
        tv_mine = (TextView) findViewById(R.id.id_tv_mine);

        mDatas = new ArrayList<>();

        ChatFragment tab01 = new ChatFragment();
        ContactFragment tab02 = new ContactFragment();
        DiscoverFragment tab03 = new DiscoverFragment();
        MyFragment tab04 = new MyFragment();

        mDatas.add(tab01);
        mDatas.add(tab02);
        mDatas.add(tab03);
        mDatas.add(tab04);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public int getCount()
            {
                return mDatas.size();
            }

            @Override
            public Fragment getItem(int arg0)
            {
                return mDatas.get(arg0);
            }
        };

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
            case R.id.id_tv_chat :
                setSelect(0);
                break;
            case R.id.id_tv_friend:
                setSelect(1);
                break;
            case R.id.id_tv_found:
                setSelect(2);
                break;
            case R.id.id_tv_mine:
                setSelect(3);
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