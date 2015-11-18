package com.avoscloud.chat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.chat.service.ConversationManager;
import com.avoscloud.chat.adapter.BaseCheckListAdapter;
import com.avoscloud.chat.util.Utils;
import com.avoscloud.leanchatlib.activity.AVBaseActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.avoscloud.leanchatlib.view.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 群聊对话拉人页面
 * Created by lzw on 14-10-11.
 * TODO: ConversationChangeEvent
 */
public class ConversationAddMembersActivity extends AVBaseActivity {
  public static final int OK = 0;
  private CheckListAdapter adapter;
  private ListView userList;
  private ConversationManager conversationManager;
  private AVIMConversation conversation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.conversation_add_members_layout);
    findView();
    conversationManager = ConversationManager.getInstance();
    String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
    conversation = AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId);
    initList();
    initActionBar();
    setListData();
  }

  private void setListData() {
    AVUser.getCurrentUser(LeanchatUser.class).findFriendsWithCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, new FindCallback<LeanchatUser>() {
      @Override
      public void done(List<LeanchatUser> users, AVException e) {
        if (filterException(e)) {
          List<String> userIds = new ArrayList<String>();
          for (AVUser user : users) {
            userIds.add(user.getObjectId());
          }
          userIds.removeAll(conversation.getMembers());
          adapter.setDatas(userIds);
          adapter.notifyDataSetChanged();
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem add = menu.add(0, OK, 0, R.string.common_sure);
    alwaysShowMenuItem(add);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    int id = item.getItemId();
    if (id == OK) {
      addMembers();
    }
    return super.onMenuItemSelected(featureId, item);
  }

  private void addMembers() {
    final List<String> checkedUsers = adapter.getCheckedDatas();
    final ProgressDialog dialog = showSpinnerDialog();
    if (checkedUsers.size() == 0) {
      finish();
    } else {
      if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
        List<String> members = new ArrayList<String>();
        members.addAll(checkedUsers);
        members.addAll(conversation.getMembers());
        conversationManager.createGroupConversation(members, new AVIMConversationCreatedCallback() {
          @Override
          public void done(final AVIMConversation conversation, AVIMException e) {
            if (filterException(e)) {
              Intent intent = new Intent(ConversationAddMembersActivity.this, ChatRoomActivity.class);
              intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
              startActivity(intent);
              finish();
            }
          }
        });
      } else {
        conversation.addMembers(checkedUsers, new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            dialog.dismiss();
            if (filterException(e)) {
              Utils.toast(R.string.conversation_inviteSucceed);
              setResult(RESULT_OK);
              finish();
            }
          }
        });
      }
    }
  }

  private void initList() {
    adapter = new CheckListAdapter(this, new ArrayList<String>());
    userList.setAdapter(adapter);
  }

  private void findView() {
    userList = (ListView) findViewById(R.id.userList);
  }

  public static class CheckListAdapter extends BaseCheckListAdapter<String> {

    public CheckListAdapter(Context ctx, List<String> datas) {
      super(ctx, datas);
    }

    @Override
    public View getView(final int position, View conView, ViewGroup parent) {
      if (conView == null) {
        conView = View.inflate(ctx, R.layout.conversation_add_members_item, null);
      }
      String userId = datas.get(position);
      AVUser user = CacheService.lookupUser(userId);
      ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
      TextView nameView = ViewHolder.findViewById(conView, R.id.username);
      ImageLoader.getInstance().displayImage(((LeanchatUser)user).getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
      nameView.setText(user.getUsername());
      CheckBox checkBox = ViewHolder.findViewById(conView, R.id.checkbox);
      setCheckBox(checkBox, position);
      checkBox.setOnCheckedChangeListener(new CheckListener(position));
      return conView;
    }
  }
}
