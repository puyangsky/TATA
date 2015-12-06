package com.avoscloud.chat.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avoscloud.leanchatlib.model.LeanchatUser;

/**
 * Created by lhq on 15/12/4.
 */
@AVClassName("Reply")
public class Reply extends AVObject{
    public static final String user = "user";
    public static final String content = "content";

    public LeanchatUser getUser(){
        return getAVUser(user);
    }

    public void setUser(LeanchatUser toUser){
        put(user, toUser);
    }

    public String getContent(){
        return getString(content);
    }

    public void setContent(String con){
        put(content, con);
    }
}
