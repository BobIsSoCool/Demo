package com.evecom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.R;
import com.evecom.bean.ChatMessage;
import com.evecom.myview.MyCircleImage;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天消息适配器
 * Created by wub on 2017/4/19.
 */
public class ChatMessageAdapter extends BaseAdapter {

    /**
     * 上下文
     */
    private Context context;
    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据集合
     */
    private List<ChatMessage> mList = new ArrayList<>();
    /**
     * 当前用户id
     */
    private String currentUserObjectID;
    /**
     * 当前用户头像url
     */
    private String currentUserAvatarUrl;
    /**
     * 聊天用户的头像url（从activity那边传过来，不用每次根据用户id去查询）
     */
    private String userAvatarUrl;

    /**
     * 构造函数
     *
     * @param context
     */

    public ChatMessageAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        currentUserObjectID = sharedPreferences.getString("objectID", null);
        currentUserAvatarUrl =sharedPreferences.getString("avatarUrl",null);
    }

    /**
     * 添加数据
     * @param list
     */
    public void addList(List<ChatMessage> list) {
        this.mList = list;
    }

    /**
     * 当前聊天用户的头像url
     * @param userAvatarUrl
     */
    public void setUserAvatarUrl(String userAvatarUrl){
        this.userAvatarUrl = userAvatarUrl;
    }

    /**
     * @return
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_chat_message, null);

            //左侧
            viewHolder.llLeft = (LinearLayout) convertView.findViewById(R.id.llLeft);
            viewHolder.ivAvatarLeft = (MyCircleImage) convertView.findViewById(R.id.ivAvatarLeft);
            viewHolder.tvDateLeft = (TextView) convertView.findViewById(R.id.tvDateLeft);
            viewHolder.tvMessageLeft = (TextView) convertView.findViewById(R.id.tvMessageLeft);
            //右侧
            viewHolder.llRight = (LinearLayout) convertView.findViewById(R.id.llRight);
            viewHolder.ivAvatarRight = (MyCircleImage) convertView.findViewById(R.id.ivAvatarRight);
            viewHolder.tvDateRight = (TextView) convertView.findViewById(R.id.tvDateRight);
            viewHolder.tvMessageRight = (TextView) convertView.findViewById(R.id.tvMessageRight);


            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //获取当前消息实例
        ChatMessage chatMessage = mList.get(position);

        //判断当前消息是否是当前用户自己发的
        String userIDOfTheMessage = chatMessage.getIdOfTheSender();

        //用户自己的消息，显示在右侧
        if (userIDOfTheMessage.equals(currentUserObjectID)) {

            //隐藏左侧布局，显示右侧布局
            viewHolder.llLeft.setVisibility(View.GONE);
            viewHolder.llRight.setVisibility(View.VISIBLE);

            //填充数据
            final ViewHolder finalViewHolder = viewHolder;

            //头像
            Glide.with(context).load(currentUserAvatarUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    finalViewHolder.ivAvatarRight.setImageBitmap(resource);
                }
            });
            //日期
            finalViewHolder.tvDateRight.setText(chatMessage.getDate());
            //聊天内容
            finalViewHolder.tvMessageRight.setText(chatMessage.getMessage());

        } else {            //对方发的消息，则显示在左侧

            //隐藏右侧布局，显示左侧布局
            viewHolder.llLeft.setVisibility(View.VISIBLE);
            viewHolder.llRight.setVisibility(View.GONE);

            //填充数据
            final ViewHolder finalViewHolder = viewHolder;
            //头像
            Glide.with(context).load(userAvatarUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    finalViewHolder.ivAvatarLeft.setImageBitmap(resource);
                }
            });
            //日期
            finalViewHolder.tvDateLeft.setText(chatMessage.getDate());
            //聊天内容
            finalViewHolder.tvMessageLeft.setText(chatMessage.getMessage());

        }

        return convertView;
    }

    class ViewHolder {

        /**
         * 左侧布局
         */
        LinearLayout llLeft;
        /**
         * 左侧头像
         */
        MyCircleImage ivAvatarLeft;
        /**
         * 左侧时间
         */
        TextView tvDateLeft;
        /**
         * 左侧消息
         */
        TextView tvMessageLeft;

        /**
         * 右侧布局
         */
        LinearLayout llRight;
        /**
         * 右侧头像
         */
        MyCircleImage ivAvatarRight;
        /**
         * 右侧时间
         */
        TextView tvDateRight;
        /**
         * 右侧消息
         */
        TextView tvMessageRight;

    }

}
