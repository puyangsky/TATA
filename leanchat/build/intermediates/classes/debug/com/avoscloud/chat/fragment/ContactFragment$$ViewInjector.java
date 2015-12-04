// Generated code from Butter Knife. Do not modify!
package com.avoscloud.chat.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ContactFragment$$ViewInjector<T extends com.avoscloud.chat.fragment.ContactFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492891, "field 'dialogTextView'");
    target.dialogTextView = finder.castView(view, 2131492891, "field 'dialogTextView'");
    view = finder.findRequiredView(source, 2131493022, "field 'friendsList'");
    target.friendsList = finder.castView(view, 2131493022, "field 'friendsList'");
    view = finder.findRequiredView(source, 2131493023, "field 'rightLetter'");
    target.rightLetter = finder.castView(view, 2131493023, "field 'rightLetter'");
  }

  @Override public void reset(T target) {
    target.dialogTextView = null;
    target.friendsList = null;
    target.rightLetter = null;
  }
}
