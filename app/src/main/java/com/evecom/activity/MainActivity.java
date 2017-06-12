package com.evecom.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.fragments.FirstFragment;
import com.evecom.fragments.SecondFragment;
import com.evecom.fragments.ThirdFragment;
import com.evecom.myview.MyCircleImage;

import java.io.File;

public class MainActivity extends FragmentActivity {

    //变量
    private DrawerLayout drawerLayout;      //drawerlayout侧滑菜单
    private MyCircleImage openSideMenuImg;  //悬浮于主页面的图片，点击打开侧滑菜单
    private RadioGroup rgp;                 //主页面底部按钮组，用于切换页面
    private FrameLayout frameLayout;        //主页面帧布

    //fragment碎片变量
    private FragmentManager fm;             //fragment管理器
    private FirstFragment fragment1;        //第一个页面fragment
    private SecondFragment fragment2;        //第二个页面fragment
    private ThirdFragment fragment3;        //第三个页面fragment
    private Fragment[] fragments;

    //侧滑菜单部分
    /**
     * 头像
     */
    MyCircleImage ivAvatar;

    /**
     * 用户ID
     */
    TextView tvName;

    /**
     * 个人信息
     */
    RelativeLayout rlPersonalInfo;
    ImageView ivPersonalInfo;
    /**
     * 我的收藏
     */
    RelativeLayout rlPersonalCollection;
    /**
     * 头像在本地的路径
     */
    private String avatarCache;
    /**
     * 签名
     */
    TextView tvQianMing;
    /**
     * RadioButton
     */
    RadioButton rb1, rb2, rb3;
    /**
     * 点击更换主题
     */
    RelativeLayout rChangeTheme;
    /**
     * 选择主题的布局
     */
    LinearLayout llSetTheme;
    /**
     * 对应主题
     */
    RelativeLayout themeRl1, themeRl2, themeRl3, themeRl4, themeRl5, themeRl6, themeRl7;
    /**
     * 勾中图标
     */
    RelativeLayout themeOk1, themeOk2, themeOk3, themeOk4, themeOk5, themeOk6, themeOk7;
    /**
     * 用于记录上一次被勾中的主题颜色
     */
    RelativeLayout lastSelectedTheme;
    /**
     * 关闭主题布局
     */
    ImageView ivClose;
    private int theme;
    /**
     *
     */
    RadioGroup rgSetTheme;
    /**
     * 当前显示的fragment的位置
     */
    private int currentIndex;
    /**
     * 当前点击的页面位置
     */
    private int index;
    /**
     *
     */
    SharedPreferences sharedPreferences;
    /**
     * 点击确定，切换主题
     */
    TextView tvOk;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取保存的主题
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        theme = sharedPreferences.getInt("theme", 0);
        //设置主题
        if (theme == 0) {
            setTheme(R.style.CustomStyleOne);
        } else {
            setTheme(theme);
        }

