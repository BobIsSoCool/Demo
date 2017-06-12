package com.evecom.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evecom.bean.MyUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 根据密保找回密码
 * Created by wub on 2017/4/11.
 */
public class FindBackPassword extends Activity {

    /**
     * 点击返回
     */
    ImageView ivBack;
    /**
     * 密保问题
     */
    TextView tvQuestion;
    private String question = "";
    /**
     * 密保答案
     */
    EditText etAnswer;
    private String answer = "";
    /**
     * 用户名
     */
    private String userName = "";
    /**
     * 用户id
     */
    private String userObjectID = "";
    /**
     * 确定按钮
     */
    TextView tvDone;
    /**
     * 重置密码
     */
    LinearLayout llResetPassWord;
    EditText etPassWord1,etPassWord2;
    /**
     * 完成按钮
     */
    TextView tvComplete;

    /**
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

        setContentView(R.layout.activity_forget_password);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }

        //得到用户名
        userName = (String) getIntent().getExtras().get("userName");

        //初始化控件
        initUI();

        //根据用户名获取用户密保数据
        getUserInfo();

    }

    /**
     * 初始化控件
     */
    public void initUI() {

        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        etAnswer = (EditText) findViewById(R.id.etAnswer);
        tvDone = (TextView) findViewById(R.id.tvDone);
        llResetPassWord = (LinearLayout) findViewById(R.id.llResetPassWord);
        etPassWord1 = (EditText) findViewById(R.id.etPassWord1);
        etPassWord2 = (EditText) findViewById(R.id.etPassWord2);
        tvComplete = (TextView) findViewById(R.id.tvComplete);

        //点击退出
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //点击确定回答完问题
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断输入回答是否为空
                String answer2 = etAnswer.getText().toString();
                if (answer2.equals("")){
                    Toast.makeText(FindBackPassword.this,"请输入...",Toast.LENGTH_SHORT).show();
                }else {
                    //判断回答是否正确
                    if (answer2.equals(answer)){
                        //回答正确，显示重置密码控件
                        llResetPassWord.setVisibility(View.VISIBLE);
                        //让密码输入框获取焦点（不用手动去点）
                        etPassWord1.setFocusable(true);
                        etPassWord1.setFocusableInTouchMode(true);
                        etPassWord1.requestFocus();
                    }else {
                        Toast.makeText(FindBackPassword.this,"回答错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //点击重置密码
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入的密码
                String pwd1 = etPassWord1.getText().toString();
                String pwd2 = etPassWord2.getText().toString();
                //判断
                if (pwd1.equals("") || pwd2.equals("")){
                    Toast.makeText(FindBackPassword.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if (!pwd1.equals(pwd2)){
                    Toast.makeText(FindBackPassword.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                }else {
                    //重置密码
                    resetPassword(pwd1);
                }
            }
        });

    }


    /**
     * 获取用户信息
     */
    public void getUserInfo() {

        //创建查询
        BmobQuery<MyUser> query = new BmobQuery<>();
        //添加条件
        query.addWhereEqualTo("username", userName);
        //查询
        query.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                if (list.size() > 0) {

                    //获取用户信息
                    MyUser user = list.get(0);
                    question = user.getQustion();
                    answer = user.getAnswer();
                    userObjectID = user.getObjectId();

                    //显示密保问题
                    tvQuestion.setText("Q："+question);

                } else {

                    Toast.makeText(FindBackPassword.this, "昵称为空或不存在", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(FindBackPassword.this, "请求出错", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 重置用户密码
     * @param password
     */
    public void resetPassword(String password){

        //创建一个用户
        MyUser user = new MyUser();

        //设置密码
        // 对password进行MD5转码
        String md = new String(Hex.encodeHex(DigestUtils.sha(password))).toUpperCase();
        user.setPassword(md);

        //根据id更新
        user.update(this, userObjectID, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(FindBackPassword.this,"重置密码成功",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(FindBackPassword.this,"重置密码失败",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
