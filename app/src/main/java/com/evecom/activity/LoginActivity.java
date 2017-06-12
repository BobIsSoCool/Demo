package com.evecom.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evecom.bean.MyUser;
import com.evecom.utils.NormalLoadPictrue;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Hex;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Bob on 2017/2/13.
 * 登录页面
 */
public class LoginActivity extends Activity {

    //变量
    /**
     * 登录界面，用户名输入框
     */
    EditText userName;
    /**
     * 登录界面，用户密码输入框
     */
    EditText userPwd;
    /**
     * 偏好设置对象
     */
    SharedPreferences sharedPreferences;
    /**
     * 头像的字节流
     */
    static byte[] picByte;
    /**
     * 当前用户
     */
    private MyUser user;
    /**
     * NormalLoadPictrue normalLoadPictrue
     */
    private NormalLoadPictrue normalLoadPictrue;
    /**
     * 记住密码（checkBox）
     */
    CheckBox cbRememberPassword;
    /**
     * 加载动画组件
     */
    SpinKitView spinKitView;
    /**
     * 点击跳转找回密码
     */
    TextView tvFindBackPassword;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取主题
        int theme = getSharedPreferences("user",MODE_PRIVATE).getInt("theme",0);
        //设置主题
        if (theme == 0){
            setTheme(R.style.CustomStyleOne);
        }else {
            setTheme(theme);
        }

        setContentView(R.layout.activity_login);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        };

        //获得偏好设置对象
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        //初始化控件
        intiUI();

        //判断是否自动填充密码
        autoInputPasswordOrNot();

    }

    /**
     * 初始化控件
     */
    public void intiUI() {
        userName = (EditText) findViewById(R.id.login_name);
        userName.setText(sharedPreferences.getString("userName", ""));
        userName.addTextChangedListener(new EdittextChangeListener());//为输入框设置监听

        userPwd = (EditText) findViewById(R.id.login_pwd);

        cbRememberPassword = (CheckBox) findViewById(R.id.cbRememberPassword);
        //进入登录界面，获取checkBox的状态，并设置相应状态,如果没有保存，默认为false（即不记住密码）
        boolean rememberPassword = sharedPreferences.getBoolean("rememberPassword", false);
        cbRememberPassword.setChecked(rememberPassword);

        //当用户点击checkBox，判断是否记住密码,并将checkBox的状态保存起来
        cbRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (cbRememberPassword.isChecked()) {
                    editor.putBoolean("rememberPassword", true);
                } else {
                    editor.putBoolean("rememberPassword", false);
                }
                editor.commit();
            }
        });

        //加载动画
        spinKitView = (SpinKitView) findViewById(R.id.duringLoadingView);

        //找回密码
        tvFindBackPassword = (TextView) findViewById(R.id.tvFindBackPassword);
        tvFindBackPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转找回密码界面
                Intent intent = new Intent(LoginActivity.this, FindBackPassword.class);
                intent.putExtra("userName",userName.getText().toString());
                startActivity(intent);
            }
        });
    }

    /**
     * 判断是否自动填充密码输入框
     */
    public void autoInputPasswordOrNot() {
        //用户名不为空、而且记住密码checkBox为选中状态，
        // 直接从sharedPreferences中获取密码，并填充到密码框
        if (!TextUtils.isEmpty(userName.getText()) && sharedPreferences.getBoolean("rememberPassword", false)) {
            String password = sharedPreferences.getString("userPassword", "");
            userPwd.setText(password);
        }
    }

    /**
     * 登录
     *
     * @param view
     */
    public void onLogin(View view) {

        //获取输入信息
        final String name = userName.getText().toString();
        final String password = userPwd.getText().toString();

        //为空，提示
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入完整...", Toast.LENGTH_SHORT).show();
            return;
        }

        //显示加载动画控件
        spinKitView.setVisibility(View.VISIBLE);

        // 在服务器数据库的MyUser表中查询是否有该用户

        //创建一个请求
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();

        //查询Myuser表中的“username”列数据
        query.addWhereEqualTo("username", name);

        //查询
        query.findObjects(this, new FindListener<MyUser>() {

            @Override
            public void onSuccess(List<MyUser> arg0) {
                if (arg0.size() > 0) {
                    // 数据表确实有用户名为username的数据记录

                    //获取用户对象
                    user = arg0.get(0);

                    // 接下来进行密码的比对
                    //MD5编码
                    String md = new String(Hex.encodeHex(DigestUtils
                            .sha(password))).toUpperCase();

                    //判断密码是否正确
                    if (user.getPassword().equals(md)) {

                        //验证成功
                        //获取Editor对象
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        //保存用户名、密码、头像url、ObjectID、签名、密保（问题及答案）
                        editor.putString("userName", name);
                        //判断用户是否选择保存密码
                        if (sharedPreferences.getBoolean("rememberPassword", false)) {
                            editor.putString("userPassword", password);
                        }
                        editor.putString("avatarUrl", user.getAvatar());
                        editor.putString("objectID", user.getObjectId());
                        editor.putString("qianMing",user.getQianming());
                        editor.putString("question",user.getQustion());
                        editor.putString("answer",user.getAnswer());

                        //保存完成，提交
                        editor.commit();

                        Toast.makeText(LoginActivity.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        spinKitView.setVisibility(View.GONE);   //隐藏动画
                        //跳转主页面，带有过渡动画
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        //startActivity(intent,
                                      //ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,ivHead, "ivSharedHead").toBundle());

                        startActivity(intent);
                        finish();


                    } else {
                        spinKitView.setVisibility(View.GONE);   //隐藏动画
                        Toast.makeText(LoginActivity.this, "用户名或密码错误",
                                Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误",
                            Toast.LENGTH_SHORT).show();
                    spinKitView.setVisibility(View.GONE);   //隐藏动画
                }
            }

            //登录失败，显示错误信息
            @Override
            public void onError(int arg0, String arg1) {
                Toast.makeText(LoginActivity.this, "登录失败，" + arg0 + ":" + arg1,
                        Toast.LENGTH_SHORT).show();
                spinKitView.setVisibility(View.GONE);   //隐藏动画
            }
        });
    }


    /**
     * 点击进入注册界面
     */
    public void gotoRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    /**
     * 监听输入框的类
     */
    class EdittextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.equals(sharedPreferences.getString("userName", "-1"))) {
                //此处监听用户名输入框内容变化后，内容和保存的用户名不一致，清空密码
                userPwd.setText("");
            }
        }
    }
}
