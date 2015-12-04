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
import com.avoscloud.chat.activity.PersonViewActivity;
import com.avoscloud.chat.activity.ProfileNotifySettingActivity;
import com.avoscloud.chat.service.PushManager;
import com.avoscloud.chat.service.UpdateService;
import com.avoscloud.leanchatlib.controller.ChatManager;


/**
 * Created by Administrator on 2015/11/30.
 */
public class MenuLeftFragment extends Fragment {
    private TextView logoutView, checkUpdateView, personProfileView, settingsView;
    ChatManager chatManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_left_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //avatar

        //person profile
        personProfileView = (TextView) getView().findViewById(R.id.tv_profile_person);
        personProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonViewActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //log out
        logoutView = (TextView) getView().findViewById(R.id.tv_log_out);
        chatManager = ChatManager.getInstance();
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //check update
        checkUpdateView = (TextView) getView().findViewById(R.id.tv_check_update);
        checkUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateService updateService = UpdateService.getInstance(getActivity());
                updateService.showSureUpdateDialog();
            }
        });

        //settings
        settingsView = (TextView) getView().findViewById(R.id.tv_settings);
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileNotifySettingActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }
}
