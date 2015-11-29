package com.avoscloud.chat.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by lhq on 15/11/28.
 */
@AVClassName("MomentFileArray")
public class MomentFileArray extends AVObject{
    private String moment = "moment";   //发布消息的id
    private String file = "file";       //发布消息对应的图片文件

    public AVFile getFile() {
        return getAVFile(file);
    }

    public void setFile(AVFile new_file) {
        put(file, new_file);
    }

    public Moment getMoment(){
        Moment m = null;
        try {
            m = getAVObject(moment, Moment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public void setMoment(Moment m){
        put(moment, m);
    }
}
