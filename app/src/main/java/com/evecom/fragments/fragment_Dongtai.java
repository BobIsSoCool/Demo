package com.evecom.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.evecom.activity.R;
import com.evecom.adapter.DongtaiListViewAdapter;
import com.evecom.bean.Dongtai;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 个人信息中的动态fragment
 * Created by Bob on 2017/3/10.
 */
public class fragment_Dongtai extends Fragment {

    /**
     * view
     */
    private View view;
    /**
     * 无数据时显示
     */
    TextView tvNoData;
    /**
     * ListView
     */
    ListView lvDongtai;
    /**
     * 数据集合
     */
    private List<Dongtai> mList = new ArrayList<>();
    /**
     * popupwindow
     */
    private PopupWindow popupWindow;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_dongtai, container, false);

        //初始化控件
        initUI();

        //获取动态数据
        getDongtai();

        return view;
    }

    /**
     * 初始化控件
     */
    public void initUI() {

        lvDongtai = (ListView) view.findViewById(R.id.lvDongtai);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);

        lvDongtai.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                //显示操作popupwindow
                showPopupWindow(position);

                return true;
            }
        });

    }

    /**
     * 获取动态数据
     */
    public void getDongtai() {

        //获取保存的用户objectID
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String objectID = sharedPreferences.getString("objectID", null);

        //从数据库动态表里，查询匹配当前objectID的动态数据
        BmobQuery<Dongtai> query = new BmobQuery();
        //查询条件
        query.addWhereEqualTo("userObjectID", objectID);
        //查询
        query.findObjects(getActivity(), new FindListener<Dongtai>() {
            @Override
            public void onSuccess(List<Dongtai> list) {

                //无数据，显示无数据图片
                if (list.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                } else if (list.size() > 0) {
                    //有数据，初始化列表
                    tvNoData.setVisibility(View.GONE);
                    mList = list;
                    initListView();
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 初始化列表
     */
    public void initListView() {

        //构建适配器
        DongtaiListViewAdapter adapter = new DongtaiListViewAdapter(getActivity(), mList);

        lvDongtai.setAdapter(adapter);

    }

    /**
     * 显示操作的popupwindow
     * @param position
     */
    public void showPopupWindow(final int position){

        //创建popupwindow
        //popupwindow布局
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_dongtai, null);

        //创建一个popupwindow
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

 //设置监听
        TextView tvDelete = (TextView) contentView.findViewById(R.id.tvDelete);
        TextView tvCancel = (TextView) contentView.findViewById(R.id.tvCancel);
        //点击删除该动态
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList.get(position).delete(getActivity(), new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        //删除成功，刷新列表
                        getDongtai();
                        popupWindow.dismiss();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
        //点击取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_dongtai,null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);

    }

}
