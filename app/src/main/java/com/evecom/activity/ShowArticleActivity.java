package com.evecom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.adapter.PingLunAdapter;
import com.evecom.bean.Article;
import com.evecom.bean.ArticleTypeFive;
import com.evecom.bean.ArticleTypeFour;
import com.evecom.bean.ArticleTypeOne;
import com.evecom.bean.ArticleTypeSix;
import com.evecom.bean.ArticleTypeThree;
import com.evecom.bean.ArticleTypeTwo;
import com.evecom.bean.FollowedUser;
import com.evecom.bean.MyCollection;
import com.evecom.bean.MyUser;
import com.evecom.bean.PingLun;
import com.evecom.bean.Zan;
import com.evecom.myview.MyCircleImage;
import com.evecom.myview.MyDialogYesOrNo;
import com.evecom.utils.MyStringUtils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.sendtion.xrichtext.RichTextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Bob on 2017/3/6.
 * 显示文章的页面
 */

public class ShowArticleActivity extends Activity {

    /**
     * 文章顶部的头像
     */
    MyCircleImage ivAvatar;
    private String avatarUrl = "";
    /**
     * 文章顶部的日期
     */
    TextView tvDate;
    private String date = "yy-mm-dd";
    /**
     * 显示图文的富文本控件
     */
    RichTextView richTextView;

    /**
     * 文章
     */
    Article article;
    /**
     * 富文本的内容集合
     */
    List<String> textList = null;
    /**
     * index
     */
    private int index = 0;
    /**
     *
     */
    String imgUrl;
    /**
     * 点赞数图标
     */
    ImageView ivDianZan;
    private int dianzan = 0;
    /**
     * 评论数图标
     */
    ImageView ivPingLun;
    private int pinglun = 0;
    /**
     * 返回图标
     */
    ImageView ivBack;
    /**
     * 作者
     */
    TextView tvAuthorName1, tvAuthorName2;
    private String authorName = "xxx";
    private String authorObjectID = "";
    /**
     * 当前用户
     */
    private String currentUserName;
    /**
     * 文章标题
     */
    TextView tvArticleTitle;
    private String articleTitle = "xxxx";
    /**
     * 收藏
     */
    CheckBox cbCollect;
    /**
     * 关注按钮（若当前用户和作者是同一个人，则隐藏）
     */
    TextView tvGuanZhu;
    /**
     * 文章的作者
     */
    MyUser author;
    /**
     * 富文本内容
     */
    private String articleContent = "";
    /**
     *
     */
    private SharedPreferences sharedPreferences;
    /**
     *
     */
    RelativeLayout rlArticle;
    /**
     *
     */
    SpinKitView duringLoadingView;
    /**
     * 当前文章关注
     */
    private FollowedUser followedUser;
    /**
     * 文章在总表的id
     */
    private String articleObjectID = "";
    /**
     * 点击写评论
     */
    TextView tvWritePingLun;
    /**
     * 评论列表
     */
    ListView lvPingLun;
    /**
     * 评论适配器
     */
    private PingLunAdapter adapter;
    /**
     * rootView
     */
    View rootView;
    /**
     * PopupWindow
     */
    private PopupWindow popupWindow;
    /**
     * 文章类型
     */
    private int type;
    /**
     * 显示点赞数
     */
    TextView tvDianZanCount;
    /**
     * 点赞图标
     */
    CheckBox cbDianZan;
    /**
     * 显示评论数
     */
    TextView tvPingLunCount;
    /**
     * 无评论时显示
     */
    TextView tvNoData;
    /**
     * 点击隐藏底部布局
     */
    ImageView ivHide;
    /**
     * 点击显示底部布局
     */
    ImageView ivShow;
    /**
     * 底部右侧布局
     */
    RelativeLayout rlRight;
    /**
     * scroll_view
     */
    ScrollView scroll_view;


