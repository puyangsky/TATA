package com.avoscloud.chat.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.Image;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.util.Bimp;
import com.avoscloud.chat.util.FileUtils;
import com.avoscloud.chat.util.ImageItem;
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.PhotoUtils;
import com.avoscloud.chat.util.PublicWay;
import com.avoscloud.chat.util.Res;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class PublishActivity extends Activity {

    private static final int IMAGE_PICK_REQUEST = 3;
    private static final int CROP_REQUEST = 4;
    private static final int TAKE_PICTURE = 5;
    
    public static final int TYPE_PERSONAL = 1;
    public static final int TYPE_OTHER = 2;

    int num = 0;
    int totalNum = 0;

    public static int HEIGHT = 720;
    public static int WEIGHT = 720;
    //当前的activity为最开始的parent
    private View parentView;

    //添加图片的按钮图片
    public static Bitmap bimap ;

    //选择提交的图片
    private Bitmap bitmap = null;

    //图片的url
    private String picPath = "";

    //类型
    private int type = TYPE_PERSONAL;

    //位置
    private static int pos = 0;
    
    //发布信息的类型
    @InjectView(R.id.publish_type)
    public ListView typeList;

    @InjectView(R.id.activity_publish_btn)
    public Button publish_btn;

    //弹出框
    private PopupWindow pop;
    //弹出框线性布局
    private LinearLayout ll_popup;

    private GridAdapter adapter;

    private GridView noScrollgridview;

    //进度条
    private ProgressDialog progressDialog;

    //更新进度条
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //设置进度条的值
            if(msg.arg1 == totalNum){
                progressDialog.dismiss();

                //清空bitmap图片和JPEG图片
                Bimp.clearImage();

                //清空文字
                text = "";

                //结束发布activity，回到主界面
                for(Activity act : PublicWay.activityList){
                    act.finish();
                }

            }else {
                progressDialog.setProgress(msg.arg1);
            }
        }
    };

    @OnClick(R.id.activity_publish_btn)
    public void onPublish_Btn_Click() {

        //判断文字是否为空
        if(publish_text.getText().toString().isEmpty()){
            Toast.makeText(PublishActivity.this, "输入内容为空", Toast.LENGTH_LONG).show();
            return ;
        }

        //确定发布类型
        type = getPublishType();

        //上传发布的信息
        uploade_publish_content();

        //显示进度条
        progressDialog = new ProgressDialog(PublishActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在上传");
        progressDialog.setTitle("uploading");
        progressDialog.setMax(totalNum);
        progressDialog.setProgress(0);
        progressDialog.show();

    }


    @InjectView(R.id.activity_publish_text)
    public EditText publish_text;

    public static String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);       //结束是所有的activity一起结束
        parentView = getLayoutInflater().inflate(R.layout.activity_publish, null);
        setContentView(parentView);
        ButterKnife.inject(this);
        InitPopWindow();
        InitPublishType();
    }

    private void InitPublishType() {
        String[] types = {"说说", "好友" };
        typeList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, types));
        typeList.setItemChecked(0, true);
        typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pos = i;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    Bitmap bm = FileUtils.getBitmapFromUrl(FileUtils.lastPic, WEIGHT, HEIGHT);  //自定义设定了拍摄图片的存储路径,并使用像素压缩
                    String fileName = String.valueOf(System.currentTimeMillis());
                    String filePath = FileUtils.saveBitmap(bm, fileName);       //使用质量压缩图片，并使用当前时间存储
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    takePhoto.setThumbnailPath(filePath);
                    takePhoto.setImagePath(FileUtils.lastPic);
                    Log.e("takePhoto", takePhoto.getThumbnailPath());
                    Log.e("takePhoto", takePhoto.getImagePath());
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
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
        saveOldConfigument();
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
        final Moment moment = new Moment();
        moment.setUser(LeanchatUser.getCurrentUser());//当前用户
        moment.setContent(publish_text.getText().toString());//文字信息
        moment.setPosition(LeanchatUser.getCurrentUser().getGeoPoint());//坐标
        moment.setType(type);//类型
        num = 0;
        totalNum = 0;
        //添加图片文件
        for(final ImageItem item : Bimp.tempSelectBitmap){
            if(!TextUtils.isEmpty(item.getImagePath())){
                totalNum ++;
                String path = "";
                final LeanchatUser user = (LeanchatUser)AVUser.getCurrentUser();
                try {
                    String fileNameLocal = String.valueOf(System.currentTimeMillis());
                    String fileNameRemote = user.getUsername()+String.valueOf(System.currentTimeMillis());
                    boolean flag = true;
                    if (!TextUtils.isEmpty(item.getThumbnailPath())) {      //如果压缩文件存在
                        try {
                            File file = new File(item.getThumbnailPath());
                            if (file.exists()) {
                                path = item.getThumbnailPath();
                                flag = false;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(flag){
                        path = item.getImagePath();
                        Bitmap bm = FileUtils.getBitmapFromUrl(path, WEIGHT, HEIGHT); //像素压缩
                        path = FileUtils.saveBitmap(bm, fileNameLocal);       //质量压缩
                    }
                    Log.e("path", path);
                    final AVFile avfile = AVFile.withAbsoluteLocalPath(fileNameRemote, path);
                    avfile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e1) {
                            if (null == e1) {        //上传成功
                                Log.e("savePic", "Yes");
                                if(avfile == null){
                                    Log.e("file", "save null");
                                    return;
                                }
                                final Image image = new Image();
                                image.setFile(avfile);
                                image.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e2) {
                                        if (null != e2) {
                                            Log.e("image", "save error");
                                            return;
                                        } else {
                                            Log.e("image", "save ok");
                                            if (image == null) {
                                                Log.e("image", "null");
                                                return;
                                            }
                                                moment.addFile(image);
//                                               //保存Moment
                                                try {
                                                    synchronized (PublishActivity.this){
                                                    moment.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(AVException e3) {
                                                            if (null == e3) {
                                                                //保存成功
                                                                Log.e("Moment", " OK");
                                                            } else {
                                                                //保存失败
                                                                Log.e("Moment", "No");
                                                            }
                                                                Log.e("num", ""+num);
                                                                num ++;
                                                                Message msg = handler.obtainMessage();
                                                                msg.arg1 = num;
                                                                handler.sendMessage(msg);
                                                            }
                                                        });
                                                    }
                                                } catch (Exception e4) {
                                                    e4.printStackTrace();
                                                }
                                            }
                                    }
                                });
                            } else {
                                Log.e("savePic", "No");
                            }
                        }
                    });
                } catch (Exception e5) {
                    Log.e("AVFile", "error  thumb "+ item.getThumbnailPath() + " path "+item.getImagePath());
                    e5.printStackTrace();
                }
            }
        }
    }

    /*
        存储图片，并返回对应的AVFile类
     */
    public AVFile saveAVFile(String path, final SaveCallback saveCallback) {
        AVFile avfile = null;
        int picNum = 0;
        //发布的图片数量加1,更改并获取最新的图片数量
        final LeanchatUser user = (LeanchatUser)AVUser.getCurrentUser();
        user.setFetchWhenSave(true);
        user.increment("publishPicNum");
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    Log.e("user num", "save error");
                    return;
                }
                Log.e("publishPicNum", "" + user.getPublishPicNum());

            }
        });
        picNum = user.getPublishPicNum();
        try {
            String fileName = user.getUsername()+"publishPic"+user.getPublishPicNum()+".png";
            avfile = AVFile.withAbsoluteLocalPath(fileName, path);
            avfile.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (null == e) {        //上传成功
                        Log.e("savePic", "Yes");
                    } else {
                        Log.e("savePic", "No");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avfile;
    }

    /**
     * 弹出框初始化
     */
    public void InitPopWindow() {

        pop = new PopupWindow(PublishActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PublishActivity.this,
                        AlbumActivity.class);
                Log.e("bt2 intent", "start");
                saveOldConfigument();           //保存进入Album之前的状态
                startActivity(intent);
                Log.e("bt2 intent", "end");
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(PublishActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(PublishActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    saveOldConfigument();
                    startActivity(intent);
                }
            }
        });

    }

    public int getPublishType() {
        int choice = TYPE_PERSONAL;
        switch (pos){
            case 0:
                choice = TYPE_PERSONAL;
                break;
            case 1:
                choice = TYPE_OTHER;
                break;
        }
        return choice;
    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 9){
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    @Override
    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        File out = new File(FileUtils.getCameraPath());
        Log.e("CameraPath", FileUtils.lastPic);
        Uri uri = Uri.fromFile(out);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        saveOldConfigument();
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    public void saveOldConfigument() {
        text = publish_text.getText().toString();
    }

    public void recoverConfigument() {
        typeList.setItemChecked(pos, true);
        publish_text.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recoverConfigument();
    }
}
