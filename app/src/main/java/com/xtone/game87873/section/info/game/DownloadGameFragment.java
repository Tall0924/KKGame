package com.xtone.game87873.section.info.game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.HomeActivity;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.game.GameFragment;
import com.xtone.game87873.section.game.adapter.GameListAdapter;

/**
 * 下载中
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-6 下午4:20:41
 */
@EFragment(R.layout.fragment_list)
public class DownloadGameFragment extends BaseFragment implements
		IXListViewListener {

	private LayoutInflater mInflater;
	private Handler mHandler;
	private GameListAdapter mAdapter;
	private List<DownloadInfo> mAppInfos;
	private DownloadManager downloadManager;
	private int mDownloadingSize;
	private List<DownloadInfo> downloadingList, downloadedList;
	@ViewById
	XListView lvContent;
	@ViewById
	View noData;
	@ViewById
	ImageView ivNodate;
	@ViewById
	Button btn_tohome;
	@ViewById
	TextView tvMsgNodata;

	@Click(R.id.btn_tohome)
	void toHome() {
		HomeActivity.mInstance.setViewShow(0);
		GameFragment.mInstance.setCurrentItem(0);
		StartUtils.startHomeActivity(getActivity());
	}

	@Override
	public void initView() {
		getData();
	}

	@AfterViews
	void afterViews() {
		mInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mAppInfos = new ArrayList<DownloadInfo>();
		mHandler = new Handler();
		mAdapter = new GameListAdapter(getActivity(), mAppInfos);
		mAdapter.setType(GameListAdapter.TYPE_DOWNLOAD);
		lvContent.setAdapter(mAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(false);
		lvContent.setPullLoadEnable(false);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				StartUtils.startAppDetail(getActivity(),
						mAppInfos.get(position - 1).getAppName(), mAppInfos
								.get(position - 1).getAppId());
			}
		});
		lvContent.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				final DownloadInfo game = mAppInfos.get(position - 1);
				if (position <= mDownloadingSize) {
					CommonDialog dialog = new CommonDialog(getActivity(),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int arg1) {
									if (arg1 == CommonDialog.CLICK_OK) {
										try {
											DownloadService.getDownloadManager(
													getActivity())
													.removeDownload(game);
											Intent intent = new Intent();
											intent.setAction(UserActions.ACTION_DOWNLOAD_CHANGE);
											getActivity().sendBroadcast(intent);// 发送广播通知下载列表

										} catch (DbException e) {
											e.printStackTrace();
										}
									}
									dialog.cancel();
								}
							});
					dialog.setContent("确定要删除该条任务？ ");
					dialog.setOkBtnText(getActivity().getResources().getString(
							R.string.confirm));
					dialog.show();
					// new HintDialog(getActivity(), "确定要删除该条任务？ ", new
					// OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface arg0, int arg1) {
					// try {
					// DownloadService.getDownloadManager(
					// getActivity()).removeDownload(game);
					// getData();
					// } catch (DbException e) {
					// e.printStackTrace();
					// }
					// }
					// }).show();
				} else {
					CommonDialog dialog = new CommonDialog(getActivity(),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int arg1) {
									if (arg1 == CommonDialog.CLICK_OK) {
										try {
											DownloadService.getDownloadManager(
													getActivity())
													.removeDownload(game);
											getData();
										} catch (DbException e) {
											e.printStackTrace();
										}
									}
									dialog.cancel();
								}
							});
					// dialog.setContent("您确定要删除 " + game.getAppName() +
					// " 的安装包吗？");
					dialog.setContent("确定要删除该条任务？ ");
					dialog.setOkBtnText(getActivity().getResources().getString(
							R.string.confirm));
					dialog.show();
					// new HintDialog(getActivity(), "您确定要删除 " +
					// game.getAppName()
					// + " 的安装包吗？", new OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface arg0, int arg1) {
					// try {
					// DownloadService.getDownloadManager(
					// getActivity()).removeDownload(game);
					// getData();
					// } catch (DbException e) {
					// e.printStackTrace();
					// }
					// }
					// }).show();
				}
				return true;
			}
		});
		btn_tohome.setText("返回首页");
		btn_tohome.setVisibility(View.VISIBLE);
		getData();
	}

	private void getData() {

		downloadManager = DownloadService.getDownloadManager(getActivity());
		downloadingList = downloadManager.getDownloadingInfoList();
		downloadedList = downloadManager.getDownloadedInfoList();
		mDownloadingSize = downloadingList.size();
		mAppInfos.clear();
		mAppInfos.addAll(downloadingList);
		mAppInfos.addAll(downloadedList);
		mAdapter.setDownloadingSize(mDownloadingSize);
		if (mAppInfos.size() == 0) {
			noData.setVisibility(View.VISIBLE);
			ivNodate.setImageResource(R.drawable.icon_download_null);
			tvMsgNodata.setText("没有下载任务~");
		} else {
			noData.setVisibility(View.GONE);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getData();
				onLoad();
			}
		}, 2000);
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 2000);
	}

	private void onLoad() {
		lvContent.stopRefresh();
		lvContent.stopLoadMore();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date refreshDate = new Date();
		lvContent.setRefreshTime(sdf.format(refreshDate));
	}
}
