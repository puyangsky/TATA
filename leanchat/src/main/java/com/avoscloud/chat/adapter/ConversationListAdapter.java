package com.avoscloud.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.avoscloud.chat.viewholder.ConversationItemHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wli on 15/10/8.
 */
public class ConversationListAdapter<T> extends RecyclerView.Adapter<ConversationItemHolder> {

  private List<T> dataList = new ArrayList<T>();

  public List<T> getDataList() {
    return dataList;
  }

  public void setDataList(List<T> datas) {
    dataList.clear();
    if (null != datas) {
      dataList.addAll(datas);
    }
  }

  public void addDataList(List<T> datas) {
    dataList.addAll(0, datas);
  }

  @Override
  public ConversationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ConversationItemHolder(parent);
  }

  @Override
  public void onBindViewHolder(ConversationItemHolder holder, int position) {
    if (position >= 0 && position < dataList.size()) {
      holder.bindData(dataList.get(position));
    }
  }

  @Override
  public int getItemCount() {
    return dataList.size();
  }
}
