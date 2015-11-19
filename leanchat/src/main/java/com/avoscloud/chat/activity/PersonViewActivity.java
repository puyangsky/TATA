package com.avoscloud.chat.activity;

import android.os.Bundle;

import com.avoscloud.chat.R;

/*
*  Created by puyangsky 2015/11/18.
* */
public class PersonViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_person_view);
        initActionBar(R.string.profile_person);
    }
}
