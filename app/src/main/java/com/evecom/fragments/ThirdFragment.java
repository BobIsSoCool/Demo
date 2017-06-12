package com.evecom.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.evecom.activity.ChatActivity;
import com.evecom.activity.R;
import com.evecom.adapter.ChatRecorderAdapter;
import com.evecom.adapter.MyGuanzhuListViewAdapter;
import com.evecom.bean.ChatRecorder;
import com.evecom.bean.MyUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by Bob on 2017/2/10.
 * 第三个页面的fragment
 */
public class ThirdFragment extends Fragment {

    /**
     * View
     */
    View rootView;
    /**
     *
     */
    ListView lvXiaoxi;
    /**
     * SwipeRefreshLayout
     */
    SwipeRefreshLayout swipe_refresh;
    /**
     * 数据集合
     */
    List<ChatRecorder> mList = new ArrayList<>();

    /**
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

        rootView = inflater.inflate(R.layout.fragment_layout_page3, container, false);

        //初始化控件
        initUI();

        //加载数据
        loadData();

        return rootView;
    }

    /**
     * 初始化控件
     */
    public void initUI() {

        swipe_refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        lvXiaoxi = (ListView) rootView.findViewById(R.id.lvChatList);

        //下拉刷新
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        //点击item，进入与该用户的聊天界面
        lvXiaoxi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取当前点击实例
                ChatRecorder chatRecorder = mList.get(i);
                //得到聊天对象的id
                //当前用户id
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_APPEND);
                String currentUserId = sharedPreferences.getString("objectId", null);
                //两个id中与当前用户id不同的，即是对方的id
                String idOfTheOther = "";
                if (chatRecorder.getIdOfUserA().equals(currentUserId)) {
                    idOfTheOther = chatRecorder.getIdOfUserB();
                } else {
                    idOfTheOther = chatRecorder.getIdOfUserA();
                }
                //根据此id去用户表查询该用户的信息（用于传递给聊天界面）
                BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                query.addWhereEqualTo("objectId", idOfTheOther);
                query.findObjects(getActivity(), new FindListener<MyUser>() {
                    @Override
                    public void onSuccess(List<MyUser> list) {
                        if (list.size() > 0) {
                            MyUser user = list.get(0);
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("userObjectID",user.getObjectId());
                            intent.putExtra("avatarUrl",user.getAvatar());
                            intent.putExtra("userName",user.getUsername());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "不存在该用户", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "" + i + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * 加载聊天记录
     */
    public void loadData() {

        swipe_refresh.setRefreshing(true);

        //当前用户id
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("objectId", null);

        //创建复合or查询，查询id中存在当前用户id的记录

        //条件1
        BmobQuery<ChatRecorder> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("idOfUserA", currentUserId);
        //条件2
        BmobQuery<ChatRecorder> query2 = new BmobQuery<>();
        query1.addWhereEqualTo("idOfUserB", currentUserId);
        //组合
        List<BmobQuery<ChatRecorder>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        //创建总查询
        BmobQuery<ChatRecorder> query = new BmobQuery<>();
        query.or(queries);
        //查询
        query.findObjects(getActivity(), new FindListener<ChatRecorder>() {
            @Override
            public void onSuccess(List<ChatRecorder> list) {
                if (list.size() > 0) {
                    //有记录，则初始化列表
                    ChatRecorderAdapter adapter = new ChatRecorderAdapter(getActivity());
                    adapter.addList(list);
                    lvXiaoxi.setAdapter(adapter);
                    swipe_refresh.setRefreshing(false);

                    mList = list;
                } else {
                    mList = new ArrayList<ChatRecorder>();
                    ChatRecorderAdapter adapter = new ChatRecorderAdapter(getActivity());
                    adapter.addList(mList);
                    lvXiaoxi.setAdapter(adapter);
                    swipe_refresh.setRefreshing(false);
                }
            }

            @Override
            public void onError(int i, String s) {
                swipe_refresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //可见时，刷新数据
        loadData();
    }
}
