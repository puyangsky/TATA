package com.avoscloud.leanchatlib_demo;

import com.avoscloud.leanchatlib.activity.AVChatActivity;
import com.avoscloud.leanchatlib.event.ImageItemClickEvent;
import com.avoscloud.leanchatlib.event.InputBottomBarLocationClickEvent;
import com.avoscloud.leanchatlib.event.LocationItemClickEvent;

/**
 * Created by lzw on 15/4/27.
 */
public class ChatRoomActivity extends AVChatActivity {

  public void onEvent(InputBottomBarLocationClickEvent event) {
    showToast("这里可以跳转到地图界面，选取地址");
  }

  public void onEvent(LocationItemClickEvent event) {
    showToast("这里跳转到地图界面，查看地理位置");
  }

  public void onEvent(ImageItemClickEvent event) {
    showToast("这里跳转到图片浏览页面，查看图片消息详情");
  }
}
