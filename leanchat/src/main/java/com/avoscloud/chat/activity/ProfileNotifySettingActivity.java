package com.avoscloud.chat.activity;

import android.os.Bundle;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.BaseActivity;

/**
 * Created by lzw on 14-9-24.
 */
public class ProfileNotifySettingActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.profile_setting_notify_layout);
    initActionBar(R.string.profile_notifySetting);
  }

}
