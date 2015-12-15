package com.avoscloud.chat.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.App;
import com.avoscloud.chat.fragment.SquareFragment;
import com.avoscloud.chat.model.Comment;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.chat.service.PreferenceMap;
import com.avoscloud.chat.service.UpdateService;
import com.avoscloud.chat.event.LoginFinishEvent;
import com.avoscloud.chat.fragment.ContactFragment;
import com.avoscloud.chat.fragment.ConversationRecentFragment;
import com.avoscloud.chat.util.GetCity;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.chat.util.Utils;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.LogUtils;
import com.avoscloud.leanchatlib.view.PlayButton;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import de.greenrobot.event.EventBus;

import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by lzw on 14-9-17.
 */
public class MainActivity extends BaseActivity {
  public static final int FRAGMENT_N = 5;
  public static final int CONVERSATION_STATUS = 1;
  public static final int CONTACT_STATUS = 2;
  public static final int SQUARE_STATUS = 3;
  private int status = CONVERSATION_STATUS;

  public static final int[] tabsNormalBackIds = new int[]{R.drawable.tabbar_chat,
      R.drawable.tabbar_contacts, R.drawable.contact_new_friends_icon, R.drawable.tabbar_discover};
  public static final int[] tabsActiveBackIds = new int[]{R.drawable.tabbar_chat_active,
      R.drawable.tabbar_contacts_active, R.drawable.contact_new_friends_icon, R.drawable.tabbar_discover_active};

  private static final String FRAGMENT_TAG_CONVERSATION = "conversation";
  private static final String FRAGMENT_TAG_CONTACT = "contact";
  private static final String FRAGMENT_TAG_PUBLISH = "publish"; //发布好友
  private static final String FRAGMENT_TAG_SQUARE = "square";//广场
  private static final String FRAGMENT_TAG_PROFILE = "profile";
  private static final String[] fragmentTags = new String[]{FRAGMENT_TAG_CONVERSATION, FRAGMENT_TAG_CONTACT,
      FRAGMENT_TAG_SQUARE, FRAGMENT_TAG_PROFILE}; //这里是对应一个fragment，发布好友对应一个Activity

  public LocationClient locClient;
  public MyLocationListener locationListener;
  Button conversationBtn, contactBtn, publishBtn, squareBtn;  //增加一个按钮
  View fragmentContainer;
  ContactFragment contactFragment;
  SquareFragment squareFragment;
  ConversationRecentFragment conversationRecentFragment;
  Button[] tabs;
  View recentTips, contactTips;
  public static DrawerLayout mDrawerLayout;

  public static LinearLayout editCommentLayout;
  public static EditText editText;
  public static ImageView sendComment;
  public static int position;

  public static void goMainActivityFromActivity(Activity fromActivity) {
    EventBus eventBus = EventBus.getDefault();
    eventBus.post(new LoginFinishEvent());

    ChatManager chatManager = ChatManager.getInstance();
    chatManager.setupManagerWithUserId(AVUser.getCurrentUser().getObjectId());
    chatManager.openClient(null);
    Intent intent = new Intent(fromActivity, MainActivity.class);
    fromActivity.startActivity(intent);

    updateUserLocation();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);
    findView();
    init();

    initDrawer();

    onTabSelect(conversationBtn);
    conversationBtn.performClick();
    initBaiduLocClient();

    CacheService.registerUser((LeanchatUser) AVUser.getCurrentUser());//缓存账号信息
    cacheFriends(); //缓存好友信息

