// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class DiscoverFragment$$ViewInjector<T extends com.avoscloud.chat.fragment.DiscoverFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493056, "field 'listView'");
    target.listView = finder.castView(view, 2131493056, "field 'listView'");
  }

  @Override public void reset(T target) {
    target.listView = null;
  }
}
