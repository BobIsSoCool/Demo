package com.evecom.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.evecom.bean.MyUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Bob on 2017/2/13.
 * 注册界面
 */
public class RegisterActivity extends Activity{

    //变量
    private EditText userName;      //用户名
    private EditText userPwd;       //用户密码
    private EditText userPwd2;      //二次输入密码
    private RadioGroup rgp;         //性别选择

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

        setContentView(R.layout.activity_regsiter);

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

    }

    /**
     * 函数：initUI（）
     * 功能：初始化控件
     * */
    public void initUI(){
        userName = (EditText)findViewById(R.id.register_name);
        userPwd = (EditText)findViewById(R.id.register_pwd);
        userPwd2 = (EditText)findViewById(R.id.register_pwd2);
        rgp = (RadioGroup)findViewById(R.id.register_gender);
    }



    /**
     * 点击注册
     * */
    public void register(View view){
        boolean flag = false;
        MyUser user = new MyUser();
        String name = userName.getText().toString();
        String pwd = userPwd.getText().toString();
        String pwd2 = userPwd2.getText().toString();

        // 判断信息填写
        if (name.equals("") || pwd.equals("") || pwd2.equals("")) {
            Toast.makeText(RegisterActivity.this, "信息填写不完整", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            if (!pwd.equals(pwd2)) {
                Toast.makeText(RegisterActivity.this, "两次密码不同",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                flag = true;
            }
        }

        // 判断性别
        boolean gender = true;
        if (rgp.getCheckedRadioButtonId() == R.id.rb_woman) {
            gender = false;
        }

        // 对password进行MD5转码
        String md = new String(Hex.encodeHex(DigestUtils.sha(pwd)))
                .toUpperCase();

        if (flag) {
            user.setAvatar("default");//注册时没有上传头像，用默认的
            user.setUsername(name);
            user.setPassword(md);
            user.setGender(gender);

            // 将user保存到服务器
            user.save(this, new SaveListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(RegisterActivity.this, "注册成功",
                            Toast.LENGTH_SHORT).show();
                    // 保存成功，重置输入框
                    userName.setText("");
                    userPwd.setText("");
                    userPwd2.setText("");

                    finish();
                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    if (arg0 == 401) {
                        Toast.makeText(RegisterActivity.this, "保存失败，该用户名已存在",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "保存失败，失败原因" + arg0 + ":" + arg1,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 点击返回登录界面
     * */
    public void backtoLogin(View view){
        finish();
    }
}
