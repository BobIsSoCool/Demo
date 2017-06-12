package com.evecom.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.evecom.activity.R;
import com.evecom.adapter.ShuoShuoAdapter;
import com.evecom.bean.Article;
import com.evecom.bean.Dongtai;
import com.evecom.bean.ShuoShuo;
import com.evecom.myview.MyDialogShuoShuo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Bob on 2017/2/10.
 * 第二个页面的fragment
 */
public class SecondFragment extends Fragment{

    /**
     * View
     */
    View view;
    /**
     * 添加说说
     */
    ImageView ivAddShuoShuo;
    /**
     * 说说列表
     */
    ListView lvShuoShuo;
    /**
     * 数据
     */
    List<ShuoShuo> mList = new ArrayList<>();
    /**
     * Dialog
     */
    private MyDialogShuoShuo dialog;
    /**
     * 上下文（给一个全局上下文参数，避免getActivity报空）
     */
    protected Context context;
    /**
     * 发布说说时显示的加载动画
     */
    LinearLayout llLodingView;
    /**
     * 下拉刷新
     */
    SwipeRefreshLayout swipeLayout;

    /**
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_layout_page2, container, false);

        //初始化控件
        initUI();

        //获取数据，显示说说
        getDate();

        return view;
    }

    /**
     *  初始化控件
     */
    public void initUI(){

        ivAddShuoShuo = (ImageView) view.findViewById(R.id.ivAddShuoShuo);
        lvShuoShuo = (ListView) view.findViewById(R.id.lvShuoShuo);
        llLodingView = (LinearLayout) view.findViewById(R.id.llLodingView);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        //点击，发布说说
        ivAddShuoShuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShuoShuo();
            }
        });

        swipeLayout.setColorSchemeResources(
                android.R.color.white
        );
        //根据主题设置颜色
        String color = "#009688";
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("theme",0);
        switch (theme){
            case R.style.CustomStyleOne:
                color = "#009688";
                break;
            case R.style.CustomStyleTwo:
                color = "#CD5555";
                break;
            case R.style.CustomStyleThree:
                color = "#E0B66C";
                break;
            case R.style.CustomStyleFour:
                color = "#313638";
                break;
            case R.style.CustomStyleFive:
                color = "#5C7148";
                break;
            case R.style.CustomStyleSix:
                color = "#708090";
                break;
            case R.style.CustomStyleSeven:
                color = "#784E3B";
                break;
        }
        swipeLayout.setProgressBackgroundColorSchemeColor(Color.parseColor(color));

        //下拉刷新
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDate();
            }
        });

    }

    /**
     * 发布一条说说
     */
    public void addShuoShuo(){

        //创建一个Dialog
        dialog = new MyDialogShuoShuo(getActivity());

        //点击事件
        dialog.setNoOnclickListener("取消", new MyDialogShuoShuo.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialog.dismiss();
            }
        });
        dialog.setYesOnclickListener("发布", new MyDialogShuoShuo.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                //获取输入框内容
                String content = dialog.getContent();
                if (content.equals("")){
                    Toast.makeText(getActivity(),"请先输入...",Toast.LENGTH_SHORT).show();
                }else {
                    //在上传说说之前，隐藏dialog，显示加载动画
                    dialog.dismiss();
                    llLodingView.setVisibility(View.VISIBLE);
                    //上传说说到数据库
                    publishShuoShuo(content);
                }
            }
        });

        //展示dialog
        dialog.show();

    }

    /**
     * 上传说说到数据库
     */
    public void publishShuoShuo(final String content){

        //新建一个说说对象
        ShuoShuo shuoshuo = new ShuoShuo();

        //设置属性

        //用户id
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID",null);
        shuoshuo.setUserObjectID(currentUserObjectID);

        //日期
        //获取当前时间
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        final String date = sDateFormat.format(new java.util.Date());
        shuoshuo.setDate(date);

        //说说内容
        shuoshuo.setContent(content);

        //上传
        shuoshuo.save(getActivity(), new SaveListener() {
            @Override
            public void onSuccess() {
                //上传成功，隐藏加载动画
                llLodingView.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"发布成功",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                //刷新列表
                getDate();
                //增加一条动态
                addDongtai(content,date);
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getActivity(),"发布失败",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


    }


    /**
     * 获取数据
     */
    public void getDate(){

        //创建查询
        BmobQuery<ShuoShuo> query = new BmobQuery<>();

        //查询所有说说
        query.findObjects(getActivity(), new FindListener<ShuoShuo>() {
            @Override
            public void onSuccess(List<ShuoShuo> list) {
                if (list.size()>0){
                    //得到数据集合
                    //构建适配器
                    ShuoShuoAdapter adapter = new ShuoShuoAdapter(context);
                    //添加数据
                    adapter.addList(list);
                    //初始化列表
                    lvShuoShuo.setAdapter(adapter);
                    swipeLayout.setRefreshing(false);
                }else {
                    swipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(int i, String s) {
                swipeLayout.setRefreshing(false);
            }
        });

    }

    /**
     * 增加一条动态到数据库
     */
    public void addDongtai(String content,String date) {

        //创建一个动态对象
        Dongtai dongtai = new Dongtai();

        //设置动态日期、内容以及所属用户的ObjectID
        dongtai.setDate(date);
        dongtai.setContent("发布了说说：" + content);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID",null);
        dongtai.setUserObjectID(currentUserObjectID);

        //保存到BMOB数据库
        dongtai.save(context, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //getDate();
    }
}
