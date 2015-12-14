package com.avoscloud.chat.model;

import android.provider.MediaStore;
import android.util.Log;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lhq on 15/11/23.
 */
@AVClassName("Moment")
public class Moment extends AVObject{


    public static final String user = "user";       // 这里让AVUser自动转换成leanchatUser
    public static final String content = "content";
    public static final String position = "position";
    public static final String fileList = "fileList";
    public static final String zan = "zan";
    public static final String type = "type";
    public static final String comment = "comment"; //对应的评论关系

    public Moment(){}

    /**
     * 获取某个用户的所有Moment
     * @param findUser
     * @return
     */
    public static List<Moment> getMomentByUser(LeanchatUser findUser){
        final List<Moment> list = new LinkedList<Moment>();
        AVQuery<Moment> query = AVObject.getQuery(Moment.class);
        query.orderByDescending("createdAt");
        query.include("createdAt");
        query.include(user);
        query.include(content);
        query.include(position);
        query.include(fileList);
        query.include(zan);
        query.whereEqualTo(user, findUser);
        query.findInBackground(new FindCallback<Moment>() {
            @Override
            public void done(List<Moment> results, AVException e) {
                if (e != null || results == null) {
                    return;
                }
                for (Moment moment : results) {
                    list.add(moment);
                    List<Image> fileList = moment.getFileList();
                    if (fileList == null) {
                        Log.e("fileList = ", "null");
                        continue;
                    }
                    for (Image file : fileList) {
                        Log.e("Image", "search start");
                        if (file.getFile() != null) {
                            Log.e("fileUrl = ", file.getFile().getUrl());
                        } else {
                            Log.e("fileUrl = ", "null");
                        }
                    }
                }
            }
        });
        Log.e("Moment find", "list size = " + list.size());
        return list;
    }


    public AVRelation<Comment> getComment(){
        return getRelation(comment);
    }

    public void addComment(Comment com){
        AVRelation<Comment> relation = getComment();
        relation.add(com);
    }

    public void removeComment(Comment com){
        AVRelation<Comment> relation = getComment();
        relation.remove(com);
    }

    public int getZan(){ return getInt(zan);}

    public void addZan(){ increment(zan);}

    @SuppressWarnings("unchecked")
    public List<Image> getFileList(){
        return (List<Image>)getList(fileList);
    }

    public void setFileList(List<Image> list){
        addAll(fileList, list);
    }

    public void addFile(Image image){
        add(fileList, image);
    }

    public LeanchatUser getUser() {
        return getAVUser(user);
    }

    public void setUser(LeanchatUser toUser) {
        put(user, toUser);
    }

    public String getContent() {
        return getString(content);
    }

    public void setContent(String publish_content) {
        put(content, publish_content);
    }

    public AVGeoPoint getPosition() {
        return getAVGeoPoint(position);
    }

    public void setPosition( AVGeoPoint point ) {
        put(position, point);
    }

    public void setType(int newType){ put(type, newType); }

    public int getType(){ return getInt(type); }
}
