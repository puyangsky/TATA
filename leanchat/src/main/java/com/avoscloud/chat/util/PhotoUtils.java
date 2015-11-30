package com.avoscloud.chat.util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.avoscloud.leanchatlib.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtils {

  /**
   * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
   * @param imageUri
   * @author yaoxing
   * @date 2014-10-12
   */
  public static String getImageAbsolutePath(Activity context, Uri imageUri) {
    if (context == null || imageUri == null)
      return null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
      if (isExternalStorageDocument(imageUri)) {
        String docId = DocumentsContract.getDocumentId(imageUri);
        String[] split = docId.split(":");
        String type = split[0];
        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      } else if (isDownloadsDocument(imageUri)) {
        String id = DocumentsContract.getDocumentId(imageUri);
        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
        return getDataColumn(context, contentUri, null, null);
      } else if (isMediaDocument(imageUri)) {
        String docId = DocumentsContract.getDocumentId(imageUri);
        String[] split = docId.split(":");
        String type = split[0];
        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = new String[] { split[1] };
        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    } // MediaStore (and general)
    else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
      // Return the remote address
      if (isGooglePhotosUri(imageUri))
        return imageUri.getLastPathSegment();
      return getDataColumn(context, imageUri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
      return imageUri.getPath();
    }
    return null;
  }

  public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
    Cursor cursor = null;
    String column = MediaStore.Images.Media.DATA;
    String[] projection = { column };
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }

  /**
   *
   */
//  public static Bitmap getImageThumbnailBitmap(Bitmap)

  /**
   * 获取指定路径下的图片的指定大小的缩略图 getImageThumbnail，比例压缩，去宽高比最小的
   *
   * @return Bitmap
   * @throws
   */
  public static Bitmap getImageThumbnail(String imagePath, int width,
                                         int height) {
    Bitmap bitmap = null;
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    // 获取这个图片的宽和高，注意此处的bitmap为null
    bitmap = BitmapFactory.decodeFile(imagePath, options);
    options.inJustDecodeBounds = false; // 设为 false
    // 计算缩放比
    int h = options.outHeight;
    int w = options.outWidth;
    int beWidth = w / width;
    int beHeight = h / height;
    int be = 1;
    if (beWidth < beHeight) {
      be = beWidth;
    } else {
      be = beHeight;
    }
    if (be <= 0) {
      be = 1;
    }
    options.inSampleSize = be;
    // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
    bitmap = BitmapFactory.decodeFile(imagePath, options);
    // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    return bitmap;
  }

  public static void saveBitmap(String filePath,
                                Bitmap bitmap) {
    //设置合适的压缩比
    int options = 100;
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
//    while(baos.toByteArray().length / 1024 > 500){
//      baos.reset();
//      options -= 10;
//      Log.e("options", "" + options);
//      bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
//    }


    //生成file文件
    File file = new File(filePath);
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(file);
      if (bitmap.compress(Bitmap.CompressFormat.PNG, options, out)) {
//        out.write(baos.toByteArray());
        out.flush();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      Utils.closeQuietly(out);
    }
  }

  public static File getFilePath(String filePath, String fileName) {
    File file = null;
    makeRootDirectory(filePath);
    try {
      file = new File(filePath + fileName);
      if (!file.exists()) {
        file.createNewFile();
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      LogUtils.logException(e);
    }
    return file;
  }

  public static void makeRootDirectory(String filePath) {
    File file = null;
    try {
      file = new File(filePath);
      if (!file.exists()) {
        file.mkdirs();
      }
    } catch (Exception e) {

    }
  }

  /**
   * 读取图片属性：旋转的角度
   *
   * @param path 图片绝对路径
   * @return degree旋转的角度
   */

  public static int readPictureDegree(String path) {
    int degree = 0;
    try {
      ExifInterface exifInterface = new ExifInterface(path);
      int orientation = exifInterface.getAttributeInt(
          ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return degree;

  }

  /**
   * 旋转图片一定角度
   * rotaingImageView
   *
   * @return Bitmap
   * @throws
   */
  public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
    // 旋转图片 动作
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    // 创建新的图片
    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    return resizedBitmap;
  }

  /**
   * 将图片变为圆角
   *
   * @param bitmap 原Bitmap图片
   * @param pixels 图片圆角的弧度(单位:像素(px))
   * @return 带有圆角的图片(Bitmap 类型)
   */
  public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
        bitmap.getHeight(), Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);
    final float roundPx = pixels;

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }

  /**
   * 将图片转化为圆形头像
   *
   * @throws
   * @Title: toRoundBitmap
   */
  public static Bitmap toRoundBitmap(Bitmap bitmap) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    float roundPx;
    float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
    if (width <= height) {
      roundPx = width / 2;

      left = 0;
      top = 0;
      right = width;
      bottom = width;

      height = width;

      dst_left = 0;
      dst_top = 0;
      dst_right = width;
      dst_bottom = width;
    } else {
      roundPx = height / 2;

      float clip = (width - height) / 2;

      left = clip;
      right = width - clip;
      top = 0;
      bottom = height;
      width = height;

      dst_left = 0;
      dst_top = 0;
      dst_right = height;
      dst_bottom = height;
    }

    Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final Paint paint = new Paint();
    final Rect src = new Rect((int) left, (int) top, (int) right,
        (int) bottom);
    final Rect dst = new Rect((int) dst_left, (int) dst_top,
        (int) dst_right, (int) dst_bottom);
    final RectF rectF = new RectF(dst);

    paint.setAntiAlias(true);// 设置画笔无锯齿

    canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

    // 以下有两种方法画圆,drawRounRect和drawCircle
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
    // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
    canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

    return output;
  }

  //质量压缩
  public static String simpleCompressImage(String path, String newPath) {
    Bitmap bitmap = BitmapFactory.decodeFile(path);
    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(newPath);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      Utils.closeQuietly(outputStream);
    }
    recycle(bitmap);
    return newPath;
  }

  public static void recycle(Bitmap bitmap) {
    if (bitmap != null && !bitmap.isRecycled()) {
      bitmap.recycle();
    }
    System.gc();
  }

}
