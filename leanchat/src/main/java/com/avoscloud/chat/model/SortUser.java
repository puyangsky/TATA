package com.avoscloud.chat.model;

import com.avoscloud.leanchatlib.model.LeanchatUser;

/**
 * Created by lzw on 15/1/9.
 */
public class SortUser {
  private LeanchatUser innerUser;
  private String sortLetters;

  public LeanchatUser getInnerUser() {
    return innerUser;
  }

  public void setInnerUser(LeanchatUser innerUser) {
    this.innerUser = innerUser;
  }

  public String getSortLetters() {
    return sortLetters;
  }

  public void setSortLetters(String sortLetters) {
    this.sortLetters = sortLetters;
  }
}