    /**
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showarticle);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }

        rootView = LayoutInflater.from(ShowArticleActivity.this).inflate(R.layout.activity_showarticle, null);

        //sharedPreferences
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        //初始化控件
        initUI();

        //获取文章
        getArticle();


    }

    /**
     * 初始化控件
     */
    public void initUI() {

        scroll_view = (ScrollView) findViewById(R.id.scroll_view);

        ivAvatar = (MyCircleImage) findViewById(R.id.ivAvatar2);
        //让头像获取焦点，防止scrollview直接显示底部
        ivAvatar.setFocusable(true);
        ivAvatar.setFocusableInTouchMode(true);
        ivAvatar.requestFocus();

        tvDate = (TextView) findViewById(R.id.tvDate);
        cbCollect = (CheckBox) findViewById(R.id.cbCollect);
        richTextView = (RichTextView) findViewById(R.id.richTextView);

        ivDianZan = (ImageView) findViewById(R.id.ivDianZan);
        ivPingLun = (ImageView) findViewById(R.id.ivPingLun);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        tvAuthorName1 = (TextView) findViewById(R.id.tvAuthorName1);
        tvAuthorName2 = (TextView) findViewById(R.id.tvAuthorName2);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvGuanZhu = (TextView) findViewById(R.id.tvGuanZhu);
        tvArticleTitle = (TextView) findViewById(R.id.tvArticleTitle);

        tvWritePingLun = (TextView) findViewById(R.id.tvWritePingLun);
        lvPingLun = (ListView) findViewById(R.id.lvPingLun);
        tvDianZanCount = (TextView) findViewById(R.id.tvDianZanCount);
        tvPingLunCount = (TextView) findViewById(R.id.tvPingLunCount);

        //
        rlArticle = (RelativeLayout) findViewById(R.id.rlArticle);
        duringLoadingView = (SpinKitView) findViewById(R.id.duringLoadingView);
        //先让文章布局不可见、显示加载view（加载完信息在显示）
        rlArticle.setVisibility(View.INVISIBLE);
        duringLoadingView.setVisibility(View.VISIBLE);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        cbDianZan = (CheckBox) findViewById(R.id.cbDianZan);

        ivHide = (ImageView) findViewById(R.id.ivHide);
        ivShow = (ImageView) findViewById(R.id.ivShow);
        rlRight = (RelativeLayout) findViewById(R.id.rlRight);
        //点击以藏底部布局
        ivHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivHide.setVisibility(View.GONE);
                tvWritePingLun.setVisibility(View.GONE);
                rlRight.setVisibility(View.GONE);

                ivShow.setVisibility(View.VISIBLE);
            }
        });
        //点击显示底部布局
        ivShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivHide.setVisibility(View.VISIBLE);
                tvWritePingLun.setVisibility(View.VISIBLE);
                rlRight.setVisibility(View.VISIBLE);

                ivShow.setVisibility(View.GONE);
            }
        });

        //点击回到上个页面
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击关注或取消关注
        tvGuanZhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取关注按钮文本
                String followOrNot = tvGuanZhu.getText().toString();
                if (followOrNot.equals("关注")) {           //文本显示为“关注”，即还未关注，点击之后添加到关注
                    //添加该作者到关注
                    addToFollowTable(authorObjectID);
                } else if (followOrNot.equals("已关注")) {   //文本显示为“已关注”，点击之后让用户确认是否取消关注
                    //弹出一个Dialog，确认是否取消关注
                    cancelFollow();
                }
            }
        });

        //点击收藏或取消收藏
        cbCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbCollect.isChecked()) {
                    addCollection();
                } else {
                    removeCollection();
                }
            }
        });

        //点击写评论
        tvWritePingLun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writePingLun();
            }
        });

        //点赞图标点击事件
        cbDianZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbDianZan.isChecked()) {
                    //增加一条点赞记录
                    addDianZan();
                }else {
                    //删除一条点赞记录
                    findOrDetelteRecoderOfDianZan(articleObjectID,"delete");
                }
            }
        });


    }

    /**
     * 获取文章
     */
    public void getArticle() {

        //判断是展示那类文章（个人文章、6种类型文章）
        Bundle bundle = getIntent().getExtras();
        String articleType = bundle.getString("activity");
        switch (articleType) {

            //展示个人文章
            case "0":
                type = 0;
                Article article = (Article) bundle.getSerializable("article");
                articleObjectID = article.getObjectId();
                avatarUrl = sharedPreferences.getString("avatarUrl", null);
                authorName = sharedPreferences.getString("userName", null);
                authorObjectID = sharedPreferences.getString("objectID", null);
                date = article.getPublishDate();
                articleTitle = article.getArticleTitle();
                articleContent = article.getArticleCotent();
                dianzan = article.getLikeNumber();
                pinglun = article.getDiscussNumber();
                //展示文章
                showArticle();
                break;

            //展示类型1文章
            case "1":
                type = 1;
                ArticleTypeOne articleTypeOne = (ArticleTypeOne) bundle.getSerializable("article");
                articleObjectID = articleTypeOne.getArticleObjectID();
                date = articleTypeOne.getPublishDate();
                articleTitle = articleTypeOne.getArticleTitle();
                articleContent = articleTypeOne.getArticleCotent();
                dianzan = articleTypeOne.getLikeNumber();
                pinglun = articleTypeOne.getDiscussNumber();
                //根据文章中用户的id去用户表获取用户名（文章表不直接存入用户名是因为用户名可能修改）
                authorObjectID = articleTypeOne.getUserObjectID();
                getUserName(articleTypeOne.getUserObjectID());
                break;

            //展示类型2文章
            case "2":
                type = 2;
                ArticleTypeTwo articleTypeTwo = (ArticleTypeTwo) bundle.getSerializable("article");
                articleObjectID = articleTypeTwo.getArticleObjectID();
                date = articleTypeTwo.getPublishDate();
                articleTitle = articleTypeTwo.getArticleTitle();
                articleContent = articleTypeTwo.getArticleCotent();
                dianzan = articleTypeTwo.getLikeNumber();
                pinglun = articleTypeTwo.getDiscussNumber();
                //根据文章中用户的id去用户表获取用户名（文章表不直接存入用户名是因为用户名可能修改）
                authorObjectID = articleTypeTwo.getUserObjectID();
                getUserName(articleTypeTwo.getUserObjectID());
                break;

            //展示类型3文章
            case "3":
                type = 3;
                ArticleTypeThree articleTypeThree = (ArticleTypeThree) bundle.getSerializable("article");
                articleObjectID = articleTypeThree.getArticleObjectID();
                date = articleTypeThree.getPublishDate();
                articleTitle = articleTypeThree.getArticleTitle();
                articleContent = articleTypeThree.getArticleCotent();
                dianzan = articleTypeThree.getLikeNumber();
                pinglun = articleTypeThree.getDiscussNumber();
                //根据文章中用户的id去用户表获取用户名（文章表不直接存入用户名是因为用户名可能修改）
                authorObjectID = articleTypeThree.getUserObjectID();
                getUserName(articleTypeThree.getUserObjectID());
                break;

            //展示类型4文章
            case "4":
                type = 4;
                ArticleTypeFour articleTypeFour = (ArticleTypeFour) bundle.getSerializable("article");
                articleObjectID = articleTypeFour.getArticleObjectID();
                date = articleTypeFour.getPublishDate();
                articleTitle = articleTypeFour.getArticleTitle();
                articleContent = articleTypeFour.getArticleCotent();
                dianzan = articleTypeFour.getLikeNumber();
                pinglun = articleTypeFour.getDiscussNumber();
                //根据文章中用户的id去用户表获取用户名（文章表不直接存入用户名是因为用户名可能修改）
                authorObjectID = articleTypeFour.getUserObjectID();
                getUserName(articleTypeFour.getUserObjectID());
                break;

            //展示类型5文章
            case "5":
                type = 5;
                ArticleTypeFive articleTypeFive = (ArticleTypeFive) bundle.getSerializable("article");
                articleObjectID = articleTypeFive.getArticleObjectID();
                date = articleTypeFive.getPublishDate();
                articleTitle = articleTypeFive.getArticleTitle();
                articleContent = articleTypeFive.getArticleCotent();
                dianzan = articleTypeFive.getLikeNumber();
                pinglun = articleTypeFive.getDiscussNumber();
                //根据文章中用户的id去用户表获取用户名（文章表不直接存入用户名是因为用户名可能修改）
                authorObjectID = articleTypeFive.getUserObjectID();
                getUserName(articleTypeFive.getUserObjectID());
                break;

            //展示类型6文章
            case "6":
                type = 6;
                ArticleTypeSix articleTypeSix = (ArticleTypeSix) bundle.getSerializable("article");
                articleObjectID = articleTypeSix.getArticleObjectID();
                date = articleTypeSix.getPublishDate();
                articleTitle = articleTypeSix.getArticleTitle();
                articleContent = articleTypeSix.getArticleCotent();
                dianzan = articleTypeSix.getLikeNumber();
                pinglun = articleTypeSix.getDiscussNumber();
                //根据文章中用户的id去用户表获取用户名（文章表不直接存入用户名是因为用户名可能修改）
                authorObjectID = articleTypeSix.getUserObjectID();
                getUserName(articleTypeSix.getUserObjectID());
                break;
        }

        //设置点赞数、评论数（不为0时显示数字）
       /* if(article.getLikeNumber()>0){
            BadgeView badgeView = new BadgeView(this,ivDianZan);
            badgeView.setText(article.getLikeNumber());
            badgeView.show();
        }
        if(article.getDiscussNumber()>0){
            BadgeView badgeView = new BadgeView(this,ivPingLun);
            badgeView.setText(article.getDiscussNumber());
            badgeView.show();
        }*/

    }


    /**
     * 展示文章
     */
    public void showArticle() {

        //用户和作者是同一个人，隐藏关注
        String currentUserID = sharedPreferences.getString("objectID", null);
        if (currentUserID.equals(authorObjectID)) {
            tvGuanZhu.setVisibility(View.GONE);
        } else {
            //两者不相等，即作者和用户不是同一人，则去判断用户是否已关注该作者，进而设置关注文本内容
            //复合查询
            //条件1
            BmobQuery<FollowedUser> query1 = new BmobQuery<>();
            query1.addWhereEqualTo("whoCareID", currentUserID);
            //条件2
            BmobQuery<FollowedUser> query2 = new BmobQuery<>();
            query2.addWhereEqualTo("careWhoID", authorObjectID);
            //组合
            List<BmobQuery<FollowedUser>> queries = new ArrayList<>();
            queries.add(query1);
            queries.add(query2);
            //创建复合查询
            BmobQuery<FollowedUser> query = new BmobQuery<>();
            query.and(queries);
            //查询
            query.findObjects(this, new FindListener<FollowedUser>() {
                @Override
                public void onSuccess(List<FollowedUser> list) {
                    if (list.size() > 0) {
                        //有数据，说明用户关注了该作者
                        tvGuanZhu.setText("已关注");
                        //拿到关注表里的该条记录（用于后面需要删除）
                        followedUser = list.get(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    //Toast.makeText(ShowArticleActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //判断用户是否已收藏该文章
        collectedOrNot();
        //判断用户是否点赞过该文章
        findOrDetelteRecoderOfDianZan(articleObjectID,"find");

        //头像
        Glide.with(this).load(avatarUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ivAvatar.setImageBitmap(resource);
            }
        });

        //昵称
        tvAuthorName2.setText(authorName);

        //日期
        tvDate.setText(date);

        //标题
        tvArticleTitle.setText(articleTitle);

        //点赞、评论
        tvPingLunCount.setText(pinglun+"");
        tvDianZanCount.setText(dianzan+"");

        //清除富文本内容
        richTextView.clearAllLayout();

        //获取富文本内容数据集合
        textList = MyStringUtils.cutStringByImgTag(articleContent);

        //展示富文本内容
        showContent(textList.get(index));

        //显示评论
        showPingLun();

    }


    /**
     * 展示富文本内容
     *
     * @param str：文本信息或者图片的url
     */
    public void showContent(String str) {

        //判断是图片的url，则获取图片，并存储到本地
        // 再得到图片本地的路径，根据路径调用压缩图片的方法
        //最后显示图片
        Log.d("tag", "showContent");
        if (str.contains("<img")) {
            Log.d("tag", "showImg");
            imgUrl = MyStringUtils.getImgSrc(str);
            Log.d("tag", "(查看图片的url是否被剥离出来)imgUrl:====" + imgUrl);
            //在相应位置显示图片
            richTextView.addImageViewAtIndex(richTextView.getLastIndex(), imgUrl);
            //saveImgToLocalSdk(imgUrl);
        } else {
            //判断是文本，则直接显示
            richTextView.addTextViewAtIndex(richTextView.getLastIndex(), str);
            Log.d("tag", "text");
        }

        index++;
        //判断是否还有元素
        if (index < textList.size()) {
            //后面还有元素，则继续显示
            showContent(textList.get(index));
        } else {
            //元素加载完，即文章数据加载完，隐藏加载view，显示文章
            duringLoadingView.setVisibility(View.GONE);
            rlArticle.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 根据用户id去用户表获取用户名
     *
     * @param userObjectID
     */
    public void getUserName(String userObjectID) {

        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objectId", userObjectID);
        bmobQuery.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size() > 0) {
                    //获得作者对象
                    MyUser user = list.get(0);
                    //获得文章作者昵称
                    authorName = user.getUsername();
                    //获得作者头像
                    avatarUrl = user.getAvatar();
                    //展示文章
                    showArticle();
                }
            }

            @Override
            public void onError(int i, String s) {
                //Toast.makeText(ShowArticleActivity.this, "查询用户名失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 添加用户到关注
     *
     * @param objectID
     */
    public void addToFollowTable(String objectID) {

        //创建一个实体类
        FollowedUser followedUser = new FollowedUser();

        //设置属性
        String currentUserID = sharedPreferences.getString("objectID", null);
        followedUser.setWhoCareID(currentUserID);
        followedUser.setCareWhoID(objectID);

        //上传到数据库
        followedUser.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                tvGuanZhu.setText("已关注");
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(ShowArticleActivity.this, "添加关注失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 取消关注
     */
    public void cancelFollow() {

        //创建一个Dialog
        final MyDialogYesOrNo dialogYesOrNo = new MyDialogYesOrNo(this);
        //点击事件
        dialogYesOrNo.setNoOnclickListener("算了", new MyDialogYesOrNo.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialogYesOrNo.dismiss();
            }
        });
        dialogYesOrNo.setYesOnclickListener("不再关注", new MyDialogYesOrNo.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                //查询关注记录
                findFollow();
                //将该关注记录从表里删除
                followedUser.delete(ShowArticleActivity.this, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        dialogYesOrNo.dismiss();
                        tvGuanZhu.setText("关注");
                        Toast.makeText(ShowArticleActivity.this, "已取消对该用户的关注", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        dialogYesOrNo.dismiss();
                        Toast.makeText(ShowArticleActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //显示
        dialogYesOrNo.show();
    }


    public void findFollow() {
        //复合查询
        //条件1
        String currentUserID = sharedPreferences.getString("objectID", null);
        BmobQuery<FollowedUser> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("whoCareID", currentUserID);
        //条件2
        BmobQuery<FollowedUser> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("careWhoID", authorObjectID);
        //组合
        List<BmobQuery<FollowedUser>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        //创建复合查询
        BmobQuery<FollowedUser> query = new BmobQuery<>();
        query.and(queries);
        //查询
        query.findObjects(this, new FindListener<FollowedUser>() {
            @Override
            public void onSuccess(List<FollowedUser> list) {
                if (list.size() > 0) {
                    //拿到关注表里的该条记录（用于删除）
                    followedUser = list.get(0);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowArticleActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addCollection() {

        //添加一条收藏记录
        MyCollection myCollection = new MyCollection();

        //设置属性

        //用户id
        String currentUserID = sharedPreferences.getString("objectID", null);
        myCollection.setUserObjectID(currentUserID);

        //文章id
        myCollection.setArticleObjectID(articleObjectID);

        //上传到数据库
        myCollection.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ShowArticleActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(ShowArticleActivity.this, "收藏失败  ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 取消关注
     */
    public void removeCollection() {

        //先用复合查询该收藏，得到objectId
        //条件1
        String currentUserID = sharedPreferences.getString("objectID", null);
        BmobQuery<MyCollection> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("userObjectID", currentUserID);
        //条件2
        BmobQuery<MyCollection> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("articleObjectID", articleObjectID);
        //组合
        List<BmobQuery<MyCollection>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        //创建复合查询
        BmobQuery<MyCollection> query = new BmobQuery<>();
        query.and(queries);
        //查询
        query.findObjects(this, new FindListener<MyCollection>() {
            @Override
            public void onSuccess(List<MyCollection> list) {
                if (list.size() > 0) {
                    //得到该条收藏
                    MyCollection myCollection = list.get(0);
                    //删除该条收藏
                    myCollection.delete(ShowArticleActivity.this, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ShowArticleActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowArticleActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 判断用户是否已收藏当前阅读的文章，进而设置收藏图标样式
     */
    public void collectedOrNot() {


        //利用复合查询，查询收藏表中是否该记录
        BmobQuery<MyCollection> query1 = new BmobQuery<>();

        //条件1
        String currentUserObjectID = sharedPreferences.getString("objectID", null);
        query1.addWhereEqualTo("userObjectID", currentUserObjectID);

        //条件2
        BmobQuery<MyCollection> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("articleObjectID", articleObjectID);

        //组合
        List<BmobQuery<MyCollection>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        BmobQuery<MyCollection> query = new BmobQuery<>();
        query.and(queries);

        //查询
        query.findObjects(this, new FindListener<MyCollection>() {
            @Override
            public void onSuccess(List<MyCollection> list) {
                if (list.size() > 0) {
                    //有记录，说明已收藏
                    cbCollect.setChecked(true);
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 写评论
     */
    public void writePingLun() {

        //popupwindow布局
        View contentView = LayoutInflater.from(ShowArticleActivity.this).inflate(R.layout.pop_write_pinglun, null);

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
                    Toast.makeText(ShowArticleActivity.this, "请输入...", Toast.LENGTH_SHORT).show();
                } else {
                    //发布一条评论
                    addPingLun(pingLunContent);
                }
            }
        });

        //在底部显示
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 添加一条评论
     */
    public void addPingLun(String content) {

        //创建评论对象
        PingLun pingLun = new PingLun();

        //设置属性

        //文章id
        pingLun.setArticleObjectID(articleObjectID);

        //评论用户的id（发布评论的是当前用户，即当前用户id）
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
        pingLun.save(ShowArticleActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                //刷新评论列表
                showPingLun();
                Toast.makeText(ShowArticleActivity.this, "评论发布成功", Toast.LENGTH_SHORT).show();
                //隐藏popupWindow
                popupWindow.dismiss();
                //更新文章评论数（+1）
                updatePingLun("add");
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(ShowArticleActivity.this, "评论发布失败", Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * 显示评论
     */
    public void showPingLun() {

        //根据当前文id查询评论
        String currentArticleID = articleObjectID;

        //创建查询
        BmobQuery<PingLun> query = new BmobQuery<>();

        //添加查询条件
        query.addWhereEqualTo("articleObjectID", currentArticleID);

        //查询
        query.findObjects(this, new FindListener<PingLun>() {
            @Override
            public void onSuccess(List<PingLun> list) {
                if (list.size() > 0) {

                    //该文章有评论
                    //构建适配器
                    adapter = new PingLunAdapter(ShowArticleActivity.this, Glide.with(ShowArticleActivity.this), new PingLunAdapter.DeleteDataOnrefresh() {
                        @Override
                        public void onRefresf() {
                            //Toast.makeText(ShowArticleActivity.this,"接口回调",Toast.LENGTH_SHORT).show();
                            if ((pinglun-1) == 0){
                                tvNoData.setVisibility(View.VISIBLE);
                                lvPingLun.setVisibility(View.GONE);
                            }else {
                                lvPingLun.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.GONE);
                            }
                            //更新数据库评论数
                            updatePingLun("delete");
                        }
                    });
                    lvPingLun.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                    //添加数据
                    adapter.addList(list);
                    //初始化列表
                    lvPingLun.setAdapter(adapter);


                    //高度
                    setListViewHeightBasedOnChildren(lvPingLun);


                }else if (list.size() == 0){
                    tvNoData.setVisibility(View.VISIBLE);
                    lvPingLun.setVisibility(View.GONE);
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

    /**
     * 更新文章评论数
     *
     * @param opration
     */
    public void updatePingLun(String opration) {

        //判断评论数是加1还是减1
        if (opration.equals("add")) {
            pinglun++;
        } else if (opration.equals("delete")) {
            pinglun--;
        } else {
        }

        //创建文章实例
        //总表
        Article article = new Article();
        article.setDiscussNumber(pinglun);
        article.update(this, articleObjectID, new UpdateListener() {
            @Override
            public void onSuccess() {
                //总表更新成功，接着更新分表评论数
                updatePingLunByType();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    /**
     * 更新分表的评论数
     */
    public void updatePingLunByType() {

        //判断分类
        switch (type) {

            case 1:
                final ArticleTypeOne articleTypeOne = new ArticleTypeOne();
                articleTypeOne.setDiscussNumber(pinglun);
                //根据articleObjectID（文章在总表id）查询objectId（在分表中的id）
                BmobQuery<ArticleTypeOne> query1 = new BmobQuery<>();
                query1.addWhereEqualTo("articleObjectID",articleObjectID);
                query1.findObjects(this, new FindListener<ArticleTypeOne>() {
                    @Override
                    public void onSuccess(List<ArticleTypeOne> list) {
                        if (list.size()>0){
                            //找到该文章
                            ArticleTypeOne articleTypeOne1 = list.get(0);
                            //更新评论数值
                            articleTypeOne1.setDiscussNumber(pinglun);
                            articleTypeOne1.update(ShowArticleActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    //更新成功，刷新界面的评论数
                                    tvPingLunCount.setText(pinglun+"");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        }else {}
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 2:
                final ArticleTypeTwo articleTypeTwo = new ArticleTypeTwo();
                articleTypeTwo.setDiscussNumber(pinglun);
                //根据articleObjectID（文章在总表id）查询objectId（在分表中的id）
                BmobQuery<ArticleTypeTwo> query2 = new BmobQuery<>();
                query2.addWhereEqualTo("articleObjectID",articleObjectID);
                query2.findObjects(this, new FindListener<ArticleTypeTwo>() {
                    @Override
                    public void onSuccess(List<ArticleTypeTwo> list) {
                        if (list.size()>0){
                            //找到该文章
                            ArticleTypeTwo articleTypeTwo1 = list.get(0);
                            //更新评论数值
                            articleTypeTwo1.setDiscussNumber(pinglun);
                            articleTypeTwo1.update(ShowArticleActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    //更新成功，刷新界面的评论数
                                    tvPingLunCount.setText(pinglun+"");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        }else {}
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 3:
                final ArticleTypeThree articleTypeThree = new ArticleTypeThree();
                articleTypeThree.setDiscussNumber(pinglun);
                //根据articleObjectID（文章在总表id）查询objectId（在分表中的id）
                BmobQuery<ArticleTypeThree> query3 = new BmobQuery<>();
                query3.addWhereEqualTo("articleObjectID",articleObjectID);
                query3.findObjects(this, new FindListener<ArticleTypeThree>() {
                    @Override
                    public void onSuccess(List<ArticleTypeThree> list) {
                        if (list.size()>0){
                            //找到该文章
                            ArticleTypeThree articleTypeThree1 = list.get(0);
                            //更新评论数值
                            articleTypeThree1.setDiscussNumber(pinglun);
                            articleTypeThree1.update(ShowArticleActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    //更新成功，刷新界面的评论数
                                    tvPingLunCount.setText(pinglun+"");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        }else {}
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 4:
                final ArticleTypeFour articleTypeFour = new ArticleTypeFour();
                articleTypeFour.setDiscussNumber(pinglun);
                //根据articleObjectID（文章在总表id）查询objectId（在分表中的id）
                BmobQuery<ArticleTypeFour> query4 = new BmobQuery<>();
                query4.addWhereEqualTo("articleObjectID",articleObjectID);
                query4.findObjects(this, new FindListener<ArticleTypeFour>() {
                    @Override
                    public void onSuccess(List<ArticleTypeFour> list) {
                        if (list.size()>0){
                            //找到该文章
                            ArticleTypeFour articleTypeFour1 = list.get(0);
                            //更新评论数值
                            articleTypeFour1.setDiscussNumber(pinglun);
                            articleTypeFour1.update(ShowArticleActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    //更新成功，刷新界面的评论数
                                    tvPingLunCount.setText(pinglun+"");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        }else {}
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 5:
                final ArticleTypeFive articleTypeFive = new ArticleTypeFive();
                articleTypeFive.setDiscussNumber(pinglun);
                //根据articleObjectID（文章在总表id）查询objectId（在分表中的id）
                BmobQuery<ArticleTypeFive> query5 = new BmobQuery<>();
                query5.addWhereEqualTo("articleObjectID",articleObjectID);
                query5.findObjects(this, new FindListener<ArticleTypeFive>() {
                    @Override
                    public void onSuccess(List<ArticleTypeFive> list) {
                        if (list.size()>0){
                            //找到该文章
                            ArticleTypeFive articleTypeFive1 = list.get(0);
                            //更新评论数值
                            articleTypeFive1.setDiscussNumber(pinglun);
                            articleTypeFive1.update(ShowArticleActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    //更新成功，刷新界面的评论数
                                    tvPingLunCount.setText(pinglun+"");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        }else {}
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            case 6:
                final ArticleTypeSix articleTypeSix = new ArticleTypeSix();
                articleTypeSix.setDiscussNumber(pinglun);
                //根据articleObjectID（文章在总表id）查询objectId（在分表中的id）
                BmobQuery<ArticleTypeSix> query6 = new BmobQuery<>();
                query6.addWhereEqualTo("articleObjectID",articleObjectID);
                query6.findObjects(this, new FindListener<ArticleTypeSix>() {
                    @Override
                    public void onSuccess(List<ArticleTypeSix> list) {
                        if (list.size()>0){
                            //找到该文章
                            ArticleTypeSix articleTypeSix1 = list.get(0);
                            //更新评论数值
                            articleTypeSix1.setDiscussNumber(pinglun);
                            articleTypeSix1.update(ShowArticleActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    //更新成功，刷新界面的评论数
                                    tvPingLunCount.setText(pinglun+"");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });

                        }else {}
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
            default:
                break;

        }

    }

    /**
     * 查询用户对当前文章是否点赞
     * @param articleObjectID
     */
    public void findOrDetelteRecoderOfDianZan(String articleObjectID, final String opration){

        //使用复合查询，找到对应的点赞记录

        //获取当前用户id
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID", null);

        //创建查询
        //一、用户id
        BmobQuery<Zan> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("userObjectID", currentUserObjectID);
        //二、被赞对象id
        BmobQuery<Zan> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("id", articleObjectID);

        //组合查询
        List<BmobQuery<Zan>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<Zan> query = new BmobQuery<>();
        query.and(queries);

        //查询
        query.findObjects(this, new FindListener<Zan>() {
            @Override
            public void onSuccess(List<Zan> list) {
                if (list.size()>0){
                    if (opration.equals("find")){
                        //只是查询
                        cbDianZan.setChecked(true);
                    }else if (opration.equals("delete")){
                        //查询后删除
                        list.get(0).delete(ShowArticleActivity.this);
                        int newCount = Integer.valueOf(tvDianZanCount.getText().toString());
                        tvDianZanCount.setText(newCount - 1 + "");
                        //更新文章的点赞数-1
                        updateArticleDianZanCount(list.get(0).getId(), "delete");
                        cbDianZan.setChecked(false);
                    }
                }else {
                    cbDianZan.setChecked(false);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 增加一条点赞记录
     */
    public void addDianZan(){

        //获取当前用户id
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String currentUserObjectID = sharedPreferences.getString("objectID", null);

        //创建一个点赞记录实例
        Zan zan = new Zan();

        //设置属性
        zan.setUserObjectID(currentUserObjectID);
        zan.setId(articleObjectID);

        //上传
        zan.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                int newCount = Integer.valueOf(tvDianZanCount.getText().toString());
                tvDianZanCount.setText(newCount + 1 + "");
                //更新该文章数据（点赞数加1）
                updateArticleDianZanCount(articleObjectID, "add");
            }

            @Override
            public void onFailure(int i, String s) {
                //Toast.makeText(context,"点赞失败",Toast.LENGTH_SHORT).show();
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
        query.findObjects(this, new FindListener<Article>() {
            @Override
            public void onSuccess(List<Article> list) {
                if (list.size() > 0) {

                    //得到文章在总表的实例
                    Article article = new Article();
                    String objectID = list.get(0).getObjectId();

                    //设置点赞数值变化
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
                    article.update(ShowArticleActivity.this);

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
                query.findObjects(this, new FindListener<ArticleTypeOne>() {
                    @Override
                    public void onSuccess(List<ArticleTypeOne> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeOne articleTypeOne = list.get(0);
                            //设置新的点赞数
                            articleTypeOne.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeOne.update(ShowArticleActivity.this);
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
                query2.findObjects(this, new FindListener<ArticleTypeTwo>() {
                    @Override
                    public void onSuccess(List<ArticleTypeTwo> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeTwo articleTypeTwo = list.get(0);
                            //设置新的点赞数
                            articleTypeTwo.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeTwo.update(ShowArticleActivity.this);
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
                query3.findObjects(this, new FindListener<ArticleTypeThree>() {
                    @Override
                    public void onSuccess(List<ArticleTypeThree> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeThree articleTypeThree = list.get(0);
                            //设置新的点赞数
                            articleTypeThree.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeThree.update(ShowArticleActivity.this);
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
                query4.findObjects(this, new FindListener<ArticleTypeFour>() {
                    @Override
                    public void onSuccess(List<ArticleTypeFour> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeFour articleTypeFour = list.get(0);
                            //设置新的点赞数
                            articleTypeFour.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeFour.update(ShowArticleActivity.this);
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
                query5.findObjects(this, new FindListener<ArticleTypeFive>() {
                    @Override
                    public void onSuccess(List<ArticleTypeFive> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeFive articleTypeFive = list.get(0);
                            //设置新的点赞数
                            articleTypeFive.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeFive.update(ShowArticleActivity.this);
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
                query6.findObjects(this, new FindListener<ArticleTypeSix>() {
                    @Override
                    public void onSuccess(List<ArticleTypeSix> list) {
                        if (list.size() > 0) {
                            //得到分表里的文章
                            ArticleTypeSix articleTypeSix = list.get(0);
                            //设置新的点赞数
                            articleTypeSix.setLikeNumber(newDianZanCount);
                            //更新
                            articleTypeSix.update(ShowArticleActivity.this);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;

        }

    }

}
