package com.avoscloud.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

/**
 * Created by puyangsky on 2015/12/16.
 */
public class MomentListView extends XListView implements XListView.IXListViewListener {

    public MomentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initMomentListView() {
        setPullLoadEnable(false);
        setPullRefreshEnable(true);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
    // TODO: 2015/12/16  fresh the square

    }

    @Override
    public void onLoadMore() {

    }

}
