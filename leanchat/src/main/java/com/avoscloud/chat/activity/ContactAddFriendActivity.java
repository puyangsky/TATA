package com.avoscloud.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avoscloud.chat.R;
import com.avoscloud.chat.App;
import com.avoscloud.chat.service.AddRequestManager;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.chat.view.BaseListView;
import com.avoscloud.chat.adapter.BaseListAdapter;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.view.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 查找好友页面
 */
public class ContactAddFriendActivity extends BaseActivity {
  @InjectView(R.id.searchNameEdit)
  EditText searchNameEdit;

  @InjectView(R.id.searchList)
  BaseListView<LeanchatUser> listView;
  private String searchName = "";
  private AddFriendListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_add_friend_activity);
    ButterKnife.inject(this);
    init();
  }

  private void init() {
    initActionBar(App.ctx.getString(R.string.contact_findFriends));
    adapter = new AddFriendListAdapter(this, new ArrayList<LeanchatUser>());
    listView.init(new BaseListView.DataFactory<LeanchatUser>() {
      @Override
      public List<LeanchatUser> getDatasInBackground(int skip, int limit, List<LeanchatUser> currentDatas) throws Exception {
        return searchUser(searchName, adapter.getCount());
      }
    }, adapter);
    adapter.setClickListener(new AddFriendListAdapter.AddButtonClickListener() {
      @Override
      public void onAddButtonClick(LeanchatUser user) {
        AddRequestManager.getInstance().createAddRequestInBackground(ContactAddFriendActivity.this, user);
      }
    });
    listView.onRefresh();
  }

  public List<LeanchatUser> searchUser(String searchName, int skip) throws AVException {
    AVQuery<LeanchatUser> q = AVUser.getQuery(LeanchatUser.class);
    q.whereContains(LeanchatUser.USERNAME, searchName);
    q.limit(Constants.PAGE_SIZE);
    q.skip(skip);
    LeanchatUser user = (LeanchatUser)AVUser.getCurrentUser();
    List<String> friendIds = new ArrayList<String>(CacheService.getFriendIds());
    friendIds.add(user.getObjectId());
    q.whereNotContainedIn(Constants.OBJECT_ID, friendIds);
    q.orderByDescending(Constants.UPDATED_AT);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    List<LeanchatUser> users = q.find();
    CacheService.registerUsers(users);
    return users;
  }

  @OnClick(R.id.searchBtn)
  public void search(View view) {
    searchName = searchNameEdit.getText().toString();
    listView.onRefresh();
  }

  public static class AddFriendListAdapter extends BaseListAdapter<LeanchatUser> {

    private AddButtonClickListener addButtonClickListener;

    public AddFriendListAdapter(Context context, List<LeanchatUser> list) {
      super(context, list);
    }

    public void setClickListener(AddButtonClickListener addButtonClickListener) {
      this.addButtonClickListener = addButtonClickListener;
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
      // TODO Auto-generated method stub
      if (conView == null) {
        conView = inflater.inflate(R.layout.contact_add_friend_item, null);
      }
      final LeanchatUser user = (LeanchatUser) datas.get(position);
      TextView nameView = ViewHolder.findViewById(conView, R.id.name);
      ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
      Button addBtn = ViewHolder.findViewById(conView, R.id.add);
      ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);
      nameView.setText(user.getUsername());
      addBtn.setText(R.string.contact_add);
      addBtn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          if (addButtonClickListener != null) {
            addButtonClickListener.onAddButtonClick(user);
          }
        }
      });
      return conView;
    }

    public interface AddButtonClickListener {
      void onAddButtonClick(LeanchatUser user);
    }

  }
}
