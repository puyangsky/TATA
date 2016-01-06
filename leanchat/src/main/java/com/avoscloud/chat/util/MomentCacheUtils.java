package com.avoscloud.chat.util;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.activity.PersonViewActivity;
import com.avoscloud.chat.fragment.SquareFragment;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by puyangsky on 2015/12/24.
 */
public class MomentCacheUtils {
    public static volatile Map<String, Moment> momentMap;

    static {
        momentMap = new HashMap<String, Moment>();
    }

	//获取缓存中所有的moment
	public static List<Moment> getCachedMoments() {
		List<Moment> list = new ArrayList<>();
		for (String key : momentMap.keySet()) {
			list.add(momentMap.get(key));
		}
		Log.d("pyt", "momentMap 大小 ：" + String.valueOf(momentMap.size()) + "\nlist大小 :" + list.size());
		return list.isEmpty() ? null : list;
	}
	public static Moment getCachedMoment(String momentId) {
		return momentMap.get(momentId);
	}

    public static void cacheMoment(String momentId, Moment moment) {
	    if (!momentMap.containsKey(momentId)) {
		    momentMap.put(momentId, moment);
	    }
    }
	public static void registerMoments() {
		AVQuery<Moment> query = AVObject.getQuery(Moment.class);
		query.orderByDescending("createdAt");
		query.include("user");
		query.include("content");
		query.include("createdAt");
		query.include("position");
		query.include("fileList");          //这里include一个类的数据，会自动填充
		query.include("zan");
		List<LeanchatUser> friends = new ArrayList<>();
		try {
			friends = CacheService.findFriends();
			friends = CacheService.getFriends();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("pyt", "friends 大小：" + friends.size());
		query.whereContainedIn("user", friends);
		query.findInBackground(new FindCallback<Moment>() {
			@Override
			public void done(List<Moment> results, AVException e) {
				if (e != null || results == null) {
					return;
				} else {
					SquareFragment.moments = results;
					for (Moment moment : results) {
						cacheMoment(moment.getObjectId(), moment);
					}
				}
				Log.d("pyt", "缓存之后的map大小：" + momentMap.size());
			}
		});

	}
    public static void cacheMoments(List<String> ids, final CacheMomentCallback cacheMomentCallback) {
        //use set for none-repeat Object id
        Set<String> uncachedIds = new HashSet<String>();
        for (String id : ids) {
            if (!momentMap.containsKey(id)) {
                uncachedIds.add(id);
            }
            if (uncachedIds.isEmpty()) {
                if (null != cacheMomentCallback) {
                    cacheMomentCallback.done(null);
                    return;
                }
            }

            AVQuery<Moment> q = AVObject.getQuery(Moment.class);
            q.whereContainedIn(Constants.OBJECT_ID, uncachedIds);
            q.setLimit(1000);
            q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
            q.findInBackground(new FindCallback<Moment>() {
                @Override
                public void done(List<Moment> list, AVException e) {
                    if (null == e) {
                        for (Moment moment : list) {
                            momentMap.put(moment.getObjectId(), moment);
                        }
                    }
                    if (null != cacheMomentCallback) {
                        cacheMomentCallback.done(e);
                    }
                }
            });
        }
    }

    public static List<Moment> getMomentFromCache(List<String> ids) {
        List<Moment> momentList = new ArrayList<>();
        for (String id: ids) {
            if (momentMap.containsKey(id)) {
                momentList.add(momentMap.get(id));
            }
        }
        return momentList.isEmpty() ? null : momentList;
    }

    public static void cacheMoments (List<String> ids) {
        cacheMoments(ids, null);
    }

    public static abstract class CacheMomentCallback {
        public abstract void done(Exception e);
    }
}

