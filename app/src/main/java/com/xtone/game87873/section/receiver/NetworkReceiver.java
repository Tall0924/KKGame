package com.xtone.game87873.section.receiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lidroid.xutils.http.HttpHandler;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadService;

/**
 * 监听网络状态
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2016-1-7 下午1:29:41
 */
public class NetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isAvailable()) {
				// 网络连接

				// String name = netInfo.getTypeName();
				if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					// WiFi网络
					continueDownload(context);
				} else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
					// 有线网络
					continueDownload(context);
				} else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					// 3g网络
					// if (AppStorage.isDownloadOnlyWifi()) {
					// CommonDialog dialog = new CommonDialog(context,
					// new DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog,
					// int arg1) {
					// if (arg1 == CommonDialog.CLICK_OK) {
					// conttnueDownload(context);
					// }
					// dialog.dismiss();
					// }
					// });
					// dialog.setContent(context.getResources().getString(
					// R.string.network_prompts));
					// dialog.setOkBtnText(context.getResources().getString(
					// R.string.continue_download));
					// dialog.show();
					// return;
					// // ToastUtils.toastShow(mContext, "您正在使用手机流量下载");
					// }

				}
			} else {
				// 网络断开

			}
		}
	}

	private void continueDownload(Context context) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		list.addAll(DownloadService.getDownloadManager(context).getDownloadingInfoList());
		for (DownloadInfo info : list) {
			HttpHandler<File> handler = info.getDownloadHandler();
			if (handler != null&& handler.getState() == HttpHandler.State.FAILURE) {
				DownloadService.getDownloadManager(context).resumeDownload(
						info, handler.getRequestCallBack());
			}
		}
	}

}
