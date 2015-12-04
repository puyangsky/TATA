// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ContactFragment$ListHeaderViewHolder$$ViewInjector<T extends com.avoscloud.chat.fragment.ContactFragment.ListHeaderViewHolder> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493026, "field 'msgTipsView'");
    target.msgTipsView = finder.castView(view, 2131493026, "field 'msgTipsView'");
    view = finder.findRequiredView(source, 2131493024, "method 'goNewFriend'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.goNewFriend();
        }
      });
    view = finder.findRequiredView(source, 2131493029, "method 'goGroupConvList'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.goGroupConvList();
        }
      });
  }

  @Override public void reset(T target) {
    target.msgTipsView = null;
  }
}
