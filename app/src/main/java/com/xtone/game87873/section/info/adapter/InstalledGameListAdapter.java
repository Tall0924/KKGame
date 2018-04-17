package com.xtone.game87873.section.info.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xtone.game87873.R;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.ToastUtils;

/**
 * 游戏列表adapter
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-14 下午1:19:29
 */
public class InstalledGameListAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<DownloadInfo> mAppInfos;

	public InstalledGameListAdapter(Context context, LayoutInflater inflater,
			List<DownloadInfo> appInfos) {
		mContext = context;
		mInflater = inflater;
		mAppInfos = appInfos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAppInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		GameItemHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_game_download, null);
			holder = new GameItemHolder();
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
			holder.tvGameName = (TextView) convertView
					.findViewById(R.id.tvGameName);
			holder.tvGameDes = (TextView) convertView
					.findViewById(R.id.tvGameDes);
			holder.gameRatingBar = (RatingBar) convertView
					.findViewById(R.id.rbGame);
			holder.btnDownload = (TextView) convertView
					.findViewById(R.id.btnDownload);
			convertView.setTag(holder);
		} else {
			holder = (GameItemHolder) convertView.getTag();
		}
		final DownloadInfo info = mAppInfos.get(position);
		holder.tvGameName.setText(info.getAppName());
		if (TextUtils.isEmpty(info.getAppDes())) {
			holder.tvGameDes.setText(info.getAppSize());
		} else {
			holder.tvGameDes.setText(info.getAppDes() + " | "
					+ info.getAppSize());
		}
		holder.gameRatingBar.setProgress(info.getApkMark());
		// VolleyUtils.setURLImage(queue, holder.ivIcon, info.getAppIconUrl(),
		// R.drawable.icon_game_loading, R.drawable.icon_game_loading);
		ImageLoaderUtils.loadImgWithConner(holder.ivIcon, info.getAppIconUrl(),
				R.drawable.icon_game_loading, R.drawable.icon_game_loading);
		holder.gameRatingBar.setVisibility(View.GONE);
		holder.btnDownload.setText(R.string.open);
		holder.btnDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean b = AppUtils.startApp(mContext,
						info.getApkPackageName());
				if (!b) {
					ToastUtils.toastShow(mContext, "该游戏已卸载");
					mAppInfos.remove(info);
					DownloadManager downloadManager = DownloadService
							.getDownloadManager(mContext);
					List<DownloadInfo> installedList = downloadManager
							.getInstalledAppList();
					if (installedList.contains(info)) {
						installedList.remove(info);
					}
					notifyDataSetChanged();
				}

			}
		});
		return convertView;
	}

	class GameItemHolder {
		ImageView ivDel;
		ImageView ivIcon;
		TextView tvGameName;
		TextView tvGameDes;
		RatingBar gameRatingBar;
		TextView btnDownload;
	}
}
