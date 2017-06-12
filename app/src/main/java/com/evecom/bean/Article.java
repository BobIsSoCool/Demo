package com.evecom.bean;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by Bob on 2017/3/3.
 * 文章的实体类
 */
public class Article extends BmobObject {

    /**
     * 作者(即所属用户)
     */
    private String userObjectID;
    /**
     * 发布时间
     */
    private String publishDate;
    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 内容（富文本内容，包括图文,以String形式保存,图片保存的是本地存储路径）
     */
    private String articleCotent;
    /**
     * 点赞数
     * 默认为0
     */
    private Integer likeNumber = 0;
    /**
     * 评论数
     * 默认为0
     */
    private Integer discussNumber = 0;
    /**
     * 分类码
     */
    private String articleType;

    @Override
    public String toString() {
        return "Article{" +
                "userObjectID='" + userObjectID + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleCotent='" + articleCotent + '\'' +
                ", likeNumber=" + likeNumber +
                ", discussNumber=" + discussNumber +
                ", articleType='" + articleType + '\'' +
                '}';
    }

    public String getUserObjectID() {
        return userObjectID;
    }

    public void setUserObjectID(String userObjectID) {
        this.userObjectID = userObjectID;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleCotent() {
        return articleCotent;
    }

    public void setArticleCotent(String articleCotent) {
        this.articleCotent = articleCotent;
    }

    public Integer getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(Integer likeNumber) {
        this.likeNumber = likeNumber;
    }

    public Integer getDiscussNumber() {
        return discussNumber;
    }

    public void setDiscussNumber(Integer discussNumber) {
        this.discussNumber = discussNumber;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }
}
