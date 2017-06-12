package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * 动态实体类
 * Created by wub on 2017/3/25.
 */
public class Dongtai extends BmobObject{

    /**
     * 日期
     */
    private String date;
    /**
     * 动态内容
     */
    private String content;
    /**
     * 所属用户objectID
     */
    private String userObjectID;

    /**
     *
     * @return
     */
    //get 、set

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserObjectID() {
        return userObjectID;
    }

    public void setUserObjectID(String userObjectID) {
        this.userObjectID = userObjectID;
    }

    @Override
    public String toString() {
        return "Dongtai{" +
                "date='" + date + '\'' +
                ", content='" + content + '\'' +
                ", userObjectID='" + userObjectID + '\'' +
                '}';
    }
}
