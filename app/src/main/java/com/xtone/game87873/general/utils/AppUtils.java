package com.xtone.game87873.general.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.xtone.game87873.general.download.DownloadInfo;

/**
 * AppUtils.java
 *
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-6-23 下午4:30:29
 */
public class AppUtils {

    /**
     * 有安装该app，且同一版本或更高
     */
    public static final int APP_INSTALLED = 1;
    /**
     * 有安装该app，但可更新
     */
    public static final int APP_UPDATE = 2;
    /**
     * 没有安装该app
     */
    public static final int APP_NO_EXIST = -1;

    /**
     * 判断该应用是否第一次启动
     *
     * @return
     */
    public static boolean isFirstStart(Context context) {
        int laseVersionCode = AppStorage.getLastVersionCode();// 上一个版本号
        int versionCode = getVersionCode(context);// 当前版本号
        if (laseVersionCode != 0) {
            if (versionCode != laseVersionCode) {
                AppStorage.saveVersionCode(versionCode);
                return true;
            }
        } else {
            AppStorage.saveVersionCode(versionCode);
            return true;
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context, String packageName) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    packageName, 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
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
     * 通过包名和版本号判断程序是否安装或可更新
     *
     * @param context
     * @param packageName
     * @param versionCode
     * @return
     */
    public static int getApkState(Context context, String packageName,
                                  int versionCode) {
        PackageInfo packageInfo;
        PackageManager pm = context.getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            // e.printStackTrace();
        }
        if (packageInfo != null) {
            if (versionCode <= packageInfo.versionCode) {
                return APP_INSTALLED;
            } else {
                return APP_UPDATE;
            }
        }
        return APP_NO_EXIST;
    }

    /**
     * 获得系统上已经安装的所有app包名及版本
     *
     * @param context
     * @return
     */
    public static List<DownloadInfo> getInstalledAppPackageList(Context context) {
        ArrayList<DownloadInfo> installedInfo = new ArrayList<DownloadInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo packageInfo : installedPackages) {
            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                DownloadInfo info = new DownloadInfo();
                info.setApkPackageName(packageInfo.packageName);
                info.setVersionCode(packageInfo.versionCode);
                info.setVersionName(packageInfo.versionName);
                installedInfo.add(info);
            }
        }
        return installedInfo;
    }

    /**
     * 获得系统上已经安装的所有app
     *
     * @param context
     * @return
     */
    public static List<DownloadInfo> getInstalledAppInfo(Context context) {
        try {
            ArrayList<DownloadInfo> installedAppInfo = new ArrayList<DownloadInfo>();
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> applicationInfos = pm
                    .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (ApplicationInfo appInfo : applicationInfos) {
                DownloadInfo info = new DownloadInfo();
                info.setAppName(appInfo.loadLabel(pm).toString());
                info.setApkPackageName(appInfo.packageName);
                try {
                    info.setVersionCode(pm.getPackageInfo(appInfo.packageName,
                            0).versionCode);
                    info.setVersionName(pm.getPackageInfo(appInfo.packageName,
                            0).versionName);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                info.setAppIcon(appInfo.loadIcon(pm));
                info.setContentLength(new File(appInfo.publicSourceDir).length());
                installedAppInfo.add(info);
            }

            return installedAppInfo;
        } catch (Exception e) {

        }
        return new ArrayList<DownloadInfo>();
    }

    /**
     * 根据包名，启动应用
     *
     * @param context
     * @param pkName
     */
    public static boolean startApp(Context context, String pkName) {
        if (pkName != null) {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(pkName);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    /**
     * 通过文件路径获取安装游戏
     *
     * @param context
     * @param file
     */
    public static boolean installApp(Context context, File file) {
        // 调用安装软件
        try {
            if (file != null && file.exists()) {
                Uri fileUri = Uri.fromFile(file);
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setDataAndType(fileUri, "application/vnd.android.package-archive");
                context.startActivity(it);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据包名卸载app
     *
     * @param context
     * @param pkName
     */
    public static void uninstallApp(Context context, String pkName) {
        Uri packageURI = Uri.parse("package:" + pkName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(intent);
    }
}
