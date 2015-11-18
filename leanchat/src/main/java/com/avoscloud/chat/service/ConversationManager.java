package com.avoscloud.chat.service;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.App;
import com.avoscloud.chat.event.ConversationChangeEvent;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.MessageAgent;
import com.avoscloud.leanchatlib.controller.MessageHelper;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.Room;
import com.avoscloud.leanchatlib.utils.AVIMConversationCacheUtils;
import com.avoscloud.leanchatlib.utils.Constants;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lzw on 15/2/11.
 */
public class ConversationManager {
  private static ConversationManager conversationManager;

  private static AVIMConversationEventHandler eventHandler = new AVIMConversationEventHandler() {
    @Override
    public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members, String kickedBy) {
      Logger.i(MessageHelper.nameByUserIds(members) + " left, kicked by " + MessageHelper.nameByUserId(kickedBy));
      refreshCacheAndNotify(conversation);
    }

    @Override
    public void onMemberJoined(AVIMClient client, AVIMConversation conversation, List<String> members, String invitedBy) {
      Logger.i(MessageHelper.nameByUserIds(members) + " joined , invited by " + MessageHelper.nameByUserId(invitedBy));
      refreshCacheAndNotify(conversation);
    }

    private void refreshCacheAndNotify(AVIMConversation conversation) {
      ConversationChangeEvent conversationChangeEvent = new ConversationChangeEvent(conversation);
      EventBus.getDefault().post(conversationChangeEvent);
    }

    @Override
    public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {
      refreshCacheAndNotify(conversation);
      Logger.i("you are kicked by " + MessageHelper.nameByUserId(kickedBy));
    }

    @Override
    public void onInvited(AVIMClient client, AVIMConversation conversation, String operator) {
      Logger.i("you are invited by " + MessageHelper.nameByUserId(operator));
      refreshCacheAndNotify(conversation);
    }
  };

  public ConversationManager() {
  }

  public static synchronized ConversationManager getInstance() {
    if (conversationManager == null) {
      conversationManager = new ConversationManager();
    }
    return conversationManager;
  }

  public static AVIMConversationEventHandler getEventHandler() {
    return eventHandler;
  }

  public void findAndCacheRooms(final Room.MultiRoomsCallback callback) {
    final List<Room> rooms = ChatManager.getInstance().findRecentRooms();
    List<String> conversationIds = new ArrayList<>();
    for (Room room : rooms) {
      conversationIds.add(room.getConversationId());
    }

    if (conversationIds.size() > 0) {
      AVIMConversationCacheUtils.cacheConversations(conversationIds, new AVIMConversationCacheUtils.CacheConversationCallback() {
        @Override
        public void done(AVException e) {
          if (e != null) {
            callback.done(rooms, e);
          } else {
            callback.done(rooms, null);
          }
        }
      });
    } else {
      callback.done(rooms, null);
    }
  }

  public void updateName(final AVIMConversation conv, String newName, final AVIMConversationCallback callback) {
    conv.setName(newName);
    conv.updateInfoInBackground(new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        if (e != null) {
          if (callback != null) {
            callback.done(e);
          }
        } else {
          if (callback != null) {
            callback.done(null);
          }
        }
      }
    });
  }

  public void findGroupConversationsIncludeMe(AVIMConversationQueryCallback callback) {
    AVIMConversationQuery conversationQuery = ChatManager.getInstance().getConversationQuery();
    if (null != conversationQuery) {
      conversationQuery.containsMembers(Arrays.asList(ChatManager.getInstance().getSelfId()));
      conversationQuery.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Group.getValue());
      conversationQuery.orderByDescending(Constants.UPDATED_AT);
      conversationQuery.limit(1000);
      conversationQuery.findInBackground(callback);
    } else if (null != callback) {
      callback.done(new ArrayList<AVIMConversation>(), null);
    }
  }

  public void createGroupConversation(List<String> members, final AVIMConversationCreatedCallback callback) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(ConversationType.TYPE_KEY, ConversationType.Group.getValue());
    final String name = MessageHelper.nameByUserIds(members);
    map.put("name", name);
    ChatManager.getInstance().createConversation(members, map, callback);
  }

  public static Bitmap getConversationIcon(AVIMConversation conversation) {
    return ColoredBitmapProvider.getInstance().createColoredBitmapByHashString(conversation.getConversationId());
  }

  public void sendWelcomeMessage(String toUserId) {
    ChatManager.getInstance().fetchConversationWithUserId(toUserId,
        new AVIMConversationCreatedCallback() {
          @Override
          public void done(AVIMConversation avimConversation, AVIMException e) {
            if (e == null) {
              MessageAgent agent = new MessageAgent(avimConversation);
              agent.sendText(App.ctx.getString(R.string.message_when_agree_request));
            }
          }
        });
  }
}
