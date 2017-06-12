package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wub on 2017/5/11.
 * 用户之间聊天后生成一条记录
 */
public class ChatRecorder extends BmobObject{

    /**
     * 用户一id
     */
    private String idOfUserA;
    /**
     * 用户二id
     */
    private String idOfUserB;
    /**
     * 两者最后一条聊天内容
     */
    private String theLastMessage;
    /**
     * 未读消息数
     */
    private int countNotRead;
    /**
     * 未读消息所属用户id（即根据此字段判断未读消息是两个用户中的哪一个）
     */
    private String idHasMessageToRead;

    public String getIdOfUserA() {
        return idOfUserA;
    }

    public void setIdOfUserA(String idOfUserA) {
        this.idOfUserA = idOfUserA;
    }

    public String getIdOfUserB() {
        return idOfUserB;
    }

    public void setIdOfUserB(String idOfUserB) {
        this.idOfUserB = idOfUserB;
    }

    public String getTheLastMessage() {
        return theLastMessage;
    }

    public void setTheLastMessage(String theLastMessage) {
        this.theLastMessage = theLastMessage;
    }

    public int getCountNotRead() {
        return countNotRead;
    }

    public void setCountNotRead(int countNotRead) {
        this.countNotRead = countNotRead;
    }

    public String getIdHasMessageToRead() {
        return idHasMessageToRead;
    }

    public void setIdHasMessageToRead(String idHasMessageToRead) {
        this.idHasMessageToRead = idHasMessageToRead;
    }

    @Override
    public String toString() {
        return "ChatRecorder{" +
                "idOfUserA='" + idOfUserA + '\'' +
                ", idOfUserB='" + idOfUserB + '\'' +
                ", theLastMessage='" + theLastMessage + '\'' +
                ", countNotRead=" + countNotRead +
                ", idHasMessageToRead='" + idHasMessageToRead + '\'' +
                '}';
    }
}
