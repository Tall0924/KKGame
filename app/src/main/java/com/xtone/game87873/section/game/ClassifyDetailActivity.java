package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.SwipeBackFragmentActivity;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.BadgeView;
import com.xtone.game87873.general.widget.SyncHorizontalScrollView;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.entity.TypeTagEntity;

/**
 * 分类列表
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-9 上午11:32:34
 */
@EActivity(R.layout.activity_classify_detail)
public class ClassifyDetailActivity extends SwipeBackFragmentActivity {
	private LayoutInflater mInflater;
	public static final String TYPE_NAME = "type_name";
	public static final String TYPE_ID = "type_id";
	public static final String TAG_ID = "tag_id";
	public static final String TAG_NAME = "tag_name";
	// public static final String PAGE_TYPE = "page_type";
	// public static final int PAGE_TYPE_CLASSIFY = 1;
	// public static final int PAGE_TYPE_TAG = 2;
	private ClassifyListFragment[] fragments = new ClassifyListFragment[2];
	public static long typeId;
	public static long tagId;
	private List<TypeTagEntity> mTags;
	private List<TextView> mTagViews;
	private List<ImageView> lines;
	// private int pageType;
	@ViewById
	RadioGroup rgNav;
	@ViewById
	RadioButton rbHot, rbNew;
	@ViewById
	TextView tvTitle, tvAll;
	@ViewById
	ViewPager vpContent;
	@ViewById
	RelativeLayout rlTags;
	@ViewById
	LinearLayout llTags, llContent;
	@ViewById
	ImageView iv_nav_right, ivDownload;
	@ViewById
	SyncHorizontalScrollView shsTags;
	@ViewById
	ImageView iv1, iv2;
	@ViewById
	GridView gvTags;

	private BadgeView badge;
	private DownloadManager downloadManager;
	private List<DownloadInfo> downloadingList;
	private BroadcastReceiver receiveBroadCast, tagReceiver;

	private BaseAdapter mAdapter;
	private boolean isTagShow;// 该页面是否要显示标签

	public boolean isTagShow() {
		return isTagShow;
	}

	// private Animation showAnim, hideAnim;

	@Click(R.id.ivReturn)
	void toReturn() {
		finish();
	}

	@Click(R.id.ivDownload)
	void toDownloadPage() {
		StartUtils.startMyGame(this);
	}

	@AfterViews
	void afterViews() {
		// 角标显示当前下载中的游戏有几个
		badge = new BadgeView(this, ivDownload);
		downloadManager = DownloadService.getDownloadManager(this);
		downloadingList = downloadManager.getDownloadingInfoList();
		if (downloadingList.size() > 0) {
			badge.setText(downloadingList.size() + "");
			badge.show();
		} else {
			badge.hide();
		}
		// 注册广播接收
		receiveBroadCast = new BadgeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UserActions.ACTION_DOWNLOAD_CHANGE); // 只有持有相同的action的接受者才能接收此广播
		registerReceiver(receiveBroadCast, filter);

		// showAnim = AnimationUtils.loadAnimation(this, R.anim.anim_head_in);
		// hideAnim = AnimationUtils.loadAnimation(this, R.anim.anim_head_out);
		// 注册广播接收器处理标签的显示和隐藏
		tagReceiver = new TagsReceiver();
		IntentFilter tagFilter = new IntentFilter();
		tagFilter.addAction(UserActions.ACTION_CLASSIFY_DETAIL_TAG_HIDE);
		tagFilter.addAction(UserActions.ACTION_CLASSIFY_DETAIL_TAG_SHOW);
		registerReceiver(tagReceiver, tagFilter);

