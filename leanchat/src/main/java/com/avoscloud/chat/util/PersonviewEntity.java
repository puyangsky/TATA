package com.avoscloud.chat.util;

import java.util.ArrayList;

/**
 * Created by Puyangsky on 2015/12/13.
 */
public class PersonviewEntity {
    private String date;
    private String content;
    private ArrayList<String > imageUrls;

    public PersonviewEntity(String date, String content, ArrayList<String> imageUrls) {
        this.date = date;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
