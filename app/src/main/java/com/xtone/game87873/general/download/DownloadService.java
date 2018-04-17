package com.xtone.game87873.general.download;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

/**
 * 下载Service类
 * 
 * @author yangpb
 * @Date:2014-8-22上午11:27:07
 */
public class DownloadService extends Service {

	private static DownloadManager DOWNLOAD_MANAGER;
	private static int maxDownloadThread = 5;

	public static DownloadManager getDownloadManager(Context appContext) {
		// if (!DownloadService.isServiceRunning(appContext)) {
		// Intent downloadSvr = new Intent("download.service.action");
		// appContext.startService(downloadSvr);
		// }
		if (DownloadService.DOWNLOAD_MANAGER == null) {
			DownloadService.DOWNLOAD_MANAGER = new DownloadManager(appContext.getApplicationContext());
			DOWNLOAD_MANAGER.setMaxDownloadThread(maxDownloadThread);
		}
		return DOWNLOAD_MANAGER;
	}

	public DownloadService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public static boolean isServiceRunning(Context context) {
		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (serviceList == null || serviceList.size() == 0) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					DownloadService.class.getName())) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
