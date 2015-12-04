// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ProfileFragment$$ViewInjector<T extends com.avoscloud.chat.fragment.ProfileFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493161, "field 'avatarView'");
    target.avatarView = finder.castView(view, 2131493161, "field 'avatarView'");
    view = finder.findRequiredView(source, 2131493162, "field 'userNameView'");
    target.userNameView = finder.castView(view, 2131493162, "field 'userNameView'");
    view = finder.findRequiredView(source, 2131493165, "method 'onCheckUpdateClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCheckUpdateClick();
        }
      });
    view = finder.findRequiredView(source, 2131493163, "method 'onShowPersonViewClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onShowPersonViewClick();
        }
      });
    view = finder.findRequiredView(source, 2131493164, "method 'onNotifySettingClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onNotifySettingClick();
        }
      });
    view = finder.findRequiredView(source, 2131493166, "method 'onLogoutClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLogoutClick();
        }
      });
    view = finder.findRequiredView(source, 2131493160, "method 'onAvatarClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAvatarClick();
        }
      });
  }

  @Override public void reset(T target) {
    target.avatarView = null;
    target.userNameView = null;
  }
}
