package com.evecom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.R;
import com.evecom.bean.Article;
import com.evecom.bean.MyUser;
import com.evecom.bean.PingLun;
import com.evecom.bean.ShuoShuo;
import com.evecom.bean.Zan;
import com.evecom.myview.MyCircleImage;
import com.evecom.myview.MyDialogYesOrNo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 说说适配器
 * Created by wub on 2017/4/7.
 */
public class ShuoShuoAdapter extends BaseAdapter {

    /**
     * LayoutInflater
     */
    private LayoutInflater layoutInflater = null;
    /**
     * 数据
     */
    private List<ShuoShuo> mList = new ArrayList<>();
    /**
     * Context
     */
    private Context context;
    /**
     * 用于存每个item删除图标显示与否
     * true: 隐藏
     * false:显示
     */
    private Boolean[] hideOrNot;
    /**
     * 用于保存每条说说对应的头像url
     * 防止列表头像错乱
     */
    private String[] avatarUrls;
    /**
     * 用于保存用户昵称
     */
    private String[] userNames;
    /**
     * 数组，用于保存点赞图标的状态
     * 0:初始状态
     * 1:false（未选中）
     * 2:true（选中）
     */
    private int[] statusOfDianzan;
    /**
     * 数组：用于保存每条说说的评论内容
     * （每个元素是对应说说的评论集合）
     */
    private List<PingLun>[] pingLunContainer;
    /**
     * 数组：用于保存每条说说评论是否需要从数据库请求
     * 查询过后保存起来，不用每次getview都去查询
     * true:需要请求数据库
     * fasle:不需要再次请求数据库
     */
    private Boolean[] queryShuoShuoOrNot;
    /**
     *
     */
    private PopupWindow popupWindow;
    /**
     * 数组：用于保存说说的评论数
     */
    private int[] pingLunCount;

