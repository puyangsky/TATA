package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.MessageHelper;
import com.avoscloud.leanchatlib.utils.LocalCacheUtils;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.avoscloud.leanchatlib.view.PlayButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by wli on 15/9/17.
 */
public class ChatItemAudioHolder extends ChatItemHolder {

  protected PlayButton playButton;

  public ChatItemAudioHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_audio, null));
    playButton = (PlayButton) itemView.findViewById(R.id.playBtn);
    if (isLeft) {
      playButton.setBackgroundResource(R.drawable.chat_left_qp);
    } else {
      playButton.setBackgroundResource(R.drawable.chat_right_qp);
    }
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    if (o instanceof  AVIMAudioMessage) {
      AVIMAudioMessage audioMessage = (AVIMAudioMessage)o;
      playButton.setLeftSide(!MessageHelper.fromMe(audioMessage));
      if (TextUtils.isEmpty(audioMessage.getFileUrl())) {
        Class temp = audioMessage.getClass();
        try {
          //TODO 因为 sdk 不支持获取本地的 localFile 的地址，所以先使用反射，稍后 sdk 会支持
          Field f = temp.getDeclaredField("localFile");
          f.setAccessible(true);
          File localFile = (File)f.get(audioMessage);
          if (null != localFile) {
            playButton.setPath(localFile.getPath());
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        playButton.setPath(MessageHelper.getFilePath(audioMessage));
        LocalCacheUtils.downloadFileAsync(audioMessage.getFileUrl(), MessageHelper.getFilePath(audioMessage));
      }
    }
  }
}