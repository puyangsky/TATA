package com.avoscloud.chat.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.App;
import com.avoscloud.chat.activity.ContactAddFriendActivity;
import com.avoscloud.chat.activity.ContactNewFriendActivity;
import com.avoscloud.chat.adapter.ContactFragmentAdapter;
import com.avoscloud.chat.model.SortUser;
import com.avoscloud.chat.service.AddRequestManager;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.chat.event.ContactRefreshEvent;
import com.avoscloud.chat.event.InvitationEvent;
import com.avoscloud.chat.activity.ChatRoomActivity;
import com.avoscloud.chat.activity.ConversationGroupListActivity;
import com.avoscloud.chat.view.BaseListView;
import com.avoscloud.chat.view.EnLetterView;
import com.avoscloud.chat.util.CharacterParser;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 联系人列表
 */
public class ContactFragment extends BaseFragment {
  private static CharacterParser characterParser;
  private static PinyinComparator pinyinComparator;
  @InjectView(R.id.dialog)
  TextView dialogTextView;
  @InjectView(R.id.list_friends)
  BaseListView<SortUser> friendsList;
  @InjectView(R.id.right_letter)
  EnLetterView rightLetter;
  View listHeaderView;
  private ContactFragmentAdapter userAdapter;
  private ListHeaderViewHolder listHeaderViewHolder = new ListHeaderViewHolder();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    View view = inflater.inflate(R.layout.contact_fragment, container, false);
    ButterKnife.inject(this, view);

    listHeaderView = inflater.inflate(R.layout.contact_fragment_header_layout, null, false);
    ButterKnife.inject(listHeaderViewHolder, listHeaderView);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onActivityCreated(savedInstanceState);
    characterParser = CharacterParser.getInstance();
    pinyinComparator = new PinyinComparator();

    initHeader();
    initListView();
    initRightLetterViewAndSearchEdit();
    refresh();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    updateNewRequestBadge();
  }

  private void initRightLetterViewAndSearchEdit() {
    rightLetter.setTextView(dialogTextView);
    rightLetter.setOnTouchingLetterChangedListener(new LetterListViewListener());
  }

  private void initHeader() {
    headerLayout.showTitle(App.ctx.getString(R.string.contact));
    headerLayout.showRightImageButton(R.drawable.base_action_bar_add_bg_selector, new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ctx, ContactAddFriendActivity.class);
        ctx.startActivity(intent);
      }
    });
  }

  private List<SortUser> convertAVUser(List<LeanchatUser> datas) {
    List<SortUser> sortUsers = new ArrayList<SortUser>();
    int total = datas.size();
    for (int i = 0; i < total; i++) {
      AVUser avUser = datas.get(i);
      SortUser sortUser = new SortUser();
      sortUser.setInnerUser((LeanchatUser)avUser);
      String username = avUser.getUsername();
      if (!TextUtils.isEmpty(username)) {
        String pinyin = characterParser.getSelling(username);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
          sortUser.setSortLetters(sortString.toUpperCase());
        } else {
          sortUser.setSortLetters("#");
        }
      } else {
        sortUser.setSortLetters("#");
      }
      sortUsers.add(sortUser);
    }
    Collections.sort(sortUsers, pinyinComparator);
    return sortUsers;
  }

  private void initListView() {
    userAdapter = new ContactFragmentAdapter(getActivity());
    friendsList.init(new BaseListView.DataFactory<SortUser>() {
      @Override
      public List<SortUser> getDatasInBackground(int skip, int limit, List<SortUser> currentDatas) throws Exception {
        return convertAVUser(CacheService.findFriends());
      }
    }, userAdapter);

    friendsList.addHeaderView(listHeaderView, null, false);
    friendsList.setPullLoadEnable(false);
    friendsList.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (getActivity().getWindow().getAttributes().softInputMode !=
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
          InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
          View currentFocus = getActivity().getCurrentFocus();
          if (currentFocus != null) {
            manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
          }
        }
        return false;
      }
    });
    friendsList.setItemListener(new BaseListView.ItemListener<SortUser>() {
      @Override
      public void onItemSelected(SortUser item) {
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra(Constants.MEMBER_ID, item.getInnerUser().getObjectId());
        startActivity(intent);
      }

      @Override
      public void onItemLongPressed(SortUser item) {
        showDeleteDialog(item);
      }
    });
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    // TODO Auto-generated method stub
    if (isVisibleToUser) {
      //refreshMsgsFromDB();
    }
    super.setUserVisibleHint(isVisibleToUser);
  }

  private void updateNewRequestBadge() {
    listHeaderViewHolder.getMsgTipsView().setVisibility(
        AddRequestManager.getInstance().hasUnreadRequests() ? View.VISIBLE : View.GONE);
  }

  private void refresh() {
    friendsList.onRefresh();
    AddRequestManager.getInstance().countUnreadRequests(new CountCallback() {
      @Override
      public void done(int i, AVException e) {
        updateNewRequestBadge();
      }
    });
  }

  public void showDeleteDialog(final SortUser user) {
    new AlertDialog.Builder(ctx).setMessage(R.string.contact_deleteContact)
        .setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            final ProgressDialog dialog1 = showSpinnerDialog();
            AVUser.getCurrentUser(LeanchatUser.class).removeFriend(user.getInnerUser().getObjectId(), new SaveCallback() {
              @Override
              public void done(AVException e) {
                dialog1.dismiss();
                if (filterException(e)) {
                  forceRefresh();
                }
              }
            });
          }
        }).setNegativeButton(R.string.chat_common_cancel, null).show();
  }

  class ListHeaderViewHolder {
    @InjectView(R.id.iv_msg_tips)
    ImageView msgTipsView;

    @OnClick(R.id.layout_new)
    void goNewFriend() {
      Intent intent = new Intent(ctx, ContactNewFriendActivity.class);
      ctx.startActivity(intent);
    }

    @OnClick(R.id.layout_group)
    void goGroupConvList() {
      Intent intent = new Intent(ctx, ConversationGroupListActivity.class);
      ctx.startActivity(intent);
    }

    public ImageView getMsgTipsView() {
      return msgTipsView;
    }
  }

  private class LetterListViewListener implements
      EnLetterView.OnTouchingLetterChangedListener {

    @Override
    public void onTouchingLetterChanged(String s) {
      int position = userAdapter.getPositionForSection(s.charAt(0));
      if (position != -1) {
        friendsList.setSelection(position);
      }
    }
  }

  public static class PinyinComparator implements Comparator<SortUser> {
    public int compare(SortUser o1, SortUser o2) {
      if (o1.getSortLetters().equals("@")
          || o2.getSortLetters().equals("#")) {
        return -1;
      } else if (o1.getSortLetters().equals("#")
          || o2.getSortLetters().equals("@")) {
        return 1;
      } else {
        return o1.getSortLetters().compareTo(o2.getSortLetters());
      }
    }
  }

  public void forceRefresh() {
    AVUser curUser = AVUser.getCurrentUser();
    AVQuery<LeanchatUser> q = null;
    try {
      q = curUser.followeeQuery(LeanchatUser.class);
    } catch (Exception e) {
      //在 currentUser.objectId 为 null 的时候抛出的，不做处理
      Logger.e(e.getMessage());
    }

    q.clearCachedResult();
    friendsList.onRefresh();
  }




  public void onEvent(ContactRefreshEvent event) {
    forceRefresh();
  }

  public void onEvent(InvitationEvent event) {
    AddRequestManager.getInstance().unreadRequestsIncrement();
    updateNewRequestBadge();
  }
}
