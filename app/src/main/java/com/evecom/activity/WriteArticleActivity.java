package com.evecom.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evecom.bean.Article;
import com.evecom.bean.ArticleTypeFive;
import com.evecom.bean.ArticleTypeFour;
import com.evecom.bean.ArticleTypeOne;
import com.evecom.bean.ArticleTypeSix;
import com.evecom.bean.ArticleTypeThree;
import com.evecom.bean.ArticleTypeTwo;
import com.evecom.bean.Dongtai;
import com.evecom.myview.MyDialogArticleType;
import com.evecom.utils.MyStringUtils;
import com.sendtion.xrichtext.RichTextEditor;
import com.sendtion.xrichtext.SDCardUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Bob on 2017/2/13.
 * 引导页面
 */
public class WriteArticleActivity extends Activity implements View.OnClickListener {

    /**
     * 点击插入图片
     */
    private ImageView img;
    /**
     * 发布按钮（其实是textview）
     */
    TextView tvPublishArticle;
    /**
     * 文章标题
     */
    EditText etArticleTitle;
    /**
     * 富文本控件（即文章内容）
     */
    private RichTextEditor richTextEditor;
    /**
     * 文章对象
     */
    Article article;
    /**
     * 富文本内容
     */
    StringBuffer content = new StringBuffer();
    /**
     *
     */
    List<RichTextEditor.EditData> editList = null;
    /**
     * 图片再服务器的url
     */
    String imgUrl = "";
    /**
     * 封面图片url
     */
    private String faceImageUrl = "";
    /**
     * 标识，把文章中第一张图片作为封面
     */
    private int flag2 = 0;
    /**
     * flag:用于标识是发布新文章还是修改已有文章
     * 1：发布新文章  (默认为1)
     * 2：修改已有文章
     */
    private int flag = 1;
    /**
     * handler（图片上传成功后，再拿取url）
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //判断是否还有元素，有则添加下一个
                index2++;
                if (index2 < editList.size()) {
                    addContent(editList.get(index2));
                } else if (index2 == editList.size()) {
                    //后面没有元素了，则发布文章
                    publishArticle();
                }
            }
        }
    };

    /**
     * index
     */
    private int index = 0;
    /**
     * index2
     */
    private int index2 = 0;
    /**
     * Dialog
     */
    MyDialogArticleType myDialogArticleType;
    /**
     * ArticleObjectID
     */
    private String articleObjectID = "";
    /**
     * 用户id
     */
    private String objectID = "";
    /**
     * 文章标题
     */
    private String articleTitle = "";
    /**
     * 日期
     */
    private String publishDate = "";
    /**
     * 生成文章的加载动画
     */
    LinearLayout llLodingView;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_article);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }

        //初始化控件
        initUI();

        //判断是写新的文章还是修改已有的文章
        //1、写新的文章则控件内容为空
        //2、修改已有文章，则在界面显示已有文章内容
        Intent intent = getIntent();
        String activityIndex = intent.getStringExtra("activity");   //1：新文章；2：修改已有文章
        if (activityIndex.equals("2")) {
            flag = 2;
            article = (Article) intent.getExtras().getSerializable("article");
            showExistArticle();
        }

    }

    /**
     * 初始化控件
     */
    public void initUI() {

        etArticleTitle = (EditText) findViewById(R.id.etArticleTitle);
        richTextEditor = (RichTextEditor) findViewById(R.id.richTextEditor);
        img = (ImageView) findViewById(R.id.click_to_addImg);
        tvPublishArticle = (TextView) findViewById(R.id.tvPublishArticle);
        llLodingView = (LinearLayout) findViewById(R.id.llLoadingView2);

        //设置监听
        img.setOnClickListener(this);
        tvPublishArticle.setOnClickListener(this);

    }

    /**
     * 监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击添加图片
            case R.id.click_to_addImg:
                addImg();
                break;
            //点击发布文章
            case R.id.tvPublishArticle:
                //用户点击发布，立即显示加载动画
                llLodingView.setVisibility(View.VISIBLE);
                getEditData();
                break;
        }
    }

    /**
     * 返回按钮，点击返回主页面
     */
    public void backtoMain(View view) {

        //跳转前，收起软键盘，防止出现画面卡帧（跳转时很难看）

        //创建延时操作task
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                finish();   //延时操作内容（回退）
            }
        };

        //先收起软键盘，再回退（finish）
        hideKeyboard();

        Timer timer = new Timer();
        timer.schedule(task, 300);//0.3s后执行回退
    }

    /**
     * 关闭软键盘
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && this.getCurrentFocus() != null) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    /**
     * 调用图库选择
     */
    private void addImg() {
        //关闭软键盘
        hideKeyboard();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");// 相片类型
        startActivityForResult(intent, 1);

    }

    /**
     * 插入图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data != null) {
                richTextEditor.measure(0, 0);
                String imagePath = SDCardUtil.getFilePathByUri(this, data.getData());
                richTextEditor.insertImage(imagePath, richTextEditor.getMeasuredWidth());
            }
        }
    }

    /**
     * 发布文章
     */
    public void publishArticle() {

        Log.d("tag===content===", content.toString());

        if (TextUtils.isEmpty(etArticleTitle.getText()) || content.toString().isEmpty()) {
            llLodingView.setVisibility(View.GONE);
            Toast.makeText(this, "文章写完整才能发布", Toast.LENGTH_SHORT).show();
        } else {

            //创建一个选择分类的Dialog
            myDialogArticleType = new MyDialogArticleType(this);

            //点击事件
            myDialogArticleType.setNoOnclickListener("取消", new MyDialogArticleType.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    myDialogArticleType.dismiss();
                }
            });

            myDialogArticleType.setYesOnclickListener("发布", new MyDialogArticleType.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    //发布
                    publish();
                }
            });

            //在分类选择界面出来之前，隐藏加载动画
            llLodingView.setVisibility(View.GONE);
            myDialogArticleType.show();
        }
    }


    /**
     * 将富文本内的内容生存数据
     *
     * @return content.toString()：返回String形式数据
     */
    private void getEditData() {
        index2 = 0;
        editList = richTextEditor.buildEditData();
        addContent(editList.get(index2));
    }


    /**
     * 上传图片到BMOB数据库
     *
     * @param imgPath:图片路径
     */
    public void publishImg(String imgPath) {
        //拿到传过来的参数
        final String imagePath = imgPath;
        //生成数据时，将图片上传到BMOB，然后得到其url，存入字符串中
        final BmobFile bmobFile = new BmobFile(new File(imagePath));
        bmobFile.uploadblock(WriteArticleActivity.this, new UploadFileListener() {
            //上传成功
            @Override
            public void onSuccess() {
                //获取图片在数据库的url
                imgUrl = bmobFile.getFileUrl(WriteArticleActivity.this);
                content.append("<img src=\"").append(imgUrl).append("\"/>");
                //判断是不是文章中出现的第一张图片，是则设置为封面
                if (flag2 == 0){
                    faceImageUrl = imgUrl;
                    flag2++;
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            //上传失败
            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(WriteArticleActivity.this, "头像上传失败" + ":" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * @param data :当前的数据对象（文本或图片）
     */
    public void addContent(RichTextEditor.EditData data) {
        if (data.inputStr != null) {
            //判断是文本，则添加文字
            content.append(data.inputStr);
            //判断是否还有元素，有则添加下一个
            index2++;
            if (index2 < editList.size()) {
                addContent(editList.get(index2));
            } else if (index2 == editList.size()) {
                //后面没有元素了，则发布文章
                publishArticle();
            }
        } else if (data.imagePath != null) {
            //判断是图片，则添加图片
            //上传图片到数据库
            publishImg(data.imagePath);
        }
    }

    /**
     * 富文本的内容集合
     */
    List<String> textList = null;
    /**
     * 展示已有的文章内容
     */
    /**
     * 展示文章
     */
    public void showExistArticle() {

        //获取文章的内容
        String content = article.getArticleCotent();

        //清除富文本内容
        richTextEditor.clearAllLayout();

        //获取富文本内容集合
        textList = MyStringUtils.cutStringByImgTag(content);

        //显示内容
        //标题
        etArticleTitle.setText(article.getArticleTitle());
        //富文本
        showContent(textList.get(index));

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
            richTextEditor.addImageViewAtIndex(richTextEditor.getLastIndex(), imgUrl);
        } else {
            //判断是文本，则直接显示
            richTextEditor.addEditTextAtIndex(richTextEditor.getLastIndex(), str);
            Log.d("tag", "text");
        }

        index++;
        //判断是否还有元素
        if (index < textList.size()) {
            //后面还有元素，则继续显示
            showContent(textList.get(index));
        }

    }

    /**
     * 发布新文章
     *
     * @param article2:文章
     */
    public void publishNewArticle(final Article article2) {

        article2.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                /*Toast.makeText(WriteArticleActivity.this, "发布成功",
                        Toast.LENGTH_SHORT).show();*/

                //获取文章在总表的objectID
                articleObjectID = article2.getObjectId();

                //发布新文章，增加一条动态
                addDongtai(article2);

                //发布文章到对应分类的表（发布、更新都调次方法，此方法自动判断是发布还是更新）
                publishArticleByType();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                if (arg0 == 401) {
                    Toast.makeText(WriteArticleActivity.this, "保存失败",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WriteArticleActivity.this,
                            "保存失败，失败原因" + arg0 + ":" + arg1,
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    /**
     * 更新已有文章
     *
     * @param article2：文章
     */
    public void updateExistArticle(Article article2) {

        //从BMOB数据库获取要更新文章的id
        String articleID = article.getObjectId();     //注意：此处是原数据库中文章对象

        //根据ID进行更新
        article2.update(this, articleID, new UpdateListener() {
            public void onSuccess() {
                /*Toast.makeText(WriteArticleActivity.this, "更新成功",
                        Toast.LENGTH_SHORT).show();*/
                //更新文章到对应分类的表（发布、更新都调次方法，此方法自动判断是发布还是更新）
                publishArticleByType();
            }

            @Override
            public void onFailure(int i, String s) {
               /* Toast.makeText(WriteArticleActivity.this, "更新失败",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    /**
     * 增加一条动态到数据库
     */
    public void addDongtai(Article article) {

        //创建一个动态对象
        Dongtai dongtai = new Dongtai();

        //设置动态日期、内容以及所属用户的ObjectID
        dongtai.setDate(article.getPublishDate());
        dongtai.setContent("发布了文章：《" + article.getArticleTitle() + "》");
        dongtai.setUserObjectID(article.getUserObjectID());

        //保存到BMOB数据库
        dongtai.save(this, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    /**
     * 发布文章（一份存到所有文章的表、一份存到对应类型文章的表）
     */
    public void publish() {

        //获取用户选择的分类
        String articleType = myDialogArticleType.getType();

        //获取文章一些属性
        articleTitle = etArticleTitle.getText().toString();              // 标题
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        objectID = sharedPreferences.getString("objectID", null);        //用户objectID（从偏好设置中获取）
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        publishDate = sDateFormat.format(new java.util.Date());          //获取当前时间

        //创建一个文章对象（总表）
        Article article0 = new Article();
        //设置属性
        article0.setUserObjectID(objectID);
        article0.setPublishDate(publishDate);
        article0.setArticleTitle(articleTitle);
        article0.setArticleCotent(content.toString());
        article0.setArticleType(articleType);

        //存到文章总表
        if (flag == 1) {
            //将新文章上传的BMOB数据库
            publishNewArticle(article0);
        } else if (flag == 2) {
            //将修改后的文章更新到BMOB数据库
            updateExistArticle(article0);
        }


    }

    /**
     * 把文章存储到对应的分类表中
     */
    public void publishArticleByType(){

        //获取用户选择的分类
        String articleType = myDialogArticleType.getType();
        //确定文章对象所属类
        switch (articleType) {
            case "分类一":
                final ArticleTypeOne article1 = new ArticleTypeOne();
                article1.setUserObjectID(objectID);
                article1.setArticleObjectID(articleObjectID);
                article1.setPublishDate(publishDate);
                article1.setArticleTitle(articleTitle);
                article1.setArticleCotent(content.toString());
                article1.setFaceImageUrl(faceImageUrl);
                if (flag == 1) {
                    //将新文章上传的BMOB数据库
                    article1.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(WriteArticleActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                            myDialogArticleType.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(WriteArticleActivity.this,"发布失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (flag == 2) {
                    //将修改后的文章更新到BMOB数据库
                    //1、获取文章在总表的objectID（在对应类别的表里，通过该字                                                                                                                                                             段找到对应文章）
                    String articleObjectID2 = article.getObjectId();
                    //2、获取对应类别表里文章的objectID
                    BmobQuery<ArticleTypeOne> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("articleObjectID",articleObjectID2);
                    bmobQuery.findObjects(this, new FindListener<ArticleTypeOne>() {
                        @Override
                        public void onSuccess(List<ArticleTypeOne> list) {
                            if (list.size()>0){
                                //3、找到之后，获取该文章在对应分类表里自己的objectID
                                String selfObjectID = list.get(0).getObjectId();
                                //4、根据objectID，更新该文章
                                article1.update(WriteArticleActivity.this, selfObjectID, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(WriteArticleActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                        myDialogArticleType.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(WriteArticleActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
                break;

            case "分类二":
                final ArticleTypeTwo article2 = new ArticleTypeTwo();
                article2.setUserObjectID(objectID);
                article2.setArticleObjectID(articleObjectID);
                article2.setPublishDate(publishDate);
                article2.setArticleTitle(articleTitle);
                article2.setArticleCotent(content.toString());
                article2.setFaceImageUrl(faceImageUrl);
                if (flag == 1) {
                    //将新文章上传的BMOB数据库
                    article2.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(WriteArticleActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                            myDialogArticleType.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(WriteArticleActivity.this,"发布失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (flag == 2) {
                    //将修改后的文章更新到BMOB数据库
                    //1、获取文章在总表的objectID（在对应类别的表里，通过该字                                                                                                                                                             段找到对应文章）
                    String articleObjectID2 = article.getObjectId();
                    //2、获取对应类别表里文章的objectID
                    BmobQuery<ArticleTypeTwo> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("articleObjectID",articleObjectID2);
                    bmobQuery.findObjects(this, new FindListener<ArticleTypeTwo>() {
                        @Override
                        public void onSuccess(List<ArticleTypeTwo> list) {
                            if (list.size()>0){
                                //3、找到之后，获取该文章在对应分类表里自己的objectID
                                String selfObjectID = list.get(0).getObjectId();
                                //4、根据objectID，更新该文章
                                article2.update(WriteArticleActivity.this, selfObjectID, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(WriteArticleActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                        myDialogArticleType.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(WriteArticleActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
                break;

            case "分类三":
                final ArticleTypeThree article3 = new ArticleTypeThree();
                article3.setUserObjectID(objectID);
                article3.setArticleObjectID(articleObjectID);
                article3.setPublishDate(publishDate);
                article3.setArticleTitle(articleTitle);
                article3.setArticleCotent(content.toString());
                article3.setFaceImageUrl(faceImageUrl);
                if (flag == 1) {
                    //将新文章上传的BMOB数据库
                    article3.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(WriteArticleActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                            myDialogArticleType.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(WriteArticleActivity.this,"发布失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (flag == 2) {
                    //将修改后的文章更新到BMOB数据库
                    //1、获取文章在总表的objectID（在对应类别的表里，通过该字                                                                                                                                                             段找到对应文章）
                    String articleObjectID2 = article.getObjectId();
                    //2、获取对应类别表里文章的objectID
                    BmobQuery<ArticleTypeThree> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("articleObjectID",articleObjectID2);
                    bmobQuery.findObjects(this, new FindListener<ArticleTypeThree>() {
                        @Override
                        public void onSuccess(List<ArticleTypeThree> list) {
                            if (list.size()>0){
                                //3、找到之后，获取该文章在对应分类表里自己的objectID
                                String selfObjectID = list.get(0).getObjectId();
                                //4、根据objectID，更新该文章
                                article3.update(WriteArticleActivity.this, selfObjectID, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(WriteArticleActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                        myDialogArticleType.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(WriteArticleActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
                break;

            case "分类四":
                final ArticleTypeFour article4 = new ArticleTypeFour();
                article4.setUserObjectID(objectID);
                article4.setArticleObjectID(articleObjectID);
                article4.setPublishDate(publishDate);
                article4.setArticleTitle(articleTitle);
                article4.setArticleCotent(content.toString());
                article4.setFaceImageUrl(faceImageUrl);
                if (flag == 1) {
                    //将新文章上传的BMOB数据库
                    article4.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(WriteArticleActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                            myDialogArticleType.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(WriteArticleActivity.this,"发布失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (flag == 2) {
                    //将修改后的文章更新到BMOB数据库
                    //1、获取文章在总表的objectID（在对应类别的表里，通过该字                                                                                                                                                             段找到对应文章）
                    String articleObjectID2 = article.getObjectId();
                    //2、获取对应类别表里文章的objectID
                    BmobQuery<ArticleTypeFour> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("articleObjectID",articleObjectID2);
                    bmobQuery.findObjects(this, new FindListener<ArticleTypeFour>() {
                        @Override
                        public void onSuccess(List<ArticleTypeFour> list) {
                            if (list.size()>0){
                                //3、找到之后，获取该文章在对应分类表里自己的objectID
                                String selfObjectID = list.get(0).getObjectId();
                                //4、根据objectID，更新该文章
                                article4.update(WriteArticleActivity.this, selfObjectID, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(WriteArticleActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                        myDialogArticleType.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(WriteArticleActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
                break;

            case "分类五":
                final ArticleTypeFive article5 = new ArticleTypeFive();
                article5.setUserObjectID(objectID);
                article5.setArticleObjectID(articleObjectID);
                article5.setPublishDate(publishDate);
                article5.setArticleTitle(articleTitle);
                article5.setArticleCotent(content.toString());
                article5.setFaceImageUrl(faceImageUrl);
                if (flag == 1) {
                    //将新文章上传的BMOB数据库
                    article5.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(WriteArticleActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                            myDialogArticleType.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(WriteArticleActivity.this,"发布失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (flag == 2) {
                    //将修改后的文章更新到BMOB数据库
                    //1、获取文章在总表的objectID（在对应类别的表里，通过该字                                                                                                                                                             段找到对应文章）
                    String articleObjectID2 = article.getObjectId();
                    //2、获取对应类别表里文章的objectID
                    BmobQuery<ArticleTypeFive> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("articleObjectID",articleObjectID2);
                    bmobQuery.findObjects(this, new FindListener<ArticleTypeFive>() {
                        @Override
                        public void onSuccess(List<ArticleTypeFive> list) {
                            if (list.size()>0){
                                //3、找到之后，获取该文章在对应分类表里自己的objectID
                                String selfObjectID = list.get(0).getObjectId();
                                //4、根据objectID，更新该文章
                                article5.update(WriteArticleActivity.this, selfObjectID, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(WriteArticleActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                        myDialogArticleType.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(WriteArticleActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
                break;

            case "分类六":
                final ArticleTypeSix article6 = new ArticleTypeSix();
                article6.setUserObjectID(objectID);
                article6.setArticleObjectID(articleObjectID);
                article6.setPublishDate(publishDate);
                article6.setArticleTitle(articleTitle);
                article6.setArticleCotent(content.toString());
                article6.setFaceImageUrl(faceImageUrl);
                if (flag == 1) {
                    //将新文章上传的BMOB数据库
                    article6.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(WriteArticleActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                            myDialogArticleType.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(WriteArticleActivity.this,"发布失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (flag == 2) {
                    //将修改后的文章更新到BMOB数据库
                    //1、获取文章在总表的objectID（在对应类别的表里，通过该字                                                                                                                                                             段找到对应文章）
                    String articleObjectID2 = article.getObjectId();
                    //2、获取对应类别表里文章的objectID
                    BmobQuery<ArticleTypeSix> bmobQuery = new BmobQuery<>();
                    bmobQuery.addWhereEqualTo("articleObjectID",articleObjectID2);
                    bmobQuery.findObjects(this, new FindListener<ArticleTypeSix>() {
                        @Override
                        public void onSuccess(List<ArticleTypeSix> list) {
                            if (list.size()>0){
                                //3、找到之后，获取该文章在对应分类表里自己的objectID
                                String selfObjectID = list.get(0).getObjectId();
                                //4、根据objectID，更新该文章
                                article6.update(WriteArticleActivity.this, selfObjectID, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(WriteArticleActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                                        myDialogArticleType.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(WriteArticleActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
                break;
        }

    }


}
