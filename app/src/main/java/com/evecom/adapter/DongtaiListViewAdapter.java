package com.evecom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.evecom.activity.R;
import com.evecom.bean.Article;
import com.evecom.bean.Dongtai;

import java.util.ArrayList;
import java.util.List;

/**
 * "我的动态"适配器
 * Created by Bob on 2017/3/25.
 */
public class DongtaiListViewAdapter extends BaseAdapter {

    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据集合
     */
    private List<Dongtai> mList = new ArrayList<>();


    /**
     * @param context
     * @param list
     */
    public DongtaiListViewAdapter(Context context, List<Dongtai> list) {

        this.mList = list;
        layoutInflater = LayoutInflater.from(context);

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
            convertView = layoutInflater.inflate(R.layout.item_dongtai, null);
            //控件
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.viewBottom = convertView.findViewById(R.id.viewBottom);

            //最后一个把线面的线隐藏
            if (position == mList.size() - 1) {
                viewHolder.viewBottom.setVisibility(View.INVISIBLE);
            }

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //填充数据
        Dongtai dongtai = mList.get(position);
        viewHolder.tvContent.setText(dongtai.getContent());
        viewHolder.tvDate.setText(dongtai.getDate());

        return convertView;

    }

    class ViewHolder {

        /**
         * 日期
         */
        TextView tvDate;
        /**
         * 动态内容
         */
        TextView tvContent;
        /**
         * 时间轴下面的线
         */
        View viewBottom;

    }
}
