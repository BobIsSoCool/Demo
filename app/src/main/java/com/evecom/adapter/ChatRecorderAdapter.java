package com.evecom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.R;
import com.evecom.bean.ChatMessage;
import com.evecom.bean.ChatRecorder;
import com.evecom.bean.MyUser;
import com.evecom.myview.MyCircleImage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wub on 2017/5/12.
 */
public class ChatRecorderAdapter extends BaseAdapter{

    /**
     * 上下文
     */
    Context context;
    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据集合
     */
    private List<ChatRecorder> mList = new ArrayList<>();
    /**
     * 用于存放头像url的数组
     */
    private String[] urls = null;

    /**
     *
     * @param context
     */
    public ChatRecorderAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void addList(List<ChatRecorder> list){
        this.mList = list;
    }

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
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_chat_list,null);

            viewHolder.ivAvatar = (MyCircleImage) convertView.findViewById(R.id.ivAvatar);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvLastMessage = (TextView) convertView.findViewById(R.id.tvLastMessage);
            viewHolder.tvMessageCount = (TextView) convertView.findViewById(R.id.tvMessageCount);

            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //当前用户id
        SharedPreferences sharedPreferences = context.getSharedPreferences("user",context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("objectId",null);

        //获取当前实例
        ChatRecorder chatRecorder = mList.get(i);

        //头像、昵称（根据对方的id去数据库获取）
        String userId = "";
        if (chatRecorder.getIdOfUserA().equals(currentUserId)){
            userId = chatRecorder.getIdOfUserB();
        }else {
            userId = chatRecorder.getIdOfUserA();
        }
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId",userId);
        final ViewHolder finalViewHolder = viewHolder;
        query.findObjects(context, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size()>0){
                    String avatarUrl = list.get(0).getAvatar();
                    String name = list.get(0).getUsername();
                    finalViewHolder.tvUserName.setText(name);
                    Glide.with(context).load(avatarUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            finalViewHolder.ivAvatar.setImageBitmap(resource);
                        }
                    });
                }else {}
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        //最后一条消息
        viewHolder.tvLastMessage.setText(chatRecorder.getTheLastMessage());

        //未读消息数
        //判断未读消息用户的id
        if (chatRecorder.getIdHasMessageToRead().equals(currentUserId)){
            //当前用户有未读消息，紧接着判断未读消息数是否>0
            if (chatRecorder.getCountNotRead()>0){
                viewHolder.tvMessageCount.setText(chatRecorder.getCountNotRead()+"");
                //显示未读消息数目
                viewHolder.tvMessageCount.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder{
        /**
         * 头像
         */
        MyCircleImage ivAvatar;
        /**
         * 昵称
         */
        TextView tvUserName;
        /**
         * 最后一条消息
         */
        TextView tvLastMessage;
        /**
         * 未读消息数
         */
        TextView tvMessageCount;
    }

}
