package com.avoscloud.chat.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.App;
import com.avoscloud.chat.service.ConversationManager;
import com.avoscloud.chat.view.ExpandGridView;
import com.avoscloud.chat.util.Utils;
import com.avoscloud.chat.adapter.BaseListAdapter;
import com.avoscloud.leanchatlib.activity.AVBaseActivity;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.controller.RoomsTable;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils.CacheUserCallback;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.avoscloud.leanchatlib.view.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lzw on 14-10-11.
 */
public class ConversationDetailActivity extends AVBaseActivity implements AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {
  private static final int ADD_MEMBERS = 0;
  private static final int INTENT_NAME = 1;
  private static List<LeanchatUser> members = new ArrayList<LeanchatUser>();
  @InjectView(R.id.usersGrid)
  ExpandGridView usersGrid;

  @InjectView(R.id.name_layout)
  View nameLayout;

  @InjectView(R.id.quit_layout)
  View quitLayout;

  private AVIMConversation conversation;
  private ConversationType conversationType;
  private ConversationManager conversationManager;
  private UserListAdapter usersAdapter;
  private boolean isOwner;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.conversation_detail_activity);
    String conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
    conversation = AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId);
    ButterKnife.inject(this);
    initData();
    initGrid();
    initActionBar(R.string.conversation_detail_title);
    setViewByConvType(conversationType);
    refresh();
  }

  private void setViewByConvType(ConversationType conversationType) {
    if (conversationType == ConversationType.Single) {
      nameLayout.setVisibility(View.GONE);
      quitLayout.setVisibility(View.GONE);
    } else {
      nameLayout.setVisibility(View.VISIBLE);
      quitLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuItem invite = menu.add(0, ADD_MEMBERS, 0, R.string.conversation_detail_invite);
    alwaysShowMenuItem(invite);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    int menuId = item.getItemId();
    if (menuId == ADD_MEMBERS) {
      Intent intent = new Intent(this, ConversationAddMembersActivity.class);
      startActivityForResult(intent, ADD_MEMBERS);
    }
    return super.onMenuItemSelected(featureId, item);
  }

  private void refresh() {
    AVUserCacheUtils.cacheUsers(conversation.getMembers(), new CacheUserCallback() {
      @Override
      public void done(Exception e) {
        usersAdapter.clear();
        usersAdapter.addAll(AVUserCacheUtils.getUsersFromCache(conversation.getMembers()));
      }
    });
  }

  private void initGrid() {
    usersAdapter = new UserListAdapter(this, members);
    usersGrid.setAdapter(usersAdapter);
    usersGrid.setOnItemClickListener(this);
    usersGrid.setOnItemLongClickListener(this);
  }

  private void initData() {
    conversationManager = ConversationManager.getInstance();
    isOwner = conversation.getCreator().equals(AVUser.getCurrentUser().getObjectId());
    conversationType = ConversationHelper.typeOfConversation(conversation);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    AVUser user = (AVUser) parent.getAdapter().getItem(position);
    ContactPersonInfoActivity.goPersonInfo(this, user.getObjectId());
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    if (conversationType == ConversationType.Single) {
      return true;
    }
    final AVUser user = (AVUser) parent.getAdapter().getItem(position);
    boolean isTheOwner = conversation.getCreator().equals(user.getObjectId());
    if (!isTheOwner) {
      new AlertDialog.Builder(this).setMessage(R.string.conversation_kickTips)
          .setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              final ProgressDialog progress = showSpinnerDialog();
              conversation.kickMembers(Arrays.asList(user.getObjectId()), new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                  progress.dismiss();
                  if (filterException(e)) {
                    Utils.toast(R.string.conversation_detail_kickSucceed);
                  }
                }
              });
            }
          }).setNegativeButton(R.string.chat_common_cancel, null).show();
    }
    return true;
  }

  @OnClick(R.id.name_layout)
  void changeName() {
    UpdateContentActivity.goActivityForResult(this, App.ctx.getString(R.string.conversation_name), INTENT_NAME);
  }

  @OnClick(R.id.quit_layout)
  void onQuitButtonClick() {
    new AlertDialog.Builder(this).setMessage(R.string.conversation_quit_group_tip)
      .setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          quitGroup();
        }
      }).setNegativeButton(R.string.chat_common_cancel, null).show();
  }

  /**
   * 退出群聊
   */
  private void quitGroup() {
    final String convid = conversation.getConversationId();
    conversation.quit(new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        if (filterException(e)) {
          RoomsTable roomsTable = ChatManager.getInstance().getRoomsTable();
          roomsTable.deleteRoom(convid);
          Utils.toast(R.string.conversation_alreadyQuitConv);
          setResult(RESULT_OK);
          finish();
        }
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      if (requestCode == INTENT_NAME) {
        String newName = UpdateContentActivity.getResultValue(data);
        conversationManager.updateName(conversation, newName, new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            if (filterException(e)) {
              refresh();
            }
          }
        });
      } else if (requestCode == ADD_MEMBERS) {
        refresh();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public static class UserListAdapter extends BaseListAdapter<LeanchatUser> {
    public UserListAdapter(Context ctx, List<LeanchatUser> datas) {
      super(ctx, datas);
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
      if (conView == null) {
        conView = View.inflate(ctx, R.layout.conversation_member_item, null);
      }
      AVUser user = datas.get(position);
      ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
      TextView nameView = ViewHolder.findViewById(conView, R.id.username);
      ImageLoader.getInstance().displayImage(((LeanchatUser)user).getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
      nameView.setText(user.getUsername());
      return conView;
    }
  }
}
