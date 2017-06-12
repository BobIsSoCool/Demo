package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * 点赞记录实体类
 * Created by wub on 2017/4/12.
 */
public class Zan extends BmobObject{

    /**
     * 被赞对象（文章、说说）的id
     */
    private String id;
    /**
     * 点赞用户的id
     */
    private String userObjectID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserObjectID() {
        return userObjectID;
    }

    public void setUserObjectID(String userObjectID) {
        this.userObjectID = userObjectID;
    }
}
