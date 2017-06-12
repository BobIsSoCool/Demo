package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * 已关注用户实体类
 * Created by wub on 2017/4/5.
 */
public class FollowedUser extends BmobObject{

    /**
     * 关注方用户id
     */
    private String whoCareID;
    /**
     * 被关注用户id
     */
    private String careWhoID;


    public String getWhoCareID() {
        return whoCareID;
    }

    public void setWhoCareID(String whoCareID) {
        this.whoCareID = whoCareID;
    }

    public String getCareWhoID() {
        return careWhoID;
    }

    public void setCareWhoID(String careWhoID) {
        this.careWhoID = careWhoID;
    }

    @Override
    public String toString() {
        return "FollowedUser{" +
                "whoCareID='" + whoCareID + '\'' +
                ", careWhoID='" + careWhoID + '\'' +
                '}';
    }
}
