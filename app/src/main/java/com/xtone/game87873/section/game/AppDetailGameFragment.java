package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.entity.Information;

/**
 * 游戏详情页-详情
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2016-1-12 上午10:44:15
 */
public class AppDetailGameFragment extends BaseFragment implements OnClickListener {

	private final int UPDATE_LOG_SHOW_LINES = 6;
	private LayoutInflater mLayoutInflater;
	private DownloadInfo downloadInfo;
	private boolean isOpen, isPromptOpen, isUpdateOpten;// 是否展开
	private List<DownloadInfo> mRecommangGames;
	private List<Information> mInformations;
	private List<ImageView> gamePics;
	private String prompt, updateLog, webUrl;

	LinearLayout llPic, llInfo, llGame, llPrompt, llUpdateLog;
	TextView tvIntro, tvIntroOpen, tvPrompt, tvPromptOpen, tvUpdateLog,tvUpdateOpen;
	ListView lvInfo;// 显示资讯
	GridView gvGame;// 显示游戏推荐
	ScrollView outter;// 外部视图

	/**
	 * 简介的展开操作
	 */
	void openIntro() {
		if (isOpen) {
			isOpen = false;
			tvIntro.setMaxLines(3);
			tvIntroOpen.setText(R.string.extend);
			Drawable right = getResources().getDrawable(R.drawable.icon_down);
			right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
			tvIntroOpen.setCompoundDrawables(null, null, right, null);
		} else {
			if (tvIntro.getLineCount() <= 3) {
				tvIntroOpen.setVisibility(View.GONE);
				return;
			}
			isOpen = true;
			tvIntro.setMaxLines(tvIntro.getLineCount());
			tvIntroOpen.setText(R.string.close);
			Drawable right = getResources().getDrawable(R.drawable.icon_up);
			right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
			tvIntroOpen.setCompoundDrawables(null, null, right, null);
		}
	}

	/**
	 * 提示的展开操作
	 */
	void openPrompt() {
		if (isPromptOpen) {
			isPromptOpen = false;
			tvPrompt.setMaxLines(3);
			tvPromptOpen.setText(R.string.extend);
			Drawable right = getResources().getDrawable(R.drawable.icon_down);
			right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
			tvPromptOpen.setCompoundDrawables(null, null, right, null);
		} else {
			if (tvPrompt.getLineCount() <= 3) {
				tvPromptOpen.setVisibility(View.GONE);
				return;
			}
			isPromptOpen = true;
			tvPrompt.setMaxLines(tvPrompt.getLineCount());
			tvPromptOpen.setText(R.string.close);
			Drawable right = getResources().getDrawable(R.drawable.icon_up);
			right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
			tvPromptOpen.setCompoundDrawables(null, null, right, null);
		}
	}

	/**
	 * 更新日志的展开操作
	 */
	void openUpdateLog() {
		if (isUpdateOpten) {
			isUpdateOpten = false;
			tvUpdateLog.setMaxLines(UPDATE_LOG_SHOW_LINES);
			tvUpdateOpen.setText(R.string.extend);
			Drawable right = getResources().getDrawable(R.drawable.icon_down);
			right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
			tvUpdateOpen.setCompoundDrawables(null, null, right, null);
		} else {
			if (tvUpdateLog.getLineCount() <= UPDATE_LOG_SHOW_LINES) {
				tvUpdateOpen.setVisibility(View.GONE);
				return;
			}
			isUpdateOpten = true;
			tvUpdateLog.setMaxLines(tvUpdateLog.getLineCount());
			tvUpdateOpen.setText(R.string.close);
			Drawable right = getResources().getDrawable(R.drawable.icon_up);
			right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
			tvUpdateOpen.setCompoundDrawables(null, null, right, null);
		}
	}

	/**
	 * 设置数据
	 */
	public void setDatas(DownloadInfo info, List<Information> informations,List<DownloadInfo> recommangGames, String prompt, String updateLog,String webUrl) {
		downloadInfo = info;
		mInformations = informations;
		mRecommangGames = recommangGames;
		this.prompt = prompt;
		this.updateLog = updateLog;
		this.webUrl = webUrl;
	}

