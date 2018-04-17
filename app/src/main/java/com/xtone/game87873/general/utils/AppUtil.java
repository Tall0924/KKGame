package com.xtone.game87873.general.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Debug.MemoryInfo;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 应用工具类
 * 
 * @author: chenyh
 * @date: 2014-7-30 下午1:14:53
 * 
 */
public class AppUtil {

	/**
	 * 获取系统分享的Intent
	 * 
	 * @param ctx
	 * @param uriStr
	 * @param subjectStr
	 * @param textStr
	 * @return
	 */
	public static Intent getShareAppIntent(final Context ctx,
			final String uriStr, final String subjectStr, final String textStr) {
		Intent intent = new Intent(Intent.ACTION_SEND);

		if (!TextUtils.isEmpty(uriStr)) {
			Uri uri = Uri.fromFile(FileUtil.getCacheFileFromUri(uriStr));
			intent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, subjectStr);
		intent.putExtra(Intent.EXTRA_TEXT, textStr);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

	/**
	 * 通过包名拿到版本号信息
	 * 
	 * @param context
	 * @param pkName
	 * @return 应用的versionName 如果没有获取到，返回-1
	 */
	public static int getAppVersionCode(Context context, String pkName) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo packageInfo = manager.getPackageInfo(pkName, 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			//
		}
		return -1;
	}
	
	/***
	 * 获取app的版本名
	 * 
	 * **/
	public static String getAppVersionName(Context context) throws NameNotFoundException{
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = null;
		packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		return packageInfo.versionName;
	}

	/**
	 * 通过一个游戏的包名，获得该游戏的icon
	 * 
	 * @param context
	 * @param pkName
	 *            游戏的包名
	 * @return 游戏的icon, drawable实例。如果包名不可用，则返回null
	 */
	public static Drawable getIconFromPKname(Context context, String pkName) {
		try {
			PackageManager manager = context.getPackageManager();
			Drawable drawable = manager.getApplicationIcon(pkName);
			return drawable;
		} catch (NameNotFoundException e) {
			// 说明这个游戏不存在，只是没有删除
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 释放内存
	 * 
	 * @param context
	 * @param whitePkg
	 *            要过略的进程名 一般指的是当前要运行的游戏进程的名字
	 * @return 要显示的数据
	 */
	public static void realeseMM(Context context, String whitePkg) {
		try {
			long releaseMM = 0;
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			// 获取系统中所有正在运行的进程
			List<RunningAppProcessInfo> appProcessInfos = activityManager
					.getRunningAppProcesses();
			for (RunningAppProcessInfo appProcessInfo : appProcessInfos) {
				final String processName = appProcessInfo.processName;
				if (processName.startsWith(context.getPackageName())
						|| processName.contains(whitePkg)
						|| appProcessInfo.importance < ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					continue;
				}

				MemoryInfo[] memoryInfo = activityManager
						.getProcessMemoryInfo(new int[] { appProcessInfo.pid });
				if (memoryInfo[0].getTotalPss() == 0) {
					continue;
				}
				releaseMM += memoryInfo[0].getTotalPrivateDirty() * 1000;

				activityManager.killBackgroundProcesses(processName);
			}
			String releaseStr = FileUtil.FormetFileSize(releaseMM);
			// String[] strings = context.getResources().getStringArray(
			// R.array.check_in_tips);

			// return String.format(strings[Math.abs(new Random().nextInt()) %
			// 4],
			// releaseStr);
		} catch (Exception e) {
			e.printStackTrace();
			// return null;
		}
	}

	/**
	 * 获得系统上已经安装的所有app的包名
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getInstalledAppPackageList(Context context) {
		ArrayList<String> installedPackageList = new ArrayList<String>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo packageInfo : installedPackages) {
			String packageName = packageInfo.packageName;
			installedPackageList.add(packageName);
		}
		return installedPackageList;
	}

	/**
	 * 通过包名判断程序是否安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isApkInstall(Context context, String packageName) {
		PackageInfo packageInfo;
		PackageManager pm = context.getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 通过包名和版本号判断程序是否可更新
	 * 
	 * @param context
	 * @param packageName
	 * @param versionCode
	 * @return
	 */
	public static boolean isApkUpdate(Context context, String packageName,
			int versionCode) {
		PackageInfo packageInfo;
		PackageManager pm = context.getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo != null) {
			if (versionCode > packageInfo.versionCode) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据包名，启动应用
	 * 
	 * @param context
	 * @param pkName
	 */
	public static void startAPP(Context context, String pkName) {
		if (pkName != null) {
			PackageManager packageManager = context.getPackageManager();
			Intent intent = new Intent();
			intent = packageManager.getLaunchIntentForPackage(pkName);
			if (intent != null) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} else {
				Toast.makeText(context, "该游戏已卸载，请刷新！", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	/**
	 * 通过文件路径获取安装游戏
	 * 
	 * @param context
	 * @param file
	 */
	public static void installGame(Context context, File file) {
		// 调用安装软件
		try {
			if (file != null && file.exists()) {
				Uri fileUri = Uri.fromFile(file);
				Intent it = new Intent(Intent.ACTION_VIEW);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				it.setDataAndType(fileUri,
						"application/vnd.android.package-archive");
				context.startActivity(it);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过游戏报名卸载游戏
	 * 
	 * @Title: uninstallGame
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param: @param context
	 * @param: @param pkName
	 * @return: void
	 * @throws
	 */
	public static void uninstallGame(Context context, String pkName) {
		Uri packageURI = Uri.parse("package:" + pkName);
		Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
		context.startActivity(intent);
	}

	/**
	 * 复制文本到剪贴板
	 * 
	 * @param context
	 * @param text
	 * @author Lrchao
	 */
	@SuppressWarnings("deprecation")
	public static void copyToClipboard(final Context context, final String text) {
		ClipboardManager clipMgr = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		if (clipMgr == null)
			return;

		if (TextUtils.isEmpty((CharSequence) text)) {
			clipMgr.setText("");
		} else {
			clipMgr.setText((CharSequence) text);
		}
		Toast.makeText(context, "已复制到剪贴板！", Toast.LENGTH_LONG).show();

	}

	@SuppressWarnings("deprecation")
	public static String getClipboardContent(Context ctx) {
		ClipboardManager cbm = (ClipboardManager) ctx
				.getSystemService(Context.CLIPBOARD_SERVICE);
		if (cbm.getText() != null) {
			return cbm.getText().toString();
		} else {
			return "";
		}

	}
}
