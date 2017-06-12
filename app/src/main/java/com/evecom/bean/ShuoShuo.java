package com.evecom.bean;

import cn.bmob.v3.BmobObject;

/**
 * 说说实体类
 * Created by wub on 2017/4/7.
 */
public class ShuoShuo extends BmobObject {

    /**
     * 用户id
     */
    private String userObjectID;
    /**
     * 发布日期
     */
    private String date;
    /**
     * 说说内容
     */
    private String content;
    /**
     * 点赞数
     */
    private Integer dianZanCount = 0;
    /**
     * 评论数
     */
    private Integer discussCount = 0;

    /**
     * 是否是转发内容（默认false：即并非转发内容）
     */
    private Boolean zhaunFaOrNot = false;

    /**
     * 转发的说说的原文
     */
    private String zhuanFaContent;


    public String getUserObjectID() {
        return userObjectID;
    }

    public void setUserObjectID(String userObjectID) {
        this.userObjectID = userObjectID;
    }

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

    public Integer getDianZanCount() {
        return dianZanCount;
    }

    public void setDianZanCount(Integer dianZanCount) {
        this.dianZanCount = dianZanCount;
    }

    public Integer getDiscussCount() {
        return discussCount;
    }

    public void setDiscussCount(Integer discussCount) {
        this.discussCount = discussCount;
    }

    public Boolean getZhaunFaOrNot() {
        return zhaunFaOrNot;
    }

    public void setZhaunFaOrNot(Boolean zhaunFaOrNot) {
        this.zhaunFaOrNot = zhaunFaOrNot;
    }

    public String getZhuanFaContent() {
        return zhuanFaContent;
    }

    public void setZhuanFaContent(String zhuanFaContent) {
        this.zhuanFaContent = zhuanFaContent;
    }

    @Override
    public String toString() {
        return "ShuoShuo{" +
                "userObjectID='" + userObjectID + '\'' +
                ", date='" + date + '\'' +
                ", content='" + content + '\'' +
                ", dianZanCount=" + dianZanCount +
                ", discussCount=" + discussCount +
                ", zhaunFaOrNot=" + zhaunFaOrNot +
                ", zhuanFaContent='" + zhuanFaContent + '\'' +
                '}';
    }
}
