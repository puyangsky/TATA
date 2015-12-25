package com.avoscloud.chat.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.activity.PersonViewActivity;
import com.avoscloud.chat.model.Moment;
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

    public static Moment getCachedMoment(String momentId) {
        return momentMap.get(momentId);
    }
	//获取缓存中所有的moment
	public static List<Moment> getCachedMoments() {
		List<Moment> list = new ArrayList<>();
		for (String key : momentMap.keySet()) {
			list.add(momentMap.get(key));
		}
		return list.isEmpty() ? null : list;
	}
	public static void registerMoments(List<Moment> moments) {
		cacheMoment();
	}
    public static void cacheMoment(String momentId, Moment moment) {
        momentMap.put(momentId, moment);
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

