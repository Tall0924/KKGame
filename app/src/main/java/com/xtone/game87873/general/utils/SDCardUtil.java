package com.xtone.game87873.general.utils;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


/**
 * sdcard相关操作工具类
 * @author: chenyh
 * @Date: 2014-8-11下午5:59:59
 */
public class SDCardUtil {
	/**
	 * 检测sd卡是否存在
	 * @return
	 */
	public static boolean checkSDcard(){
		String status = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(status)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 获取SD卡，可用容量
	 * @return 返回sdcard可用容量。如果没有装载sdcard，返回-1
	 */
	public static long getAvailCapacity() {
		long size = 0;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long availBlocks = sf.getAvailableBlocks();

			size =  bSize * availBlocks;// 可用大小
		}else{
			size = -1;
		}
		Log.e("SDcardSize",size+"");
		return size;
	}
	
	public static class SDCardUnavailException extends IOException{
		public static int SDCARD_FULL = 1;
		public static int SDCARD_UNMOUNTED = 2;
		private int causeCode;
		public int getCauseCode(){
			return causeCode;
		}
		public SDCardUnavailException(int causeCode) {
			super();
			this.causeCode = causeCode;
		}
		
	}
}
