package com.evecom.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.R;
import com.evecom.bean.FollowedUser;
import com.evecom.bean.UserData;
import com.evecom.myview.CrossSlipView;
import com.evecom.myview.MyCircleImage;

public class SlipViewAdapter extends BaseAdapter {

    /**
     * 数据
     */
    private UserData[] userDatas;

    Context context;
    LayoutInflater inflater;

    private Map<Integer, View> viewMap = new HashMap<Integer, View>();

    /**
     * ViewHolder
     */
    class ViewHolder {
        Button bt_left, bt_right;
        MyCircleImage ivAvatar;
        TextView tvUserName,tvQianMing;

        public ViewHolder(View view) {
            bt_left = (Button) view.findViewById(R.id.bt_left);
            bt_right = (Button) view.findViewById(R.id.bt_right);
            ivAvatar = (MyCircleImage) view.findViewById(R.id.ivAvatar);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvQianMing = (TextView) view.findViewById(R.id.tvQianMing);
            view.setTag(this);
        }
    }

    public SlipViewAdapter(Context context, UserData[] userDatas) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.userDatas = userDatas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return userDatas.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return userDatas[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final int location = position;
        View view_left = null;
        View view_right = null;
        View view_center = null;
        ViewHolder holder = null;
        if (null == viewMap.get(location) || !(convertView.getTag() instanceof ViewHolder)) {
            view_left = inflater.inflate(R.layout.view_left, null);
            view_center = inflater.inflate(R.layout.view_center, null);
            view_right = inflater.inflate(R.layout.view_right, null);
            convertView = new CrossSlipView(view_center, view_left, view_right);
//            convertView = new CrossSlipView(view_center, null, null);
//            convertView = new CrossSlipView(view_center, null, view_right);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            viewMap.put(location, convertView);
        } else {
            convertView = viewMap.get(location);
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bt_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "对" + location + "执行A操作", Toast.LENGTH_LONG).show();
            }
        });
        holder.bt_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "对" + location + "执行B操作", Toast.LENGTH_LONG).show();
            }
        });

        //填充数据
        //头像
        final ViewHolder finalHolder = holder;
        Glide.with(context).load(userDatas[position].getAvatarUrl()).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                finalHolder.ivAvatar.setImageBitmap(resource);
            }
        });
        //昵称
        finalHolder.tvUserName.setText(userDatas[position].getUserName());
        //签名
        finalHolder.tvQianMing.setText(userDatas[position].getQianMing());

        return convertView;
    }

}
