package com.avoscloud.chat.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.Conversation;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by lhq on 15/11/23.
 */
@AVClassName("Moment")
public class Moment extends AVObject{

//    private LeanchatUser user;    //存储user_key
//    final AVUser user = AVUser.getCurrentUser();      //获取当前的user

    private String user = "user";       //对应到leanCloud上的属性列
    private String content = "content";
    private String imageUrls = "imageUrls";
    private String position = "position";
    //    private String createdAt; 在AVObject已经存在

    public AVUser getUser() {
        return getAVUser(user);
    }

    public void setUser(AVUser toUser) {
        put(user, toUser);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return getList(imageUrls);
    }

    public void setImageUrls(List<String> imageUrls) {
        put(this.imageUrls, imageUrls);
    }

    public AVGeoPoint getPosition() {
        return getAVGeoPoint(position);
    }

    public void setPosition( AVGeoPoint point ) {
        put(position, point);
    }

}
