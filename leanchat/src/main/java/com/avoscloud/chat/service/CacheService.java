package com.avoscloud.chat.service;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;
import com.avoscloud.leanchatlib.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lzw on 14/12/19.
 * TODO 此类需要与 AVUserCacheUtils 合并
 */


public class CacheService {
  private static volatile List<String> friendIds = new ArrayList<String>();     //加载时是最新的

  public static LeanchatUser lookupUser(String userId) {
    return AVUserCacheUtils.getCachedUser(userId);
  }

  /**
   * 获取好友列表
   * @return
   */
  public static List<LeanchatUser> getFriends(){
    LeanchatUser currentUser = LeanchatUser.getCurrentUser();
    List<LeanchatUser> friends = new ArrayList<LeanchatUser>();
    friends.add(currentUser);
    for(String friendId : friendIds){
      friends.add(CacheService.lookupUser(friendId));
    }
    return friends;
  }

  public static void registerUser(LeanchatUser user) {
    AVUserCacheUtils.cacheUser(user.getObjectId(), user);
  }

  public static void registerUsers(List<LeanchatUser> users) {
    for (LeanchatUser user : users) {
      Log.e("registerUsers", user.getObjectId());
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
    Log.e("foundUser", "" + foundUsers.size());
    registerUsers(foundUsers);    //这里cache好友
  }

  /**
   * 先查询，然后缓存好友id，然后再缓存好友
   * @param userIds
   * @return
   * @throws AVException
   */
  public static List<LeanchatUser> findUsers(List<String> userIds) throws AVException {
    if (userIds.size() <= 0) {
      return Collections.EMPTY_LIST;
    }
    AVQuery<LeanchatUser> q = AVUser.getQuery(LeanchatUser.class);
    q.whereContainedIn(Constants.OBJECT_ID, userIds);
//    q.setLimit(20);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    return q.find();
  }

  /**
   * 缓存好友列表
   * @return
   * @throws Exception
   */
//  public static List<LeanchatUser> findFriends() throws Exception {
//    final List<LeanchatUser> friends = new ArrayList<LeanchatUser>();
//    final AVException[] es = new AVException[1];
//    final CountDownLatch latch = new CountDownLatch(1);
//    LeanchatUser.getCurrentUser(LeanchatUser.class).findFriendsWithCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, new FindCallback<LeanchatUser>() {
//      @Override
//      public void done(List<LeanchatUser> avUsers, AVException e) {
//        if (e != null) {
//          es[0] = e;
//        } else {
//          friends.addAll(avUsers);
//        }
//        latch.countDown();
//      }
//    });
//    latch.await();
//    if (es[0] != null) {
//      throw es[0];
//    } else {
//      List<String> userIds = new ArrayList<String>();
//      for (LeanchatUser user : friends) {
//        userIds.add(user.getObjectId());
////          Log.e("friend", user.getUsername());
////        CacheService.registerUser(user);
//      }
//      CacheService.setFriendIds(userIds);
//      CacheService.cacheUsers(userIds);     //cache之后才能找得到
//        return getFriends();
//    }
//  }

  public static List<LeanchatUser> findFriends() throws Exception {
      final List<LeanchatUser> friends = new ArrayList<LeanchatUser>();
      final AVException[] es = new AVException[1];
      final CountDownLatch latch = new CountDownLatch(1);
      LeanchatUser.getCurrentUser(LeanchatUser.class).findFriendsWithCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, new FindCallback<LeanchatUser>() {
          @Override
          public void done(List<LeanchatUser> avUsers, AVException e) {
              if (e != null) {
                  es[0] = e;
              } else {
                  friends.addAll(avUsers);
              }
              latch.countDown();
          }
      });
      latch.await();
      if (es[0] != null) {
          throw es[0];
      } else {
          List<String> userIds = new ArrayList<String>();
          for (LeanchatUser user : friends) {
              userIds.add(user.getObjectId());
          }
          CacheService.setFriendIds(userIds);
          CacheService.cacheUsers(userIds);
          List<LeanchatUser> newFriends = new ArrayList<>();
          for (LeanchatUser user : friends) {
              newFriends.add(CacheService.lookupUser(user.getObjectId()));
          }
          return newFriends;
      }
  }
}
