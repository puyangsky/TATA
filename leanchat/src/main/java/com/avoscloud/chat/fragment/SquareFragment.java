package com.avoscloud.chat.fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.LocationActivity;
import com.avoscloud.chat.adapter.ListItemAdapter;
import com.avoscloud.chat.model.Image;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.chat.util.GetCity;
import com.avoscloud.chat.util.ItemEntity;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by puyangsky on 2015/12/3.
 */
public class SquareFragment extends BaseFragment{
    private ListView mListView;
    private ArrayList<ItemEntity> itemEntities;
    private ListItemAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.square_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView) getView().findViewById(R.id.square_list_item);
        initData();
        adapter = new ListItemAdapter(getActivity(), itemEntities);
        mListView.setAdapter(adapter);
        headerLayout.showTitle(R.string.square_title);
    }


    public void initData(){
        itemEntities = new ArrayList<ItemEntity>();
        final LeanchatUser currentUser = (LeanchatUser) AVUser.getCurrentUser();
        AVQuery<Moment> query = AVObject.getQuery(Moment.class);
        query.orderByDescending("createdAt");
        query.setLimit(10);
        query.include("user");
        query.include("content");
        query.include("createdAt");
        query.include("position");
        query.include("fileList");          //这里include一个类的数据，会自动填充
        query.include("zan");
        Log.e("friends", CacheService.getFriendIds().toString());
        List<LeanchatUser> friends = new ArrayList<LeanchatUser>();
        friends.add(currentUser);
        for(String friendId : CacheService.getFriendIds()){
            friends.add(CacheService.lookupUser(friendId));
        }
        query.whereContainedIn("user", friends );
        query.findInBackground(new FindCallback<Moment>() {
            @Override
            public void done(List<Moment> results, AVException e) {
                if (e != null || results == null) {
                    return;
                }
                for (Moment moment : results) {
                    List<Image> imageList = moment.getFileList();
                    //图片为空，略过
                    if (imageList == null) {
                        Log.e("fileList = ", "null");
                        continue;
                    }
                    //获取图片urls
                    ArrayList<String> imageUrls = new ArrayList<String>();
                    for (Image image : imageList) {
                        imageUrls.add(image.getFile().getUrl());
                        Log.e("itemEntitiesUrl", image.getFile().getUrl());
                    }
                    try {
                        String city = "";
                        final double lat = moment.getPosition().getLatitude();
                        final double log = moment.getPosition().getLongitude();
                        try {
                            FutureTask<String> task = new FutureTask<String>(
                                    new Callable<String>() {
                                        @Override
                                        public String call() throws Exception {
                                            return GetCity.getCity(lat, log);
                                        }
                                    }
                            );
                            new Thread(task).start();
                            city =  task.get();
                        }catch (Exception e1) {
                            Log.d("pyt", "ERROR城市：" + e1.getMessage());
                        }
                        ItemEntity entity = new ItemEntity(
                                moment.getUser().getAvatarUrl(),
                                moment.getUser().getUsername(),
                                moment.getContent(),
                                imageUrls,
                                city,
                                new SimpleDateFormat("MM-dd HH:mm").format(moment.getCreatedAt()),
                                -1
                        );
                        itemEntities.add(entity);
                    } catch (Exception e2) {
                        Log.d("pyt", "失败:" + e2.getMessage());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    public static List<AVUser> findFriends() throws Exception {
        final List<AVUser> friends = new ArrayList<AVUser>();
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
            for (AVUser user : friends) {
                userIds.add(user.getObjectId());
            }
            CacheService.setFriendIds(userIds);
            CacheService.cacheUsers(userIds);
            List<AVUser> newFriends = new ArrayList<>();
            for (AVUser user : friends) {
                newFriends.add(CacheService.lookupUser(user.getObjectId()));
            }
            return newFriends;
        }
    }
}
