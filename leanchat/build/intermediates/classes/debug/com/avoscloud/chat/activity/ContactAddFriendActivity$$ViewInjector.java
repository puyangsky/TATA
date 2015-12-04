// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ContactAddFriendActivity$$ViewInjector<T extends com.avoscloud.chat.activity.ContactAddFriendActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493014, "field 'searchNameEdit'");
    target.searchNameEdit = finder.castView(view, 2131493014, "field 'searchNameEdit'");
    view = finder.findRequiredView(source, 2131493016, "field 'listView'");
    target.listView = finder.castView(view, 2131493016, "field 'listView'");
    view = finder.findRequiredView(source, 2131493015, "method 'search'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.search(p0);
        }
      });
  }

  @Override public void reset(T target) {
    target.searchNameEdit = null;
    target.listView = null;
  }
}
