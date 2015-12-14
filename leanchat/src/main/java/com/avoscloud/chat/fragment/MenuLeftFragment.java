package com.avoscloud.chat.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.PhotoUtils;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;


/**
 * Created by puyangsky on 2015/11/30.
 */
public class MenuLeftFragment extends Fragment {
    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;
    private TextView logoutView, checkUpdateView, personProfileView, settingsView, userNameView;
    private ImageView personAvatarView;
    ChatManager chatManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_left_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //avatar
        personAvatarView = (ImageView) getView().findViewById(R.id.person_avatar_view);
        personAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, IMAGE_PICK_REQUEST);
            }
        });

        //username
        userNameView = (TextView) getView().findViewById(R.id.tv_current_username);

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

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        LeanchatUser curUser = (LeanchatUser)AVUser.getCurrentUser();
        userNameView.setText(AVUser.getCurrentUser().getUsername());
        ImageLoader.getInstance().displayImage(curUser.getAvatarUrl(), personAvatarView, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);
        Log.d("pyt", "头像url: " + curUser.getAvatarUrl());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                Uri uri = data.getData();
                startImageCrop(uri, 200, 200, CROP_REQUEST);
            } else if (requestCode == CROP_REQUEST) {
                final String path = saveCropAvatar(data);
                LeanchatUser user = (LeanchatUser)AVUser.getCurrentUser();
                user.saveAvatar(path, null);
            }
        }
    }
    public Uri startImageCrop(Uri uri, int outputX, int outputY,
                              int requestCode) {
        Intent intent = null;
        intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        String outputPath = PathUtils.getAvatarTmpPath();
        Uri outputUri = Uri.fromFile(new File(outputPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false); // face detection
        startActivityForResult(intent, requestCode);
        return outputUri;
    }

    private String saveCropAvatar(Intent data) {
        Bundle extras = data.getExtras();
        String path = null;
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                bitmap = PhotoUtils.toRoundCorner(bitmap, 10);
                path = PathUtils.getAvatarCropPath();
                PhotoUtils.saveBitmap(path, bitmap);
                if (bitmap != null && bitmap.isRecycled() == false) {
                    bitmap.recycle();
                }
            }
        }
        return path;
    }
}
