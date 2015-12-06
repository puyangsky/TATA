package com.avoscloud.chat.model;

import android.util.Log;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lhq on 15/11/23.
 */
@AVClassName("Moment")
public class Moment extends AVObject{

//    private LeanchatUser user;    //存储user_key
//    final AVUser user = AVUser.getCurrentUser();      //获取当前的user

    public static final String user = "user";       // 这里让AVUser自动转换成leanchatUser
    public static final String content = "content";
//    private String imageUrls = "imageUrls";
    public static final String position = "position";
    public static final String fileList = "fileList";
    public static final String zan = "zan";

    public static final String comment = "comment"; //对应的评论关系

//    private String momentFileArray = "momentFileArray";         //对应图片的url
    //    private String createdAt; 在AVObject已经存在

    public Moment(){}

    public static List<Moment> getMomentByUser(LeanchatUser findUser){
        final List<Moment> list = new LinkedList<>();
        AVQuery<Moment> query = AVObject.getQuery(Moment.class);
        query.whereEqualTo(user, findUser);
        query.findInBackground(new FindCallback<Moment>() {
            @Override
            public void done(List<Moment> results, AVException e) {
                if (e != null || results == null) {
                    return ;
                }
                for (Moment moment : results) {
                    list.add(moment);
//                    LogUtil.log.d("content=" + moment.getContent());
                }
            }
        });
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

    public List<AVFile> getFileList(){
        return getList("fileList");
    }

    public void setFileList(List<AVFile> list){
        addAll(fileList, list);
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

//    public List<String> getImageUrls() {
//        return getList(imageUrls);
//    }
//
//    public void setImageUrls(List<String> images) {
//        put(imageUrls, images);
//    }

    public AVGeoPoint getPosition() {
        return getAVGeoPoint(position);
    }

    public void setPosition( AVGeoPoint point ) {
        put(position, point);
    }

}
