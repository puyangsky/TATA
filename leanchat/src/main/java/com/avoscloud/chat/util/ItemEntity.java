package com.avoscloud.chat.util;

import java.util.ArrayList;

/**
 * Created by Puyangsky on 2015/11/22.
 */
public class ItemEntity {
    private String avatar; // 用户头像URL
    private String username; // 用户名
    private String content; // 内容
    private ArrayList<String> imageUrls; // 九宫格图片的URL集合
    private String position;  //地点
    private String publishTime;   //发布时间

    public ItemEntity(String avatar, String username, String content, ArrayList<String> imageUrls, String position, String publishTime) {
        super();
        this.avatar = avatar;
        this.username = username;
        this.content = content;
        this.imageUrls = imageUrls;
        this.position = position;
        this.publishTime = publishTime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        return "ItemEntity [avatar=" + avatar + ", username=" + username + ", content=" + content + ", imageUrls=" + imageUrls + "position"
        + position + "publishTime" + publishTime + "]";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}