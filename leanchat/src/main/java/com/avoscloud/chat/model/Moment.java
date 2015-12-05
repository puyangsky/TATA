package com.avoscloud.chat.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.util.List;

/**
 * Created by lhq on 15/11/23.
 */
@AVClassName("Moment")
public class Moment extends AVObject{

//    private LeanchatUser user;    //存储user_key
//    final AVUser user = AVUser.getCurrentUser();      //获取当前的user

    private String user = "user";       // 这里让AVUser自动转换成leanchatUser
    private String content = "content";
//    private String imageUrls = "imageUrls";
    private String position = "position";
    private String fileList = "fileList";
    private String zan = "zan";

    private String comment = "comment"; //对应的评论关系

//    private String momentFileArray = "momentFileArray";         //对应图片的url
    //    private String createdAt; 在AVObject已经存在

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
