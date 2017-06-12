package com.evecom.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.evecom.activity.R;
import com.evecom.activity.ShowArticleActivity;
import com.evecom.adapter.ArticleTypeAdapter;
import com.evecom.bean.ArticleTypeOne;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Bob on 2017/2/10.
 * 热文第一个页面的fragment
 */
public class rewenFragment1 extends Fragment {

    //变量
    private View view;
    private PullToRefreshListView plsv;         //pulltorefreshlistview
    private ListView lsv;                       //pulltorefreshlistview中的listview

    private ArticleTypeAdapter adapter;


    private List<ArticleTypeOne> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.rewen_fragment_layout_page, container, false);

        //初始化PullToRefreshListView
        initPtrl();

        return view;
    }

    /***
     * 函数：initUI()
     * 功能：初始化控件
     */
    public void initPtrl() {

        //PullToRefreshListView
        plsv = (PullToRefreshListView) view.findViewById(R.id.rewen_pageone_plsv);

        //同时支持下拉和上拉
        plsv.setMode(PullToRefreshBase.Mode.BOTH);

        //设置下拉、上拉的提示文字
        ILoadingLayout startLabels = plsv
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("你的求知欲还真强...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("路漫漫，唯有等待...");// 刷新时
        startLabels.setReleaseLabel("再拉我的脖子就断了...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = plsv.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel("还想学点什么呢...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("偷来梨蕊三分白，借得梅花一缕魂...");// 刷新时
        endLabels.setReleaseLabel("诗和远方...");// 下来达到一定距离时，显示的提示

        //获得pulltorefreshlistview中的listview
        lsv = plsv.getRefreshableView();

        //构建适配器
        adapter = new ArticleTypeAdapter(getActivity());

        //获取数据
        refreshRoot();

        //设置监听
        plsv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新
                refreshRoot();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                // 加载完，刷新UI
                plsv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        plsv.onRefreshComplete();
                    }
                }, 1000);
                // TODO: 2017/4/10
            }
        });

        //列表点击事件（展示文章）
        lsv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ShowArticleActivity.class);
                Bundle bundle = new Bundle();
                //跳转展示文章的界面，传一个1告诉给界面是展示分类一页面的文章
                bundle.putString("activity","1");
                //传文章对象
                ArticleTypeOne articleToShow = mList.get(i-1);
                bundle.putSerializable("article",articleToShow);
                intent.putExtras(bundle);
                //跳转文章展示界面
                startActivity(intent);
            }
        });

    }

    /**
     * 函数：refreshRoot
     * 功能：加载数据
     */
    protected void refreshRoot() {

        // 加载完，刷新UI
        plsv.postDelayed(new Runnable() {
            @Override
            public void run() {
                plsv.onRefreshComplete();
            }
        }, 1000);


        BmobQuery<ArticleTypeOne> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(getActivity(), new FindListener<ArticleTypeOne>() {
            @Override
            public void onSuccess(List<ArticleTypeOne> list) {
                if (list.size()>0){
                    mList = list;
                    adapter.addListTypeOne(mList);
                    lsv.setAdapter(adapter);
                }
            }

            @Override
            public void onError(int i, String s) {
                //Toast.makeText(getActivity(),"获取失败...",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //进入页面自动获取数据
        //refreshRoot();
    }

}
