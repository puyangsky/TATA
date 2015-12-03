package com.avoscloud.chat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.EntryLoginActivity;
import com.avoscloud.chat.service.PushManager;
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
//                Toast.makeText(getActivity(), "FUCK", Toast.LENGTH_SHORT).show();
                chatManager.closeWithCallback(new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                    }
                });
                PushManager.getInstance().unsubscribeCurrentUserChannel();
                AVUser.logOut();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), EntryLoginActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }
}
