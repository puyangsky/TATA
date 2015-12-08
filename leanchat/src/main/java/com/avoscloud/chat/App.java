package com.avoscloud.chat;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avoscloud.chat.model.AddRequest;
import com.avoscloud.chat.model.Comment;
import com.avoscloud.chat.model.Image;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.model.Reply;
import com.avoscloud.chat.model.UpdateInfo;
import com.avoscloud.chat.service.ConversationManager;
import com.avoscloud.chat.service.PushManager;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.chat.util.Utils;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by lzw on 14-5-29.
 */
public class App extends Application {
  public static boolean debug = true;
  public static App ctx;

  @Override
  public void onCreate() {
    super.onCreate();
    ctx = this;
    Utils.fixAsyncTaskBug();


    //初始化ID和Key
    String appId = "DDQ2nIzsXRqsNYGt5FHpfpgK";
    String appKey = "i9rVBhIdlPrGr0vxL2QX0HuH";

    AVUser.alwaysUseSubUserClass(LeanchatUser.class);
      AVObject.registerSubclass(AddRequest.class);
      AVObject.registerSubclass(UpdateInfo.class);

      AVObject.registerSubclass(Moment.class);      //发布信息类
      AVObject.registerSubclass(Comment.class);      //评论类
      AVObject.registerSubclass(Reply.class);      //评论类
      AVObject.registerSubclass(Image.class);       //图片类


    AVOSCloud.initialize(this, appId, appKey);
    //AVOSCloud.initialize(this, publicId,publicKey);
    //AVOSCloud.initialize(this, testAppId, testAppKey);

    // 节省流量
    AVOSCloud.setLastModifyEnabled(true);

    PushManager.getInstance().init(ctx);
    AVOSCloud.setDebugLogEnabled(debug);
    AVAnalytics.enableCrashReport(this, !debug);
    initImageLoader(ctx);
    initBaiduMap();
    if (App.debug) {
      openStrictMode();
    }

    initChatManager();

    if (App.debug) {
      Logger.level = Logger.VERBOSE;
    } else {
      Logger.level = Logger.NONE;
    }
  }

  private void initChatManager() {
    final ChatManager chatManager = ChatManager.getInstance();
    chatManager.init(this);
    if (LeanchatUser.getCurrentUser() != null) {
      chatManager.setupManagerWithUserId(LeanchatUser.getCurrentUser().getObjectId());
    }
    chatManager.setConversationEventHandler(ConversationManager.getEventHandler());
    ChatManager.setDebugEnabled(App.debug);
  }

  public void openStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()   // or .detectAll() for all detectable problems
        .penaltyLog()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects()
        .detectLeakedClosableObjects()
        .penaltyLog()
            //.penaltyDeath()
        .build());
  }

  /**
   * 初始化ImageLoader
   */
  public static void initImageLoader(Context context) {
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
        context)
        .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory()
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .diskCacheSize(50*1024*1024)
        .diskCacheFileCount(100)
        .writeDebugLogs()
        .build();
    ImageLoader.getInstance().init(config);
  }

  private void initBaiduMap() {
    SDKInitializer.initialize(this);
  }
}
