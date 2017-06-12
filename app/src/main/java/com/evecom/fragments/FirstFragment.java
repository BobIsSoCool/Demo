package com.evecom.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.evecom.activity.R;
import com.evecom.activity.ShowArticleActivity;
import com.evecom.adapter.ViewPagerAdapter;
import com.evecom.myview.DepthPageTransformer;
import com.evecom.myview.ZoomOutPageTransformer;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bob on 2017/2/10.
 * 第一个页面的fragment(热文页面)
 */
public class FirstFragment extends Fragment {

    //变量
    private View view;
    private ViewPager mViewPager = null;       //viewpager

    private static final String[] CHANNELS =
            new String[]{"第一页", "第二页", "第三页", "第四页", "第五页", "第六页"}; //分页指示器标题
    private List<String> mDataList = Arrays.asList(CHANNELS);



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_layout_page1, container, false);

        //初始化控件
        initUI();

        //初始化分页指示器、viewpager
        initViewPager();
        initIndicator();

        return view;
    }

    /**
     * 初始化控件
     */
    public void initUI() {

        //viewpager
        mViewPager = (ViewPager) view.findViewById(R.id.rewen_viewPager);

        //为viewpager添加动画
        //mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }


    /**
     * 函数：initIndicator（）
     * 功能：初始化分页指示器、viewpager
     */
    public void initIndicator() {

        MagicIndicator mIndicator = (MagicIndicator) view.findViewById(R.id.mIndicator);
        mIndicator.setBackgroundColor(Color.WHITE);

        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setScrollPivotX(0.35f);

        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#3a3c3b"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#51aa5c"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#ae3a3c3b"));
                return indicator;
            }
        });

        mIndicator.setNavigator(commonNavigator);

        //indicator与viewpager绑定
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }

    /**
     * 初始化viewpager
     */
    public void initViewPager() {

        //新建fragment集合
        List<Fragment> fragments = new ArrayList<>();

        //创建热文三个页面的fragment实例
        rewenFragment1 f1 = new rewenFragment1();
        rewenFragment2 f2 = new rewenFragment2();
        rewenFragment3 f3 = new rewenFragment3();
        rewenFragment4 f4 = new rewenFragment4();
        rewenFragment5 f5 = new rewenFragment5();
        rewenFragment6 f6 = new rewenFragment6();

        //添加实例的集合
        fragments.add(f1);
        fragments.add(f2);
        fragments.add(f3);
        fragments.add(f4);
        fragments.add(f5);
        fragments.add(f6);

        //为viewpager设置适配器
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments));

    }
}