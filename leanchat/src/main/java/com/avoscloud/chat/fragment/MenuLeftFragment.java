package com.avoscloud.chat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avoscloud.chat.R;

/**
 * Created by Administrator on 2015/11/30.
 */
public class MenuLeftFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_left_menu, container, false);
    }
}
