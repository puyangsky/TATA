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

    public static final String images = "images";


//    private String momentFileArray = "momentFileArray";         //对应图片的url
    //    private String createdAt; 在AVObject已经存在

    public Moment(){}

    /**
     * 获取某个用户的所有Moment
     * @param findUser
     * @return
     */
    public static List<Moment> getMomentByUser(LeanchatUser findUser){
        final List<Moment> list = new LinkedList<>();
        AVQuery<Moment> query = AVObject.getQuery(Moment.class);
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
                    Log.e("fileUrl = ", fileList.get(0).getFile().getUrl());
//                    LogUtil.log.d("content = " + moment.getContent());
//                    List<AVFile> list = moment.getFileList();
//                    for (AVFile file : list) {
//                        LogUtil.log.d("url = " + file.getUrl());
//                    }
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

//    public List<AVFile> getFileList(){
//        List<AVObject>list = getList(fileList);
//        List<AVFile> fileList = new LinkedList<AVFile>();
//        for(AVObject file : list){
//            AVFile avFile = AVFile.withAVObject(file);
//            fileList.add(avFile);
//        }
//        return fileList;
//    }
//
//    public void setFileList(List<AVFile> list){
//        addAll(fileList, list);
//    }

    public List<Image> getFileList(){
        return (List<Image>)getList(fileList, Image.class);
    }

    public void setFileList(List<Image> list){
        addAll(fileList, list);
    }

    public void addFile(Image image){
        add(fileList, image);
    }

    public void setImages(List<Image> list){
        addAll(images, list);
    }

    public void addImages(Image image){
        add(images, image);
    }

//    public List<String> getFileList() {
//        String s = getString(fileList);
//        String[] array = s.split(",");
//        List<String> list = new ArrayList<>();
//        for(int i=0; i<array.length; i++){
//            list.add(array[i]);
//        }
//        return list;
//    }
//
//    public void setFileList(List<String> list){
//        String urls = new String();
//        boolean flag = true;
//        for(String file : list){
//            if(flag){
//                urls += file;
//                flag = false;
//            }else{
//                urls += ","+file;
//            }
//        }
//        put(fileList, urls);
//    }

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
