package com.avoscloud.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.PersonviewItemAdapter;
import com.avoscloud.chat.fragment.ChatMainTabFragment;
import com.avoscloud.chat.fragment.ContactMainTabFragment;
import com.avoscloud.chat.fragment.FriendMainTabFragment;
import com.avoscloud.chat.model.Image;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.util.PersonviewEntity;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.nio.BufferUnderflowException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Puyangsky on 2015/11/18.
 */
public class PersonViewActivity extends BaseActivity {

    private ListView listView;
    private ViewPager vp;
    private CircleIndicator circleIndicator;
    private FragmentPagerAdapter fAdapter;
    private List<Fragment> data;
    private ArrayList<PersonviewEntity> itemEntities;
    private PersonviewItemAdapter adapter;
    protected Context ctx;
    private AVUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_person_view);

        try{
            user = getIntent().getParcelableExtra("user");
        }catch (ClassCastException e) {
            Log.d("pyt", e.getMessage());
            user = null;
        }

        ctx = PersonViewActivity.this;

        listView = (ListView) findViewById(R.id.moment_list_item);
        initData();
        adapter = new PersonviewItemAdapter(ctx, itemEntities);
        listView.setAdapter(adapter);

        initView();
        initActionBar(R.string.profile_person);
    }

    private void initView() {
        vp = (ViewPager)findViewById(R.id.recommends);
        circleIndicator = (CircleIndicator)findViewById(R.id.circleIndicator);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.personRel);
        layout.bringChildToFront(circleIndicator);

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
        circleIndicator.setViewPager(vp);
    }

    public void initData(){
        itemEntities = new ArrayList<>();
        data = new ArrayList<Fragment>();
//        final LeanchatUser currentUser = LeanchatUser.getCurrentUser();
        final AVUser currentUser = user;
        AVQuery<Moment> query = AVObject.getQuery(Moment.class);
        query.orderByDescending("createdAt");
        query.include("user");
        query.include("content");
        query.include("createdAt");
        query.include("position");
        query.include("fileList");          //这里include一个类的数据，会自动填充
        query.include("zan");
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<Moment>() {
            @Override
            public void done(List<Moment> results, AVException e) {
                if (e != null || results == null) {
                    return;
                }
                Log.d("pyt", "找到了" + results.size() + "条记录");
                for (Moment moment : results) {

                    List<Image> imageList = moment.getFileList();
                    //图片为空，略过

                    //获取图片urls
                    ArrayList<String> imageUrls = new ArrayList<String>();
                    if (imageList == null) {
                        Log.d("pyt", "fileList = null");
//                        continue;
                        imageUrls = null;
                    }else{
                        for (Image image : imageList) {
                            imageUrls.add(image.getFile().getUrl());
                        }
                    }

//                    Log.d("pyt", "内容：" + moment.getContent() +
//                            "\nurl：" + imageUrls +
//                            "\ntime:" + new SimpleDateFormat("MM/dd").format(moment.getCreatedAt()));

                    PersonviewEntity entity = new PersonviewEntity(
                            new SimpleDateFormat("MM/dd").format(moment.getCreatedAt()),
                            moment.getContent(),
                            imageUrls
                    );
                    itemEntities.add(entity);

                    //添加到好友推荐界面
//                    if(moment.getType() == PublishActivity.TYPE_OTHER){
//                        ContactMainTabFragment fragment = new ContactMainTabFragment();
//                        ImageView imageView = (ImageView)fragment.getView().findViewById(R.id.tab_3_pic);
//                        DisplayImageOptions options = new DisplayImageOptions.Builder()//
//                                .cacheInMemory(true)//
//                                .cacheOnDisk(true)//
//                                .bitmapConfig(Bitmap.Config.RGB_565)//
//                                .build();
//                        ImageLoader.getInstance().displayImage(imageUrls.get(0), imageView, options);
//                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
