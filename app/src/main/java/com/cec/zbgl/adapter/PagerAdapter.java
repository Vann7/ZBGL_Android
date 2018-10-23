package com.cec.zbgl.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.cec.zbgl.utils.LogUtil;

import java.util.List;

/**
 * 首页滚动pager 适配器
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;


    public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
        this.mFragmentManager = fm;
        fm.beginTransaction().commitAllowingStateLoss();
    }


    @Override
    public Fragment getItem(int position) {
        return this.mFragments.get(position);
    }

    @Override
    public int getCount() {
        return this.mFragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment)object);

    }



}
