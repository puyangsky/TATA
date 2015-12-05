package com.avoscloud.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.ListItemAdapter;
import com.avoscloud.chat.fragment.ChatMainTabFragment;
import com.avoscloud.chat.fragment.ContactMainTabFragment;
import com.avoscloud.chat.fragment.FriendMainTabFragment;
import com.avoscloud.chat.util.ItemEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/*
*  Created by puyangsky 2015/11/18.
* */
public class PersonViewActivity extends BaseActivity {

    private ListView listView;
    private ViewPager vp;
    private FragmentPagerAdapter fAdapter;
    private List<Fragment> data;
    private ArrayList<ItemEntity> itemEntities;
    protected Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_person_view);

        ctx = PersonViewActivity.this;

        listView = (ListView) findViewById(R.id.list_item);
        initData();
        listView.setAdapter(new ListItemAdapter(ctx, itemEntities));

        initView();
        initActionBar(R.string.profile_person);
    }

    private void initData() {
        itemEntities = new ArrayList<ItemEntity>();

        ItemEntity entity1 = new ItemEntity(
                "http://pic14.nipic.com/20110522/7411759_164157418126_2.jpg", "王尼玛", "今天暴走大事件开播啦，大家快来上优酷观看最新一期视频吧~~", null,
                "广州", "18:10", -1);
        itemEntities.add(entity1);

        ArrayList<String> urls_1 = new ArrayList<String>();
        urls_1.add("http://pic.nipic.com/2007-11-09/2007119122519868_2.jpg");
        ItemEntity entity2 = new ItemEntity(
                "http://pic2.ooopic.com/01/03/51/25b1OOOPIC19.jpg", "哈士奇", "今天的狗粮真难吃！！！", urls_1,
                "上海", "17:10", -1);
        itemEntities.add(entity2);

        ArrayList<String> urls_2 = new ArrayList<String>();
        urls_2.add("http://pic.nipic.com/2007-11-09/200711912453162_2.jpg");
        urls_2.add("http://down.tutu001.com/d/file/20101129/2f5ca0f1c9b6d02ea87df74fcc_560.jpg");
        urls_2.add("http://pica.nipic.com/2008-03-19/2008319183523380_2.jpg");
        ItemEntity entity3 = new ItemEntity(
                "http://pic.nipic.com/2007-11-09/200711912230489_2.jpg", "萨摩耶", "伦家是萌萌的小公举~", urls_2,
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
                "http://ppt360.com/background/UploadFiles_6733/201012/2010122016291897.jpg", "拉布拉多", "又带铲屎的出来浪，看我拍的美照！", urls_3,
                "长沙", "10:30", -1);
        itemEntities.add(entity4);
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
}
