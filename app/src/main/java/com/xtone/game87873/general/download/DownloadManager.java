package com.xtone.game87873.general.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.db.AppDownloadDao;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.DateUtil;
import com.xtone.game87873.general.utils.FileUtil;
import com.xtone.game87873.section.game.GameStatistication;

/**
 * 下载工具类
 */
public class DownloadManager {

    private int maxDownloadThread = 5;
    public static String APK_SAVE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "87873game"
            + File.separator + "apkDownload" + File.separator;
    public static String AD_SAVE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "87873game"
            + File.separator + "ad" + File.separator;

    /**
     * 系统上所有安装的app的包名列表
     */
    private List<DownloadInfo> downloadingInfoList;// 正在下载
    private List<DownloadInfo> downloadedInfoList;// 下载完成的
    private List<DownloadInfo> installedInfoList;// 已安装
    private List<DownloadInfo> updateInfoList;// 可更新
    private List<DownloadInfo> installedAppList;// 已安装
    /**
     * 所有下载列表
     */
    private List<DownloadInfo> hasDownloadList;
    // private DownloadingListAdapter mDownloadingListAdapter;
    // private DownloadedListAdapter mDownloadedListAdapter;
    // private DownloadInstallListAdapter mDownloadInstallListAdapter;

    private Context mContext;
    private HttpUtils httpUtils = new HttpUtils();

    private AppDownloadDao mAppDao;

    // private LoadingView loadingView;
    // private LoadingView loadedView;
    // private LoadingView installView;

    DownloadManager(Context appContext) {
        mAppDao = new AppDownloadDao(appContext);
        updateInfoList = new ArrayList<DownloadInfo>();
        installedAppList = new ArrayList<DownloadInfo>();
        ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class,
                new HttpHandlerStateConverter());
        mContext = appContext;

        downloadingInfoList = mAppDao.queryByWhereNoState("state",
                HttpHandler.State.SUCCESS);
        downloadedInfoList = mAppDao.queryByWhereEqState("state",
                HttpHandler.State.SUCCESS);

        hasDownloadList = mAppDao.queryAll();

        if (downloadingInfoList == null) {
            downloadingInfoList = new ArrayList<DownloadInfo>();
        }
        if (downloadedInfoList == null) {
            downloadedInfoList = new ArrayList<DownloadInfo>();
        }
        if (installedInfoList == null) {
            installedInfoList = new ArrayList<DownloadInfo>();
        }

        if (hasDownloadList == null) {
            hasDownloadList = new ArrayList<DownloadInfo>();
        }

        File file = new File(APK_SAVE_PATH);
        if (!file.exists())
            file.mkdirs();

