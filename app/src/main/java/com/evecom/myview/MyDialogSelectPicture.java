package com.evecom.myview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evecom.activity.R;

/**
 * 自定义Dialog
 * Created by wub on 2017/3/23.
 */
public class MyDialogSelectPicture extends Dialog {

    /**
     * 拍照
     */
    Button btnCamera;
    RelativeLayout rlCamera;
    /**
     * 相册
     */
    Button btnPhoto;
    RelativeLayout rlPhoto;
    /**
     * Dialog标题
     */
    TextView tvDialogTitle;
    /**
     * 从外界设置的title文本
     */
    private String titleStr;
    /**
     * 确定文本和取消文本的显示内容
     */
    private String yesStr, noStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    /**
     * 构造函数
     *
     * @param context：上下文
     */
    public MyDialogSelectPicture(Context context) {
        super(context, R.style.MyDialog);
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog);

        //按空白处是否取消
        setCanceledOnTouchOutside(true);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        rlCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });

        //设置取消按钮被点击后，向外界提供监听
        rlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {

        //如果用户自定了title
        if (titleStr != null) {
            tvDialogTitle.setText(titleStr);
        }

        //如果设置按钮的文字
        if (yesStr != null) {
            btnCamera.setText(yesStr);
        }
        if (noStr != null) {
            btnPhoto.setText(noStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        tvDialogTitle = (TextView) findViewById(R.id.tvDialogTitle);
        rlCamera = (RelativeLayout) findViewById(R.id.rlCamera);
        rlPhoto = (RelativeLayout) findViewById(R.id.rlPhoto);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}

