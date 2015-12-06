package com.avoscloud.chat.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avoscloud.leanchatlib.model.LeanchatUser;

/**
 * Created by lhq on 15/12/4.
 */
@AVClassName("Comment")
public class Comment extends AVObject{
    public static final String user = "user";
    public static final String content = "content";
    public static final String reply = "reply";

    public AVRelation<Reply> getReply(){
        return getRelation(reply);
    }

    public void addReply(Reply reply){
        AVRelation<Reply> relation = getReply();
        relation.add(reply);
    }

    public void removeReply(Reply reply){
        AVRelation<Reply> relation = getReply();
        relation.remove(reply);
    }

    public LeanchatUser getUser() {
        return getAVUser(user);
    }

    public void setUser(LeanchatUser toUser) {
        put(user, toUser);
    }

    public String getContent(){
        return getString(content);
    }

    public void setContent(String con){
        put(content, con);
    }
}