        // appAddUpdate();

    }

    // public void appAddUpdate() {
    // List<String> mSystemInstallPackageList = AppUtil
    // .getInstalledAppPackageList(mContext);
    //
    // List<DownloadInfo> removeList = downloadedInfoList;
    //
    // for (DownloadInfo downloadInfo : removeList) {
    // if (mSystemInstallPackageList.contains(downloadInfo
    // .getApkPackageName())) {
    // installedInfoList.add(downloadInfo);
    // }
    // }
    // for (DownloadInfo downloadInfo : installedInfoList) {
    // downloadedInfoList.remove(downloadInfo);
    // }
    // }

    // public void appDeleteUpdate() {
    // downloadedInfoList = DownloadDTOController.queryByWhereEqState("state",
    // HttpHandler.State.SUCCESS);
    // installedInfoList.clear();
    // appAddUpdate();
    // }

    // public void setLoadingView(LoadingView loadingView) {
    // this.loadingView = loadingView;
    // }
    //
    // public void setLoadedView(LoadingView loadedView) {
    // this.loadedView = loadedView;
    // }
    //
    // public void setInstalllView(LoadingView installView) {
    // this.installView = installView;
    // }

    // public void refreshAdapter() {
    // if (mDownloadingListAdapter != null) {
    // mDownloadingListAdapter.notifyDataSetChanged();
    // if (loadingView != null) {
    // if (mDownloadingListAdapter.getCount() == 0) {
    // loadingView.showNoData();
    // loadingView.setVisibility(View.VISIBLE);
    // } else
    // loadingView.setVisibility(View.GONE);
    // }
    // }
    // if (mDownloadedListAdapter != null) {
    // mDownloadedListAdapter.notifyDataSetChanged();
    // if (loadedView != null) {
    // if (mDownloadedListAdapter.getCount() == 0) {
    // loadedView.showNoData();
    // loadedView.setVisibility(View.VISIBLE);
    // } else
    // loadedView.setVisibility(View.GONE);
    // }
    // }
    // if (mDownloadInstallListAdapter != null) {
    // mDownloadInstallListAdapter.notifyDataSetChanged();
    // if (installView != null) {
    // if (mDownloadInstallListAdapter.getCount() == 0) {
    // installView.showNoData();
    // installView.setVisibility(View.VISIBLE);
    // } else
    // installView.setVisibility(View.GONE);
    // }
    // }
    // }

    public void setDownloadTimeout(int timeout) {
        httpUtils.configTimeout(timeout);
    }

    public void setMaxDownloadThread(int maxDownloadThread) {
        this.maxDownloadThread = maxDownloadThread;
        httpUtils.configRequestThreadPoolSize(maxDownloadThread);
    }

    public int getMaxDownloadThread() {
        return maxDownloadThread;
    }

    public int getDownloadingListCount() {
        return downloadingInfoList.size();
    }

    public int getDownloadedListCount() {
        return downloadedInfoList.size();
    }

    public int getInstallListCount() {
        return installedInfoList.size();
    }

    public DownloadInfo getDownloadingInfo(int position) {
        return downloadingInfoList.get(position);
    }

    public DownloadInfo getDownloadedInfo(int position) {
        return downloadedInfoList.get(position);
    }

    public DownloadInfo getInstallInfo(int position) {
        return installedInfoList.get(position);
    }

    public List<DownloadInfo> getDownloadingInfoList() {
        return downloadingInfoList;
    }

    public List<DownloadInfo> getDownloadedInfoList() {
        return downloadedInfoList;
    }

    public List<DownloadInfo> getInstalledInfoList() {
        return installedInfoList;
    }

    public List<DownloadInfo> getInstalledAppList() {
        return installedAppList;
    }

    public List<DownloadInfo> getUpdateAppList() {
        return updateInfoList;
    }

    // public void setDownloadingAdapter(DownloadingListAdapter adapter) {
    // this.mDownloadingListAdapter = adapter;
    // }
    //
    // public void setDownloadedAdapter(DownloadedListAdapter adapter) {
    // this.mDownloadedListAdapter = adapter;
    // }
    //
    // // TODO
    // public void setInstalledAdapter(DownloadInstallListAdapter adapter) {
    // this.mDownloadInstallListAdapter = adapter;
    // }

    /**
     * 设置下载文件存放路径 如：/mnt/sdcard/fileSave/
     *
     * @param path
     */
    public void setDownloadFileSavePath(String path) {
        APK_SAVE_PATH = path;
    }

    /**
     * 返回已下的程序ID存放列表
     *
     * @return
     */
    public List<DownloadInfo> getHasDownList() {
        return hasDownloadList;
    }

    /**
     * 下载的回调方法类
     */
    public class ManagerCallBack extends RequestCallBack<File> {

        private DownloadInfo downloadInfo;
        private RequestCallBack<File> baseCallBack;

        public RequestCallBack<File> getBaseCallBack() {
            return baseCallBack;
        }

        public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
        }

        private ManagerCallBack(DownloadInfo downloadInfo,
                                RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
            this.downloadInfo = downloadInfo;
        }

        @Override
        public Object getUserTag() {
            if (baseCallBack == null)
                return null;
            return baseCallBack.getUserTag();
        }

        @Override
        public void setUserTag(Object userTag) {
            if (baseCallBack == null)
                return;
            baseCallBack.setUserTag(userTag);
        }

        @Override
        public void onStart() {
            HttpHandler<File> handler = downloadInfo.getDownloadHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

            // 将准备下载的数据更新数据库
            mAppDao.addOrUpdate(downloadInfo);

            if (baseCallBack != null) {
                baseCallBack.onStart();
            }
            // hasDownloadList.add(downloadInfo);
        }

        @Override
        public void onCancelled() {
            HttpHandler<File> handler = downloadInfo.getDownloadHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

            // 将下载暂停的数据更新数据库
            mAppDao.addOrUpdate(downloadInfo);

            if (baseCallBack != null) {
                baseCallBack.onCancelled();
            }
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            HttpHandler<File> handler = downloadInfo.getDownloadHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            downloadInfo.setDownloadTime(DateUtil.getNowDate());
            downloadInfo.setApkSavePath(APK_SAVE_PATH
                    + responseInfo.result.getName());

            // 从下载列表中移除，加入下载完成的列表
            downloadingInfoList.remove(downloadInfo);
            downloadedInfoList.add(downloadInfo);
            // appAddUpdate();

            // 将下载完的数据更新数据库
            mAppDao.addOrUpdate(downloadInfo);

            if (baseCallBack != null) {
                baseCallBack.onSuccess(responseInfo);
            }

            // refreshAdapter();
            GameStatistication.AddGameStatistics(mContext,
                    downloadInfo.getAppId(), GameStatistication.TYPE_DOWNLOAD);// 游戏下载统计
            Intent intent = new Intent();
            intent.setAction(UserActions.ACTION_DOWNLOAD_CHANGE);
            mContext.sendBroadcast(intent);// 发送广播通知下载列表

            // 自动开始安装
            AppUtils.installApp(mContext,
                    new File(downloadInfo.getApkSavePath()));
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            HttpHandler<File> handler = downloadInfo.getDownloadHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

            // 将下载失败的数据更新数据库
            mAppDao.addOrUpdate(downloadInfo);

            if (baseCallBack != null) {
                baseCallBack.onFailure(error, msg);
            }
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            HttpHandler<File> handler = downloadInfo.getDownloadHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            downloadInfo.setContentLength(total);
            downloadInfo.setCurrLength(current);

            // 将正在下载的数据写入数据库
            mAppDao.addOrUpdate(downloadInfo);

            if (baseCallBack != null) {
                baseCallBack.onLoading(total, current, isUploading);
            }
        }
    }

