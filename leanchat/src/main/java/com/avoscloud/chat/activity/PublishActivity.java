package com.avoscloud.chat.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.PhotoUtils;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.util.Log;
import android.widget.ImageView;

import javax.security.auth.login.LoginException;

public class PublishActivity extends Activity {

    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;

    //选择提交的图片
    private Bitmap bitmap = null;

    //图片的url
    private String picPath = "";

    @InjectView(R.id.activity_publish_btn)
    public Button publish_btn;

    @OnClick(R.id.activity_publish_btn)
    public void onPublish_Btn_Click() {
        //上传发布的信息
        uploade_publish_content();
        //结束发布activity，回到主界面
        this.finish();
    }

    @InjectView(R.id.activity_publish_text)
    public EditText publish_text;

    @InjectView(R.id.publish_addbutton_view)
    public ImageView imageView;

    @OnClick(R.id.publish_addbutton_view)
    public void onAvatarClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.inject(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                Uri uri = data.getData();
                picPath = PhotoUtils.getImageAbsolutePath(this, uri);
                Log.e("picPath", picPath);
                bitmap = PhotoUtils.getImageThumbnail(picPath, 300, 300);  //压缩图片,压缩比需要调整
                Log.e("compress bitmap", "done");
                imageView.setImageBitmap(bitmap);       //更新本地图片，这里没有更改压缩图片，下一步更新
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
                Log.i("bitmap Path = ", path);
                PhotoUtils.saveBitmap(path, bitmap);
                if (bitmap != null && bitmap.isRecycled() == false) {
                    bitmap.recycle();
                }
            }
        }
        return path;
    }

    private void uploade_publish_content() {
        Moment moment = new Moment();
        moment.setUser(LeanchatUser.getCurrentUser());//当前用户
        moment.setContent(publish_text.getText().toString());//文字信息
        moment.setPosition(LeanchatUser.getCurrentUser().getGeoPoint());//坐标

        //本地存储照片，并释放bitmap内存
        String path = "";
        if (bitmap != null) {
            path = PathUtils.getAvatarCropPath();
            PhotoUtils.saveBitmap(path, bitmap);                //这里是压缩之后的bitmap
            if (bitmap != null && bitmap.isRecycled() == false) {
                bitmap.recycle();
            }
        }

        //保存AVFile
        AVFile file = null;
        if(!path.equals("")){
            file = saveAVFile(path, null);          //这里下载之后的大小还是那么大
        }

        //AVFileList
        List<AVFile> list = new LinkedList<AVFile>();
        list.add(file);
        moment.setFileList(list);

        //保存Moment
        try {
            moment.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (null == e){
                        //保存成功
                        Log.e("Moment", "OK");
                    }else {
                        //保存失败
                        Log.e("Moment", "No");
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
        存储图片，并返回对应的AVFile类
     */
    public AVFile saveAVFile(String path, final SaveCallback saveCallback) {
        AVFile file = null;

        int picNum = 0;
        //发布的图片数量加1,更改并获取最新的图片数量
        final LeanchatUser user = (LeanchatUser)AVUser.getCurrentUser();
        user.setFetchWhenSave(true);
        user.increment("publishPicNum");
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                Log.e("publishPicNum", "" + user.getPublishPicNum());
            }
        });
        picNum = user.getPublishPicNum();
        try {
            String fileName = user.getUsername()+"publishPic"+user.getPublishPicNum()+".png";
            file = AVFile.withAbsoluteLocalPath(fileName, path);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (null == e) {        //上传成功
                        Log.e("savePic", "Yes");
                    } else {
                        Log.e("savePic", "No");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