      editCommentLayout = (LinearLayout) findViewById(R.id.editCommentLayout);
      editText = (EditText) findViewById(R.id.editComment);
      //编辑框失去焦点，隐藏软键盘
      editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
              Log.d("pyt", "是否有焦点：" + (hasFocus ? "1" : "0"));
              if (!hasFocus) {
                  hideSoftInput(ctx);
              }
          }
      });
      sendComment = (ImageView) findViewById(R.id.sendComment);
      //点击发送按钮触发事件：上传评论并刷新评论列表
      sendComment.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String data;
              data = (AVUser.getCurrentUser().getUsername().toString() + " : " + editText.getText().toString());
              final Comment comment = new Comment();
              Moment moment = SquareFragment.moments.get(getPosition());
              comment.setContent(data);
              comment.setMoment(moment);
              new Thread(new Runnable() {
                  @Override
                  public void run() {
                      try {
                          comment.save();
                      } catch (AVException e) {
                          Log.e("pyt", e.getMessage());
                      }
                  }
              }).start();

              Log.d("pyt", "点击了第" + getPosition() + "个listview");

              hideSoftInput(ctx);
          }
      });
  }

  @Override
  protected void onResume() {
    super.onResume();
    UpdateService updateService = UpdateService.getInstance(this);
    updateService.checkUpdate();
    recoverConfig();
  }

  private void cacheFriends() {
    try {
      FutureTask<String> task = new FutureTask<String>(
              new Callable<String>() {
                @Override
                public String call() throws Exception {
                    CacheService.findFriends();//缓存好友信息;
                    return "";
                }
              }
      );
      new Thread(task).start();
    }catch (Exception e1) {
      Log.d("lhq", "ERROR cache friend：" + e1.getMessage());
    }

  }

  private void initBaiduLocClient() {
    locClient = new LocationClient(this.getApplicationContext());
    locClient.setDebug(true);
    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    option.setScanSpan(5000);
    option.setIsNeedAddress(false);
    option.setCoorType("bd09ll");
    option.setIsNeedAddress(true);
    locClient.setLocOption(option);

    locationListener = new MyLocationListener();
    locClient.registerLocationListener(locationListener);
    locClient.start();
  }

  private void init() {
    tabs = new Button[]{conversationBtn, contactBtn, publishBtn, squareBtn};
  }

  private void findView() {
    conversationBtn = (Button) findViewById(R.id.btn_message);
    contactBtn = (Button) findViewById(R.id.btn_contact);
    publishBtn = (Button)  findViewById(R.id.btn_publish_friend);//加入发布好友按钮
    squareBtn = (Button) findViewById(R.id.btn_square);
    fragmentContainer = findViewById(R.id.fragment_container);
    recentTips = findViewById(R.id.iv_recent_tips);
    contactTips = findViewById(R.id.iv_contact_tips);
  }

  public void onTabSelect(View v) {
    int id = v.getId();
    FragmentManager manager = getFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    hideFragments(manager, transaction);
    setNormalBackgrounds();
    if (id == R.id.btn_message) {
      status = CONVERSATION_STATUS;
      if (conversationRecentFragment == null) {
        conversationRecentFragment = new ConversationRecentFragment();
        transaction.add(R.id.fragment_container, conversationRecentFragment, FRAGMENT_TAG_CONVERSATION);
      }
      transaction.show(conversationRecentFragment);
    } else if (id == R.id.btn_contact) {
      status = CONTACT_STATUS;
      if (contactFragment == null) {
        contactFragment = new ContactFragment();
        transaction.add(R.id.fragment_container, contactFragment, FRAGMENT_TAG_CONTACT);
      }
      transaction.show(contactFragment);
    } else if (id == R.id.btn_square) {
      status = SQUARE_STATUS;
      if(squareFragment == null) {
        squareFragment = new SquareFragment();
        transaction.add(R.id.fragment_container, squareFragment, FRAGMENT_TAG_SQUARE);
      }
      transaction.show(squareFragment);
    } else if (id == R.id.btn_publish_friend){
        Intent intent = new Intent(this, PublishActivity.class);
        this.startActivity(intent);
    }
    int pos;
    for (pos = 0; pos < FRAGMENT_N; pos++) {
      if (tabs[pos] == v) {
        break;
      }
    }
    transaction.commit();
    setTopDrawable(tabs[pos], tabsActiveBackIds[pos]);
  }

  private void setNormalBackgrounds() {
    for (int i = 0; i < tabs.length; i++) {
      Button v = tabs[i];
      setTopDrawable(v, tabsNormalBackIds[i]);
    }
  }

  private void setTopDrawable(Button v, int resId) {
    v.setCompoundDrawablesWithIntrinsicBounds(null, ctx.getResources().getDrawable(resId), null, null);
  }

  private void hideFragments(FragmentManager fragmentManager, FragmentTransaction transaction) {
    for (int i = 0; i < fragmentTags.length; i++) {
      Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags[i]);
      if (fragment != null && fragment.isVisible()) {
        transaction.hide(fragment);
      }
    }
  }

  public static void updateUserLocation() {
    PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(App.ctx);
    AVGeoPoint lastLocation = preferenceMap.getLocation();
    if (lastLocation != null) {
      final AVUser user = AVUser.getCurrentUser();
      final AVGeoPoint location = user.getAVGeoPoint(LeanchatUser.LOCATION);
      if (location == null || !Utils.doubleEqual(location.getLatitude(), lastLocation.getLatitude())
        || !Utils.doubleEqual(location.getLongitude(), lastLocation.getLongitude())) {
        user.put(LeanchatUser.LOCATION, lastLocation);
        user.saveInBackground(new SaveCallback() {
          @Override
          public void done(AVException e) {
            if (e != null) {
              LogUtils.logException(e);
            } else {
              AVGeoPoint avGeoPoint = user.getAVGeoPoint(LeanchatUser.LOCATION);
              if (avGeoPoint == null) {
                Logger.e("avGeopoint is null");
              } else {
                Logger.v("save location succeed latitude " + avGeoPoint.getLatitude()
                  + " longitude " + avGeoPoint.getLongitude());
              }
            }
          }
        });
      }
    }
  }

  public class MyLocationListener implements BDLocationListener {

    @Override
    public void onReceiveLocation(BDLocation location) {
      double latitude = location.getLatitude();
      double longitude = location.getLongitude();
      int locType = location.getLocType();
      Logger.d("onReceiveLocation latitude=" + latitude + " longitude=" + longitude
          + " locType=" + locType + " address=" + location.getAddrStr());
      AVUser user = AVUser.getCurrentUser();
      if (user != null) {
        PreferenceMap preferenceMap = new PreferenceMap(ctx, user.getObjectId());
        AVGeoPoint avGeoPoint = preferenceMap.getLocation();
        if (avGeoPoint != null && avGeoPoint.getLatitude() == location.getLatitude()
            && avGeoPoint.getLongitude() == location.getLongitude()) {
          updateUserLocation();
          locClient.stop();
        } else {
          AVGeoPoint newGeoPoint = new AVGeoPoint(location.getLatitude(),
              location.getLongitude());
          if (newGeoPoint != null) {
            preferenceMap.setLocation(newGeoPoint);
          }
        }
      }
    }
  }
    //进入发布界面，提交发布信息后，回来处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
  private void initDrawer()
  {
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerStateChanged(int newState) {
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            View mContent = mDrawerLayout.getChildAt(0);
            View mMenu = drawerView;
            float scale = 1 - slideOffset;
            float rightScale = 0.8f + scale * 0.2f;

            if (drawerView.getTag().equals("LEFT")) {

                float leftScale = 1 - 0.3f * scale;

                ViewHelper.setScaleX(mMenu, leftScale);
                ViewHelper.setScaleY(mMenu, leftScale);
                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent,
                        mMenu.getMeasuredWidth() * (1 - scale));
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent,
                        mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);
            } else {
                ViewHelper.setTranslationX(mContent,
                        -mMenu.getMeasuredWidth() * slideOffset);
                ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                ViewHelper.setPivotY(mContent,
                        mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);
            }
        }

        @Override
        public void onDrawerOpened(View drawerView) {
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }
    });
  }
  public void recoverConfig() {
    switch (status) {
      case CONVERSATION_STATUS:
        conversationBtn.performClick();
        onTabSelect(conversationBtn);
        break;
      case SQUARE_STATUS:
        squareBtn.performClick();
        onTabSelect(squareBtn);
        break;
      case CONTACT_STATUS:
        contactBtn.performClick();
        onTabSelect(contactBtn);
        break;
    }
  }
  public static void openDrawer() {
    mDrawerLayout.openDrawer(Gravity.LEFT);
  }

    // Editted by pyt
    public static void showEditText(Context context, int position) {
        editCommentLayout.setVisibility(View.VISIBLE);
        editText.setText(null);
        editText.setHint("请输入评论..");
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
//        toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        setPosition(position);
    }

    public static void hideSoftInput (Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editCommentLayout.setVisibility(View.GONE);
    }

    public static int getPosition() {
        return position;
    }

    public static void setPosition(int position) {
        MainActivity.position = position;
    }
}
