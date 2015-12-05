package com.avoscloud.chat.fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.LocationActivity;
import com.avoscloud.chat.adapter.ListItemAdapter;
import com.avoscloud.chat.util.ItemEntity;

import java.util.ArrayList;

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
        initData();
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
}