		lines = new ArrayList<ImageView>();
		lines.add(iv1);
		lines.add(iv2);
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setVisibility(View.INVISIBLE);
		}
		lines.get(0).setVisibility(View.VISIBLE);
		// pageType = getIntent().getIntExtra(PAGE_TYPE, 0);
		// if (pageType == PAGE_TYPE_CLASSIFY) {
		// rlTags.setVisibility(View.VISIBLE);
		// } else if (pageType == PAGE_TYPE_TAG) {
		// rlTags.setVisibility(View.GONE);
		// }
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTags = new ArrayList<TypeTagEntity>();
		mTagViews = new ArrayList<TextView>();
		Intent intent = getIntent();
		typeId = intent.getLongExtra(TYPE_ID, 0);
		tagId = intent.getLongExtra(TAG_ID, 0);

		if (tagId == 0) {
			String typeName = intent.getStringExtra(TYPE_NAME);
			tvTitle.setText(typeName);
		} else {
			String tagName = intent.getStringExtra(TAG_NAME);
			tvTitle.setText(tagName);
		}

		vpContent.setAdapter(new GamePagerAdapter(getSupportFragmentManager()));
		rgNav.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.rbHot:
					vpContent.setCurrentItem(0);
					break;
				case R.id.rbNew:
					vpContent.setCurrentItem(1);
					break;

				default:
					break;
				}
			}
		});
		vpContent.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					rbHot.setChecked(true);
					break;
				case 1:
					rbNew.setChecked(true);
					break;

				default:
					break;
				}
				if (fragments[arg0] != null) {
					fragments[arg0].initView();
				}
				for (int i = 0; i < lines.size(); i++) {
					lines.get(i).setVisibility(View.INVISIBLE);
				}
				lines.get(arg0).setVisibility(View.VISIBLE);
			}
		});

		rbHot.setChecked(true);
		shsTags.setSomeParam(llTags, tvAll, iv_nav_right, this);
		if (tagId == 0) {
			getTagsFromeNet();

		} else {
			rlTags.setVisibility(View.GONE);
			gvTags.setVisibility(View.GONE);
			isTagShow = false;
		}
	}

	/**
	 * 广播接收器，改变角标的显示
	 * 
	 */
	class BadgeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (downloadingList.size() > 0) {
				badge.setText(downloadingList.size() + "");
				badge.show();
			} else {
				badge.hide();
			}
		}
	}

	/**
	 * 广播接收器，标签的显示与隐藏
	 * 
	 */
	class TagsReceiver extends BroadcastReceiver {
		private boolean isTagShow = true;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ClassifyDetailActivity.this.isTagShow) {
				String action = intent.getAction();
				if (!isTagShow
						&& UserActions.ACTION_CLASSIFY_DETAIL_TAG_SHOW
								.equals(action)) {
					android.widget.LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) llContent
							.getLayoutParams();
					param.setMargins(0, 0, 0, 0);
					llContent.setLayoutParams(param);
					Animation anim = new TranslateAnimation(0, 0,
							gvTags.getMeasuredHeight() * (-1), 0);
					anim.setDuration(200);
					llContent.startAnimation(anim);
					isTagShow = true;
				}
				if (isTagShow
						&& UserActions.ACTION_CLASSIFY_DETAIL_TAG_HIDE
								.equals(action)) {
					android.widget.LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) llContent
							.getLayoutParams();
					param.setMargins(0, gvTags.getMeasuredHeight() * (-1), 0, 0);
					llContent.setLayoutParams(param);
					Animation anim = new TranslateAnimation(0, 0,
							gvTags.getMeasuredHeight(), 0);
					anim.setDuration(200);
					llContent.startAnimation(anim);
					isTagShow = false;
				}
			}
		}
	}

	/**
	 * 根据typeId获取tag
	 */
	private void getTagsFromeNet() {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("typeid", typeId + "");
		// params.put("client", ApiUrl.SOURCE_APP+"");
		VolleyUtils.requestStringWithLoading(this, ApiUrl.TAG_LIST_BY_TYPE,
				params, this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("classify_detail_tagbytype______",
									response);
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								List<TypeTagEntity> tags = new ArrayList<TypeTagEntity>();
								JSONArray tagArray = JsonUtils.getJSONArray(
										responseJson, "data");
								if (tagArray != null && tagArray.length() > 0) {
									int len = tagArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject tagObj = tagArray
												.getJSONObject(i);
										TypeTagEntity tag = new TypeTagEntity();
										tag.setId(tagObj.getLong("id"));
										tag.setName(tagObj.getString("name"));
										tags.add(tag);
									}
								}
								mTags.clear();
								mTags.addAll(tags);
								setView();
							} else {
								ToastUtils.toastShow(
										ClassifyDetailActivity.this, msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// ToastUtils.toastShow(ClassifyDetailActivity.this,
						// error.getLocalizedMessage());
					}

				});

	}

	private void setView() {
		isTagShow = true;
		gvTags.setVisibility(View.VISIBLE);
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = mInflater.inflate(
							R.layout.item_classifydetail_tag, null);
				}
				if (position <= mTags.size()) {
					if (position > 0) {
						TypeTagEntity tag = mTags.get(position - 1);
						if (tagId == tag.getId()) {
							((TextView) convertView)
									.setBackgroundResource(R.color.bg_classifydetail_tag_p);
							((TextView) convertView)
									.setTextColor(getResources().getColor(
											R.color.text_classifydetail_tag_p));
						} else {
							((TextView) convertView)
									.setBackgroundResource(R.color.bg_classifydetail_tag_n);
							((TextView) convertView)
									.setTextColor(getResources().getColor(
											R.color.text_classifydetail_tag_n));
						}
						((TextView) convertView).setText(tag.getName());
					} else {
						((TextView) convertView).setText(R.string.all_item);
						if (tagId == 0) {
							((TextView) convertView)
									.setBackgroundResource(R.color.bg_classifydetail_tag_p);
							((TextView) convertView)
									.setTextColor(getResources().getColor(
											R.color.text_classifydetail_tag_p));
						} else {
							((TextView) convertView)
									.setBackgroundResource(R.color.bg_classifydetail_tag_n);
							((TextView) convertView)
									.setTextColor(getResources().getColor(
											R.color.text_classifydetail_tag_n));
						}
					}
				} else {
					((TextView) convertView)
							.setBackgroundResource(R.color.bg_classifydetail_tag_n);
					((TextView) convertView).setText("");
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return (mTags.size() + 1) < 8 ? 8 : (mTags.size() + 1);
			}
		};
		gvTags.setAdapter(mAdapter);
		gvTags.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position <= mTags.size()) {
					if (position == 0) {
						if (tagId == 0) {
							return;
						}
						tagId = 0;
						tvTitle.setText(getIntent().getStringExtra(TYPE_NAME));
					} else {
						TypeTagEntity tag = mTags.get(position - 1);
						if (tagId == mTags.get(position - 1).getId()) {
							return;
						}
						tagId = tag.getId();
						tvTitle.setText(tag.getName());
					}
					for (ClassifyListFragment frag : fragments) {
						frag.isRefresh = true;
					}
					fragments[vpContent.getCurrentItem()].initView();
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void setView1() {
		llTags.removeAllViews();
		mTagViews.clear();
		for (int i = 0; i < mTags.size(); i++) {
			TextView tView = (TextView) mInflater.inflate(R.layout.shs_item,
					null);
			final TypeTagEntity tag = mTags.get(i);
			tView.setText(tag.getName());
			if (tag.getId() == tagId) {
				tView.setTextColor(getResources().getColor(R.color.index_nav_p));
			} else {
				tView.setTextColor(getResources().getColor(R.color.text_index2));
			}
			mTagViews.add(tView);
			tView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tagId != tag.getId()) {
						tagId = tag.getId();
						tvAll.setTextColor(getResources().getColor(
								R.color.text_index2));
						for (TextView tv : mTagViews) {
							tv.setTextColor(getResources().getColor(
									R.color.text_index2));
						}
						((TextView) v).setTextColor(getResources().getColor(
								R.color.index_nav_p));
						for (ClassifyListFragment frag : fragments) {
							frag.isRefresh = true;
						}
						fragments[vpContent.getCurrentItem()].initView();
						tvTitle.setText(tag.getName());
					}
				}
			});
			llTags.addView(tView);
		}
		if (tagId == 0) {
			tvAll.setTextColor(getResources().getColor(R.color.index_nav_p));
		} else {
			tvAll.setTextColor(getResources().getColor(R.color.text_index2));
		}
		tvAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tagId != 0) {
					tagId = 0;
					for (TextView tv : mTagViews) {
						tv.setTextColor(getResources().getColor(
								R.color.text_index2));
					}
					((TextView) v).setTextColor(getResources().getColor(
							R.color.index_nav_p));
					for (ClassifyListFragment frag : fragments) {
						frag.isRefresh = true;
					}
					fragments[vpContent.getCurrentItem()].initView();
					tvTitle.setText(getIntent().getStringExtra(TYPE_NAME));
				}
			}
		});
		// fragments[vpContent.getCurrentItem()].initView();
	}

	private class GamePagerAdapter extends FragmentPagerAdapter {

		public GamePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				if (fragments[0] == null) {
					fragments[0] = new ClassifyListFragment_();
					fragments[0].setType(ClassifyListFragment.TYPE_HOT);
				}
				break;
			case 1:
				if (fragments[1] == null) {
					fragments[1] = new ClassifyListFragment_();
					fragments[1].setType(ClassifyListFragment.TYPE_NEW);
				}
				break;
			}
			return fragments[arg0];
		}

		@Override
		public int getCount() {
			return fragments.length;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		int item = vpContent.getCurrentItem();
		if (fragments[item] != null) {
			fragments[item].initView();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		if (tagId == 0) {
			String typeName = getIntent().getStringExtra(TYPE_NAME);
			map.put("tag_id", typeId + "");
			map.put("tag_name", typeName);
		} else {
			String tagName = getIntent().getStringExtra(TAG_NAME);
			map.put("tag_id", tagId + "");
			map.put("tag_name", tagName);
		}
		// 友盟计数统计
		MobclickAgent.onEvent(this, UmengEvent.CLICK_TAG, map);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiveBroadCast);
		unregisterReceiver(tagReceiver);
	}
}
