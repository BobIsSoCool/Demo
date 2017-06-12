package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * 聊天消息实体类
 * Created by wub on 2017/4/19.
 */
public class ChatMessage extends BmobObject {

    /**
     * 发送者id
     */
    private String idOfTheSender;
    /**
     * 接收者id
     */
    private String idOfTheReciever;
    /**
     * 聊天内容
     */
    private String message;
    /**
     * 该条聊天发布的时间
     */
    private String date;
    /**
     * 布尔值，用于表示该条消息是否已读
     */
    private Boolean readOrNot;

    public ChatMessage(){}
    public ChatMessage(String idOfTheSender, String idOfTheReciever, String message, String date, Boolean readOrNot) {
        this.idOfTheSender = idOfTheSender;
        this.idOfTheReciever = idOfTheReciever;
        this.message = message;
        this.date = date;
        this.readOrNot = readOrNot;
    }

    public String getIdOfTheSender() {
        return idOfTheSender;
    }

    public void setIdOfTheSender(String idOfTheSender) {
        this.idOfTheSender = idOfTheSender;
    }

    public String getIdOfTheReciever() {
        return idOfTheReciever;
    }

    public void setIdOfTheReciever(String idOfTheReciever) {
        this.idOfTheReciever = idOfTheReciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getReadOrNot() {
        return readOrNot;
    }

    public void setReadOrNot(Boolean readOrNot) {
        this.readOrNot = readOrNot;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "idOfTheSender='" + idOfTheSender + '\'' +
                ", idOfTheReciever='" + idOfTheReciever + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", readOrNot=" + readOrNot +
                '}';
    }
}
