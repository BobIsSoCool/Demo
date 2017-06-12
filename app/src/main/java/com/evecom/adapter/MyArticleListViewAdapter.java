package com.evecom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.evecom.activity.R;
import com.evecom.bean.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * "我的文章"适配器
 * Created by Bob on 2017/3/13.
 */
public class MyArticleListViewAdapter extends BaseAdapter {

    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据
     */
    private List<Article> lvMyArticle = new ArrayList<>();

    /**
     * checkBox的状态
     * true：显示
     * false：隐藏（默认）
     */
    private boolean hideCheckBoxOrNot;
    /**
     * 存储item选中状态的数组
     * true:表示对应位置的item是选中的
     * false:表示对应位置的item是未选中的
     */
    boolean[] lastStatus;

    /**
     * @param context
     * @param lvMyArticle
     */
    public MyArticleListViewAdapter(Context context, List<Article> lvMyArticle) {
        this.lvMyArticle = lvMyArticle;
        layoutInflater = LayoutInflater.from(context);

        //初始化选中状态
        lastStatus = new boolean[lvMyArticle.size()];
        for (int i = 0; i < lastStatus.length; i++) {
            lastStatus[i] = false;
        }

    }

    @Override
    public int getCount() {
        return lvMyArticle.size();
    }

    @Override
    public Object getItem(int position) {
        return lvMyArticle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * 设置item显示或隐藏
     * @param flag1：true显示；false隐藏
     */
    public void setStatuOfCheckBox(boolean flag1) {
        hideCheckBoxOrNot = flag1;
    }

    /**
     * @return: 返回保存选中状态的数组
     */
    public boolean[] getLastStatus(){
        return lastStatus;
    }

    /**
     * @return :返回选中个数
     */
    public int getCheckedCount(){
        int checkedCount = 0;
        for (int i = 0;i<lastStatus.length;i++){
            if (lastStatus[i]){
                checkedCount++;
            }
        }
        return checkedCount;
    }

    /**
     *
     * @param flag2 :true全选；false全不选
     */
    public void setCheckAllOrNot(boolean flag2){
        for (int i = 0;i<lastStatus.length;i++){
            lastStatus[i] = flag2;
        }
    }

    /**
     *
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
            convertView = layoutInflater.inflate(R.layout.item_article, null);

            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvArticleDate);
            viewHolder.tvArticleTitle = (TextView) convertView.findViewById(R.id.tvArticleTitle);
            viewHolder.tvArticleType = (TextView) convertView.findViewById(R.id.tvArticleType);
            viewHolder.tvDianZan = (TextView) convertView.findViewById(R.id.tvDianZan);
            viewHolder.tvPingLun = (TextView) convertView.findViewById(R.id.tvPingLun);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //获取当前文章对象
        Article article = lvMyArticle.get(position);

        //拿到item的checkbox，并为其设置监听
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cbOperation);

        //根据参数，设置是否显示
        if (hideCheckBoxOrNot){
            checkBox.setVisibility(View.VISIBLE);
        }else {
            checkBox.setVisibility(View.GONE);
        }

        //checkbox的状态发生改变时，把新的状态保存起来
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    lastStatus[position] = true;
                } else {
                    lastStatus[position] = false;
                }
            }
        });

        //根据保存的状态进行显示
        checkBox.setChecked(lastStatus[position]);

        //填充数据
        viewHolder.tvDate.setText(article.getPublishDate());
        viewHolder.tvArticleTitle.setText(article.getArticleTitle());
        viewHolder.tvArticleType.setText(article.getArticleType());
        viewHolder.tvDianZan.setText(article.getLikeNumber() + "");
        viewHolder.tvPingLun.setText(article.getDiscussNumber() + "");

        return convertView;

    }

    class ViewHolder {
        /**
         * 发布日期
         */
        TextView tvDate;
        /**
         * 文章标题
         */
        TextView tvArticleTitle;
        /**
         * 所属分类
         */
        TextView tvArticleType;
        /**
         * 点赞数
         */
        TextView tvDianZan;
        /**
         * 评论数
         */
        TextView tvPingLun;

    }
}
