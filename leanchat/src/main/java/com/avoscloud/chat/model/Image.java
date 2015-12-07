package com.avoscloud.chat.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by lhq on 15/12/7.
 */
@AVClassName("Image")
public class Image extends AVObject {

    public static final String file = "file";

    public Image(){}

    public AVFile getFile(){
        return getAVFile(file);
    }

    public void setFile(AVFile avFile){
        put(file, avFile);
    }
}
