package com.evecom.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.evecom.adapter.MyArticleListViewAdapter;
import com.evecom.adapter.MyCollectionAdapter;
import com.evecom.bean.MyCollection;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * “我的收藏”界面
 * Created by wub on 2017/4/6.
 */
public class MyCollectionActivity extends Activity{

    /**
     * 返回图标
     */
    ImageView ivBack;
    /**
     * 列表
     */
    ListView lvMyCollection;
    /**
     * 适配器
     */
    MyCollectionAdapter adapter;
    /**
     * 数据集合
     */
    List<MyCollection> mList = new ArrayList<>();


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
        }else {
            setTheme(theme);
        }

        setContentView(R.layout.activity_my_collection);

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

        //获取数据
        getData();

    }

    /**
     * 初始化控件
     */
    public void initUI(){

        ivBack = (ImageView) findViewById(R.id.ivBack);
        lvMyCollection = (ListView) findViewById(R.id.lvMyCollection);

        //点击退出
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * 获取当前用户收藏的文章
     */
    public void getData(){

        //获取当前用户id
        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID",null);

        //根据用户id去收藏表中查询
        BmobQuery<MyCollection> query = new BmobQuery<>();
        query.addWhereEqualTo("userObjectID",currentUserObjectID);
        query.findObjects(this, new FindListener<MyCollection>() {
            @Override
            public void onSuccess(List<MyCollection> list) {
                if (list.size()>0){
                    //得到收藏表中所有记录集合
                    mList = list;
                    //初始化列表
                    initListView();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MyCollectionActivity.this,"查询失败"+i+s,Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * 初始化列表
     */
    public void initListView(){

        //构建适配器
        adapter = new MyCollectionAdapter(this);

        //添加数据
        adapter.addList(mList);

        lvMyCollection.setAdapter(adapter);

    }

}
