package com.xtone.game87873.general.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {
	
	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
	
	/**
	 * 获取图片名称获取图片的资源id的方法
	 * 
	 * @param imageName
	 * @return
	 */
	public static int getResource(String imageName, Context context) {
		int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
		return resId;
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmapByAssets(Context context, String url) {
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(url);
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 判断asset是否存在图片
	 * 
	 * @param context
	 * @param pt
	 * @return
	 */
	public static boolean hasAssetsFile(Context context, String pt) {
		AssetManager am = context.getAssets();
		try {
			String[] names = am.list("");
			for (int i = 0; i < names.length; i++) {
				if (names[i].equals(pt.trim())) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
