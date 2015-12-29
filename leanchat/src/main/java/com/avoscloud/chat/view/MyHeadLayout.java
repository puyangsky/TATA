package com.avoscloud.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avoscloud.chat.R;

/**
 * Created by Administrator on 2015/12/29.
 */
public class MyHeadLayout extends HeaderLayout {
	public MyHeadLayout(Context context) {
		super(context);
	}

	public MyHeadLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void init() {
		mInflater = LayoutInflater.from(getContext());
		header = (RelativeLayout) mInflater.inflate(R.layout.my_header, null, false);
//		header = (RelativeLayout) mInflater.inflate(R.layout.chat_common_base_header, null, false);
		titleView = (TextView) header.findViewById(R.id.titleView);
		leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
		rightContainer = (LinearLayout) header.findViewById(R.id.rightContainer);
		backBtn = (Button) header.findViewById(R.id.backBtn);
		addView(header);
	}
}
