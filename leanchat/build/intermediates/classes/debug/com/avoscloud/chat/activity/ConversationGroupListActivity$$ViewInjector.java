// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ConversationGroupListActivity$$ViewInjector<T extends com.avoscloud.chat.activity.ConversationGroupListActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493054, "field 'groupListView'");
    target.groupListView = finder.castView(view, 2131493054, "field 'groupListView'");
  }

  @Override public void reset(T target) {
    target.groupListView = null;
  }
}
