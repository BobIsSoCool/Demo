package com.evecom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.R;
import com.evecom.bean.MyUser;
import com.evecom.bean.PingLun;
import com.evecom.myview.MyCircleImage;
import com.evecom.myview.MyDialogYesOrNo;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 评论适配器
 * Created by wub on 2017/4/7.
 */
public class PingLunAdapter extends BaseAdapter {

    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据
     */
    private List<PingLun> mList = new ArrayList<>();
    /**
     * Context
     */
    private Context context;
    /**
     *
     */
    private RequestManager requestManager;
    private int pos;
    private DeleteDataOnrefresh deleteDataOnrefresh;

    /**
     * 构造函数
     *
     * @param context
     */
    public PingLunAdapter(Context context, RequestManager requestManager,DeleteDataOnrefresh deleteDataOnrefresh) {

        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.requestManager = requestManager;
        this.deleteDataOnrefresh=deleteDataOnrefresh;
    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addList(List<PingLun> list) {

        this.mList = list;

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

    /**
     * 接口，删除一条评论后，
     * 告诉Activity刷新界面显示的评论数、更新数据库中文章的评论数
     */
    public interface DeleteDataOnrefresh{
        public void onRefresf();
    }

    /**
     *
     * @param position
     * @param convertView
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_pinglun, null);

            viewHolder.ivAvatar = (MyCircleImage) convertView.findViewById(R.id.ivAvatar);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.tvDelete = (TextView) convertView.findViewById(R.id.tvDeletePingLun);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //获取当前评论记录
        final PingLun pingLun = mList.get(position);

        //填充数据

        //头像、昵称（根据用户id查询该用户信息，获取头像昵称）
        String userObjectID = pingLun.getUserObjectID();
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", userObjectID);
        final ViewHolder finalViewHolder = viewHolder;
        query.findObjects(context, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size() > 0) {
                    //得到用户
                    MyUser myUser = list.get(0);
                    //得到头像url、昵称
                    String avatarUrl = myUser.getAvatar();
                    String name = myUser.getUsername();
                    //显示头像、昵称
                    if (avatarUrl != null && !avatarUrl.equals("default")) {

                        loadImage(requestManager,avatarUrl,finalViewHolder.ivAvatar);

                    }
                    //设置昵称
                    finalViewHolder.tvName.setText(name);
                }
            }

            @Override
            public void onError(int i, String s) {
                // TODO: 2017/4/7
            }
        });

        //显示日期
        finalViewHolder.tvDate.setText(pingLun.getDate());
        //显示评论内容
        finalViewHolder.tvContent.setText(pingLun.getPingLunContent());
        //判断如果是当前用户的评论，则显示删除按钮
        SharedPreferences sha = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sha.getString("objectID", null);
        if (currentUserObjectID.equals(pingLun.getUserObjectID())) {
            finalViewHolder.tvDelete.setVisibility(View.VISIBLE);
        }


        //删除按钮点击事件
        finalViewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提示是否删除该评论
                deleteOrNot(pingLun);
            }
        });


        return convertView;
    }

    /**
     * 删除一条评论
     *
     * @param pinglun
     */
    public void deleteOrNot(final PingLun pinglun) {

        //创建一个Dialog
        final MyDialogYesOrNo dialog = new MyDialogYesOrNo(context);

        dialog.setTitle("是否删除该评论");

        //设置监听
        dialog.setNoOnclickListener("取消", new MyDialogYesOrNo.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialog.dismiss();
            }
        });
        dialog.setYesOnclickListener("删除", new MyDialogYesOrNo.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                pinglun.delete(context, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        //重新获取数据，并刷新列表
                        getDate(pinglun.getArticleObjectID());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();

    }

    /**
     * 重新获取数据，并刷新列表
     */
    public void getDate(String articleObjectID) {

        //根据当前文id查询评论
        String currentArticleID = articleObjectID;

        //创建查询
        BmobQuery<PingLun> query = new BmobQuery<>();

        //添加查询条件
        query.addWhereEqualTo("articleObjectID", currentArticleID);

        //查询
        query.findObjects(context, new FindListener<PingLun>() {
            @Override
            public void onSuccess(List<PingLun> list) {
                //刷新列表
                mList = list;
                notifyDataSetChanged();
                //接口回调，通知activity刷新显示的评论数
                deleteDataOnrefresh.onRefresf();
            }

            @Override
            public void onError(int i, String s) {
            }
        });

    }

    class ViewHolder {

        /**
         * 头像
         */
        MyCircleImage ivAvatar;
        /**
         * 用户昵称
         */
        TextView tvName;
        /**
         * 评论日期
         */
        TextView tvDate;
        /**
         * 评论内容
         */
        TextView tvContent;
        /**
         * 删除按钮
         */
        TextView tvDelete;
    }

    /**
     *
     * @param glide
     * @param url
     * @param view
     */
    public static void loadImage(RequestManager glide, String url, final MyCircleImage view) {
        glide.load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                view.setImageBitmap(resource);
            }
        });
    }

    public interface deletePingLun{

        public void delete(PingLun pingLun);

    }

}
