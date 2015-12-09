package com.avoscloud.chat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.avoscloud.chat.model.Image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Bimp {
	public static int max = 0;
	
	public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>();   //选择的图片的临时列表

	public static void clearImage(){
		for(ImageItem imageItem : tempSelectBitmap){
			tempSelectBitmap.remove(imageItem);
			imageItem.getBitmap().recycle();
			//删除压缩文件
			File f = new File(imageItem.getThumbnailPath());
			if (f.exists()) {
				f.delete();
			}
		}
		max = 0;
	}

	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1280)
					&& (options.outHeight >> i <= 1280)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
}
