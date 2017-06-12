package com.evecom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.R;
import com.evecom.bean.Article;
import com.evecom.bean.ArticleTypeOne;
import com.evecom.bean.MyCollection;
import com.evecom.bean.MyUser;
import com.evecom.myview.MyCircleImage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 我的收藏适配器
 * Created by wub on 2017/3/31.
 */
public class MyCollectionAdapter extends BaseAdapter {

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
    private List<MyCollection> mList = new ArrayList<>();

    /**
     *
     * @param context
     */
    public MyCollectionAdapter(Context context) {

        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    /**
     * 添加数据
     */
    public void addList(List<MyCollection> list){
        mList = list;
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

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_guanzhu, null);

            viewHolder.ivAvatar = (MyCircleImage) convertView.findViewById(R.id.ivAvatar);
            viewHolder.tvAuthorName = (TextView) convertView.findViewById(R.id.tvUserName);
            //因为用了和关注列表同一个item布局，此处标题是原签名位置
            viewHolder.tvArticleTitle = (TextView) convertView.findViewById(R.id.tvQianMing);

            viewHolder.ivOperation = (ImageView) convertView.findViewById(R.id.ivRight);
            viewHolder.ivOperation.setVisibility(View.GONE);

            //控件

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //填充数据

        //获取当前对象
        MyCollection myCollection = mList.get(position);
        //文章id
        String articleObjectID = myCollection.getArticleObjectID();
        //去文章总表查询该文章
        BmobQuery<Article> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId",articleObjectID);
        //查询
        final ViewHolder finalViewHolder = viewHolder;
        query.findObjects(context, new FindListener<Article>() {
            @Override
            public void onSuccess(List<Article> list) {
                if (list.size()>0){
                    //得到该文章对象
                    Article article = list.get(0);
                    //已经拿到文章对象，直接可以设置标题内容
                    finalViewHolder.tvArticleTitle.setText("《"+article.getArticleTitle()+"》");
                    //紧接着根据文章中的作者id去获取作者的头像和昵称
                    String userObjectID = article.getUserObjectID();
                    BmobQuery<MyUser> bmobQuery = new BmobQuery<MyUser>();
                    bmobQuery.addWhereEqualTo("objectId",userObjectID);
                    //查询
                    bmobQuery.findObjects(context, new FindListener<MyUser>() {
                        @Override
                        public void onSuccess(List<MyUser> list) {
                            if (list.size()>0){
                                //得到文章对应的用户对象
                                MyUser myUser = list.get(0);
                                //设置用户头像及昵称
                                String avatarUrl = myUser.getAvatar();
                                String userName = myUser.getUsername();
                                //头像
                                if (avatarUrl!=null&&!avatarUrl.equals("default")){
                                    Glide.with(context).load(avatarUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            finalViewHolder.ivAvatar.setImageBitmap(resource);
                                        }
                                    });
                                }
                                //昵称
                                finalViewHolder.tvAuthorName.setText(userName);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            //Toast.makeText(context,"查询失败1"+i+s,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                //Toast.makeText(context,"查询失败2"+i+s,Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }

    class ViewHolder {

        /**
         * 头像
         */
        MyCircleImage ivAvatar;
        /**
         * 作者
         */
        TextView tvAuthorName;
        /**
         * 标题
         */
        TextView tvArticleTitle;
        /**
         *
         */
        ImageView ivOperation;
    }


}


