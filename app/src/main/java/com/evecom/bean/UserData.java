package com.evecom.bean;

/**
 * Created by wub on 2017/4/11.
 */
public class UserData {

    /**
     * id
     */
    private String userObjectID;
    /**
     * 头像url
     */
    private String avatarUrl;
    /**
     * 昵称
     */
    private String userName;
    /**
     * 签名
     */
    private String qianMing;

    public String getUserObjectID() {
        return userObjectID;
    }

    public void setUserObjectID(String userObjectID) {
        this.userObjectID = userObjectID;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQianMing() {
        return qianMing;
    }

    public void setQianMing(String qianMing) {
        this.qianMing = qianMing;
    }
}