        setContentView(R.layout.activity_main);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }

        //初始化控件
        initialUI();

        //设置头像
        initAvatar();

        //给控件设置监听
        setListeners();

    }

    /**
     * 函数：initialUI（）
     * 功能：初始化控件
     */
    public void initialUI() {

        llSetTheme = (LinearLayout) findViewById(R.id.llSetTheme);
        ivClose = (ImageView) findViewById(R.id.ivClose);


        //侧滑菜单
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(0x00ffffff); //这一句让主页面保持高亮，不再是变暗
        drawerLayout.setScrimColor(Color.TRANSPARENT);//禁用纸盒shadow

        //左上角悬浮头像
        openSideMenuImg = (MyCircleImage) findViewById(R.id.openSideMenuImg);


        //初始化radiogroup
        rgp = (RadioGroup) findViewById(R.id.rg_main_bottom);

        //设置第一个radiobutton默认选中
        ((RadioButton) (rgp.getChildAt(0))).setChecked(true);

        //主页面帧布局
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);

        //获取fragment管理器
        fm = getSupportFragmentManager();

        //侧滑菜单头像
        ivAvatar = (MyCircleImage) findViewById(R.id.ivAvatar);

        //用户ID
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(sharedPreferences.getString("userName", null));

        //个人信息
        rlPersonalInfo = (RelativeLayout) findViewById(R.id.rlPersonalInfo);
        ivPersonalInfo = (ImageView) findViewById(R.id.ivPersonalInfo);
        //我的收藏
        rlPersonalCollection = (RelativeLayout) findViewById(R.id.rlPersonalCollection);

        tvQianMing = (TextView) findViewById(R.id.tvQianMing);
        tvQianMing.setText(sharedPreferences.getString("qianMing", null));

        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);

        Drawable drawable1 = getResources().getDrawable(R.drawable.icon_article_selected);
        drawable1.setBounds(0, 0, 100, 100);
        Drawable drawable2 = getResources().getDrawable(R.drawable.icon_shuoshuo_normal);
        drawable2.setBounds(0, 0, 100, 100);
        Drawable drawable3 = getResources().getDrawable(R.drawable.icon_xiaoxi_normal);
        drawable3.setBounds(0, 0, 100, 100);

        rb1.setCompoundDrawables(null, drawable1, null, null);
        rb2.setCompoundDrawables(null, drawable2, null, null);
        rb3.setCompoundDrawables(null, drawable3, null, null);

        rChangeTheme = (RelativeLayout) findViewById(R.id.rChangeTheme);

        fragment1 = new FirstFragment();
        fragment2 = new SecondFragment();
        fragment3 = new ThirdFragment();

        fragments = new Fragment[]{fragment1, fragment2, fragment3};

        getSupportFragmentManager().beginTransaction()
                .add(R.id.framelayout, fragment1).add(R.id.framelayout, fragment2).add(R.id.framelayout, fragment3)
                .hide(fragment2).hide(fragment3).show(fragment1)
                .commit();
        currentIndex = 0;

        //点击显示更换主题布局
        rChangeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llSetTheme.setVisibility(View.VISIBLE);
            }
        });
        //点击隐藏主题布局
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llSetTheme.setVisibility(View.GONE);
            }
        });

        //点击选择主题颜色
        /*rgSetTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int chekedID = rgSetTheme.getCheckedRadioButtonId();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (chekedID) {
                    case R.id.rbTheme1:
                        theme = R.style.CustomStyleOne;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#009688");
                        break;
                    case R.id.rbTheme2:
                        theme = R.style.CustomStyleTwo;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#CD5555");
                        break;
                    case R.id.rbTheme3:
                        theme = R.style.CustomStyleThree;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#E0B66C");
                        break;
                    case R.id.rbTheme4:
                        theme = R.style.CustomStyleFour;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#313638");
                        break;
                    case R.id.rbTheme5:
                        theme = R.style.CustomStyleFive;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#5C7148");
                        break;
                    case R.id.rbTheme6:
                        theme = R.style.CustomStyleSix;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#708090");
                        break;
                    case R.id.rbTheme7:
                        theme = R.style.CustomStyleSeven;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#784E3B");
                        break;
                    default:
                        theme = R.style.CustomStyleOne;
                        svThemeColor.setVisibility(View.GONE);
                        editor.putString("themeColor","#009688");
                        break;
                }

                //保存主题
                editor.putInt("theme", theme);
                editor.commit();
                //刷新界面
                recreate();
            }
        });*/

        //点击设置主题的一些初始化设置
        selectTheme();
    }

    public void selectTheme() {

        themeRl1 = (RelativeLayout) findViewById(R.id.rlTheme1);
        themeRl2 = (RelativeLayout) findViewById(R.id.rlTheme2);
        themeRl3 = (RelativeLayout) findViewById(R.id.rlTheme3);
        themeRl4 = (RelativeLayout) findViewById(R.id.rlTheme4);
        themeRl5 = (RelativeLayout) findViewById(R.id.rlTheme5);
        themeRl6 = (RelativeLayout) findViewById(R.id.rlTheme6);
        themeRl7 = (RelativeLayout) findViewById(R.id.rlTheme7);

        themeOk1 = (RelativeLayout) findViewById(R.id.rlGou1);
        themeOk2 = (RelativeLayout) findViewById(R.id.rlGou2);
        themeOk3 = (RelativeLayout) findViewById(R.id.rlGou3);
        themeOk4 = (RelativeLayout) findViewById(R.id.rlGou4);
        themeOk5 = (RelativeLayout) findViewById(R.id.rlGou5);
        themeOk6 = (RelativeLayout) findViewById(R.id.rlGou6);
        themeOk7 = (RelativeLayout) findViewById(R.id.rlGou7);

        //根据当前主题，将对应颜色勾中，lastSelectedTheme记录最后一次勾中的选项，便于下一次修改主题后隐藏
        switch (theme) {
            case R.style.CustomStyleOne:
                themeOk1.setVisibility(View.VISIBLE);
                lastSelectedTheme = themeOk1;
                break;
            case R.style.CustomStyleTwo:
                themeOk2.setVisibility(View.VISIBLE);
                lastSelectedTheme = themeOk2;
                break;
            case R.style.CustomStyleThree:
                themeOk3.setVisibility(View.VISIBLE);
                lastSelectedTheme = themeOk3;
                break;
            case R.style.CustomStyleFour:
                themeOk4.setVisibility(View.VISIBLE);
                lastSelectedTheme = themeOk4;
                break;
            case R.style.CustomStyleFive:
                themeOk5.setVisibility(View.VISIBLE);
                lastSelectedTheme = themeOk5;
                break;
            case R.style.CustomStyleSix:
                themeOk6.setVisibility(View.VISIBLE);
                lastSelectedTheme = themeOk6;
                break;
            case R.style.CustomStyleSeven:
                themeOk7.setVisibility(View.VISIBLE);
                lastSelectedTheme = themeOk7;
                break;
        }

        //点击事件
        //点击主题一
        themeRl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当前主题与点击的主题不同才会切换，否则不进行操作
                if (theme != R.style.CustomStyleOne) {
                    //隐藏上一次勾中的“勾”图标
                    lastSelectedTheme.setVisibility(View.GONE);
                    //显示当前点击项的“勾”图标
                    themeOk1.setVisibility(View.VISIBLE);
                    //将当前项记录为最后一次勾中项
                    lastSelectedTheme = themeOk1;
                    //设置主题
                    theme = R.style.CustomStyleOne;
                }
            }
        });
        //点击主题二
        themeRl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当前主题与点击的主题不同才会切换，否则不进行操作
                if (theme != R.style.CustomStyleTwo) {
                    //隐藏上一次勾中的“勾”图标
                    lastSelectedTheme.setVisibility(View.GONE);
                    //显示当前点击项的“勾”图标
                    themeOk2.setVisibility(View.VISIBLE);
                    //将当前项记录为最后一次勾中项
                    lastSelectedTheme = themeOk2;
                    //设置主题
                    theme = R.style.CustomStyleTwo;
                }
            }
        });
        //点击主题三
        themeRl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当前主题与点击的主题不同才会切换，否则不进行操作
                if (theme != R.style.CustomStyleThree) {
                    //隐藏上一次勾中的“勾”图标
                    lastSelectedTheme.setVisibility(View.GONE);
                    //显示当前点击项的“勾”图标
                    themeOk3.setVisibility(View.VISIBLE);
                    //将当前项记录为最后一次勾中项
                    lastSelectedTheme = themeOk3;
                    //设置主题
                    theme = R.style.CustomStyleThree;
                }
            }
        });
        //点击主题四
        themeRl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当前主题与点击的主题不同才会切换，否则不进行操作
                if (theme != R.style.CustomStyleFour) {
                    //隐藏上一次勾中的“勾”图标
                    lastSelectedTheme.setVisibility(View.GONE);
                    //显示当前点击项的“勾”图标
                    themeOk4.setVisibility(View.VISIBLE);
                    //将当前项记录为最后一次勾中项
                    lastSelectedTheme = themeOk4;
                    //设置主题
                    theme = R.style.CustomStyleFour;
                }
            }
        });
        //点击主题五
        themeRl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当前主题与点击的主题不同才会切换，否则不进行操作
                if (theme != R.style.CustomStyleFive) {
                    //隐藏上一次勾中的“勾”图标
                    lastSelectedTheme.setVisibility(View.GONE);
                    //显示当前点击项的“勾”图标
                    themeOk5.setVisibility(View.VISIBLE);
                    //将当前项记录为最后一次勾中项
                    lastSelectedTheme = themeOk5;
                    //设置主题
                    theme = R.style.CustomStyleFive;
                }
            }
        });
        //点击主题六
        themeRl6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当前主题与点击的主题不同才会切换，否则不进行操作
                if (theme != R.style.CustomStyleSix) {
                    //隐藏上一次勾中的“勾”图标
                    lastSelectedTheme.setVisibility(View.GONE);
                    //显示当前点击项的“勾”图标
                    themeOk6.setVisibility(View.VISIBLE);
                    //将当前项记录为最后一次勾中项
                    lastSelectedTheme = themeOk6;
                    //设置主题
                    theme = R.style.CustomStyleSix;
                }
            }
        });
        //点击主题七
        themeRl7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当前主题与点击的主题不同才会切换，否则不进行操作
                if (theme != R.style.CustomStyleSeven) {
                    //隐藏上一次勾中的“勾”图标
                    lastSelectedTheme.setVisibility(View.GONE);
                    //显示当前点击项的“勾”图标
                    themeOk7.setVisibility(View.VISIBLE);
                    //将当前项记录为最后一次勾中项
                    lastSelectedTheme = themeOk7;
                    //设置主题
                    theme = R.style.CustomStyleSeven;
                }
            }
        });

        //点击确定，切换主题，刷新界面
        tvOk = (TextView) findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断主题是否有改变
                //重新往偏好设置中取主题的值，与当前theme值比较
                int oldTheme = sharedPreferences.getInt("theme",-1);
                if (oldTheme!=theme){

                    //主题已改变，保存新主题
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("theme",theme);
                    editor.commit();
                    //刷新界面
                    recreate();

                }else {
                    //主题没改动，直接隐藏主题选择布局
                    llSetTheme.setVisibility(View.GONE);
                }
            }
        });

    }


    /**
     * 函数：setListeners()
     * 功能：给控件设置监听
     */
    public void setListeners() {

        //给悬浮图片设置监听，点击打开侧滑菜单
        openSideMenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开左侧菜单
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //给底部radiogroup设置监听
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //根据选中的radiobutton，显示相应的fragment
                switch (checkedId) {
                    //选中第一个radiobutton，隐藏fragment2、fragment3，显示fragment1（后面同理）
                    case R.id.rb1:

                        index = 0;

                        Drawable drawable1 = getResources().getDrawable(R.drawable.icon_article_selected);
                        drawable1.setBounds(0, 0, 100, 100);
                        Drawable drawable2 = getResources().getDrawable(R.drawable.icon_shuoshuo_normal);
                        drawable2.setBounds(0, 0, 100, 100);
                        Drawable drawable3 = getResources().getDrawable(R.drawable.icon_xiaoxi_normal);
                        drawable3.setBounds(0, 0, 100, 100);

                        rb1.setCompoundDrawables(null, drawable1, null, null);
                        rb2.setCompoundDrawables(null, drawable2, null, null);
                        rb3.setCompoundDrawables(null, drawable3, null, null);
                        break;
                    //同上
                    case R.id.rb2:
                        index = 1;

                        Drawable drawable4 = getResources().getDrawable(R.drawable.icon_article_normal);
                        drawable4.setBounds(0, 0, 100, 100);
                        Drawable drawable5 = getResources().getDrawable(R.drawable.icon_shuoshuo_selected);
                        drawable5.setBounds(0, 0, 100, 100);
                        Drawable drawable6 = getResources().getDrawable(R.drawable.icon_xiaoxi_normal);
                        drawable6.setBounds(0, 0, 100, 100);

                        rb1.setCompoundDrawables(null, drawable4, null, null);
                        rb2.setCompoundDrawables(null, drawable5, null, null);
                        rb3.setCompoundDrawables(null, drawable6, null, null);
                        break;
                    //同上
                    case R.id.rb3:
                        index = 2;

                        Drawable drawable7 = getResources().getDrawable(R.drawable.icon_article_normal);
                        drawable7.setBounds(0, 0, 100, 100);
                        Drawable drawable8 = getResources().getDrawable(R.drawable.icon_shuoshuo_normal);
                        drawable8.setBounds(0, 0, 100, 100);
                        Drawable drawable9 = getResources().getDrawable(R.drawable.icon_xiaoxi_selected);
                        drawable9.setBounds(0, 0, 100, 100);

                        rb1.setCompoundDrawables(null, drawable7, null, null);
                        rb2.setCompoundDrawables(null, drawable8, null, null);
                        rb3.setCompoundDrawables(null, drawable9, null, null);
                        break;
                }

                //点击的页面与显示的界面不是同一界面
                if (index != currentIndex) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //隐藏之前的fragment
                    fragmentTransaction.hide(fragments[currentIndex]);
                    //显示点击位置的fragment
                    if (!fragments[index].isAdded()) {
                        //没有添加则先添加
                        fragmentTransaction.add(R.id.framelayout, fragments[index]);
                    }
                    //显示
                    fragmentTransaction.show(fragments[index]).commit();
                    //将index复制给currentIndex，作为新的显示的页面位置
                    currentIndex = index;

                }

            }
        });

        //自定义drawerlayout滑动效果
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {//slideOffset滑动距离（0-1）
                View mContent = drawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;                //1-0
                float leftScrale = 0.8f + slideOffset * 0.2f;   //0.5-1
                mMenu.setScaleX(leftScrale);
                mMenu.setScaleY(leftScrale);
                mContent.setTranslationX(mMenu.getWidth() * slideOffset);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //个人信息,点击跳转到个人信息界面
        rlPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, ivPersonalInfo, "sideMenu_personalInfo").toBundle());
                //startActivity(intent);
            }
        });

        //我的收藏，点击跳转收藏文章界面
        rlPersonalCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyCollectionActivity.class);
                startActivity(intent);
            }
        });
    }

    private long lastTime;// 记录第一次点击的时间

    /**
     * 函数：onBackPressed()
     * 功能：点击两次返回键，退出程序
     */
    @Override
    public void onBackPressed() {

        // 获取系统时间
        long currentTime = System.currentTimeMillis();
        // 两次点击间隔小于2秒，退出应用
        if (currentTime - lastTime < 2 * 1000) {
            super.onBackPressed();
        } else {
            // 两次点击间隔不小于2秒，提示再次点击退出应用
            Toast.makeText(this, "再次点击退出应用", Toast.LENGTH_SHORT).show();
            // 把当前时间设置为第一次点击时间
            lastTime = currentTime;
        }
    }

    /**
     * 函数：writeArticle()
     * 功能：弹出文章编辑界面
     */
    public void writeArticle(View view) {
        //传过去一个int，告诉文章编辑界面，这是新增（还有修改文章也是跳到这个界面，用于区分）
        Intent intent = new Intent(this, WriteArticleActivity.class);
        intent.putExtra("activity", "1");
        startActivity(intent);
    }

    /**
     * 函数：existLogin()
     * 功能：点击退出登录，跳转登录界面
     */
    public void existLogin(View view) {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * 初始化头像
     */
    public void initAvatar() {

        //判断数据库是否存有头像（登录时已将头像url保存在SharedPreferences中）
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String avatarUrl = sharedPreferences.getString("avatarUrl", null);      //服务器url
        avatarCache = sharedPreferences.getString("avatarCache", null);         //本地路径

        String url = sharedPreferences.getString("avatarUrl", null);
        if (url != null && !url.equals("default")) {
            Glide.with(this).load(url).asBitmap().centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            ivAvatar.setImageBitmap(resource);
                            openSideMenuImg.setImageBitmap(resource);
                        }
                    });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //可见时就更新头像、用户名、签名
        //头像
        initAvatar();

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        //用户名
        String newName = sharedPreferences.getString("userName", null);
        tvName.setText(newName);
        //签名
        String qianMing = sharedPreferences.getString("qianMing", null);
        tvQianMing.setText(qianMing);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //去掉了这一句，防止切换主题，activity重建时导致fragment重叠
        //super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