    /**
     * 构造函数
     *
     * @param context
     */
    public ShuoShuoAdapter(Context context) {

        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addList(List<ShuoShuo> list) {

        this.mList = list;
        //初始化数组
        initShuZu(list.size());

    }

    /**
     * 初始化用于保存信息的数组
     * @param size
     */
    public void initShuZu(int size){

        hideOrNot = new Boolean[size];
        avatarUrls = new String[size];
        userNames = new String[size];
        statusOfDianzan = new int[size];
        queryShuoShuoOrNot = new Boolean[size];
        pingLunContainer = new ArrayList[size];
        pingLunCount = new int[size];

        //获取当前用户id
        SharedPreferences sha = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sha.getString("objectID", null);

        for (int i = 0; i < size; i++) {

            //根据id设置每个item删除图标状态
            if (mList.get(i).getUserObjectID().equals(currentUserObjectID)) {
                //id相同，显示图标
                hideOrNot[i] = false;
            } else {
                //id不同，即说说不是当前用户发布的，隐藏删除图标
                hideOrNot[i] = true;
            }

            //默认设置存储头像url、昵称的每项的值为""
            avatarUrls[i] = "";
            userNames[i] = "";
            //默认设置点赞图标状态码为0
            statusOfDianzan[i] = 0;
            //默认评论内容都需要从数据库请求
            queryShuoShuoOrNot[i] = true;
            //初始化保存评论内容的集合
            pingLunContainer[i] = new ArrayList<>();
            //将评论数保存到数组
            pingLunCount[i] = mList.get(i).getDiscussCount();

        }
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
            convertView = layoutInflater.inflate(R.layout.item_shuoshuo, null);

            viewHolder.ivAvatar = (MyCircleImage) convertView.findViewById(R.id.ivAvatar);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);

            viewHolder.cbZan = (CheckBox) convertView.findViewById(R.id.cbZan);
            viewHolder.tvDianZanCount = (TextView) convertView.findViewById(R.id.tvDianZanCount);
            viewHolder.ivPingLun = (ImageView) convertView.findViewById(R.id.ivPingLun);
            viewHolder.tvPingLunCount = (TextView) convertView.findViewById(R.id.tvPingLunCount);
            viewHolder.ivZhuanFa = (ImageView) convertView.findViewById(R.id.ivZhuanFa);
            viewHolder.lvPingLun = (ListView) convertView.findViewById(R.id.lvPingLun);
            viewHolder.tvZhuanFaContent = (TextView) convertView.findViewById(R.id.tvZhuanFaContent);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //获取当前说说对象
        final ShuoShuo shuoShuo = mList.get(position);

        //填充数据
        final ViewHolder finalViewHolder = viewHolder;

        //先判断数组中是否已经保存了用户的信息
        // 没有再去查询
        if (avatarUrls[position].equals("") || userNames[position].equals("")) {

            //头像、昵称（根据用户id查询该用户信息，获取头像昵称）
            String userObjectID = shuoShuo.getUserObjectID();
            BmobQuery<MyUser> query = new BmobQuery<>();
            query.addWhereEqualTo("objectId", userObjectID);

            //查询
            query.findObjects(context, new FindListener<MyUser>() {
                @Override
                public void onSuccess(List<MyUser> list) {
                    if (list.size() > 0) {
                        //得到用户
                        MyUser myUser = list.get(0);
                        //得到头像url、昵称
                        String avatarUrl = myUser.getAvatar();
                        String name = myUser.getUsername();
                        //保存信息到数组
                        avatarUrls[position] = avatarUrl;
                        userNames[position] = name;
                        //显示头像、昵称
                        if (avatarUrl != null && !avatarUrl.equals("default")) {
                            Glide.with(context).load(avatarUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    //显示头像
                                    finalViewHolder.ivAvatar.setImageBitmap(resource);
                                }
                            });
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
        } else {
            //数组中已经保存了用户信息，则直接显示头像、昵称，不需要再次查询
            //头像
            Glide.with(context).load(avatarUrls[position]).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    finalViewHolder.ivAvatar.setImageBitmap(resource);
                }
            });
            //昵称
            finalViewHolder.tvName.setText(userNames[position]);

        }


        //日期
        finalViewHolder.tvDate.setText(shuoShuo.getDate());

        //显示说说内容
        //判断是否是转发的说说
        if (shuoShuo.getZhaunFaOrNot()) {
            //是转发的说说，分别显示转发附带内容和原说说
            viewHolder.tvZhuanFaContent.setVisibility(View.VISIBLE);
            viewHolder.tvContent.setText(shuoShuo.getContent());
            viewHolder.tvZhuanFaContent.setText(shuoShuo.getZhuanFaContent());
        } else {
            viewHolder.tvContent.setText(shuoShuo.getContent());
        }


        //根据实现的判断，设置是否显示删除图标
        if (hideOrNot[position]) {
            finalViewHolder.ivDelete.setVisibility(View.GONE);
        } else {
            finalViewHolder.ivDelete.setVisibility(View.VISIBLE);
        }


        //删除图标点击事件
        finalViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提示是否删除该评论
                deleteOrNot(shuoShuo);
            }
        });

        //设置点赞图标状态
        //先根据状态码判断是否已经保存了状态，为初始值0，则去数据库查询
        //不为0，则设置相应状态
        int currentStatu = statusOfDianzan[position];
        if (currentStatu == 1) {
            //未选中
            finalViewHolder.cbZan.setChecked(false);
        } else if (currentStatu == 2) {
            //选中
            finalViewHolder.cbZan.setChecked(true);
        } else if (currentStatu == 0) {
            //状态吗为初始值0，说明为查询过该文章的点赞记录
            //查询，然后保存状态吗到数组
            findOrDeleteZan(shuoShuo.getObjectId(), 1, finalViewHolder.cbZan, finalViewHolder.tvDianZanCount, position);
        }

        //设置点赞图标的点击事件
        finalViewHolder.cbZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalViewHolder.cbZan.isChecked()) {
                    //增加一条点赞记录
                    addZan(shuoShuo.getObjectId(), finalViewHolder.tvDianZanCount, position);
                } else {
                    //删除一条点赞记录
                    findOrDeleteZan(shuoShuo.getObjectId(), 2, finalViewHolder.cbZan, finalViewHolder.tvDianZanCount, position);
                }
            }
        });
        //显示点赞数
        finalViewHolder.tvDianZanCount.setText(shuoShuo.getDianZanCount() + "");
        //显示评论数
        finalViewHolder.tvPingLunCount.setText(pingLunCount[position] + "");

        //点击评论说说
        finalViewHolder.ivPingLun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //初始化说说下面的评论列表
        //判断是否需要从数据库请求数据（首次加载评论内容需请求数据库，之后从数组中拿即可）
        if (!queryShuoShuoOrNot[position]) {

            //不需要请求数据库，说说评论列表的数据已保存在数组中
            //构建适配器
            PingLunOfShuoShuoAdapter adapter = new PingLunOfShuoShuoAdapter(context);
            //添加数据
            adapter.addList(pingLunContainer[position]);
            finalViewHolder.lvPingLun.setAdapter(adapter);
            setListViewHeightBasedOnChildren(finalViewHolder.lvPingLun);
        } else {

            //需要请求数据库，即首次加载评论内容
            //获取当前说说的id，根据此id，在说说表中查询属于它的评论
            String currentShuoShuoID = shuoShuo.getObjectId();
            //创建查询
            BmobQuery<PingLun> query = new BmobQuery<>();
            //添加条件（articleObjectID可以表示文章和说说的id，之前写的没修改，这里做个提醒）
            query.addWhereEqualTo("articleObjectID", currentShuoShuoID);
            //查询
            query.findObjects(context, new FindListener<PingLun>() {
                @Override
                public void onSuccess(List<PingLun> list) {
                    //得到当前说说的评论数据集合
                    if (list.size() > 0) {
                        //构建适配器
                        PingLunOfShuoShuoAdapter adapter = new PingLunOfShuoShuoAdapter(context);
                        //添加数据
                        adapter.addList(list);
                        finalViewHolder.lvPingLun.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(finalViewHolder.lvPingLun);
                        //将数据保存到数组，便于下次显示
                        pingLunContainer[position] = list;
                        //保存完之后，将用于判断是否需要请求数据库的状态的数组赋值false,
                        // 即下次不用请求数据库了（描述好乱。。。）
                        queryShuoShuoOrNot[position] = false;
                    }
                }

                @Override
                public void onError(int i, String s) {
                }
            });
        }

        //点击发布评论
        finalViewHolder.ivPingLun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writePingLun(shuoShuo.getObjectId(), position, finalViewHolder.lvPingLun, finalViewHolder.tvPingLunCount);
            }
        });

        //点击转发说说
        finalViewHolder.ivZhuanFa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //转发说说
                zhuanFaShuoShuo(position);
            }
        });

        return convertView;
    }

    /**
     * 转发一条说说
     *
     * @param position
     */
    public void zhuanFaShuoShuo(final int position) {

        //显示一个popupwindow让用户输入转发附带的内容
        //popupwindow布局
        View contentView = layoutInflater.inflate(R.layout.pop_write_pinglun, null);

        //创建一个popupwindow
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        //设置监听事件
        final EditText etPingLun = (EditText) contentView.findViewById(R.id.etPingLun);
        TextView tvPingLun = (TextView) contentView.findViewById(R.id.tvPingLun);
        etPingLun.setHint("转发前说点啥呗...");
        tvPingLun.setText("转发");

        tvPingLun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //获取评论输入框内容
                String pingLunContent = etPingLun.getText().toString();
                if (pingLunContent.equals("")) {
                    Toast.makeText(context, "请输入...", Toast.LENGTH_SHORT).show();
                } else {
                    //转发一条说说
                    //创建一个说说对象
                    final ShuoShuo shuoshuo = new ShuoShuo();
                    //设置属性
                    //用户id
                    SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                    String currentUserObjectID = sharedPreferences.getString("objectID", null);
                    shuoshuo.setUserObjectID(currentUserObjectID);
                    //日期
                    //获取当前时间
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                    final String date = sDateFormat.format(new java.util.Date());
                    shuoshuo.setDate(date);
                    //转发附带内容
                    shuoshuo.setContent(pingLunContent);
                    //转发的说说的原文（这里写成“@原作者昵称：原说说内容”的形式）
                    shuoshuo.setZhuanFaContent("@" + userNames[position] + " : " + mList.get(position).getContent());
                    //给标识符赋值，说明这是转发的说说
                    shuoshuo.setZhaunFaOrNot(true);
                    //发布
                    shuoshuo.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "转发成功", Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                            //刷新列表
                            mList.add(shuoshuo);
                            initShuZu(mList.size());
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(context, "转发失败:" + i + "/" + s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //在底部显示
        View rootView = layoutInflater.inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 写评论
     */
    public void writePingLun(final String shuoShuoObjectID, final int position, final ListView lvPingLun, final TextView tvPingLunCount) {

        //popupwindow布局
        View contentView = layoutInflater.inflate(R.layout.pop_write_pinglun, null);

        //创建一个popupwindow
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        //设置监听事件
        final EditText etPingLun = (EditText) contentView.findViewById(R.id.etPingLun);
        TextView tvPingLun = (TextView) contentView.findViewById(R.id.tvPingLun);

        tvPingLun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取评论输入框内容
                String pingLunContent = etPingLun.getText().toString();
                if (pingLunContent.equals("")) {
                    Toast.makeText(context, "请输入...", Toast.LENGTH_SHORT).show();
                } else {
                    //发布一条评论
                    addPingLun(pingLunContent, shuoShuoObjectID, position, lvPingLun, tvPingLunCount);
                }
            }
        });

        //在底部显示
        View rootView = layoutInflater.inflate(R.layout.activity_main, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 为说说添加一条评论
     */
    public void addPingLun(final String content, final String shuoShuoObjectID, final int position, final ListView lvPingLun, final TextView tvPingLunCount) {

        //创建评论对象
        final PingLun pingLun = new PingLun();

        //设置属性

        //说说id
        pingLun.setArticleObjectID(shuoShuoObjectID);

        //评论用户的id（发布评论的是当前用户，即当前用户id）
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID", null);
        pingLun.setUserObjectID(currentUserObjectID);

        //评论内容
        pingLun.setPingLunContent(content);

        //评论日期（即当前日期）
        //获取当前时间
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        pingLun.setDate(date);

        //发布评论
        pingLun.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                //隐藏popupWindow
                popupWindow.dismiss();
                Toast.makeText(context, "评论发布成功", Toast.LENGTH_SHORT).show();
                //刷新评论列表
                pingLunContainer[position].add(pingLun);
                PingLunOfShuoShuoAdapter adapter = new PingLunOfShuoShuoAdapter(context);
                adapter.addList(pingLunContainer[position]);
                lvPingLun.setAdapter(adapter);
                //计算item中listview的高度
                setListViewHeightBasedOnChildren(lvPingLun);
                //更新说说评论数（+1）
                //1、界面显示
                int newPingLunCount = Integer.valueOf(tvPingLunCount.getText().toString()) + 1;
                tvPingLunCount.setText(newPingLunCount + "");
                //2、数据库更新
                ShuoShuo shuoShuo = new ShuoShuo();
                shuoShuo.setDiscussCount(newPingLunCount);
                shuoShuo.setObjectId(shuoShuoObjectID);
                shuoShuo.update(context);
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(context, "评论发布失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 删除一条说说
     *
     * @param shuoShuo
     */
    public void deleteOrNot(final ShuoShuo shuoShuo) {

        //创建一个Dialog
        final MyDialogYesOrNo dialog = new MyDialogYesOrNo(context);

        dialog.setTitle("是否删除该说说");

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
                shuoShuo.delete(context, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //重新获取数据并刷新列表
                        getDate();
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
     * 获取数据
     */
    public void getDate() {

        //创建查询
        BmobQuery<ShuoShuo> query = new BmobQuery<>();

        //查询所有说说
        query.findObjects(context, new FindListener<ShuoShuo>() {
            @Override
            public void onSuccess(List<ShuoShuo> list) {
                if (list.size() > 0) {
                    //得到数据集合
                    addList(list);
                }
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
         * 日期
         */
        TextView tvDate;
        /**
         * 说说内容
         */
        TextView tvContent;
        /**
         * 转发内容
         */
        TextView tvZhuanFaContent;
        /**
         * 删除按钮
         */
        ImageView ivDelete;
        /**
         * 点赞图标
         */
        CheckBox cbZan;
        /**
         * 点赞数
         */
        TextView tvDianZanCount;
        /**
         * 评论图标
         */
        ImageView ivPingLun;
        /**
         * 评论数
         */
        TextView tvPingLunCount;
        /**
         * 转发图标
         */
        ImageView ivZhuanFa;
        /**
         * 评论列表
         */
        ListView lvPingLun;
    }

    /**
     * 查询点赞记录
     *
     * @param shuoshuoID
     * @param flag:1、查询后设置样式；2、查询后删除
     * @param checkBox:点赞图标
     */
    public void findOrDeleteZan(String shuoshuoID, final int flag, final CheckBox checkBox, final TextView tvDianZan, final int position) {

        //使用复合查询，找到对应的点赞记录

        //获取当前用户id
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID", null);

        //创建查询
        //一、用户id
        BmobQuery<Zan> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("userObjectID", currentUserObjectID);
        //二、被赞对象id
        BmobQuery<Zan> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("id", shuoshuoID);

        //组合查询
        List<BmobQuery<Zan>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<Zan> query = new BmobQuery<>();
        query.and(queries);

        //查询
        query.findObjects(context, new FindListener<Zan>() {
            @Override
            public void onSuccess(List<Zan> list) {
                if (list.size() > 0) {
                    //得到该条点赞记录
                    Zan currentZan = list.get(0);
                    //根据flag判断是单纯查询还是删除操作
                    if (flag == 1) {
                        statusOfDianzan[position] = 2;  //有点赞记录，保存该条item点赞状态到数组
                        checkBox.setChecked(true);
                    } else if (flag == 2) {
                        currentZan.delete(context); //传过来2，删除查到后的记录
                        int newCount = Integer.valueOf(tvDianZan.getText().toString()) - 1;
                        tvDianZan.setText(newCount + "");
                        //更新文章的点赞数-1
                        updateArticleDianZanCount(currentZan.getId(), "delete");
                    } else {
                    }
                } else {
                    //无数据，即用户对该文章未点赞
                    statusOfDianzan[position] = 1;//无点赞记录，保存该条item点赞状态到数组
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 增加一条点赞记录
     *
     * @param shuoshuoID
     */
    public void addZan(final String shuoshuoID, final TextView tvDianZan, final int position) {

        //获取当前用户id
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID", null);

        //创建一个点赞记录实例
        Zan zan = new Zan();

        //设置属性
        zan.setUserObjectID(currentUserObjectID);
        zan.setId(shuoshuoID);

        //上传
        zan.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                int newCount = Integer.valueOf(tvDianZan.getText().toString()) + 1;
                tvDianZan.setText(newCount + "");
                //更新该说说数据库中的点赞数
                updateArticleDianZanCount(shuoshuoID, "add");
            }

            @Override
            public void onFailure(int i, String s) {
                //Toast.makeText(context,"点赞失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 更新说说点赞数
     *
     * @param shuoshuoID
     */
    public void updateArticleDianZanCount(final String shuoshuoID, final String operation) {

        //创建一个查询对象
        BmobQuery<ShuoShuo> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", shuoshuoID);

        //查询
        query.findObjects(context, new FindListener<ShuoShuo>() {
            @Override
            public void onSuccess(List<ShuoShuo> list) {
                if (list.size() > 0) {

                    //得到该说说实例
                    ShuoShuo shuoShuo = list.get(0);

                    int newDianZanCount = 0;
                    //判断是+1还是-1
                    if (operation.equals("add")) {
                        //点赞记录+1
                        newDianZanCount = shuoShuo.getDianZanCount() + 1;
                    } else if (operation.equals("delete")) {
                        //点赞数-1
                        newDianZanCount = shuoShuo.getDianZanCount() - 1;
                    } else {
                    }

                    //设置点赞数
                    shuoShuo.setDianZanCount(newDianZanCount);

                    //更新
                    shuoShuo.update(context);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 计算scrollview中listview高度
     *
     * @param listView
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight

                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);

    }

}
