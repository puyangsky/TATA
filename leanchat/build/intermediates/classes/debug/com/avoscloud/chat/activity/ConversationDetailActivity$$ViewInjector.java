// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ConversationDetailActivity$$ViewInjector<T extends com.avoscloud.chat.activity.ConversationDetailActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493046, "field 'usersGrid'");
    target.usersGrid = finder.castView(view, 2131493046, "field 'usersGrid'");
    view = finder.findRequiredView(source, 2131493047, "field 'nameLayout' and method 'changeName'");
    target.nameLayout = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.changeName();
        }
      });
    view = finder.findRequiredView(source, 2131493048, "field 'quitLayout' and method 'onQuitButtonClick'");
    target.quitLayout = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onQuitButtonClick();
        }
      });
  }

  @Override public void reset(T target) {
    target.usersGrid = null;
    target.nameLayout = null;
    target.quitLayout = null;
  }
}
