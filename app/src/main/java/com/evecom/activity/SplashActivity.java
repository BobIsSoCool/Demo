package com.evecom.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 欢迎页面
 * */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		//状态栏沉浸模式（安卓5.0之后才支持）
		if (Build.VERSION.SDK_INT >= 21) {

			View decorView = getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);

			//状态栏背景色
			getWindow().setStatusBarColor(Color.parseColor("#3a3c3b"));

		}

		//初始化Bmob
		Bmob.initialize(this, "234704f54ac1b1d67a645bad577e7c02");

		//判断是否是第一次运行
		isFirstRun();

		//判断是否有保存的主题，没有则给一个默认的主题
		SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
		int theme = sharedPreferences.getInt("theme",0);
		if (theme == 0){
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt("theme",R.style.CustomStyleOne);
			editor.commit();
		}


	}

	/**
	 * 判断是否是第一次运行
	 * */
	private void isFirstRun() {
		SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE); 
		boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true); 
		Editor editor = sharedPreferences.edit(); 
		if (isFirstRun){ 
			Toast.makeText(this, "第一次运行，将进入引导页...", Toast.LENGTH_SHORT).show();
			editor.putBoolean("isFirstRun", false); //第一次运行后，将isFirstRun设置为false
			editor.commit(); 
			mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 2000);
		} else{ 
			Toast.makeText(this, "不是第一次运行，直接进入登录页面...", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(SWITCH_LoginACTIVITY, 2000);
		}
	}
	
    /**
     * Handler:璺宠浆鑷充笉鍚岄〉闈�
	 **/
    private final static int SWITCH_LoginACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;

    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
            case SWITCH_LoginACTIVITY://进入登录页面
                Intent mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mIntent);
                finish();
                break;

            case SWITCH_GUIDACTIVITY://进入引导页面
                mIntent = new Intent(SplashActivity.this, GuidActivity.class);
                startActivity(mIntent);
                finish();
                break;
            }
            super.handleMessage(msg);
        }
    };
	
}
