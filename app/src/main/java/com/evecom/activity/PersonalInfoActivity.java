package com.evecom.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.adapter.ViewPagerAdapter;
import com.evecom.bean.FollowedUser;
import com.evecom.fragments.fragment_Article;
import com.evecom.fragments.fragment_Dongtai;
import com.evecom.fragments.fragment_Guanzhu;
import com.evecom.myview.DepthPageTransformer;
import com.evecom.myview.MyCircleImage;
import com.evecom.myview.ZoomOutPageTransformer;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 个人信息界面
 * Created by Bob on 2017/3/9.
 */
public class PersonalInfoActivity extends FragmentActivity implements View.OnClickListener{

//控件
    /**
     * 返回图标
     */
    ImageView ivBack;
    /**
     * 编辑个人信息图标
     */
    ImageView ivEditInfo;
    /**
     * 关注数
     */
    TextView tvGuanzhuCount;
    /**
     * 粉丝数
     */
    TextView tvFensiCount;
    /**
     * 头像
     */
    MyCircleImage ivAvatar;
    /**
     * 分页指示器
     */
    MagicIndicator magicIndicator;
    /**
     * viewPager
     */
    ViewPager vpPersonalInfo;
    /**
     * themeColor
     */
    private String themeColor = "#009688";

//变量
    /**
     * 分页指示器标题
     */
    private static final String[] indicatorTitle =  new String[]{"动态", "文章","已关注"};
    private List<String> mDataList = Arrays.asList(indicatorTitle); //转换成集合
    /**
     * fragment
     */
    fragment_Dongtai fragment_dongtai;
    fragment_Article fragment_article;
    fragment_Guanzhu fragment_guanzhu;
    /**
     * vpIndex:用于表示当前处于哪个fragment页面
     */
    private int vpIndex;
    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //获取主题
        int theme = getSharedPreferences("user",MODE_PRIVATE).getInt("theme",0);
        //设置主题
        if (theme == 0){
            setTheme(R.style.CustomStyleOne);
            theme = R.style.CustomStyleOne;
        }else {
            setTheme(theme);
            switch (theme){
                case R.style.CustomStyleOne:
                    themeColor = "#009688";
                    break;
                case R.style.CustomStyleTwo:
                    themeColor = "#CD5555";
                    break;
                case R.style.CustomStyleThree:
                    themeColor = "#E0B66C";
                    break;
                case R.style.CustomStyleFour:
                    themeColor = "#313638";
                    break;
                case R.style.CustomStyleFive:
                    themeColor = "#5C7148";
                    break;
                case R.style.CustomStyleSix:
                    themeColor = "#708090";
                    break;
                case R.style.CustomStyleSeven:
                    themeColor = "#784E3B";
                    break;
                default:
                    themeColor = "#009688";
            }
        }

        setContentView(R.layout.activity_personal_info);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }

        //初始化控件
        initUI();

        //获取关注的人数
        getTheCountYouFollowed();
        //获取粉丝数
        getTheCountBeFollowed();

        //设置监听
        setListeners();

    }

    /**
     * 初始化控件
     */
    public void initUI(){

        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivEditInfo = (ImageView) findViewById(R.id.ivEditInfo);

        ivAvatar = (MyCircleImage) findViewById(R.id.ivAvatar);
        tvGuanzhuCount = (TextView)findViewById(R.id.tvGuanzhuCount);
        tvFensiCount = (TextView)findViewById(R.id.tvFensiCount);

        //初始化viewpager
        vpPersonalInfo = (ViewPager) findViewById(R.id.vpPersonalInfo);
        //为viewpager添加动画
        //vpPersonalInfo.setPageTransformer(true, new DepthPageTransformer());
        vpPersonalInfo.setPageTransformer(true, new DepthPageTransformer());

        initViewPager();
        //初始化分页指示器
        magicIndicator = (MagicIndicator) findViewById(R.id.magicIndicator);
        initMagicIndicator();
    }

    /**
     * 设置监听
     */
    public void setListeners(){

        ivBack.setOnClickListener(this);
        ivEditInfo.setOnClickListener(this);

    }

    /**
     * 实现监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            //点击，返回主界面
            case R.id.ivBack:
                onBackPressed();    //不用finish，为了让activity退出时也有过度动画
                break;

            //点击，跳转编辑个人信息界面
            case R.id.ivEditInfo:
                Intent intent = new Intent(this,EditPersonalInfoActivity.class);
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this,
                                ivAvatar, "ivSharedHead").toBundle());
                //startActivity(intent);
                break;

        }

    }

    /**
     * 初始化viewpager
     */
    public void initViewPager(){

        //创建fragment集合
        List<Fragment> fragmentList = new ArrayList<>();

        //添加fragment元素
        fragment_dongtai = new fragment_Dongtai();
        fragment_article = new fragment_Article();
        fragment_guanzhu = new fragment_Guanzhu();

        fragmentList.add(fragment_dongtai);
        fragmentList.add(fragment_article);
        fragmentList.add(fragment_guanzhu);

        //为viewpager设置适配器
        vpPersonalInfo.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),fragmentList));
    }

    /**
     * 函数：initIndicator（）
     * 功能：初始化分页指示器
     */
    public void initMagicIndicator() {

        //背景色
        magicIndicator.setBackgroundColor(Color.parseColor(themeColor));

        CommonNavigator commonNavigator = new CommonNavigator(this);
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
                simplePagerTitleView.setNormalColor(Color.parseColor("#ffffff"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vpPersonalInfo.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            //指示器样式
            @Override
            public LinePagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                //indicator.setFillColor(Color.parseColor("#ae3a3c3b"));
                indicator.setColors(Color.WHITE);
                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);

        //indicator与viewpager绑定
        ViewPagerHelper.bind(magicIndicator, vpPersonalInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String avatarUrl = sharedPreferences.getString("avatarUrl", null);
        if (avatarUrl!=null&&!avatarUrl.equals("default")){
            Glide.with(this).load(avatarUrl).asBitmap().centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            ivAvatar.setImageBitmap(resource);
                        }
                    });
        }


    }

    /**
     * 查询关注人数
     */
    public void getTheCountYouFollowed(){

        //创建查询对象
        BmobQuery<FollowedUser> query = new BmobQuery<>();
        //条件（关注方id为当前用户id）
        String currentUserObjectID = getSharedPreferences("user",MODE_PRIVATE).getString("objectID",null);
        query.addWhereEqualTo("whoCareID",currentUserObjectID);
        //查询
        query.findObjects(this, new FindListener<FollowedUser>() {
            @Override
            public void onSuccess(List<FollowedUser> list) {
                //显示关注数量
                tvGuanzhuCount.setText(list.size()+"");
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 查询粉丝数
     */
    public void getTheCountBeFollowed(){

        //创建查询对象
        BmobQuery<FollowedUser> query = new BmobQuery<>();
        //条件（被关注方id为当前用户id）
        String currentUserObjectID = getSharedPreferences("user",MODE_PRIVATE).getString("objectID",null);
        query.addWhereEqualTo("careWhoID",currentUserObjectID);
        //查询
        query.findObjects(this, new FindListener<FollowedUser>() {
            @Override
            public void onSuccess(List<FollowedUser> list) {
                //显示关注数量
                tvFensiCount.setText(list.size()+"");
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

}
