package com.avoscloud.chat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/Photo_LJ/";

	public static String originPath = SDPATH + "originPic" + ".JPEG";

	public static String getOriginPath(String fileName){
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			originPath = SDPATH + fileName + ".JPEG";
			File f = new File(SDPATH, fileName + ".JPEG");
			if (f.exists()) {
				f.delete();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return originPath;
	}

	public static Bitmap getBitmapFromUrl(String url, double width, double height){
		Matrix matrix = new Matrix();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(url);
		options.inJustDecodeBounds = false;
		int oldWidth = bitmap.getWidth();
		int oldHeight = bitmap.getHeight();

		if (width < 0 || height < 0){
			return bitmap;
		}else{
//			float scaleWidth = 1;
//			float scaleHeight = 1;
//			if(oldWidth <= oldHeight){
//				scaleWidth = (float)(width/oldWidth);
//				scaleHeight = (float)(height/oldHeight);
//			}else{
//				scaleWidth = (float)(height/oldHeight);
//				scaleHeight = (float)(width/oldWidth);
//			}
			//等比例压缩
			float scale = 1;
			if(oldWidth <= oldHeight){
				scale = (float)(height / oldHeight);
			}else{
				scale = (float)(height / oldWidth);
			}
			matrix.postScale(scale, scale);
		}
		Bitmap nbitmap = Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, matrix, true);
		bitmap.recycle();
		return nbitmap;
	}

	public static String saveBitmap(Bitmap bm, String picName) {
		String path = null;
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			path = SDPATH + picName + ".JPEG";
			File f = new File(SDPATH, picName + ".JPEG"); 
			if (f.exists()) {
				f.delete();
			}

			//压缩到500k
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int options = 92;
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
			Log.e("JPEG Options ", "" + baos.toByteArray().length / 1024);
			while(baos.toByteArray().length / 1024 > 300){
				baos.reset();
				options -= 2;
				bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
				Log.e("JPEG Options ", "" + baos.toByteArray().length / 1024);
			}

			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, options, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

}