	public void afterView() {
		mLayoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setView();

		GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) {//快速滑动的时松手回调
				try {
					float y = e2.getY() - e1.getY();
					if (y > 5) {
						getActivity().sendBroadcast(new Intent(UserActions.ACTION_APP_DETAIL_TOP_SHOW));
					} else if (y < -5) {
						getActivity().sendBroadcast(new Intent(UserActions.ACTION_APP_DETAIL_TOP_HIDE));
					}
				} catch (Exception e) {

				}
				return false;
			}
		};
		final GestureDetector gestureDetector = new GestureDetector(getActivity(), onGestureListener);
		OnTouchListener touchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		// outter.setOnTouchListener(touchListener);
		outter.setOnTouchListener(new OnTouchListener() {
			private boolean isFirst = true;
			private boolean isShow = true;
			private boolean isAnim = false;
			private float firstY;

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						if (isFirst) {
							if(outter.getScrollY() == 0){
								isAnim = true;
							}
							firstY = event.getRawY();
							isFirst = false;
							return true;
						}
						float moveY = event.getRawY();
						if (moveY - firstY > ViewConfiguration.get(getActivity()).getScaledTouchSlop() && !isShow&& isAnim) {
							getActivity().sendBroadcast(new Intent(UserActions.ACTION_APP_DETAIL_TOP_SHOW));
							isShow = true;
							isAnim = false;
							return true;
						}
						if (moveY - firstY < 0 && isShow&& isAnim) {
							getActivity().sendBroadcast(new Intent(UserActions.ACTION_APP_DETAIL_TOP_HIDE));
							isShow = false;
							isAnim = false;
							return true;
						}

						break;
					case  MotionEvent.ACTION_UP:
						isFirst = true;
						break;
				}
				return false;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_appdetail_game,container, false);
		llPic = (LinearLayout) view.findViewById(R.id.llPic);
		llInfo = (LinearLayout) view.findViewById(R.id.llInfo);
		llGame = (LinearLayout) view.findViewById(R.id.llGame);
		llPrompt = (LinearLayout) view.findViewById(R.id.llPrompt);
		llUpdateLog = (LinearLayout) view.findViewById(R.id.llUpdateLog);

		tvIntro = (TextView) view.findViewById(R.id.tvIntro);
		tvIntroOpen = (TextView) view.findViewById(R.id.tvIntroOpen);
		tvPrompt = (TextView) view.findViewById(R.id.tvPrompt);
		tvPromptOpen = (TextView) view.findViewById(R.id.tvPromptOpen);
		tvUpdateLog = (TextView) view.findViewById(R.id.tvUpdateLog);
		tvUpdateOpen = (TextView) view.findViewById(R.id.tvUpdateOpen);

		lvInfo = (ListView) view.findViewById(R.id.lvInfo);
		gvGame = (GridView) view.findViewById(R.id.gvGame);
		outter = (ScrollView) view.findViewById(R.id.outter);

		afterView();

		View llContent = view.findViewById(R.id.llContent);
		CyanSdk.getInstance(getActivity()).addCommentToolbar((ViewGroup) llContent,"87873game_game-" + downloadInfo.getAppId(),downloadInfo.getAppName(), webUrl);
		return view;
	}

	@Override
	public void initView() {
	}

	public void setView() {

		// 游戏截图
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(),R.drawable.pic_game_detail_loading, options);
		gamePics = new ArrayList<ImageView>();
		int height = (int) (1.0 / 2 * CommonUtils.getScreenHeight(getActivity()));
		for (int i = 0; i < downloadInfo.getPicUrls().size(); i++) {
			ImageView iv = (ImageView) mLayoutInflater.inflate(R.layout.iv_intro, null);
			// iv.setScaleType(ScaleType.FIT_XY);
			// iv.setLayoutParams(new LayoutParams(bitmap.getWidth(), bitmap
			// .getHeight()));
			VolleyUtils.setURLImage(getActivity(), iv, downloadInfo.getPicUrls().get(i), R.drawable.icon_game_loading,
					R.drawable.icon_game_loading, height, height);
			llPic.addView(iv);
			gamePics.add(iv);
			final int position = i;
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					PicActivity.picUrls = downloadInfo.getPicUrls();
					Intent intent = new Intent(getActivity(),PicActivity_.class);
					intent.putExtra(PicActivity.CURRENT_POSITION, position);
					startActivity(intent);
				}
			});
		}
		// 游戏详情
		View.OnTouchListener listener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView widget = (TextView) v;
				Object text = widget.getText();
				if (text instanceof Spanned) {
					Spanned buffer = (Spanned) text;

					int action = event.getAction();

					if (action == MotionEvent.ACTION_UP|| action == MotionEvent.ACTION_DOWN) {
						int x = (int) event.getX();
						int y = (int) event.getY();

						x -= widget.getTotalPaddingLeft();
						y -= widget.getTotalPaddingTop();

						x += widget.getScrollX();
						y += widget.getScrollY();

						Layout layout = widget.getLayout();
						int line = layout.getLineForVertical(y);
						int off = layout.getOffsetForHorizontal(line, x);

						ClickableSpan[] link = buffer.getSpans(off, off,ClickableSpan.class);

						if (link.length != 0) {
							if (action == MotionEvent.ACTION_UP) {
								link[0].onClick(widget);
							} else if (action == MotionEvent.ACTION_DOWN) {
								// Selection only works on Spannable text. In
								// our case setSelection doesn't work on spanned
								// text
								// Selection.setSelection(buffer,
								// buffer.getSpanStart(link[0]),
								// buffer.getSpanEnd(link[0]));
							}
							return true;
						}
					}

				}

				return false;
			}
		};

		tvIntro.setText(Html.fromHtml(downloadInfo.getGameContent()));
		// tvIntro.setMovementMethod(LinkMovementMethod.getInstance());
		tvIntro.setOnTouchListener(listener);
		tvIntro.post(new Runnable() {
			@Override
			public void run() {
				if (tvIntro.getLineCount() <= 3) {
					tvIntroOpen.setVisibility(View.GONE);
					tvIntro.setMaxLines(tvIntro.getLineCount());
				} else {
					tvIntroOpen.setVisibility(View.VISIBLE);
					tvIntro.setMaxLines(3);
					tvIntroOpen.setText(R.string.extend);
					Drawable right = getResources().getDrawable(R.drawable.icon_down);
					right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
					tvIntroOpen.setCompoundDrawables(null, null, right, null);
				}
			}
		});
		// 提示
		if (!TextUtils.isEmpty(prompt)) {
			llPrompt.setVisibility(View.VISIBLE);
			tvPrompt.setText(Html.fromHtml(prompt));
			// tvPrompt.setMovementMethod(LinkMovementMethod.getInstance());//
			// 此行必须，否则超链接无法点击,ScrollingMovementMethod实现滚动条
			tvPrompt.setOnTouchListener(listener);
			tvPrompt.post(new Runnable() {
				@Override
				public void run() {
					if (tvPrompt.getLineCount() <= 3) {
						tvPromptOpen.setVisibility(View.GONE);
						tvPrompt.setMaxLines(tvPrompt.getLineCount());
					} else {
						tvPromptOpen.setVisibility(View.VISIBLE);
						tvPrompt.setMaxLines(3);
						tvPromptOpen.setText(R.string.extend);
						Drawable right = getResources().getDrawable(R.drawable.icon_down);
						right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
						tvPromptOpen.setCompoundDrawables(null, null, right,null);
					}
				}
			});

		}
		// 更新内容
		if (!TextUtils.isEmpty(updateLog)) {
			llUpdateLog.setVisibility(View.VISIBLE);
			tvUpdateLog.setText(Html.fromHtml(updateLog));
			// tvUpdateLog.setMovementMethod(LinkMovementMethod.getInstance());
			tvUpdateLog.setOnTouchListener(listener);
			tvUpdateLog.post(new Runnable() {
				@Override
				public void run() {
					if (tvUpdateLog.getLineCount() <= UPDATE_LOG_SHOW_LINES) {
						tvUpdateOpen.setVisibility(View.GONE);
						tvUpdateLog.setMaxLines(tvUpdateLog.getLineCount());
					} else {
						tvUpdateOpen.setVisibility(View.VISIBLE);
						tvUpdateLog.setMaxLines(UPDATE_LOG_SHOW_LINES);
						tvUpdateOpen.setText(R.string.extend);
						Drawable right = getResources().getDrawable(R.drawable.icon_down);
						right.setBounds(0, 0, right.getMinimumWidth(),right.getMinimumHeight()); // 设置边界
						tvUpdateOpen.setCompoundDrawables(null, null, right,null);
					}
				}
			});

		}
		// 游戏推荐
		if (mRecommangGames == null || mRecommangGames.size() == 0) {
			llGame.setVisibility(View.GONE);
		} else {
			llGame.setVisibility(View.VISIBLE);
			gvGame.setAdapter(new GameAdapter());
			gvGame.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					StartUtils.startAppDetail(getActivity(), mRecommangGames.get(position).getAppName(),mRecommangGames.get(position).getAppId());
				}
			});
		}
		// 资讯
		if (mInformations == null || mInformations.size() == 0) {
			llInfo.setVisibility(View.GONE);
		} else {
			llInfo.setVisibility(View.VISIBLE);
			lvInfo.setAdapter(new GameInformationAdapter());
			lvInfo.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					Intent intent = new Intent(getActivity(),InformationDetailWebViewActivity_.class);
					intent.putExtra(InformationDetailWebViewActivity.INFO_ID,mInformations.get(position).getId());
					intent.putExtra(InformationDetailWebViewActivity.INFO_TITLE,mInformations.get(position).getTitle());
					AppLog.redLog("---------------------title",mInformations.get(position).getTitle());
					startActivity(intent);
				}
			});
		}

		tvIntroOpen.setOnClickListener(this);
		tvPromptOpen.setOnClickListener(this);
		tvUpdateOpen.setOnClickListener(this);
		tvIntro.setOnClickListener(this);
		tvPrompt.setOnClickListener(this);
		tvUpdateLog.setOnClickListener(this);
	}

	private class GameAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mRecommangGames.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GameViewHolder holder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_game_icon,null);
				holder = new GameViewHolder();
				holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
				holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
				convertView.setTag(holder);
			} else {
				holder = (GameViewHolder) convertView.getTag();
			}
			DownloadInfo info = mRecommangGames.get(position);
			// VolleyUtils.setURLImage(mQueue, holder.ivIcon,
			// info.getAppIconUrl(), R.drawable.icon_game_loading,
			// R.drawable.icon_game_loading);
			ImageLoaderUtils.loadImgWithConner(holder.ivIcon,info.getAppIconUrl(), R.drawable.icon_game_loading,R.drawable.icon_game_loading);
			holder.tvName.setText(info.getAppName());
			return convertView;
		}
	}

	class GameViewHolder {
		TextView tvName;
		ImageView ivIcon;
	}

	private class GameInformationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mInformations.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GameInfoViewHolder holder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_game_detail_information, null);
				holder = new GameInfoViewHolder();
				holder.tvIcon = (TextView) convertView.findViewById(R.id.tvIcon);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
				convertView.setTag(holder);
			} else {
				holder = (GameInfoViewHolder) convertView.getTag();
			}
			Information info = mInformations.get(position);
			if (info.getType() == Information.TYPE_NEWS) {
				holder.tvIcon.setBackgroundResource(R.drawable.bg_green);
				holder.tvIcon.setText(R.string.type_news);
			} else if (info.getType() == Information.TYPE_ACTIVITY) {
				holder.tvIcon.setBackgroundResource(R.drawable.bg_red);
				holder.tvIcon.setText(R.string.type_activity);
			} else if (info.getType() == Information.TYPE_EVALUATING) {
				holder.tvIcon.setText(R.string.type_evaluating);
				holder.tvIcon.setBackgroundResource(R.drawable.bg_orange);
			} else if (info.getType() == Information.TYPE_INDUSTRY) {
				holder.tvIcon.setText(R.string.type_industry);
				holder.tvIcon.setBackgroundResource(R.drawable.bg_blue);
			} else if (info.getType() == Information.TYPE_STRATEGY) {
				holder.tvIcon.setText(R.string.type_strategy);
				holder.tvIcon.setBackgroundResource(R.drawable.bg_purple);
			}
			holder.tvTitle.setText(info.getTitle());
			return convertView;
		}
	}

	class GameInfoViewHolder {
		TextView tvTitle;
		TextView tvIcon;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvIntro:
			openIntro();
			break;
		case R.id.tvIntroOpen:
			openIntro();
			break;
		case R.id.tvPrompt:
			openPrompt();
			break;
		case R.id.tvPromptOpen:
			openPrompt();
			break;
		case R.id.tvUpdateLog:
			openUpdateLog();
			break;
		case R.id.tvUpdateOpen:
			openUpdateLog();
			break;

		default:
			break;
		}
	}
}
