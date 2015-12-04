// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ConversationRecentFragment$$ViewInjector<T extends com.avoscloud.chat.fragment.ConversationRecentFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492960, "field 'imClientStateView'");
    target.imClientStateView = view;
    view = finder.findRequiredView(source, 2131493106, "field 'refreshLayout'");
    target.refreshLayout = finder.castView(view, 2131493106, "field 'refreshLayout'");
    view = finder.findRequiredView(source, 2131493107, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131493107, "field 'recyclerView'");
  }

  @Override public void reset(T target) {
    target.imClientStateView = null;
    target.refreshLayout = null;
    target.recyclerView = null;
  }
}