//	/**
//	 * 添加一个下载任务
//	 *
//	 * @param appName
//	 *            应用名称
//	 * @param appIconUrl
//	 *            应用图标url
//	 * @param downloadUrl
//	 *            应用下载url
//	 * @param fileSaveName
//	 *            应用文件保存文件名，如 abc.apk,下载完成后，会根据下载的apk，自动进行修改
//	 * @param apkPackageName
//	 *            应用包名,如 com.example.abc
//	 */
    // public boolean addAppTask(String id, String appName, String appIconUrl,
    // String downloadUrl, String fileSaveName, String apkPackageName,
    // RequestCallBack<File> callback) {
    // if (!NetworkUtils.isNetworkAvailable(mContext)) {
    // ToastUtils.toastShow(mContext, "网络不可用！");
    // return false;
    // }
    // hasDownloadList = DownloadDTOController.queryAll();
    // if (hasDownloadList == null)
    // hasDownloadList = new ArrayList<DownloadInfo>();
    // for (DownloadInfo info : hasDownloadList) {
    // if (info.getId().equals(id))
    // return false;
    // }
    //
    // final DownloadInfo downloadInfo = new DownloadInfo();
    //
    // downloadInfo.setId(id);
    // downloadInfo.setAppName(appName);
    // downloadInfo.setAppIconUrl(appIconUrl);
    // downloadInfo.setApkDownloadUrl(downloadUrl);
    // downloadInfo.setApkSavePath(APK_SAVE_PATH + fileSaveName);
    // downloadInfo.setApkPackageName(apkPackageName);
    //
    // HttpHandler<File> downloadHandler = httpUtils.download(downloadUrl,
    // APK_SAVE_PATH + fileSaveName, true, true, new ManagerCallBack(
    // downloadInfo, callback));
    //
    // downloadInfo.setDownloadHandler(downloadHandler);
    // downloadInfo.setState(downloadHandler.getState());
    //
    // downloadingInfoList.add(downloadInfo);
    //
    // DownloadDTOController.addOrUpdate(downloadInfo);
    //
    // return true;
    // }

    public void addAppTask(final DownloadInfo downloadInfo,
                           final RequestCallBack<File> callback) {
        // hasDownloadList = DownloadDTOController.queryAll();
        // if (hasDownloadList == null)
        // hasDownloadList = new ArrayList<DownloadInfo>();
        // for (DownloadInfo info : hasDownloadList) {
        // if (info.getId().equals(downloadInfo.getId()))
        // return false;
        // }

        toDownload(downloadInfo, callback);
    }

    private boolean toDownload(DownloadInfo downloadInfo,
                               RequestCallBack<File> callback) {
        if (downloadInfo.getCurrLength() == 0) {
            new File(downloadInfo.getApkSavePath()).delete();
        }
        HttpHandler<File> downloadHandler = httpUtils.download(downloadInfo
                        .getApkDownloadUrl(), downloadInfo.getApkSavePath(), true,
                true, new ManagerCallBack(downloadInfo, callback));

        downloadInfo.setDownloadHandler(downloadHandler);
        downloadInfo.setState(downloadHandler.getState());

        downloadingInfoList.add(downloadInfo);
        hasDownloadList.add(downloadInfo);
        // Toast.makeText(mContext, downloadHandler.getState() + "",
        // Toast.LENGTH_SHORT).show();
        mAppDao.addOrUpdate(downloadInfo);

        // 友盟计数统计
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("game_id", downloadInfo.getAppId() + "");
        map.put("game_name", downloadInfo.getAppName());
        MobclickAgent.onEvent(mContext, UmengEvent.DOWNLOAD_GAME, map);

        Intent intent = new Intent();
        intent.setAction(UserActions.ACTION_DOWNLOAD_CHANGE);
        mContext.sendBroadcast(intent);// 发送广播通知下载列表
        return true;
    }

    /**
     * 暂停某个下载任务
     *
     * @param downloadInfo
     * @throws DbException
     */
    public void stopDownload(DownloadInfo downloadInfo) {
        HttpHandler<File> handler = downloadInfo.getDownloadHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        }
        if (downloadInfo.getContentLength() == 0) {
            downloadingInfoList.remove(downloadInfo);
            hasDownloadList.remove(downloadInfo);
            mAppDao.delete(downloadInfo);
            Intent intent = new Intent();
            intent.setAction(UserActions.ACTION_DOWNLOAD_CHANGE);
            mContext.sendBroadcast(intent);//角标变化：发送广播通知下载列表
            return;
        }
        downloadInfo.setState(HttpHandler.State.CANCELLED);
        mAppDao.addOrUpdate(downloadInfo);
    }

    /**
     * 删除某个任务
     *
     * @param downloadInfo
     * @throws DbException
     */
    public void removeDownload(DownloadInfo downloadInfo) throws DbException {
        HttpHandler<File> handler = downloadInfo.getDownloadHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        }
        if (downloadingInfoList.contains(downloadInfo))
            downloadingInfoList.remove(downloadInfo);
        if (downloadedInfoList.contains(downloadInfo))
            downloadedInfoList.remove(downloadInfo);
        if (installedInfoList.contains(downloadInfo))
            installedInfoList.remove(downloadInfo);
        if (hasDownloadList.contains(downloadInfo))
            hasDownloadList.remove(downloadInfo);
        // hasDownloadList.remove(downloadInfo);

        FileUtil.deleteFile(downloadInfo.getApkSavePath());// 删除下载的apk文件

        mAppDao.delete(downloadInfo);
    }

    /**
     * 继续下载某个任务
     *
     * @param downloadInfo
     * @param callback
     * @throws DbException
     */
    public void resumeDownload(final DownloadInfo downloadInfo,
                               final RequestCallBack<File> callback) {

        toContinueDownload(downloadInfo, callback);
    }

    private void toContinueDownload(DownloadInfo downloadInfo,
                                    final RequestCallBack<File> callback) {
        HttpHandler<File> handler = httpUtils.download(downloadInfo
                        .getApkDownloadUrl(), downloadInfo.getApkSavePath(), true,
                true, new ManagerCallBack(downloadInfo, callback));
        downloadInfo.setDownloadHandler(handler);
        downloadInfo.setState(handler.getState());

        mAppDao.addOrUpdate(downloadInfo);
    }

    private class HttpHandlerStateConverter implements
            ColumnConverter<HttpHandler.State> {

        @Override
        public HttpHandler.State getFieldValue(Cursor cursor, int index) {
            return HttpHandler.State.valueOf(cursor.getInt(index));
        }

        @Override
        public HttpHandler.State getFieldValue(String fieldStringValue) {
            if (fieldStringValue == null)
                return null;
            return HttpHandler.State.valueOf(fieldStringValue);
        }

        @Override
        public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
            return fieldValue.value();
        }

        @Override
        public ColumnDbType getColumnDbType() {
            return ColumnDbType.INTEGER;
        }
    }
}
