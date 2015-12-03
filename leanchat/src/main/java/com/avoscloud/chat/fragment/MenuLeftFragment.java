package com.avoscloud.chat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avoscloud.chat.R;
import com.avoscloud.leanchatlib.controller.ChatManager;


/**
 * Created by Administrator on 2015/11/30.
 */
public class MenuLeftFragment extends Fragment {
    private TextView logoutView;
    ChatManager chatManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_left_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatManager = ChatManager.getInstance();
        logoutView = (TextView) getView().findViewById(R.id.tv_log_out);
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "FUCK", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
