package com.evecom.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Bob on 2017/2/13.
 * viewpager适配器
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    //变量
    private List<Fragment> mFragments;

    /**
     * 构造函数：ViewPagerAdapter()
     */
    public ViewPagerAdapter(FragmentManager fm,List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    /**
     * 函数：getItem()
     */
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * 函数：getCount()
     */
    @Override
    public int getCount() {
        return mFragments.size();
    }

    /**
     * 函数：getPageTitle()
     */
    @Override
    public CharSequence getPageTitle(int position) {//选择性实现
        return mFragments.get(position).getClass().getSimpleName();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
