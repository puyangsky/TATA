// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class PublishActivity$$ViewInjector<T extends com.avoscloud.chat.activity.PublishActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492947, "field 'publish_btn' and method 'onPublish_Btn_Click'");
    target.publish_btn = finder.castView(view, 2131492947, "field 'publish_btn'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onPublish_Btn_Click();
        }
      });
    view = finder.findRequiredView(source, 2131492945, "field 'publish_text'");
    target.publish_text = finder.castView(view, 2131492945, "field 'publish_text'");
  }

  @Override public void reset(T target) {
    target.publish_btn = null;
    target.publish_text = null;
  }
}
