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
import com.avoscloud.chat.util.ItemEntity;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by puyangsky on 2015/12/3.
 */
public class SquareFragment extends BaseFragment{
    private ListView mListView;
    private ArrayList<ItemEntity> itemEntities;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.square_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView) getView().findViewById(R.id.square_list_item);
        initData1();
        mListView.setAdapter(new ListItemAdapter(getActivity(), itemEntities));
        headerLayout.showTitle(R.string.square_title);
    }

    private void initData() {
        itemEntities = new ArrayList<ItemEntity>();

        ItemEntity entity1 = new ItemEntity(
                "http://pic14.nipic.com/20110522/7411759_164157418126_2.jpg", "王尼玛", "我真的是无语了fuck uuuuuuuuuuuuuuuuuuuuuuuu", null,
                "广州", "18:10", -1);
        itemEntities.add(entity1);

        ArrayList<String> urls_1 = new ArrayList<String>();
        urls_1.add("http://pic.nipic.com/2007-11-09/2007119122519868_2.jpg");
        ItemEntity entity2 = new ItemEntity(
                "http://pic2.ooopic.com/01/03/51/25b1OOOPIC19.jpg", "王尼玛", "我真的是无语了fuck uuuuuuuuuuuuuuuuuuuuuuuu！！！", urls_1,
                "上海", "17:10", -1);
        itemEntities.add(entity2);

        ArrayList<String> urls_2 = new ArrayList<String>();
        urls_2.add("http://pic.nipic.com/2007-11-09/200711912453162_2.jpg");
        urls_2.add("http://down.tutu001.com/d/file/20101129/2f5ca0f1c9b6d02ea87df74fcc_560.jpg");
        urls_2.add("http://pica.nipic.com/2008-03-19/2008319183523380_2.jpg");
        ItemEntity entity3 = new ItemEntity(
                "http://pic.nipic.com/2007-11-09/200711912230489_2.jpg", "王尼玛", "我真的是无语了fuck uuuuuuuuuuuuuuuuuuuuuuuu~", urls_2,
                "北京", "13:10", -1);
        itemEntities.add(entity3);

        ArrayList<String> urls_3 = new ArrayList<String>();
        urls_3.add("http://www.photophoto.cn/m6/018/030/0180300271.jpg");
        urls_3.add("http://pic24.nipic.com/20121022/9252150_193011306000_2.jpg");
        urls_3.add("http://anquanweb.com/uploads/userup/913/1322O9102-2596.jpg");
        urls_3.add("http://pic1.nipic.com/2008-08-12/200881211331729_2.jpg");
        urls_3.add("http://imgsrc.baidu.com/forum/pic/item/3ac79f3df8dcd1004e9102b8728b4710b9122f1e.jpg");
        urls_3.add("http://pica.nipic.com/2008-01-09/200819134250665_2.jpg");
        ItemEntity entity4 = new ItemEntity(
                "http://ppt360.com/background/UploadFiles_6733/201012/2010122016291897.jpg", "王尼玛", "我真的是无语了fuck uuuuuuuuuuuuuuuuuuuuuuuu", urls_3,
                "长沙", "10:30", -1);
        itemEntities.add(entity4);
    }

    public void initData1(){
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
                        ItemEntity entity = new ItemEntity(
                                moment.getUser().getAvatarUrl(),
//                                currentUser.getAvatarUrl(),
//                                currentUser.getUsername(),
                                moment.getUser().getUsername(),
                                moment.getContent(),
                                imageUrls,
                                "北京",
                                new SimpleDateFormat("MM-dd HH:mm").format(moment.getCreatedAt()),
                                -1
                        );
                        itemEntities.add(entity);
                    } catch (Exception e2) {
                        Log.d("pyt", "失败:" + e2.getMessage());
                    }
                }
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
