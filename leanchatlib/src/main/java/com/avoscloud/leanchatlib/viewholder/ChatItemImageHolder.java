package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.MessageHelper;
import com.avoscloud.leanchatlib.event.ImageItemClickEvent;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.lang.reflect.Field;

import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/9/17.
 */
public class ChatItemImageHolder extends ChatItemHolder {

  protected ImageView contentView;

  public ChatItemImageHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    if (isLeft) {
      conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_image_layout, null));
      contentView = (ImageView)itemView.findViewById(R.id.chat_item_left_image_view);
      contentView.setBackgroundResource(R.drawable.chat_left_qp);
    } else {
      conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_left_image_layout, null));
      contentView = (ImageView)itemView.findViewById(R.id.chat_item_left_image_view);
      contentView.setBackgroundResource(R.drawable.chat_right_qp);
    }

    contentView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ImageItemClickEvent clickEvent = new ImageItemClickEvent();
        clickEvent.message = message;
        EventBus.getDefault().post(clickEvent);
      }
    });
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    AVIMMessage message = (AVIMMessage)o;
    if (message instanceof AVIMImageMessage) {
      AVIMImageMessage imageMsg = (AVIMImageMessage) message;
      if (TextUtils.isEmpty(imageMsg.getFileUrl())) {
        Class temp = imageMsg.getClass();
        try {
          //TODO 因为 sdk 不支持获取本地的 localFile 的地址，所以先使用反射，稍后 sdk 会支持
          Field f = temp.getDeclaredField("localFile");
          f.setAccessible(true);
          File localFile = (File)f.get(imageMsg);
          if (null != localFile) {
            ImageLoader.getInstance().displayImage(localFile.getPath(), contentView);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

      } else {
        PhotoUtils.displayImageCacheElseNetwork(contentView, MessageHelper.getFilePath(imageMsg),
          imageMsg.getFileUrl());
      }
    }
  }
}