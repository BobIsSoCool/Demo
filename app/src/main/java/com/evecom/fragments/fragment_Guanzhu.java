package com.evecom.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evecom.activity.R;
import com.evecom.adapter.MyGuanzhuListViewAdapter;
import com.evecom.adapter.SlipViewAdapter;
import com.evecom.bean.FollowedUser;
import com.evecom.bean.MyUser;
import com.evecom.bean.UserData;
import com.evecom.myview.CrossSlipListView;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 个人信息中的已关注fragment
 * Created by Bob on 2017/3/10.
 */
public class fragment_Guanzhu extends Fragment {

    /**
     * view
     */
    private View view;
    /**
     * 列表
     */
    ListView lvGuanZhu;
    /**
     * 适配器
     */
    MyGuanzhuListViewAdapter adapter;
    /**
     * 数据集合
     */
    private List<FollowedUser> mList = new ArrayList<>();
    /**
     * index
     */
    private int index = 0;
    /**
     * 存放数据的数组
     */
    private UserData[] userDatas = new UserData[10];
    /**
     * 加载动画
     */
    SpinKitView loadingView;
    /**
     * 无数据时显示
     */
    TextView tvNoData;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_guanzhu, container, false);

        //初始化控件
        initUI();

        //获取数据
        getDate();

        return view;
    }

    /**
     * 初始化控件
     */
    public void initUI() {

        lvGuanZhu = (ListView) view.findViewById(R.id.lvGuanZhu);
        loadingView = (SpinKitView) view.findViewById(R.id.loadingView);
        //显示加载动画
        loadingView.setVisibility(View.VISIBLE);
        //无数据显示
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);
    }

    /**
     * 获取数据
     */
    public void getDate() {

        //获取当前用户所有关注的对象的信息（即关注表中第一个id和当前用户id相同的数据）
        //当前用户id
        String currentUserID = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE).getString("objectID", null);

        //在数据库中查询
        BmobQuery<FollowedUser> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("whoCareID", currentUserID);
        bmobQuery.findObjects(getActivity(), new FindListener<FollowedUser>() {
            @Override
            public void onSuccess(List<FollowedUser> list) {
                if (list.size() > 0) {

                    //得到数据集合
                    mList = list;

                    //转换数据
                    exchangeDate();

                    tvNoData.setVisibility(View.GONE);

                }else {
                    //隐藏加载动画
                    loadingView.setVisibility(View.GONE);
                    //显示无数据
                    tvNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void exchangeDate() {

        index = 0;

        //存储数据的数组
        userDatas = new UserData[mList.size()];

        //获取单条关注被关注用户信息
        getFollow(index);

    }

    /**
     * 根据id，获取用户信息（被关注人的数据）
     *
     * @param n:下标
     */
    public void getFollow(int n) {

        //得到被关注用户id
        String careWhoID = mList.get(n).getCareWhoID();

        //创建查询
        BmobQuery<MyUser> query = new BmobQuery<>();

        //查询条件
        query.addWhereEqualTo("objectId", careWhoID);

        //查询
        query.findObjects(getActivity(), new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size() > 0) {
                    MyUser user = list.get(0);
                    //把用户数据存入数组中
                    userDatas[index] = new UserData();
                    userDatas[index].setAvatarUrl(user.getAvatar());
                    userDatas[index].setUserName(user.getUsername());
                    userDatas[index].setQianMing(user.getQianming());
                    userDatas[index].setUserObjectID(user.getObjectId());
                    //存完数据，查询下一个
                    index++;
                    if (index < mList.size()) {
                        getFollow(index);
                    } else {
                        //没有元素了，把转换后的数据传给adapter（即数组userDates）
                        loadingView.setVisibility(View.GONE);
                        //构建适配器

                        adapter = new MyGuanzhuListViewAdapter(getActivity(), userDatas,tvNoData);
                        lvGuanZhu.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

}
