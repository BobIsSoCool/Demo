package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * 我的收藏实体类
 * Created by wub on 2017/4/6.
 */
public class MyCollection extends BmobObject{

    /**
     * 用户id
     */
    private String userObjectID;
    /**
     * 文章id
     */
    private String articleObjectID;

    public String getUserObjectID() {
        return userObjectID;
    }

    public void setUserObjectID(String userObjectID) {
        this.userObjectID = userObjectID;
    }

    public String getArticleObjectID() {
        return articleObjectID;
    }

    public void setArticleObjectID(String articleObjectID) {
        this.articleObjectID = articleObjectID;
    }

    @Override
    public String toString() {
        return "MyCollection{" +
                "userObjectID='" + userObjectID + '\'' +
                ", articleObjectID='" + articleObjectID + '\'' +
                '}';
    }
}
