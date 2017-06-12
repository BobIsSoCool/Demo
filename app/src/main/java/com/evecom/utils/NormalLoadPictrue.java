package com.evecom.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.os.Message;

public class NormalLoadPictrue {
    /**
     * 图片的url
     */
    private String uri;
    /**
     * 通过url得到的图片的字节流
     */
    private byte[] picByte = null;

    /**
     *
     */
     private Handler handler;
     
    public NormalLoadPictrue(String uri,Handler handler){
        this.uri = uri;
        this.handler = handler;
        new Thread(runnable).start();
    }
     
//    @SuppressLint("HandlerLeak")
//    Handler handle = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1) {
//                if (picByte != null) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
//                }
//            }
//        }
//    };

    /**
     * 通过url得到图片的字节流
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
            	URL url = null;
            	if(uri.equals("default")){
            		url = new URL("http://img5.imgtn.bdimg.com/it/u=207139244,1603635459&fm=11&gp=0.jpg");
            	}else{
            		url = new URL(uri);
            	}
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                 
                if (conn.getResponseCode() == 200) {
                    InputStream fis =  conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = fis.read(bytes)) != -1) {
                        bos.write(bytes, 0, length);
                    }
                    picByte = bos.toByteArray();
                    bos.close();
                    fis.close();
                     
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     *
     * @return picByte：返回字节流
     */
    public byte[] getPicByte(){
        return  picByte;
    }
}
