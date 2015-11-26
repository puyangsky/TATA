package com.avoscloud.chat.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.Conversation;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.util.ArrayList;

/**
 * Created by lhq on 15/11/23.
 */
@AVClassName("Moment")
public class Moment extends AVObject{

    private LeanchatUser user;    //存储user_key
    private String content;
    private ArrayList<String> imageUrls;
    private String position;
    //    private String createdAt; 在AVObject已经存在

    public LeanchatUser getUser() {
        return user;
    }

    public void setUser(LeanchatUser user) {
        this.user = user;
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

    public AVGeoPoint getPosition() {
        return getAVGeoPoint(position);
    }

    public void setPosition( AVGeoPoint point ) {
        put(position, point);
    }

}
