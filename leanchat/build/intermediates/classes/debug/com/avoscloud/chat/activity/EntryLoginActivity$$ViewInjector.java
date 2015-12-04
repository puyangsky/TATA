// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class EntryLoginActivity$$ViewInjector<T extends com.avoscloud.chat.activity.EntryLoginActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493060, "field 'userNameView'");
    target.userNameView = finder.castView(view, 2131493060, "field 'userNameView'");
    view = finder.findRequiredView(source, 2131493061, "field 'passwordView'");
    target.passwordView = finder.castView(view, 2131493061, "field 'passwordView'");
    view = finder.findRequiredView(source, 2131493062, "method 'onLoginClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLoginClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493063, "method 'onRegisterClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onRegisterClick(p0);
        }
      });
  }

  @Override public void reset(T target) {
    target.userNameView = null;
    target.passwordView = null;
  }
}
