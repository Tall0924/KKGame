package com.xtone.game87873.section.gift.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.utils.DateUtil;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.entity.GameGiftEntity;
import com.xtone.game87873.section.gift.GiftDetailActivity_;

/**
 * GiftListAdapter.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-22 下午3:38:26
 */
public class GiftListAdapter extends BaseAdapter {

	private List<GameGiftEntity> giftList;
	private Context mContext;

	public GiftListAdapter(Context context, List<GameGiftEntity> gifts) {
		mContext = context;
		giftList = gifts;
	}

	@Override
	public int getCount() {
		return giftList.size();
	}

	@Override
	public Object getItem(int position) {
		return giftList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		LayoutInflater infalter = LayoutInflater.from(mContext);
		ViewHolder holder;
		if (view == null) {
			view = infalter.inflate(R.layout.list_item_gift_prefecture, null);
			holder = new ViewHolder();
			holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			holder.tv_giftName = (TextView) view.findViewById(R.id.tv_giftName);
			holder.tv_giftResidue = (TextView) view
					.findViewById(R.id.tv_giftResidue);
			holder.tv_giftContent = (TextView) view
					.findViewById(R.id.tv_giftContent);
			holder.pb_giftResidue = (ProgressBar) view
					.findViewById(R.id.pb_giftResidue);
			holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		GameGiftEntity gift = giftList.get(position);
		holder.tv_giftContent.setText(gift.getContent().replace("\n", " "));
		holder.tv_giftName.setText(gift.getName());
		// VolleyUtils.setURLImage(mContext, holder.iv_icon, gift.getIcon(),
		// R.drawable.icon_game_loading, R.drawable.icon_game_loading);
		ImageLoaderUtils.loadImgWithConner(holder.iv_icon, gift.getIcon(),
				R.drawable.icon_game_loading, R.drawable.icon_game_loading);
		holder.pb_giftResidue.setMax(gift.getCode_num());
		holder.pb_giftResidue.setProgress(gift.getSurplus_num());
		switch (gift.getStatus()) {
		case ApiUrl.GIFT_STATUS_TAKE_NO:
			holder.tv_status.setText(R.string.take);
			if (gift.getSurplus_num() == 0) {
				holder.tv_status.setText(R.string.take_out);
			}
			break;
		case ApiUrl.GIFT_STATUS_FOR_NO:
			holder.tv_status.setText(R.string.for_num);
			break;
		case ApiUrl.GIFT_STATUS_FINISH:
			holder.tv_status.setText(R.string.finish);
			break;
		case ApiUrl.GIFT_STATUS_ORDER:

			break;
		}
		if (gift.getIs_receive() == 1) {
			holder.tv_status.setText(R.string.already_take_2);
		}
		long residueTime = DateUtil.strToTime(gift.getEnd_time())
				- System.currentTimeMillis();
		if (residueTime <= 0) {
			holder.tv_status.setText(R.string.finish);
		}
		holder.tv_status.setTag(gift);
		holder.tv_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, GiftDetailActivity_.class);
				GameGiftEntity temp = (GameGiftEntity) v.getTag();
				intent.putExtra("giftId", temp.getId());
				mContext.startActivity(intent);
			}
		});
		return view;
	}

	class ViewHolder {
		ImageView iv_icon;
		TextView tv_giftName, tv_giftResidue, tv_giftContent, tv_status;
		ProgressBar pb_giftResidue;
	}

}
