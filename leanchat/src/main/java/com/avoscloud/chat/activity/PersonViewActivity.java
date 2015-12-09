package com.avoscloud.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.ListItemAdapter;
import com.avoscloud.chat.fragment.ChatMainTabFragment;
import com.avoscloud.chat.fragment.ContactMainTabFragment;
import com.avoscloud.chat.fragment.FriendMainTabFragment;
import com.avoscloud.chat.model.Image;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.util.GetCity;
import com.avoscloud.chat.util.ItemEntity;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/*
*  Created by puyangsky 2015/11/18.
* */
public class PersonViewActivity extends BaseActivity {

    private ListView listView;
    private ViewPager vp;
    public static LinearLayout editCommentLayout;
    public static EditText editText;
    public static ImageView sendComment;
    private FragmentPagerAdapter fAdapter;
    private List<Fragment> data;
    private ArrayList<ItemEntity> itemEntities;
    protected Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_person_view);

        ctx = PersonViewActivity.this;

        editCommentLayout = (LinearLayout) findViewById(R.id.editCommentLayout);
        editText = (EditText) findViewById(R.id.editComment);
        sendComment = (ImageView) findViewById(R.id.sendComment);

        listView = (ListView) findViewById(R.id.list_item);
        initData();
        listView.setAdapter(new ListItemAdapter(ctx, itemEntities));

        initView();
        initActionBar(R.string.profile_person);
    }

    private void initView() {
        vp = (ViewPager)findViewById(R.id.recommends);
        data = new ArrayList<Fragment>();
        ChatMainTabFragment chatMainTabFragment = new ChatMainTabFragment();
        FriendMainTabFragment friendMainTabFragment = new FriendMainTabFragment();
        ContactMainTabFragment contactMainTabFragment = new ContactMainTabFragment();
        data.add(chatMainTabFragment);
        data.add(friendMainTabFragment);
        data.add(contactMainTabFragment);

        fAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int i) {
                return data.get(i);
            }

            @Override
            public int getCount() {
                return data.size();
            }
        };

        vp.setAdapter(fAdapter);

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }


    public void initData(){
        itemEntities = new ArrayList<>();
        final LeanchatUser currentUser = (LeanchatUser) AVUser.getCurrentUser();
        AVQuery<Moment> query = AVObject.getQuery(Moment.class);
        query.orderByDescending("createdAt");
        query.include("user");
        query.include("content");
        query.include("createdAt");
        query.include("position");
        query.include("fileList");          //这里include一个类的数据，会自动填充
        query.include("zan");
        List<AVUser> friends = new ArrayList<AVUser>();
        friends.add(currentUser);
        query.whereContainedIn("user", friends);
//        query.whereEqualTo("user", currentUser);
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
                                currentUser.getAvatarUrl(),
                                currentUser.getUsername(),
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
            }
        });
    }

    public static void showEditText(Context context) {
        editCommentLayout.setVisibility(View.VISIBLE);
        editText.setText(null);
        editText.setHint("请输入评论..");
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
