package com.avoscloud.chat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.avoscloud.chat.activity.ChatRoomActivity;
import com.avoscloud.chat.activity.ContactNewFriendActivity;
import com.avoscloud.chat.activity.EntrySplashActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.utils.Constants;

/**
 * Created by wli on 15/9/8.
 * 因为 notification 点击时，控制权不在 app，此时如果 app 被 kill 或者上下文改变后，
 * 有可能对 notification 的响应会做相应的变化，所以此处将所有 notification 都发送至此类，
 * 然后由此类做分发。
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (ChatManager.getInstance().getImClient() == null) {
      gotoLoginActivity(context);
    } else {
      String tag = intent.getStringExtra(Constants.NOTOFICATION_TAG);
      if (Constants.NOTIFICATION_GROUP_CHAT.equals(tag)) {
        //TODO 此处还要在好好测试一下
        gotoChatActivity(context, intent);
      } else if (Constants.NOTIFICATION_SINGLE_CHAT.equals(tag)) {
        gotoChatActivity(context, intent);
      } else if (Constants.NOTIFICATION_SYSTEM.equals(tag)) {
        gotoNewFriendActivity(context,intent);
      }
    }
  }

  /**
   * 如果 app 上下文已经缺失，则跳转到登陆页面，走重新登陆的流程
   * @param context
   */
  private void gotoLoginActivity(Context context) {
    Intent startActivityIntent = new Intent(context, EntrySplashActivity.class);
    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(startActivityIntent);
  }

  /**
   * 跳转至聊天页面
   * @param context
   * @param intent
   */
  private void gotoChatActivity(Context context, Intent intent) {
    Intent startActivityIntent = new Intent(context, ChatRoomActivity.class);
    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (intent.hasExtra(Constants.MEMBER_ID)) {
      startActivityIntent.putExtra(Constants.MEMBER_ID, intent.getStringExtra(Constants.MEMBER_ID));
    } else {
      startActivityIntent.putExtra(Constants.CONVERSATION_ID, intent.getStringExtra(Constants.CONVERSATION_ID));
    }
    context.startActivity(startActivityIntent);
  }

  private void gotoNewFriendActivity(Context context, Intent intent) {
    Intent startActivityIntent = new Intent(context, ContactNewFriendActivity.class);
    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(startActivityIntent);
  }
}
