package com.evecom.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.evecom.bean.MyUser;
import com.evecom.myview.MyCircleImage;
import com.evecom.myview.MyDialogChangeText;
import com.evecom.myview.MyDialogSelectPicture;
import com.evecom.myview.MyDialogShuoShuo;
import com.github.ybq.android.spinkit.SpinKitView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 修改个人信息界面
 * Created by wub on 2017/3/22.
 */
public class EditPersonalInfoActivity extends Activity implements View.OnClickListener {

    /**
     * 返回
     */
    ImageView ivBack;
    /**
     * 保存
     */
    TextView tvSaveChange;
    /**
     * 更换头像
     */
    RelativeLayout rlChangeAvatar;
    /**
     * 点击更改昵称
     */
    RelativeLayout rlChangeName;
    /**
     * 头像
     */
    MyCircleImage ivAvatar;
    /**
     * 昵称
     */
    TextView tvName;
    /**
     * 头像路径
     */
    private String avatarCache;
    /**
     * flag:用于标识用户是否修改了头像（通过是否执行了裁剪图片那一步来判断）
     * false : 默认没有修改
     * true :修改了
     */
    private boolean flag = false;
    /**
     * 旧头像的url
     */
    private String oldAvatarUrl;
    /**
     * 上传图片后得到的url
     */
    private String imgUrl;
    /**
     * 用户实例（用于更新）
     */
    private MyUser user;
    /**
     *
     */
    private SharedPreferences sharedPreferences;
    /**
     *
     */
    MyDialogChangeText myDialogChangeText;
    /**
     * 点击更改签名
     */
    RelativeLayout rlChangeQianMing;
    /**
     * 签名
     */
    TextView tvQianMing;
    private String qianMing = "", oldQianMing = "";
    /**
     * 密保
     */
    EditText etQuestion, etAnswer;
    private String question = "", answer = "";
    /**
     * 点击设置密保
     */
    RelativeLayout rlSetMiBao;
    /**
     * 密保布局
     */
    LinearLayout llMiBao;
    /**
     *
     */
    ImageView ivMiBao;
    /**
     * 加载动画
     */
    SpinKitView duringLoadingView;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取SharedPreferences
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        //获取主题
        int theme = sharedPreferences.getInt("theme",0);
        //设置主题
        if (theme == 0){
            setTheme(R.style.CustomStyleOne);
        }else {
            setTheme(theme);
        }

