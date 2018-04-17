package com.xtone.game87873.general.widget.listpopupwindow;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.xtone.game87873.R;
import com.xtone.game87873.general.utils.DensityUtil;

/**
 * @author: chenyh
 * @Date: 2014-9-10上午9:14:24
 */
public class ListPopupWindow implements OnClickListener {

	private Activity mContext;
	private LayoutInflater mInflater;

//	private int[] mBackgrounds = { R.drawable.btn_more_dialog_above_selector,
//			R.drawable.btn_more_dialog_mid_selector,
//			R.drawable.btn_more_dialog_below_selector,
//			R.drawable.btn_more_dialog_around_selector };
	private int[] mBackgrounds = { R.drawable.btn_more_pop_above,
			R.drawable.btn_more_dialog_mid_selector,
			R.drawable.btn_more_pop_bottom,
			R.drawable.btn_more_pop_bg };

	private int mXOffset;
	private int mYOffset;

	private PopupWindow mPopupWindowMore;
	private View mImageButtonMore;

	ArrayList<IntEntry> values;
	private OnListPopupWindowClickListener onItemClickListener; // 给start界面使用
	private LinearLayout container;

	/**
	 * ListPopupWindow 构造方法
	 * 
	 * @param context
	 *            Context对象
	 * @param values
	 *            维护了一个SimpleEntry<Integer, Integer> 键值对的队列， <br/>
	 *            且Entry中key为对应Item要设置图标的id，value为要设置标题的id
	 * @param IBMore
	 *            Popuwindow 抛锚View组件
	 */
	public ListPopupWindow(Activity context, ArrayList<IntEntry> values,
			View IBMore) {
		mContext = context;
		mInflater = mContext.getLayoutInflater();
		mImageButtonMore = IBMore;
		this.values = values;
		initView();
	}

	private void initView() {

		mXOffset = (int) (DensityUtil.getDensity(mContext) * -45);

		mYOffset = (int) (DensityUtil.getDensity(mContext) * 2);
		container = new LinearLayout(mContext);

		container.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.RIGHT;
		container.setLayoutParams(params);
		int lenth = values.size();
		for (int i = 0; i < lenth; i++) {
			IntEntry entry = values.get(i);
			LinearLayout itemManager = (LinearLayout) mInflater.inflate(
					R.layout.more_list_item, null);
			itemManager.setId(i);

			if (lenth == 1) {
				itemManager.setBackgroundResource(mBackgrounds[3]);
			} else {
				itemManager.setBackgroundResource(i == 0 ? mBackgrounds[0]
						: (i == lenth - 1 ? mBackgrounds[2] : mBackgrounds[1]));
			}

			ImageView imageViewManager = (ImageView) itemManager.getChildAt(0);
			imageViewManager.setImageResource(entry.getKey());
			TextView textViewManager = (TextView) itemManager.getChildAt(1);
			textViewManager.setText(entry.getValue());
			// 这里的宽度一定要FillPARENT 不然会导致有长有短的结果
			android.widget.LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			params2.gravity = Gravity.RIGHT;
			params2.rightMargin = (int) (DensityUtil.getDensity(mContext) * 10);
			container.addView(itemManager, params2);
			itemManager.setOnClickListener(this);
		}
		mPopupWindowMore = new PopupWindow(container,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPopupWindowMore.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindowMore.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				mImageButtonMore.setPressed(false);
			}
		});
	}

	/**
	 * 将当前Popwindow 显示出来
	 */
	public void show() {
		mPopupWindowMore.showAsDropDown(mImageButtonMore, mXOffset, mYOffset);
		mPopupWindowMore.setFocusable(true);
		mPopupWindowMore.update();
		this.mImageButtonMore.setPressed(true);
	}

	/**
	 * dismiss掉当前Popuwindow
	 */
	public void dismiss() {
		if (mPopupWindowMore != null && mPopupWindowMore.isShowing()) {
			mPopupWindowMore.dismiss();
		}
	}

	/**
	 * 当前Popuwindow 是否正在显示
	 * 
	 * @return true为显示，否则为不显示
	 */
	public boolean isShowing() {
		return mPopupWindowMore.isShowing();
	}

	/**
	 * 设置对应Item 是否显示有提示 一般在 {@link #show()}之前调用
	 * 
	 * @param show
	 *            show or dismiss
	 * @param showIndexs
	 *            Popuwindow 内list对应的位置 可以为多个
	 */
	public void setNotice(boolean show, int... showIndexs) {
		for (int index : showIndexs) {
			if (index < 0 || index >= values.size()) {
				continue;
			}
			View view = container.getChildAt(index);
			if (view != null
					&& (view = view.findViewById(R.id.iv_settings_new_notice)) != null) {
				view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
			}

		}
	}

	/**
	 * 更新某个位置的文本信息
	 * 
	 * @param index
	 *            位置
	 * @param text
	 *            要更新成的文本信息
	 */
	public void updateText(int index, int text) {
		updateText(index, text, 0);
	}

	/**
	 * 更新某个位置的文本信息
	 * 
	 * @param index
	 *            位置
	 * @param text
	 *            要更新成的文本信息
	 */
	public void updateText(int index, int text, int drawableID) {
		if (index < 0 || index >= values.size()) {
			return;
		}
		View view = container.getChildAt(index);
		if (view != null) {
			TextView tv = (TextView) view.findViewById(R.id.tv_more_item);
			if (tv != null) {
				tv.setText(text);
			}
			ImageView iv = (ImageView) view.findViewById(R.id.iv_more_item);
			if (iv == null || drawableID == 0) {
				return;
			}
			Drawable drawable = this.mContext.getResources().getDrawable(
					drawableID);
			if (drawable != null) {
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				iv.setImageDrawable(drawable);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			onItemClickListener.onItemClick(v, v.getId());
		}
	}

	/*
	 * 点击评论按钮的接口
	 */
	public interface OnListPopupWindowClickListener {
		/**
		 * Popuwindown 上View的点击监听事件
		 * 
		 * @param view
		 *            被点击的Item
		 * @param index
		 *            在队列中的偏移量
		 */
		public void onItemClick(View view, int index);
	}

	/**
	 * 设置Popuwindown 内List 内item的点击监听
	 * 
	 * @param onItemClickListener
	 */
	public void setOnItemClickListener(
			OnListPopupWindowClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

}
