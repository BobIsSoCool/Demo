package com.evecom.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.ChatActivity;
import com.evecom.activity.R;
import com.evecom.bean.Article;
import com.evecom.bean.FollowedUser;
import com.evecom.bean.MyUser;
import com.evecom.bean.UserData;
import com.evecom.myview.MyCircleImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * "我的关注"适配器
 * Created by Bob on 2017/3/13.
 */
public class MyGuanzhuListViewAdapter extends BaseAdapter {

    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据
     */
    private UserData[] userDatas;
    /**
     * Context
     */
    private Context context;
    /**
     *
     */
    private boolean flag = false;
    /**
     *
     */
    PopupWindow popupWindow;
    /**
     * 重新获取的数据
     */
    private List<FollowedUser> mList = new ArrayList<>();
    /**
     * index
     */
    private int index = 0;
    /**
     * 当取关后，关注人员为0
     * 显示此控件
     */
    TextView tvNoData;


    /**
     * @param context
     * @param userDatas
     */
    public MyGuanzhuListViewAdapter(Context context, UserData[] userDatas, TextView tvNoData) {

        this.context = context;
        this.userDatas = userDatas;
        this.tvNoData = tvNoData;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return userDatas.length;
    }

    @Override
    public Object getItem(int position) {
        return userDatas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_guanzhu, null);

            viewHolder.ivAvatar = (MyCircleImage) convertView.findViewById(R.id.ivAvatar);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvQianMing = (TextView) convertView.findViewById(R.id.tvQianMing);
            viewHolder.ivRight = (ImageView) convertView.findViewById(R.id.ivRight);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //头像
        final ViewHolder finalViewHolder = viewHolder;
        Glide.with(context).load(userDatas[position].getAvatarUrl()).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                finalViewHolder.ivAvatar.setImageBitmap(resource);
            }
        });
        //昵称
        finalViewHolder.tvUserName.setText(userDatas[position].getUserName());
        //签名
        finalViewHolder.tvQianMing.setText(userDatas[position].getQianMing());

        //为item右侧的操作图标设置监听
        finalViewHolder.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出一个popupwindow
                showPopupwindow(finalViewHolder.ivRight, userDatas[position]);
            }
        });

        return convertView;

    }


    /**
     * @param rootView:popupwindow显示在此view下方
     * @param userData：当前操作的用户
     */
    public void showPopupwindow(ImageView rootView, final UserData userData) {

        //popupwindow布局
        final View contentView = LayoutInflater.from(context).inflate(R.layout.pop_guanzhu, null);

        //创建一个popupwindow
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        //设置监听
        TextView tvChat = (TextView) contentView.findViewById(R.id.tvChat);
        TextView tvCancelGuanzhu = (TextView) contentView.findViewById(R.id.tvCancelGuanzhu);

        //点击取消关注
        tvCancelGuanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,"点击了取关",Toast.LENGTH_SHORT).show();
                cancelGuanzhu(userData);
            }
        });

        //点击进入聊天窗口（把当前聊天对象信息传过去）
        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userObjectID",userData.getUserObjectID());
                intent.putExtra("avatarUrl",userData.getAvatarUrl());
                intent.putExtra("userName",userData.getUserName());
                context.startActivity(intent);
                popupWindow.dismiss();
            }
        });


        //显示在操作图标下面
        popupWindow.showAsDropDown(rootView);


    }

    /**
     * 取消对该用户的关注
     *
     * @param userData
     */
    public void cancelGuanzhu(UserData userData) {

        //当前用户objectID
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        final String currentUserObjectID = sharedPreferences.getString("objectID", null);

        //被关注用户的objectID
        String careWhoID = userData.getUserObjectID();

        //复合查询，删除
        //查询1
        BmobQuery<FollowedUser> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("whoCareID", currentUserObjectID);
        //查询2
        BmobQuery<FollowedUser> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("careWhoID", careWhoID);
        //组合两个查询
        List<BmobQuery<FollowedUser>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        //创建复合查询
        BmobQuery<FollowedUser> query = new BmobQuery<>();
        query.and(queries);
        //查询
        query.findObjects(context, new FindListener<FollowedUser>() {
            @Override
            public void onSuccess(List<FollowedUser> list) {
                if (list.size() > 0) {
                    //删除该条关注记录
                    list.get(0).delete(context, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            popupWindow.dismiss();
                            Toast.makeText(context, "取消关注", Toast.LENGTH_SHORT).show();
                            //删除成功，刷新列表
                            refreshList(currentUserObjectID);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(context, "取消关注失败" + i + s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "查询失败" + i + s, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void refreshList(String currentUserObjectID) {

        //重寻查询当前用户的关注记录
        BmobQuery<FollowedUser> query = new BmobQuery<>();
        query.addWhereEqualTo("whoCareID", currentUserObjectID);
        query.findObjects(context, new FindListener<FollowedUser>() {
            @Override
            public void onSuccess(List<FollowedUser> list) {

                if (list.size() > 0) {

                    mList = list;

                    //转换数据
                    //存储数据的数组

                    userDatas = new UserData[mList.size()];

                    index = 0;
                    //获取单条关注被关注用户信息
                    getFollow(index);

                    tvNoData.setVisibility(View.GONE);


                } else {

                    tvNoData.setVisibility(View.VISIBLE);
                    userDatas = new UserData[0];
                    notifyDataSetChanged();

                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });

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
        query.findObjects(context, new FindListener<MyUser>() {
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
                        //没有元素了，刷新列表
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    class ViewHolder {

        /**
         * 用户头像url
         */
        MyCircleImage ivAvatar;
        /**
         * 用户昵称
         */
        TextView tvUserName;
        /**
         * 用户签名
         */
        TextView tvQianMing;
        /**
         * 右侧操作按钮
         */
        ImageView ivRight;

    }
}
