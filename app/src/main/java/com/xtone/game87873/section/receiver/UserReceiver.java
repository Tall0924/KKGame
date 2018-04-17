package com.xtone.game87873.section.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xtone.game87873.R;
import com.xtone.game87873.general.db.AppDownloadDao;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.utils.AppStorage;
import com.xtone.game87873.general.utils.FileUtil;
import com.xtone.game87873.general.utils.ToastUtils;

/**
 * 广播接收器
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-11-6 下午3:00:17
 */
public class UserReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// 接收安装广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			if (AppStorage.isAutoDeletePackage()) {
				String packageName = intent.getDataString();
				packageName = packageName.substring(8);
				List<DownloadInfo> apps = new AppDownloadDao(context).queryByPackage(packageName);
				// System.out.println("安装了:" + packageName + "包名的程序" + apps);
				if (apps != null) {
					for (DownloadInfo info : apps) {
						if (FileUtil.deleteFile(info.getApkSavePath())) {// 删除下载的apk文件
							ToastUtils.toastShow(context,R.string.install_sucess_prompts);
						}
					}
				}
			}
		}
		// 接收卸载广播
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			String packageName = intent.getDataString();
			// System.out.println("卸载了:" + packageName + "包名的程序");
		}
	}
}
