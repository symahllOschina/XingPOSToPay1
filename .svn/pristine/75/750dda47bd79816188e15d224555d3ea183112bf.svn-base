package com.wanding.xingpos.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.wanding.xingpos.R;


public class PhotoModule {
	public static final int REQUEST_CODE_CAMERA = 0x1001;
	public static final int REQUEST_CODE_CROP_PHOTO = 0x1002;
	private static final String TEMP_PHOTO = "temp_photo.jpg";
	private static final String PHOTO_NAME = "photo.jpg";
	private Context context;

	public PhotoModule(Context context) {
		this.context = context;
	}

	public File getTempPhoto() {
		File file = new File(context.getExternalCacheDir() + File.separator + TEMP_PHOTO);
		return file;
	}

	/**
	 * 保存缓存图片
	 * 
	 * @param bitmap
	 */
	public void savePhoto(Bitmap bitmap) {
		File file = new File(context.getExternalCacheDir() + File.separator + PHOTO_NAME);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取缓存图片
	 * 
	 * @return
	 */
	public Bitmap getPhoto() {
		File file = new File(context.getExternalCacheDir() + File.separator + PHOTO_NAME);
		if (!file.exists()) {
			return null;
		} else {
			Bitmap bitmap = BitmapFactory.decodeFile(context.getExternalCacheDir() + File.separator + PHOTO_NAME);
			return bitmap;
		}
	}

	/**
	 * 获取照片
	 * 
	 * @param request_code
	 */
	public void takePhoto(int request_code) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 照相
			if (request_code == REQUEST_CODE_CAMERA) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempPhoto()));
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
				((Activity) context).startActivityForResult(intent, REQUEST_CODE_CAMERA);
			}
			// 相册中选取
			else if (request_code == REQUEST_CODE_CROP_PHOTO) {
//				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//				i.addCategory(Intent.CATEGORY_OPENABLE);
//				i.setType("image/*");
//				i.putExtra("crop", "true");
//				i.putExtra("aspectX", 1);
//				i.putExtra("aspectY", 1);
//				i.putExtra("outputX", 300);
//				i.putExtra("outputY", 300);
//				i.putExtra("return-data", true);
//				((Activity) context).startActivityForResult(i, REQUEST_CODE_CROP_PHOTO);
				
				
				//选择本地相册文件设置头像
				Intent intent = new Intent(Intent.ACTION_PICK,
	            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
	            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_CROP_PHOTO);// //适用于4.4及以上android版本
			}
		} else {
			Toast.makeText(context,"SD卡不存在！",Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 裁剪照片
	 */
	public void cropPhoto() {
		Intent i = new Intent("com.android.camera.action.CROP");
		i.setDataAndType(Uri.fromFile(getTempPhoto()), "image/*");
		i.putExtra("crop", "true");
		i.putExtra("aspectX", 1);
		i.putExtra("aspectY", 1);
		i.putExtra("outputX", 300);
		i.putExtra("outputY", 300);
		i.putExtra("return-data", true);
		((Activity) context).startActivityForResult(i, REQUEST_CODE_CROP_PHOTO);
	}

}
