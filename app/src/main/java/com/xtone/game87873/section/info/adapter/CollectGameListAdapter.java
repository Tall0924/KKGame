package com.xtone.game87873.section.info.adapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xtone.game87873.R;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.utils.AppStorage;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.NumberFormatUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.info.game.MyCollectionActivity;

/**
 * 游戏列表adapter
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-14 下午1:19:29
 */
public class CollectGameListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<DownloadInfo> mAppInfos;
	private List<Long> mChooseIds;
	private DownloadManager downloadManager;
	private List<DownloadInfo> downloadedList, downloadingList, downloadList;
	private int mType;
	private int adWidth, adHeight;
	private int ratingBarHeight;

	public CollectGameListAdapter(Context context, List<DownloadInfo> appInfos,
			List<Long> ids) {
		this.mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAppInfos = appInfos;
		mChooseIds = ids;
		adWidth = CommonUtils.getScreenWidth(context);
		adHeight = CommonUtils
				.getFitHeight(context, R.drawable.pic_index_slide);

		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inJustDecodeBounds = true;
		Bitmap ratingbar = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.ratingbar_empty);
		ratingBarHeight = ratingbar.getHeight();
	}

	public void setType(int type) {
		mType = type;
	}

	@Override
	public int getCount() {
		return mAppInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final GameItemHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_game_collect, null);
			holder = new GameItemHolder();
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
			holder.tvGameName = (TextView) convertView
					.findViewById(R.id.tvGameName);
			holder.llInfo = (LinearLayout) convertView
					.findViewById(R.id.llInfo);
			holder.tvGameDes = (TextView) convertView
					.findViewById(R.id.tvGameDes);
			holder.tvDownloadSize = (TextView) convertView
					.findViewById(R.id.tvDownloadSize);
			holder.gameRatingBar = (RatingBar) convertView
					.findViewById(R.id.rbGame);

			LayoutParams params = (LayoutParams) holder.gameRatingBar
					.getLayoutParams();
			params.height = ratingBarHeight;
			holder.gameRatingBar.setLayoutParams(params);
			holder.gameRatingBar.setMinimumHeight(ratingBarHeight);

			holder.llPb = (LinearLayout) convertView.findViewById(R.id.llPb);
			holder.pbDownload = (ProgressBar) convertView
					.findViewById(R.id.pbDownload);
			holder.tvDownloadSize = (TextView) convertView
					.findViewById(R.id.tvDownloadSize);
			holder.btnDownload = (TextView) convertView
					.findViewById(R.id.btnDownload);
			holder.tvUpdateShow = (TextView) convertView
					.findViewById(R.id.tvUpdateShow);
			holder.ivSelect = (ImageView) convertView
					.findViewById(R.id.ivSelect);
			holder.tvDownloadState = (TextView) convertView
					.findViewById(R.id.tvDownloadState);
			convertView.setTag(holder);
		} else {
			holder = (GameItemHolder) convertView.getTag();
		}

		setButton(position, holder);
		final DownloadInfo info = mAppInfos.get(position);
		holder.info = info;
		holder.tvGameName.setText(info.getAppName());
		if (TextUtils.isEmpty(info.getTypeName())) {
			holder.tvGameDes.setText(info.getAppSize());
		} else {
			holder.tvGameDes.setText(info.getTypeName() + " | "
					+ info.getAppSize());
		}
		holder.gameRatingBar.setProgress(info.getApkMark());
		// VolleyUtils.setURLImage(queue, holder.ivIcon, info.getAppIconUrl(),
		// R.drawable.icon_game_loading, R.drawable.icon_game_loading);

		ImageLoaderUtils.loadImgWithConner(holder.ivIcon, info.getAppIconUrl(),
				R.drawable.icon_game_loading, R.drawable.icon_game_loading);
		if (MyCollectionActivity.isEdit) {
			holder.ivSelect.setVisibility(View.VISIBLE);
			holder.btnDownload.setVisibility(View.INVISIBLE);
			if (mChooseIds != null && mChooseIds.contains(info.getAppId())) {
				holder.ivSelect
						.setImageResource(R.drawable.cb_my_gift_selected);
			} else {
				holder.ivSelect
						.setImageResource(R.drawable.cb_my_gift_unselected);
			}
		} else {
			holder.ivSelect.setVisibility(View.GONE);
			holder.btnDownload.setVisibility(View.VISIBLE);
		}

		holder.btnDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (info != null) {
					int state = info.getAppState();
					switch (state) {
					case DownloadInfo.STATE_HAS_INSTALLED:
						boolean b = AppUtils.startApp(mContext,
								info.getApkPackageName());
						if (!b) {
							ToastUtils.toastShow(mContext, "该游戏已卸载");
							DownloadManager downloadManager = DownloadService
									.getDownloadManager(mContext);
							List<DownloadInfo> installedList = downloadManager
									.getInstalledAppList();
							if (installedList.contains(info)) {
								installedList.remove(info);
							}
						}
						break;
					case DownloadInfo.STATE_HAS_DOWNLOADED:
						if (info != null) {
							boolean isInstall = AppUtils.installApp(mContext,
									new File(info.getApkSavePath()));
							if (!isInstall) {
								ToastUtils.toastShow(mContext, "该游戏安装包已删除");
								try {
									DownloadService
											.getDownloadManager(mContext)
											.removeDownload(info);
								} catch (DbException e) {
									e.printStackTrace();
								}
							}
						}
						break;
					case DownloadInfo.STATE_DOWNLOADING:
						if (info != null) {
							downloadManager.stopDownload(info);
						}
						break;
					case DownloadInfo.STATE_PAUSE:
						if (info != null) {
							if (!NetworkUtils.isNetworkAvailable(mContext)) {
								ToastUtils.toastShow(mContext, "网络不可用！");
								// return;
							} else {
								if (AppStorage.isDownloadOnlyWifi()) {
									if (!NetworkUtils.isWIfi(mContext)) {
										// ToastUtils.toastShow(mContext,
										// "您正在使用手机流量下载");
										CommonDialog dialog = new CommonDialog(
												mContext,
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int arg1) {
														if (arg1 == CommonDialog.CLICK_OK) {
															downloadManager
																	.resumeDownload(
																			info,
																			null);
															setButton(position,
																	holder);
														}
														dialog.dismiss();
													}
												});
										dialog.setContent(mContext
												.getResources()
												.getString(
														R.string.network_prompts));
										dialog.setOkBtnText(mContext
												.getResources()
												.getString(
														R.string.continue_download));
										dialog.show();
										return;
									}
								}
							}
							downloadManager.resumeDownload(info, null);
						}
						break;
					case DownloadInfo.STATE_NO_DOWNLOAD:
						if (!NetworkUtils.isNetworkAvailable(mContext)) {
							ToastUtils.toastShow(mContext, "网络不可用！");
							// return;
						} else {
							if (AppStorage.isDownloadOnlyWifi()) {
								if (!NetworkUtils.isWIfi(mContext)) {
									CommonDialog dialog = new CommonDialog(
											mContext,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int arg1) {
													if (arg1 == CommonDialog.CLICK_OK) {
														downloadManager
																.addAppTask(
																		info,
																		null);
														setButton(position,
																holder);
													}
													dialog.dismiss();
												}
											});
									dialog.setContent(mContext
											.getResources()
											.getString(R.string.network_prompts));
									dialog.setOkBtnText(mContext.getResources()
											.getString(
													R.string.continue_download));
									dialog.show();
									return;
									// ToastUtils.toastShow(mContext,
									// "您正在使用手机流量下载");
								}
							}
						}
						downloadManager.addAppTask(info, null);
						break;
					default:
						break;
					}
					setButton(position, holder);
				}

			}
		});

		return convertView;
	}

	private void setButton(int position, GameItemHolder itemHolder) {
		downloadManager = DownloadService.getDownloadManager(mContext);
		downloadedList = downloadManager.getDownloadedInfoList();
		downloadingList = downloadManager.getDownloadingInfoList();
		downloadList = downloadManager.getHasDownList();
		DownloadInfo downloadInfo = mAppInfos.get(position);
		// 1.2.3版本修改-改数据库表结构
		// downloadInfo.setId(downloadInfo.getAppId() + "");
		downloadInfo.setApkSavePath(DownloadManager.APK_SAVE_PATH
				+ downloadInfo.getApkDownloadUrl().substring(
						downloadInfo.getApkDownloadUrl().lastIndexOf("/") + 1));
		itemHolder.btnDownload.setText(R.string.download);
		itemHolder.llInfo.setVisibility(View.VISIBLE);
		itemHolder.llPb.setVisibility(View.GONE);
		itemHolder.tvDownloadState.setText("");
		if (downloadInfo != null) {
			int state = AppUtils.getApkState(mContext,
					downloadInfo.getApkPackageName(),
					downloadInfo.getVersionCode());
			if (state == AppUtils.APP_INSTALLED) {// 已安装
				itemHolder.btnDownload.setText(R.string.open);
				// itemHolder.pbDownload.setProgress(100);
				downloadInfo.setAppState(DownloadInfo.STATE_HAS_INSTALLED);

			} else {

				DownloadInfo infoDb = null;
				for (int i = 0; i < downloadList.size(); i++) {
					DownloadInfo info = downloadList.get(i);
					if (info.equals(downloadInfo)) {
						infoDb = info;
						mAppInfos.set(position, info);
						info.setAppState(downloadInfo.getAppState());
						info.setAppSize(downloadInfo.getAppSize());
						info.setAppDes(downloadInfo.getAppDes());
						info.setDownloadTotal(downloadInfo.getDownloadTotal());
						info.setGameIconSuperscript(downloadInfo
								.getGameIconSuperscript());
						downloadInfo = info;
						break;
					}
				}
				if (infoDb != null) {
					if (downloadedList.contains(downloadInfo)) {// 已下载
						itemHolder.btnDownload.setText(R.string.install);
						itemHolder.pbDownload.setProgress(100);
						downloadInfo
								.setAppState(DownloadInfo.STATE_HAS_DOWNLOADED);
					} else if (downloadingList.contains(downloadInfo)) {
						downloadingList.set(
								downloadingList.indexOf(downloadInfo),
								downloadInfo);
						HttpHandler<File> handler = infoDb.getDownloadHandler();

						if (handler != null) {

							RequestCallBack callBack = handler
									.getRequestCallBack();
							ButtonChangeHolder holder = new ButtonChangeHolder(
									position, downloadInfo, itemHolder);
							if (callBack instanceof DownloadManager.ManagerCallBack) {
								DownloadManager.ManagerCallBack managerCallBack = (DownloadManager.ManagerCallBack) callBack;
								managerCallBack
										.setBaseCallBack(new DownloadRequestCallBack());
							}
							callBack.setUserTag(new WeakReference<ButtonChangeHolder>(
									holder));
						}
						if (handler != null
								&& (handler.getState() != HttpHandler.State.CANCELLED && handler
										.getState() != HttpHandler.State.FAILURE)) {// 下载中
							downloadInfo
									.setAppState(DownloadInfo.STATE_DOWNLOADING);
							if (handler.getState() == HttpHandler.State.WAITING) {
								itemHolder.btnDownload.setText(mContext
										.getString(R.string.waiting_download));
							} else {
								itemHolder.btnDownload.setText(mContext
										.getString(R.string.pause));
							}
							itemHolder.llPb.setVisibility(View.VISIBLE);
							itemHolder.llInfo.setVisibility(View.GONE);
							if (infoDb.getCurrLength() > 0) {

								float curr = (float) infoDb.getCurrLength()
										/ (float) infoDb.getContentLength();
								itemHolder.pbDownload
										.setProgress((int) (curr * 100));
								itemHolder.tvDownloadSize
										.setText(NumberFormatUtils
												.getNumFormat((double) downloadInfo
														.getCurrLength() / 1024 / 1024)
												+ "M/"
												+ NumberFormatUtils
														.getNumFormat((double) downloadInfo
																.getContentLength() / 1024 / 1024)
												+ "M");
								itemHolder.tvDownloadState
										.setText(NumberFormatUtils
												.getNumFormat(curr * 100) + "%");
							} else {
								itemHolder.pbDownload.setProgress(0);
								itemHolder.tvDownloadSize.setText("0M/"
										+ downloadInfo.getAppSize());
								itemHolder.tvDownloadState.setText("0%");
							}
						} else {// 暂停
							itemHolder.llPb.setVisibility(View.VISIBLE);
							itemHolder.llInfo.setVisibility(View.GONE);
							if (infoDb.getCurrLength() > 0) {

								float curr = (float) infoDb.getCurrLength()
										/ (float) infoDb.getContentLength();

								itemHolder.pbDownload
										.setProgress((int) (curr * 100));
								itemHolder.tvDownloadSize
										.setText(NumberFormatUtils
												.getNumFormat((double) downloadInfo
														.getCurrLength() / 1024 / 1024)
												+ "M/"
												+ NumberFormatUtils
														.getNumFormat((double) downloadInfo
																.getContentLength() / 1024 / 1024)
												+ "M");
							} else {
								itemHolder.pbDownload.setProgress(0);
								itemHolder.tvDownloadSize.setText("0M/"
										+ downloadInfo.getAppSize());
							}
							if (handler != null
									&& handler.getState() == HttpHandler.State.FAILURE) {
								itemHolder.btnDownload.setText(mContext
										.getString(R.string.try_again));
								itemHolder.tvDownloadState
										.setText(R.string.wait_get);

							} else {
								itemHolder.btnDownload.setText(mContext
										.getString(R.string.go_on));
								itemHolder.tvDownloadState
										.setText(R.string.has_pause);
							}
							downloadInfo.setAppState(DownloadInfo.STATE_PAUSE);
						}
					}
				} else {// 下载
					downloadInfo.setAppState(DownloadInfo.STATE_NO_DOWNLOAD);
					itemHolder.pbDownload.setProgress(0);
					if (state == AppUtils.APP_UPDATE) {
						itemHolder.btnDownload.setText(R.string.update);
					} else if (state == AppUtils.APP_NO_EXIST) {
						itemHolder.btnDownload.setText(R.string.download);
					}

				}
			}
		}
	}

	class GameItemHolder {
		DownloadInfo info;
		ImageView ivDel;
		LinearLayout llPb;
		LinearLayout llInfo;
		ProgressBar pbDownload;
		TextView tvDownloadSize, tvDownloadState;
		ImageView ivIcon;
		TextView tvGameName;
		TextView tvGameDes;
		TextView btnDownload;
		TextView tvUpdateShow;
		TextView tvComplete, tvTag;
		RatingBar gameRatingBar;
		ImageView ivSelect;
	}

	private class ButtonChangeHolder {

		private int position;
		private GameItemHolder mHolder;
		private DownloadInfo info;
		private LinearLayout llPb;
		private TextView button;
		private ProgressBar pb;
		private TextView pbText;

		public ButtonChangeHolder(int position, DownloadInfo info,
				GameItemHolder holder) {
			this.position = position;
			this.info = info;
			mHolder = holder;
			this.llPb = holder.llPb;
			this.pb = holder.pbDownload;
			this.button = holder.btnDownload;
			this.pbText = holder.tvDownloadSize;
		}

		public void update() {
			if (info != null && info.equals(mHolder.info)
					&& info.getDownloadHandler() != null) {
				HttpHandler.State state = info.getDownloadHandler().getState();
				switch (state) {
				case LOADING:
					mHolder.llPb.setVisibility(View.VISIBLE);
					mHolder.llInfo.setVisibility(View.GONE);
					button.setText(mContext.getString(R.string.pause));
					if (info.getContentLength() > 0) {
						float curr = (float) info.getCurrLength()
								/ (float) info.getContentLength();
						pb.setProgress((int) (curr * 100));
						pbText.setText(NumberFormatUtils
								.getNumFormat((double) info.getCurrLength() / 1024 / 1024)
								+ "M/"
								+ NumberFormatUtils.getNumFormat((double) info
										.getContentLength() / 1024 / 1024)
								+ "M");
						mHolder.tvDownloadState.setText(NumberFormatUtils
								.getNumFormat(curr * 100) + "%");
					} else {
						mHolder.pbDownload.setProgress(0);
						mHolder.tvDownloadSize.setText("0M/"
								+ info.getAppSize());
						mHolder.tvDownloadState.setText("0%");
					}
					// else {
					// pb.setProgress(0);
					// }
					break;
				default:
					setButton(position, mHolder);
					break;
				}
			}
		}
	}

	private class DownloadRequestCallBack extends RequestCallBack<File> {

		@SuppressWarnings("unchecked")
		private void refreshListItem() {
			if (userTag == null)
				return;
			WeakReference<ButtonChangeHolder> tag = (WeakReference<ButtonChangeHolder>) userTag;
			ButtonChangeHolder holder = tag.get();
			if (holder != null) {
				holder.update();
			}
		}

		@Override
		public void onStart() {
			refreshListItem();
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			refreshListItem();
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			refreshListItem();
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			refreshListItem();
		}

		@Override
		public void onCancelled() {
			refreshListItem();
		}
	}

}
