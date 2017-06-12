package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * 评论实体类
 * Created by wub on 2017/4/7.
 */
public class PingLun extends BmobObject{

    /**
     * 文章id
     */
    private String articleObjectID;
    /**
     * 评论用户的id
     */
    private String userObjectID;
    /**
     * 该条评论的内容
     */
    private String pingLunContent;
    /**
     * 评论日期
     */
    private String date;

    public String getArticleObjectID() {
        return articleObjectID;
    }

    public void setArticleObjectID(String articleObjectID) {
        this.articleObjectID = articleObjectID;
    }

    public String getUserObjectID() {
        return userObjectID;
    }

    public void setUserObjectID(String userObjectID) {
        this.userObjectID = userObjectID;
    }

    public String getPingLunContent() {
        return pingLunContent;
    }

    public void setPingLunContent(String pingLunContent) {
        this.pingLunContent = pingLunContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PingLun{" +
                "articleObjectID='" + articleObjectID + '\'' +
                ", userObjectID='" + userObjectID + '\'' +
                ", pingLunContent='" + pingLunContent + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