        setContentView(R.layout.activity_edit_personalinfo);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }

        //获取旧的头像url、签名、密保
        oldAvatarUrl = sharedPreferences.getString("avatarUrl", null);
        oldQianMing = sharedPreferences.getString("qianMing", null);
        question = sharedPreferences.getString("question", null);
        answer = sharedPreferences.getString("answer", null);


        //初始化控件
        initUI();
    }

    /**
     * 初始化控件
     */
    public void initUI() {

        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvSaveChange = (TextView) findViewById(R.id.tvSaveChange);
        rlChangeAvatar = (RelativeLayout) findViewById(R.id.rlChangeAvatar);
        rlChangeName = (RelativeLayout) findViewById(R.id.rlChangeName);

        ivBack.setOnClickListener(this);
        tvSaveChange.setOnClickListener(this);
        rlChangeAvatar.setOnClickListener(this);
        rlChangeName.setOnClickListener(this);

        ivAvatar = (MyCircleImage) findViewById(R.id.ivAvatar);
        tvName = (TextView) findViewById(R.id.tvName);
        rlChangeQianMing = (RelativeLayout) findViewById(R.id.rlChangeQianMing);
        tvQianMing = (TextView) findViewById(R.id.tvQianMing);
        rlChangeQianMing.setOnClickListener(this);

        etQuestion = (EditText) findViewById(R.id.etQuestion);
        etAnswer = (EditText) findViewById(R.id.etAnswer);
        rlSetMiBao = (RelativeLayout) findViewById(R.id.rlSetMiBao);
        rlSetMiBao.setOnClickListener(this);
        llMiBao = (LinearLayout) findViewById(R.id.llMiBao);
        ivMiBao = (ImageView) findViewById(R.id.ivMiBao);

        //显示昵称
        String savedName = sharedPreferences.getString("userName", null);
        tvName.setText(savedName);
        //显示密保
        etQuestion.setText(question);
        etAnswer.setText(answer);

        //显示头像
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String avatarUrl = sharedPreferences.getString("avatarUrl", null);
        if (avatarUrl != null && !avatarUrl.equals("default")) {
            Glide.with(this).load(avatarUrl).asBitmap().centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            ivAvatar.setImageBitmap(resource);
                        }
                    });
        }

        //显示签名
        tvQianMing.setText(oldQianMing);

        //加载动画
        duringLoadingView = (SpinKitView) findViewById(R.id.duringLoadingView);


    }

    /**
     * 点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //点击退出
            case R.id.ivBack:
                //finish();
                onBackPressed();    //为了退出的时候也有过度动画，不用finish
                break;

            //点击保存修改
            case R.id.tvSaveChange:
                saveAndUpdatePersonalInfo();
                break;

            //点击更换头像
            case R.id.rlChangeAvatar:
                changeAvatar();
                break;

            //点击更改昵称
            case R.id.rlChangeName:
                changeName();
                break;

            //点击更改签名
            case R.id.rlChangeQianMing:
                changeQianMing();
                break;

            //点击设置密保
            case R.id.rlSetMiBao:
                setMiBao();
                break;

        }
    }

    /**
     * 切换头像
     */
    public void changeAvatar() {

        //创建一个Dialog
        final MyDialogSelectPicture myDialogSelectPicture = new MyDialogSelectPicture(this);

        //设置dialog监听
        //拍照
        myDialogSelectPicture.setYesOnclickListener("拍照", new MyDialogSelectPicture.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                //拍照
                takePhoto();
                myDialogSelectPicture.dismiss();


            }
        });
        //从相册获取
        myDialogSelectPicture.setNoOnclickListener("从相册选择", new MyDialogSelectPicture.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                //从相册选择
                chooseFromAlbum();
                myDialogSelectPicture.dismiss();
            }
        });


        //显示Dialog
        myDialogSelectPicture.show();
    }

    /**
     * 更改昵称
     */
    public void changeName() {

        //创建一个Dialog
        myDialogChangeText = new MyDialogChangeText(this, tvName.getText().toString());

        //给Dialog写点击事件

        //取消
        myDialogChangeText.setNoOnclickListener("取消", new MyDialogChangeText.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                myDialogChangeText.dismiss();
            }
        });

        //确定
        myDialogChangeText.setYesOnclickListener("确定", new MyDialogChangeText.onYesOnclickListener() {
            @Override
            public void onYesClick() {

                //获取输入框内容
                String newName = myDialogChangeText.getNewName();
                //判断是否为空
                if (newName.equals("")) {
                    Toast.makeText(EditPersonalInfoActivity.this, "请输入...", Toast.LENGTH_SHORT).show();
                } else {
                    //判断与原来的昵称是否一样
                    if (newName.equals(sharedPreferences.getString("userName", null))) {
                        Toast.makeText(EditPersonalInfoActivity.this, "昵称没有变化...", Toast.LENGTH_SHORT).show();
                    } else {
                        //判断是否已存在该昵称
                        nameExistOrNot(newName);
                    }
                }
            }
        });

        //显示
        myDialogChangeText.show();

    }

    /**
     * 拍照
     */
    public void takePhoto() {
        //构建隐式Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //调用系统相机（安卓6.0需要动态授权）
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            //执行拍照
            startActivityForResult(intent, 1);
        } else {//请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 3);
            startActivityForResult(intent, 1);
        }

    }

    /**
     * 从相册选择图片
     */
    public void chooseFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    /**
     * 处理选择图片的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            //处理拍照结果
            case 1:
                //用户点击了取消
                if (data == null) {
                    return;
                } else {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        //获得拍的照片
                        Bitmap bm = extras.getParcelable("data");
                        //将Bitmap转化为uri
                        Uri uri = saveBitmap(bm, "temp");
                        Log.d("tag===url:相机", uri + "");
                        //启动图像裁剪
                        startImageZoom(uri);
                    }
                }
                break;

            //处理相册选择结果
            case 2:
                if (data == null) {
                    return;
                } else {
                    //用户从图库选择图片后会返回所选图片的Uri
                    Uri uri;
                    //获取到用户所选图片的Uri
                    uri = data.getData();
                    //返回的Uri为content类型的Uri,不能进行复制等操作,需要转换为文件Uri
                    Log.d("tag===url:相册", uri + "");
                    uri = convertUri(uri);
                    Log.d("tag===url:文件uri", uri + "");
                    startImageZoom(uri);
                }
                break;

            //处理裁剪结果
            case 5:

                //用户点击了取消
                if (data == null) {
                    return;
                } else {

                    //执行了裁剪，说明用户修改了头像
                    flag = true;

                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        //获取到裁剪后的图像
                        Bitmap bm = extras.getParcelable("data");
                        //显示头像
                        ivAvatar.setImageBitmap(bm);

                        //保存裁剪后的图片，作为头像的本地缓存图片
                        //将头像路径保存到SharedPreferences
                        saveBitmap(bm, "headImage");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("avatarCache", avatarCache);
                        editor.commit();

                    }
                }
                break;
            default:
                break;

        }
    }

    /**
     * 将Bitmap写入SD卡中的一个文件中,并返回写入文件的Uri
     *
     * @param bm
     * @param dirPath
     * @return
     */
    private Uri saveBitmap(Bitmap bm, String dirPath) {

        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }

        //新建文件存储裁剪后的图片
        File img = new File(tmpDir.getAbsolutePath() + "/avatar.png");
        if (img.exists()) {
            img.delete();
        }


        try {
            //打开文件输出流
            FileOutputStream fos = new FileOutputStream(img);
            //将bitmap压缩后写入输出流(参数依次为图片格式、图片质量和输出流)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            //刷新输出流
            fos.flush();
            //关闭输出流
            fos.close();
            //返回File类型的Uri
            //头像本地路径
            avatarCache = img.getAbsolutePath();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将content类型的Uri转化为文件类型的Uri
     *
     * @param uri
     * @return
     */
    private Uri convertUri(Uri uri) {
        InputStream is;
        try {
            //Uri ----> InputStream
            is = getContentResolver().openInputStream(uri);
            //InputStream ----> Bitmap
            Bitmap bm = BitmapFactory.decodeStream(is);
            //关闭流
            is.close();
            return saveBitmap(bm, "temp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 通过Uri传递图像信息以供裁剪
     *
     * @param uri
     */
    private void startImageZoom(Uri uri) {
        //构建隐式Intent来启动裁剪程序
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置数据uri和类型为图片类型
        intent.setDataAndType(uri, "image/*");
        //显示View为可裁剪的
        intent.putExtra("crop", true);
        //裁剪的宽高的比例为1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //输出图片的宽高均为150
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        //裁剪之后的数据是通过Intent返回
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 5);
    }

    /**
     * 更新个人信息
     */
    public void saveAndUpdatePersonalInfo() {

        //获取文本控件的昵称
        String newName = tvName.getText().toString();

        //获取保存的用户名
        String savedName = sharedPreferences.getString("userName", null);

        //获取新的密保
        String newQuestion = etQuestion.getText().toString();
        String newAnswer = etAnswer.getText().toString();

        //获取新的签名
        String newQianMing = tvQianMing.getText().toString();


        //判断用户是否修改了信息
        if (savedName.equals(newName) && !flag && newQianMing.equals(oldQianMing) && newQuestion.equals(question) && newAnswer.equals(answer)) {
            Toast.makeText(this, "信息没有改动哦...", Toast.LENGTH_SHORT).show();
        } else {

            //显示加载动画
            duringLoadingView.setVisibility(View.VISIBLE);

            //构建一个用户实例
            user = new MyUser();

            //用户名改变了
            if (!savedName.equals(newName)) {
                user.setUsername(newName);
            }

            //设置签名、密保
            user.setQianming(tvQianMing.getText().toString());
            user.setQustion(etQuestion.getText().toString());
            user.setAnswer(etAnswer.getText().toString());

            //用户头像修改了
            if (flag) {
                //将裁剪后的图片先上传到BMOB数据库，得到url
                uploadImage(avatarCache);
            } else {
                upDateUser();
            }

        }

    }

    /**
     * 上传图片到BMOB数据库
     *
     * @param avatarCache ：图片本地路径
     */
    public void uploadImage(String avatarCache) {

        final BmobFile bmobFile = new BmobFile(new File(avatarCache));

        bmobFile.uploadblock(this, new UploadFileListener() {

            //上传成功
            @Override
            public void onSuccess() {

                //
                Toast.makeText(EditPersonalInfoActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();

                //获取图片在数据库的url
                imgUrl = bmobFile.getFileUrl(EditPersonalInfoActivity.this);
                user.setAvatar(imgUrl);

                //更新用户信息
                upDateUser();

            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(EditPersonalInfoActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
            }

        });

    }

    /**
     * 更新用户信息
     */
    public void upDateUser() {

        //获取用户对象在数据库的ObjectID（登陆时已保存在sharedPreferences中）
        String objectID = sharedPreferences.getString("objectID", null);

        //更新
        user.update(this, objectID, new UpdateListener() {
            //成功
            @Override
            public void onSuccess() {
                //隐藏加载动画
                duringLoadingView.setVisibility(View.GONE);

                Toast.makeText(EditPersonalInfoActivity.this, "更新成功...", Toast.LENGTH_SHORT).show();
                //保存数据到偏好设置
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("avatarUrl",imgUrl);
                editor.putString("userName",tvName.getText().toString());
                editor.putString("qianMing",user.getQianming());
                editor.putString("question",user.getQustion());
                editor.putString("answer",user.getAnswer());
                editor.commit();
                //若之前上传过头像，则删除数据库原来的图片
                if (oldAvatarUrl != null && !oldAvatarUrl.equals("default")) {
                    deleteOldAvatar();
                }
            }

            //失败
            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(EditPersonalInfoActivity.this, "更新失败...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 判断是否已存在昵称
     *
     * @param newName
     */
    public void nameExistOrNot(final String newName) {

        //创建一个请求
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();

        //查询Myuser表中的“username”列数据
        query.addWhereEqualTo("username", newName);

        //查询
        query.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size() == 0) {
                    //说明改昵称可用,显示在昵称文本框
                    myDialogChangeText.dismiss();
                    tvName.setText(newName);
                } else if (list.size() > 0) {
                    //说明昵称已存在
                    Toast.makeText(EditPersonalInfoActivity.this, "昵称已存在233", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                //查询失败
            }
        });

    }


    /**
     * 删除数据库原来的头像
     */
    public void deleteOldAvatar() {

        BmobFile file = new BmobFile();
        file.setUrl(oldAvatarUrl);
        file.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditPersonalInfoActivity.this, "已删除原头像", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                //Toast.makeText(EditPersonalInfoActivity.this, "原头像删除失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 更改签名
     */
    public void changeQianMing() {

        //创建一个Dialog
        final MyDialogShuoShuo dialog = new MyDialogShuoShuo(this);

        //标题
        dialog.setTitle("修改签名");

        //监听
        dialog.setNoOnclickListener("取消", new MyDialogShuoShuo.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialog.dismiss();
            }
        });

        dialog.setYesOnclickListener("确定", new MyDialogShuoShuo.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                String oldQianMing = tvQianMing.getText().toString();
                String newQianMing = dialog.getContent();
                if (oldQianMing.equals(newQianMing)) {
                    Toast.makeText(EditPersonalInfoActivity.this, "签名没有变化", Toast.LENGTH_SHORT).show();
                } else {
                    qianMing = newQianMing;
                    tvQianMing.setText(qianMing);
                    dialog.dismiss();
                }
            }
        });

        //显示Dialog
        dialog.show();

    }

    /**
     * 设置密保
     */
    public void setMiBao() {

        if (llMiBao.getVisibility() == View.GONE) {
            llMiBao.setVisibility(View.VISIBLE);
        } else {
            llMiBao.setVisibility(View.GONE);
        }

    }

}
