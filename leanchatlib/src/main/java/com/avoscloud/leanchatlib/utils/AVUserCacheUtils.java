package com.avoscloud.leanchatlib.utils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wli on 15/9/30.
 */
public class AVUserCacheUtils {

  private static Map<String, LeanchatUser> userMap;

  static {
    userMap = new HashMap<String, LeanchatUser>();
  }

  public static LeanchatUser getCachedUser(String objectId) {
    return userMap.get(objectId);
  }

  public static void cacheUser(String userId, LeanchatUser user) {
    userMap.put(userId, user);
  }

  public static void cacheUsers(List<String> ids, final CacheUserCallback cacheUserCallback) {
    Set<String> uncachedIds = new HashSet<String>();
    for (String id : ids) {
      if (!userMap.containsKey(id)) {
        uncachedIds.add(id);
      }
    }

    if (uncachedIds.isEmpty()) {
      if (null != cacheUserCallback) {
        cacheUserCallback.done(null);
        return;
      }
    }

    AVQuery<LeanchatUser> q = AVUser.getQuery(LeanchatUser.class);
    q.whereContainedIn(Constants.OBJECT_ID, uncachedIds);
    q.setLimit(1000);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    q.findInBackground(new FindCallback<LeanchatUser>() {
      @Override
      public void done(List<LeanchatUser> list, AVException e) {
        if (null == e) {
          for (AVUser user : list) {
            userMap.put(user.getObjectId(), (LeanchatUser)user);
          }
        }
        if (null != cacheUserCallback) {
          cacheUserCallback.done(e);
        }
      }
    });
  }

  public static List<LeanchatUser> getUsersFromCache(List<String> ids) {
    List<LeanchatUser> userList = new ArrayList<LeanchatUser>();
    for (String id : ids) {
      if (userMap.containsKey(id)) {
        userList.add(userMap.get(id));
      }
    }
    return userList;
  }

  public static void cacheUsers(List<String> ids) {
    cacheUsers(ids, null);
  }

  public static abstract class CacheUserCallback {
    public abstract void done(Exception e);
  }
}
