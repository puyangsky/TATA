package com.avoscloud.chat.model;

import android.util.Log;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

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
//    private String imageUrls = "imageUrls";
    private String position = "position";
    private String fileList = "fileList";

//    private String momentFileArray = "momentFileArray";         //对应图片的url
    //    private String createdAt; 在AVObject已经存在

    public List<AVFile> getFileList(){
        return getList("fileList");
    }

    public void setFileList(List<AVFile> list){
        addAll(fileList, list);
    }

    public AVUser getUser() {
        return getAVUser(user);
    }

    public void setUser(AVUser toUser) {
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
