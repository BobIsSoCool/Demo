package com.evecom.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.activity.R;
import com.evecom.bean.Article;
import com.evecom.bean.ArticleTypeFive;
import com.evecom.bean.ArticleTypeFour;
import com.evecom.bean.ArticleTypeOne;
import com.evecom.bean.ArticleTypeSix;
import com.evecom.bean.ArticleTypeThree;
import com.evecom.bean.ArticleTypeTwo;
import com.evecom.bean.MyUser;
import com.evecom.bean.Zan;
import com.evecom.myview.MyCircleImage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 第一类文章适配器
 * Created by wub on 2017/3/31.
 */
public class ArticleTypeAdapter extends BaseAdapter {

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
    private List<ArticleTypeOne> mList1 = new ArrayList<>();
    private List<ArticleTypeTwo> mList2 = new ArrayList<>();
    private List<ArticleTypeThree> mList3 = new ArrayList<>();
    private List<ArticleTypeFour> mList4 = new ArrayList<>();
    private List<ArticleTypeFive> mList5 = new ArrayList<>();
    private List<ArticleTypeSix> mList6 = new ArrayList<>();

    /**
     * size
     */
    private int size = 0;
    /**
     * type
     */
    private int type = 0;
    /**
     * 用户id
     */
    private String userObjectID = "";
    /**
     * 日期
     */
    private String date = "";
    /**
     * 标题
     */
    private String articleTitle = "";
    /**
     * 封面图片
     */
    private String faceImageUrl = "";
    /**
     * 点赞记录实例
     */
    private Zan currentZan = new Zan();
    /**
     * 点赞数
     */
    private int dianCount = 0;
    /**
     * 评论数
     */
    private int discussCount = 0;
    /**
     * 用于记录item点赞数
     */
    private int[] dianZanCounts;
    /**
     * 用于保存每篇文章对应的头像url
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
     * @param context
     */
    public ArticleTypeAdapter(Context context) {

        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    /**
     * 添加数据
     */
    public void addListTypeOne(List<ArticleTypeOne> list) {
        mList1 = list;
        size = list.size();
        type = 1;

        //初始化用于存储文章一些信息的数组
        initShuZu(list.size());

    }

    public void addListTypeTwo(List<ArticleTypeTwo> list) {
        mList2 = list;
        size = list.size();
        type = 2;

        //初始化用于存储文章一些信息的数组
        initShuZu(list.size());
    }

    public void addListTypeThree(List<ArticleTypeThree> list) {
        mList3 = list;
        size = list.size();
        type = 3;

        //初始化用于存储文章一些信息的数组
        initShuZu(list.size());
    }

    public void addListTypeFour(List<ArticleTypeFour> list) {
        mList4 = list;
        size = list.size();
        type = 4;

        //初始化用于存储文章一些信息的数组
        initShuZu(list.size());
    }

    public void addListTypeFive(List<ArticleTypeFive> list) {
        mList5 = list;
        size = list.size();
        type = 5;

        //初始化用于存储文章一些信息的数组
        initShuZu(list.size());
    }

    public void addListTypeSix(List<ArticleTypeSix> list) {
        mList6 = list;
        size = list.size();
        type = 6;

        //初始化用于存储文章一些信息的数组
        initShuZu(list.size());
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        switch (type) {
            case 1:
                return mList1.get(position);
            case 2:
                return mList2.get(position);
            case 3:
                return mList3.get(position);
            case 4:
                return mList4.get(position);
            case 5:
                return mList5.get(position);
            case 6:
                return mList6.get(position);
        }
        return null;
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
            convertView = layoutInflater.inflate(R.layout.listview_item, null);
            //控件
            viewHolder.ivAvatar = (MyCircleImage) convertView.findViewById(R.id.ivAvatar);
            viewHolder.tvAuthorName = (TextView) convertView.findViewById(R.id.tvAuthorName);
            viewHolder.tvPublishDate = (TextView) convertView.findViewById(R.id.tvPublishDate);
            viewHolder.tvArticleTitle = (TextView) convertView.findViewById(R.id.tvArticleTitle);
            viewHolder.tvDianZan = (TextView) convertView.findViewById(R.id.tvDianZanCount);
            viewHolder.tvPingLun = (TextView) convertView.findViewById(R.id.tvPingLun);
            viewHolder.ivFaceImage = (ImageView) convertView.findViewById(R.id.ivFaceImage);
            viewHolder.cbZan = (CheckBox) convertView.findViewById(R.id.cbZan);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        String articlrID = "";
        //获取当前文章对象（）
        switch (type) {
            case 1:
                ArticleTypeOne articleTypeOne = mList1.get(position);
                userObjectID = articleTypeOne.getUserObjectID();
                date = articleTypeOne.getPublishDate();
                articleTitle = articleTypeOne.getArticleTitle();
                faceImageUrl = articleTypeOne.getFaceImageUrl();
                articlrID = articleTypeOne.getArticleObjectID();
                dianCount = articleTypeOne.getLikeNumber();
                discussCount = articleTypeOne.getDiscussNumber();

                dianZanCounts[position] = articleTypeOne.getLikeNumber();
                break;
            case 2:
                ArticleTypeTwo articleTypeTwo = mList2.get(position);
                userObjectID = articleTypeTwo.getUserObjectID();
                date = articleTypeTwo.getPublishDate();
                articleTitle = articleTypeTwo.getArticleTitle();
                faceImageUrl = articleTypeTwo.getFaceImageUrl();
                articlrID = articleTypeTwo.getArticleObjectID();
                dianCount = articleTypeTwo.getLikeNumber();
                discussCount = articleTypeTwo.getDiscussNumber();

                dianZanCounts[position] = articleTypeTwo.getLikeNumber();
                break;
            case 3:
                ArticleTypeThree articleTypeThree = mList3.get(position);
                userObjectID = articleTypeThree.getUserObjectID();
                date = articleTypeThree.getPublishDate();
                articleTitle = articleTypeThree.getArticleTitle();
                faceImageUrl = articleTypeThree.getFaceImageUrl();
                articlrID = articleTypeThree.getArticleObjectID();
                dianCount = articleTypeThree.getLikeNumber();
                discussCount = articleTypeThree.getDiscussNumber();

                dianZanCounts[position] = articleTypeThree.getLikeNumber();
                break;
            case 4:
                ArticleTypeFour articleTypeFour = mList4.get(position);
                userObjectID = articleTypeFour.getUserObjectID();
                date = articleTypeFour.getPublishDate();
                articleTitle = articleTypeFour.getArticleTitle();
                faceImageUrl = articleTypeFour.getFaceImageUrl();
                articlrID = articleTypeFour.getArticleObjectID();
                dianCount = articleTypeFour.getLikeNumber();
                discussCount = articleTypeFour.getDiscussNumber();

                dianZanCounts[position] = articleTypeFour.getLikeNumber();
                break;
            case 5:
                ArticleTypeFive articleTypeFive = mList5.get(position);
                userObjectID = articleTypeFive.getUserObjectID();
                date = articleTypeFive.getPublishDate();
                articleTitle = articleTypeFive.getArticleTitle();
                faceImageUrl = articleTypeFive.getFaceImageUrl();
                articlrID = articleTypeFive.getArticleObjectID();
                dianCount = articleTypeFive.getLikeNumber();
                discussCount = articleTypeFive.getDiscussNumber();

                dianZanCounts[position] = articleTypeFive.getLikeNumber();
                break;
            case 6:
                ArticleTypeSix articleTypeSix = mList6.get(position);
                userObjectID = articleTypeSix.getUserObjectID();
                date = articleTypeSix.getPublishDate();
                articleTitle = articleTypeSix.getArticleTitle();
                faceImageUrl = articleTypeSix.getFaceImageUrl();
                articlrID = articleTypeSix.getArticleObjectID();
                dianCount = articleTypeSix.getLikeNumber();
                discussCount = articleTypeSix.getDiscussNumber();

                dianZanCounts[position] = articleTypeSix.getLikeNumber();
                break;

        }

        //填充数据
        final ViewHolder finalViewHolder = viewHolder;

        //判断数组中是否已经保存了用户头像、昵称
        //没有再去数据库查询
        if (avatarUrls[position].equals("") || userNames[position].equals("")) {
            //查询用户
            BmobQuery<MyUser> query = new BmobQuery<>();
            query.addWhereEqualTo("objectId", userObjectID);
            query.findObjects(context, new FindListener<MyUser>() {
                @Override
                public void onSuccess(List<MyUser> list) {
                    if (list.size() > 0) {
                        //得到用户
                        MyUser user = list.get(0);
                        //头像url
                        String avatarUrl = user.getAvatar();
                        //昵称
                        String userName = user.getUsername();
                        //保存信息到数组
                        avatarUrls[position] = avatarUrl;
                        userNames[position] = userName;
                        //显示
                        //昵称
                        finalViewHolder.tvAuthorName.setText(userName);
                        //头像
                        if (avatarUrl != null && !avatarUrl.equals("default")) {
                            Glide.with(context).load(avatarUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    finalViewHolder.ivAvatar.setImageBitmap(resource);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(int i, String s) {
                }
            });
        } else {

            //数组中已经保存了信息，直接显示，不用重复查询
            //头像
            finalViewHolder.tvAuthorName.setText(userNames[position]);
            //昵称
            Glide.with(context).load(avatarUrls[position]).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    finalViewHolder.ivAvatar.setImageBitmap(resource);
                }
            });

        }
        //日期
        viewHolder.tvPublishDate.setText(date);

        //标题
        viewHolder.tvArticleTitle.setText(articleTitle);

        //封面
        if (faceImageUrl == "") {
            //无封面图片，则隐藏封面
            finalViewHolder.ivFaceImage.setVisibility(View.INVISIBLE);
        } else {
            //显示封面
            Glide.with(context).load(faceImageUrl).centerCrop().into(finalViewHolder.ivFaceImage);
        }

        //点赞数
        finalViewHolder.tvDianZan.setText(dianCount + "");

        //评论数
        finalViewHolder.tvPingLun.setText(discussCount + "");

        //为点赞图标设置监听
        final String finalArticlrID = articlrID;
        finalViewHolder.cbZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalViewHolder.cbZan.isChecked()) {
                    //增加一条点赞记录
                    addZan(finalArticlrID, finalViewHolder.tvDianZan, position);
                } else {
                    //删除一条点赞记录
                    findOrDeleteZan(finalArticlrID, 2, finalViewHolder.cbZan, finalViewHolder.tvDianZan, position);
                }
            }
        });

        //设置点赞图标样式
        //先判断保存点赞图标状态的数组中是否有已经保存了状态码（初始为0，为1或者2说明保存了状态）
        int currentStatu = statusOfDianzan[position];
        if (currentStatu == 1) {
            //未选中
            finalViewHolder.cbZan.setChecked(false);
        } else if (currentStatu == 2) {
            //选中
            finalViewHolder.cbZan.setChecked(true);
        } else if (currentStatu == 0){
            //状态吗为初始值0，说明为查询过该文章的点赞记录
            //查询，然后保存状态吗到数组
            findOrDeleteZan(finalArticlrID, 1, finalViewHolder.cbZan, finalViewHolder.tvDianZan, position);
        }


        return convertView;
    }

    /**
     * 增加一条点赞记录
     *
     * @param articleID
     */
    public void addZan(final String articleID, final TextView tvDianZan, final int position) {

        //获取当前用户id
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID", null);

        //创建一个点赞记录实例
        Zan zan = new Zan();

        //设置属性
        zan.setUserObjectID(currentUserObjectID);
        zan.setId(articleID);

        //上传
        zan.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                int newCount = Integer.valueOf(tvDianZan.getText().toString());
                tvDianZan.setText(newCount + 1 + "");
                //更新该文章数据（点赞数加1）
                updateArticleDianZanCount(articleID, "add");
            }

            @Override
            public void onFailure(int i, String s) {
                //Toast.makeText(context,"点赞失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 查询点赞记录
     *
     * @param articleID
     * @param flag:1、查询后设置样式；2、查询后删除
     * @param checkBox:点赞图标
     */
    public void findOrDeleteZan(String articleID, final int flag, final CheckBox checkBox, final TextView tvDianZan, final int position) {

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
        query2.addWhereEqualTo("id", articleID);

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
                    currentZan = list.get(0);
                    //根据flag判断是单纯查询还是删除操作
                    if (flag == 1) {
                        statusOfDianzan[position] = 2;  //有点赞记录，保存该条item点赞状态到数组
                        checkBox.setChecked(true);
                    } else if (flag == 2) {
                        currentZan.delete(context); //传过来2，删除查到后的记录
                        int newCount = Integer.valueOf(tvDianZan.getText().toString());
                        tvDianZan.setText(newCount - 1 + "");
                        //更新文章的点赞数-1
                        updateArticleDianZanCount(currentZan.getId(), "delete");
                        checkBox.setChecked(false);
                    } else {
                    }
                }else {
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
     * 更新文章点赞数
     *
     * @param articleID
     */
    public void updateArticleDianZanCount(final String articleID, final String operation) {

        //总表
        BmobQuery<Article> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", articleID);

        //查询
        query.findObjects(context, new FindListener<Article>() {
            @Override
            public void onSuccess(List<Article> list) {
                if (list.size() > 0) {

                    //总表
                    Article article = new Article();
                    String objectID = list.get(0).getObjectId();
                    int newDianZanCount = 0;

                    //判断是+1还是-1
                    if (operation.equals("add")) {
                        //点赞记录+1
                        newDianZanCount = list.get(0).getLikeNumber() + 1;
                    } else if (operation.equals("delete")) {
                        //点赞数-1
                        newDianZanCount = list.get(0).getLikeNumber() - 1;
                    } else {
                    }

                    //设置点赞数
                    article.setLikeNumber(newDianZanCount);

                    //根据objectID更新
                    article.setObjectId(objectID);
                    article.update(context);

                    //分表
                    updateArticleDianZanCountByType(articleID, newDianZanCount);

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 更新分表里文章的点赞数
     *
     * @param articleID
     * @param newDianZanCount ：点赞数新值
     */
    public void updateArticleDianZanCountByType(String articleID, final int newDianZanCount) {

        //根据文章类型，去对应表里查询文章
        switch (type) {

            case 1:
                BmobQuery<ArticleTypeOne> query = new BmobQuery<>();
                query.addWhereEqualTo("articleObjectID", articleID);
                query.findObjects(context, new FindListener<ArticleTypeOne>() {
                    @Override
                    public void onSuccess(List<ArticleTypeOne> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeOne articleTypeOne = list.get(0);
                            //设置新的点赞数
                            articleTypeOne.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeOne.update(context);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 2:
                BmobQuery<ArticleTypeTwo> query2 = new BmobQuery<>();
                query2.addWhereEqualTo("articleObjectID", articleID);
                query2.findObjects(context, new FindListener<ArticleTypeTwo>() {
                    @Override
                    public void onSuccess(List<ArticleTypeTwo> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeTwo articleTypeTwo = list.get(0);
                            //设置新的点赞数
                            articleTypeTwo.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeTwo.update(context);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 3:
                BmobQuery<ArticleTypeThree> query3 = new BmobQuery<>();
                query3.addWhereEqualTo("articleObjectID", articleID);
                query3.findObjects(context, new FindListener<ArticleTypeThree>() {
                    @Override
                    public void onSuccess(List<ArticleTypeThree> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeThree articleTypeThree = list.get(0);
                            //设置新的点赞数
                            articleTypeThree.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeThree.update(context);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 4:
                BmobQuery<ArticleTypeFour> query4 = new BmobQuery<>();
                query4.addWhereEqualTo("articleObjectID", articleID);
                query4.findObjects(context, new FindListener<ArticleTypeFour>() {
                    @Override
                    public void onSuccess(List<ArticleTypeFour> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeFour articleTypeFour = list.get(0);
                            //设置新的点赞数
                            articleTypeFour.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeFour.update(context);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 5:
                BmobQuery<ArticleTypeFive> query5 = new BmobQuery<>();
                query5.addWhereEqualTo("articleObjectID", articleID);
                query5.findObjects(context, new FindListener<ArticleTypeFive>() {
                    @Override
                    public void onSuccess(List<ArticleTypeFive> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeFive articleTypeFive = list.get(0);
                            //设置新的点赞数
                            articleTypeFive.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeFive.update(context);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 6:
                BmobQuery<ArticleTypeSix> query6 = new BmobQuery<>();
                query6.addWhereEqualTo("articleObjectID", articleID);
                query6.findObjects(context, new FindListener<ArticleTypeSix>() {
                    @Override
                    public void onSuccess(List<ArticleTypeSix> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeSix articleTypeSix = list.get(0);
                            //设置新的点赞数
                            articleTypeSix.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeSix.update(context);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;

        }

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
         * 日期
         */
        TextView tvPublishDate;
        /**
         * 标题
         */
        TextView tvArticleTitle;
        /**
         * 封面图片
         */
        ImageView ivFaceImage;
        /**
         * 点赞数
         */
        TextView tvDianZan;
        /**
         * 评论数
         */
        TextView tvPingLun;
        /**
         * 点赞图标
         */
        CheckBox cbZan;
    }

    /**
     * 初始化用于存储文章一些信息的数组
     *
     * @param size
     */
    public void initShuZu(int size) {

        avatarUrls = new String[size];
        userNames = new String[size];
        dianZanCounts = new int[size];
        statusOfDianzan = new int[size];

        //给初始值
        for (int i = 0; i < size; i++) {
            avatarUrls[i] = "";
            userNames[i] = "";
            statusOfDianzan[i] = 0;     //点赞图标状态码，0为初始状态，1为未选中，2为选中
        }

    }

}


