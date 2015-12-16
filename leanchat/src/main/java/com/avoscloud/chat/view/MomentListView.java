package com.avoscloud.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.avoscloud.chat.activity.MainActivity;
import com.avoscloud.chat.adapter.ListItemAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by puyangsky on 2015/12/16.
 */
public class MomentListView extends MyXListView implements MyXListView.IXListViewListener {
    private ListItemAdapter adapter;
    private Handler handler;
    public MomentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initMomentListView(ListItemAdapter adapter) {
        setPullRefreshEnable(true);
        setPullLoadEnable(true);
        setAutoLoadEnable(true);
        setXListViewListener(this);
        setRefreshTime(getTime());
        this.adapter = adapter;
        setAdapter(adapter);
        handler = new Handler();
        setOnScrollListener(new XListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                MainActivity.hideSoftInput(getContext());
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.hideSoftInput(getContext());
                return false;
            }
        });
    }
    public String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }


    @Override
    public void onRefresh() {
    // TODO: 2015/12/16  fresh the square

    }

    @Override
    public void onLoadMore() {

    }

}
