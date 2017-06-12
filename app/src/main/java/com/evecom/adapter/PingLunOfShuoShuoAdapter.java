package com.evecom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evecom.activity.R;
import com.evecom.bean.MyUser;
import com.evecom.bean.PingLun;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 说说下面的评论的适配器
 * Created by wub on 2017/5/2.
 */
public class PingLunOfShuoShuoAdapter extends BaseAdapter {

    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据
     */
    private List<PingLun> mList = new ArrayList<>();
    /**
     * 上下文
     */
    private Context context;
    /**
     * 数组：用于保存用户名
     */
    private String[] userNames;
    /**
     * 数组：保存布尔类型
     * 默认为false，当一条评论的用户名从数据库查询过后，复制为true
     * 下次显示时不用再次请求数据库
     */
    private boolean[] queryOrNot;

    /**
     * 构造方法
     *
     * @param context
     */
    public PingLunOfShuoShuoAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addList(List<PingLun> list) {

        //数据
        this.mList = list;

        //初始化用于保存信息的数组
        userNames = new String[list.size()];
        for (int i = 0;i<list.size();i++){
            userNames[i] = "";
        }

    }

    /**
     * 刷新
     */
    public void refreshData(){
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            //新建一个viewholder
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_pinglun_of_shuoshuo, null);
            //控件
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvPingLunContent = (TextView) convertView.findViewById(R.id.tvContentOfPingLun);
            //关联viewholder
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前评论
        PingLun currentPingLun = mList.get(position);

        //填充数据
        final ViewHolder finalViewHolder = viewHolder;
        //判断数组中是否有保存的用户名
        //1、有则直接显示
        //2、没有则根据该条评论的用户id请求数据库，查询后保存到数组并显示
        if (userNames[position].equals("")){
            //请求数据库，查询用户名
            String userObjectID = currentPingLun.getUserObjectID();
            //创建一个查询
            BmobQuery<MyUser> query = new BmobQuery<>();
            //添加条件
            query.addWhereEqualTo("objectId",userObjectID);
            //查询
            query.findObjects(context, new FindListener<MyUser>() {
                @Override
                public void onSuccess(List<MyUser> list) {
                    //得到查询结果
                    if (list.size()>0){
                        String name = list.get(0).getUsername();
                        finalViewHolder.tvName.setText(name+":");
                        //保存到数组
                        userNames[position] = name;
                    }
                }
                @Override
                public void onError(int i, String s) {
                }
            });
        }else {
            //直接显示已保存的用户名
            viewHolder.tvName.setText(userNames[position]+":");
        }

        //评论内容
        viewHolder.tvPingLunContent.setText(currentPingLun.getPingLunContent());

        return convertView;
    }

    /**
     * ViewHolder
     */
    class ViewHolder {
        /**
         * 用户名
         */
        TextView tvName;
        /**
         * 评论内容
         */
        TextView tvPingLunContent;
    }


}
