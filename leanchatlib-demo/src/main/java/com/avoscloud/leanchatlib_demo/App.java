package com.avoscloud.leanchatlib_demo;

import android.app.Application;
import android.content.Context;
import com.avos.avoscloud.AVOSCloud;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by lzw on 15/4/27.
 */
public class App extends Application {
  public static void initImageLoader(Context context) {
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
        context)
        .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
            //.memoryCache(new WeakMemoryCache())
        .denyCacheImageMultipleSizesInMemory()
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .build();
    ImageLoader.getInstance().init(config);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    AVOSCloud.initialize(this, "xcalhck83o10dntwh8ft3z5kvv0xc25p6t3jqbe5zlkkdsib",
        "m9fzwse7od89gvcnk1dmdq4huprjvghjtiug1u2zu073zn99");
    ChatManager.setDebugEnabled(true);// tag leanchatlib
    AVOSCloud.setDebugLogEnabled(true);  // set false when release
    ChatManager.getInstance().init(this);
    initImageLoader(this);
  }
}
