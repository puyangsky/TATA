package com.avoscloud.chat.service;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;
import com.avoscloud.leanchatlib.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lzw on 14/12/19.
 * TODO 此类需要与 AVUserCacheUtils 合并
 */
public class CacheService {
  private static volatile List<String> friendIds = new ArrayList<String>();

  public static LeanchatUser lookupUser(String userId) {
    return AVUserCacheUtils.getCachedUser(userId);
  }

  public static void registerUser(LeanchatUser user) {
    AVUserCacheUtils.cacheUser(user.getObjectId(), user);
  }

  public static void registerUsers(List<LeanchatUser> users) {
    for (LeanchatUser user : users) {
      registerUser(user);
    }
  }

  public static List<String> getFriendIds() {
    return friendIds;
  }

  public static void setFriendIds(List<String> friendList) {
    friendIds.clear();
    if (friendList != null) {
        friendIds.addAll(friendList);
    }
  }

  public static void cacheUsers(List<String> ids) throws AVException {
    Set<String> uncachedIds = new HashSet<String>();
    for (String id : ids) {
      if (lookupUser(id) == null) {
        uncachedIds.add(id);
      }
    }
    List<LeanchatUser> foundUsers = findUsers(new ArrayList<String>(uncachedIds));
    registerUsers(foundUsers);
  }

  public static List<LeanchatUser> findUsers(List<String> userIds) throws AVException {
    if (userIds.size() <= 0) {
      return Collections.EMPTY_LIST;
    }
    AVQuery<LeanchatUser> q = AVUser.getQuery(LeanchatUser.class);
    q.whereContainedIn(Constants.OBJECT_ID, userIds);
    q.setLimit(1000);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    return q.find();
  }
}
