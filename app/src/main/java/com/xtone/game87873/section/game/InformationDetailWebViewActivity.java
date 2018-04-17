package com.xtone.game87873.section.game;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.DensityUtil;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.listpopupwindow.IntEntry;
import com.xtone.game87873.general.widget.listpopupwindow.ListPopupWindow;
import com.xtone.game87873.general.widget.listpopupwindow.ListPopupWindow.OnListPopupWindowClickListener;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.dialog.UserProgressDialog;

/**
 * 资讯详情页(WebView)
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-14 上午11:32:34
 */
@EActivity(R.layout.activity_information_web)
public class InformationDetailWebViewActivity extends SwipeBackActivity {
	public LayoutInflater mInflater;
	private Handler handler = new Handler();
	private int width, height;
	@ViewById
	TextView tvTitle, tvGameName, tvGameDes, btnDownload;

	@ViewById
	RelativeLayout rlContent, rlGame;
	@ViewById
	LinearLayout il_loadFailure;
	@ViewById
	Button btn_refresh;
	@ViewById
	ImageView ivMsg, ivInfo, ivIcon, ivDownload;
	@ViewById
	TextView tvMsg, tv_Msg2;
	@ViewById
	WebView mWebView;
	private long infoId;
	public static final String INFO_ID = "info_id";
	public static final String INFO_TITLE = "info_title";
	private boolean isCollect, isLoad;
	private ListPopupWindow popupWindow;
	private ArrayList<IntEntry> popupList;
	private long gameId;
	private String gameUrl, gameName;
	private String shareTitle, shareContent, shareUrl, shareIcon;// 分享
	/**
	 * 要实现图片的显示需要使用Html.fromHtml的一个重构方法：public static Spanned fromHtml (String
	 * source, Html.ImageGetterimageGetter, Html.TagHandler
	 * tagHandler)其中Html.ImageGetter是一个接口，我们要实现此接口，在它的getDrawable (String
	 * source)方法中返回图片的Drawable对象才可以。
	 */
	ImageGetter imageGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			// TODO Auto-generated method stub
			URL url;
			Drawable drawable = null;
			try {
				url = new URL(source);
				drawable = Drawable.createFromStream(url.openStream(), null);
				if (drawable != null) {
					int iWidth = drawable.getIntrinsicWidth();
					int iHeight = drawable.getIntrinsicHeight();
					int width = CommonUtils
							.getScreenWidth(InformationDetailWebViewActivity.this)
							- DensityUtil.dip2px(
									InformationDetailWebViewActivity.this, 10)
							* 2;
					int height = CommonUtils.getHeightWithSize(
							InformationDetailWebViewActivity.this, iWidth,
							iHeight, width);
					drawable.setBounds(0, 0, width, height);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return drawable;
		}
	};

	// 没有网络
	private void noNetwork() {
		rlContent.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.icon_signal);
		tvMsg.setText("加载失败");
		tvMsg.setVisibility(View.VISIBLE);
		tv_Msg2.setVisibility(View.VISIBLE);
		btn_refresh.setText("重新加载");
	}

	private void reLoadData() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			return;
		}
		il_loadFailure.setVisibility(View.GONE);
		getNetData();
		String url = getString(R.string.info_url);
		mWebView.loadUrl(String.format(url, infoId));
	}

	@Click(R.id.btn_refresh)
	void refreshClick() {
		reLoadData();
	}

	@Click(R.id.ivReturn)
	void toReturn() {
		finish();
	}

	@Click(R.id.ivDownload)
	void showPopup() {
		if (!isLoad) {
			return;
		}
		initPopupWindow();
	}

	@Click(R.id.btnDownload)
	void download() {
		Intent intent = new Intent(this, AppDetailActivity_.class);
		intent.putExtra(AppDetailActivity.GAME_ID, gameId);
		intent.putExtra(AppDetailActivity.GAME_NAME, gameName);
		startActivity(intent);
	}

	private void doCollect() {

		long userId = new PreferenceManager(this).getUserId();
		if (userId == -1) {
			showLoginDialog();
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "3");// 1游戏，2专题,3资讯
		params.put("uid", userId + "");
		params.put("targetid", infoId + "");
		final String url;
		if (isCollect) {
			url = ApiUrl.DEL_COLLECTION;
		} else {
			url = ApiUrl.ADD_COLLECTION;
		}
		VolleyUtils.requestString(InformationDetailWebViewActivity.this, url,
				params, this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("jjjjj___rank___", response);
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {

								if (url == ApiUrl.DEL_COLLECTION) {
									isCollect = false;
									ToastUtils
											.toastShow(
													InformationDetailWebViewActivity.this,
													R.string.collect_cancel);
									// setResult(MyCollectionSubjectFragment.RESULT_CODE_NO_COLLECT);
								} else {
									isCollect = true;
									ToastUtils
											.toastShow(
													InformationDetailWebViewActivity.this,
													R.string.collect_success);
									// setResult(MyCollectionSubjectFragment.RESULT_CODE_COLLECT);
								}
								if (isCollect) {
									popupList.set(1, new IntEntry(
											R.drawable.icon_detail_collected,
											R.string.collected));
								} else {
									popupList.set(1, new IntEntry(
											R.drawable.icon_detail_collect,
											R.string.collect));
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {

					}

				});
	}

	@SuppressLint("NewApi")
	@AfterViews
	public void afterViews() {
		tvTitle.setText(R.string.info_detail);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rlContent.setVisibility(View.GONE);
		width = CommonUtils
				.getScreenWidth(InformationDetailWebViewActivity.this);
		height = CommonUtils.getFitHeight(
				InformationDetailWebViewActivity.this,
				R.drawable.pic_banner_loading);
		infoId = getIntent().getLongExtra(INFO_ID, 0);
		ivDownload.setImageResource(R.drawable.icon_info_title_left);
		popupList = new ArrayList<IntEntry>();
		popupList.add(new IntEntry(R.drawable.btn_head_share, R.string.share));
		popupList.add(new IntEntry(R.drawable.icon_detail_collect,
				R.string.collect));
		getNetData();

		WebSettings ws = mWebView.getSettings();
		ws.setBlockNetworkImage(false);
		ws.setJavaScriptEnabled(true); // 设置支持javascript脚本
		ws.setAllowFileAccess(true); // 允许访问文件
		// ws.setBuiltInZoomControls(true); // 设置显示缩放按钮
		ws.setSupportZoom(true); // 支持缩放

		ws.setDomStorageEnabled(true);
		ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(true);
		// ws.setUserAgentString(ws.getUserAgentString() +
		// ComplexRes.context.userAgent);

		ws.setDatabaseEnabled(true);
		ws.setGeolocationEnabled(true);
		String dir = getDir("database", Context.MODE_PRIVATE).getPath();
		ws.setDatabasePath(dir);
		ws.setGeolocationDatabasePath(dir);

		ws.setAppCacheEnabled(true);
		String cacheDir = getDir("cache", Context.MODE_PRIVATE).getPath();
		ws.setAppCachePath(cacheDir);
		ws.setCacheMode(WebSettings.LOAD_DEFAULT);
		ws.setAppCacheMaxSize(1024 * 1024 * 10);
		ws.setAllowFileAccess(true);

		ws.setRenderPriority(RenderPriority.HIGH);
		ws.setJavaScriptCanOpenWindowsAutomatically(true);

		ws.setBuiltInZoomControls(true);
		if (Build.VERSION.SDK_INT >= 11) {
			ws.setDisplayZoomControls(false);
		}

		/**
		 * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
		 * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
		 */
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ws.setDefaultTextEncodingName("utf-8"); // 设置文本编码
		mWebView.requestFocus();
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				// Intent intent = new
				// Intent(InformationDetailWebViewActivity.this,
				// WebActivity_.class);
				// intent.putExtra("url", url);
				// startActivity(intent);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
				// handler.cancel();
				// handler.handleMessage(null);
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				UserProgressDialog.getInstane().show(
						InformationDetailWebViewActivity.this);
				UserProgressDialog.getInstane().setCancelable(true);
			}

			public void onPageFinished(WebView view, String url) {
				UserProgressDialog.getInstane().dismiss();
			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				UserProgressDialog.getInstane().dismiss();
			}
		});
		mWebView.setDownloadListener(new MyWebViewDownLoadListener());
		String url = String.format(getString(R.string.info_url), infoId);
		mWebView.loadUrl(url);
//		AppLog.redLog("-------------------url", String.format(url, infoId));mWebView.loadUrl("http://wap.87873.cn/news/" + infoId+ ".html?from=app");

		CyanSdk.getInstance(this).addCommentToolbar(this,"87873game_info-" + infoId,getIntent().getStringExtra(INFO_TITLE), url);
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Log.i("tag", "url=" + url);
			Log.i("tag", "userAgent=" + userAgent);
			Log.i("tag", "contentDisposition=" + contentDisposition);
			Log.i("tag", "mimetype=" + mimetype);
			Log.i("tag", "contentLength=" + contentLength);
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	private void initPopupWindow() {

		popupWindow = new ListPopupWindow(this, popupList, this.ivDownload);
		popupWindow.setOnItemClickListener(new OnListPopupWindowClickListener() {

					@Override
					public void onItemClick(View view, int index) {
						switch (index) {
						case 0:
							share();
							break;

						case 1:
							doCollect();
							break;
						default:
							break;
						}
						popupWindow.dismiss();
					}
				});
		popupWindow.show();
	}

	// 资讯分享
	private void share() {
		Bundle bundle = new Bundle();
		bundle.putString("share_content", Html.fromHtml(shareContent)
				.toString());
		bundle.putString("share_url", shareUrl);
		bundle.putString("share_title", shareTitle);
		bundle.putString("icon", shareIcon);
		StartUtils.startCustomShareDialogActivity(
				InformationDetailWebViewActivity.this, bundle);
	}

	// 提示登录
	private void showLoginDialog() {
		CommonDialog dialog = new CommonDialog(this,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						if (arg1 == CommonDialog.CLICK_OK) {
							StartUtils
									.startLogin(InformationDetailWebViewActivity.this);
						}
						dialog.cancel();
					}
				});
		dialog.setContent(getResources().getString(R.string.login_to_collect));
		dialog.show();
	}

	private void getNetData() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			noNetwork();
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", infoId + "");
		// params.put("client", ApiUrl.SOURCE_APP + "");
		long userId = new PreferenceManager(this).getUserId();
		if (userId != -1) {
			params.put("uid", userId + "");
		}
		VolleyUtils.requestString(InformationDetailWebViewActivity.this,
				ApiUrl.INFORMATION_INFO, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("info_detail_____", response);
							isLoad = true;
							JSONObject responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {

								final JSONObject dataObj = responseJson
										.getJSONObject("data");
								rlContent.setVisibility(View.VISIBLE);
								String title = JsonUtils.getJSONString(dataObj,
										"title");
								// tvInfoTitle.setText(title);
								shareTitle = JsonUtils.getJSONString(dataObj,
										"title");
								shareUrl = JsonUtils.getJSONString(dataObj,
										"url");
								shareIcon = JsonUtils.getJSONString(dataObj,
										"thumbnail");
								shareContent = JsonUtils.getJSONString(dataObj,
										"content");
								// tvWriter.setText(JsonUtils.getJSONString(
								// dataObj, "publish_time")
								// + "   作者："
								// + JsonUtils.getJSONString(dataObj,
								// "author"));
								//
								// handler = new Handler() {
								// @Override
								// public void handleMessage(Message msg) {
								// if (msg.what == 0x101) {
								// UserProgressDialog.getInstane()
								// .dismiss();
								// tvContent
								// .setText((CharSequence) msg.obj);
								// }
								// super.handleMessage(msg);
								// }
								// };
								// UserProgressDialog.getInstane().show(
								// InformationDetailWebViewActivity.this);//
								// 显示加载框
								// 因为从网上下载图片是耗时操作 所以要开启新线程
								// Thread t = new Thread(new Runnable() {
								// Message msg = Message.obtain();
								//
								// @Override
								// public void run() {
								// CharSequence test = Html.fromHtml(
								// JsonUtils.getJSONString(
								// dataObj, "content"),
								// imageGetter, null);
								// msg.what = 0x101;
								// msg.obj = test;
								// handler.sendMessage(msg);
								// }
								// });
								// t.start();

								if (dataObj.has("collection")) {
									int col = dataObj.getInt("collection");
									if (col == 1) {
										isCollect = true;
									} else {
										isCollect = false;
									}
								}
								if (isCollect) {
									popupList.set(1, new IntEntry(
											R.drawable.icon_detail_collected,
											R.string.collected));
								} else {
									popupList.set(1, new IntEntry(
											R.drawable.icon_detail_collect,
											R.string.collect));
								}
								JSONObject gameObj = JsonUtils.getJSONObject(
										dataObj, "game");
								if (gameObj != null) {
									rlGame.setVisibility(View.VISIBLE);
									gameId = JsonUtils.getJSONLong(gameObj,
											"id");
									gameUrl = JsonUtils.getJSONString(gameObj,
											"apk_url");
									gameName = JsonUtils.getJSONString(gameObj,
											"name_zh");
									tvGameName.setText(gameName);
									String typeName = JsonUtils.getJSONString(
											gameObj, "typename");
									String size = JsonUtils.getJSONString(
											gameObj, "apk_size");
									if (TextUtils.isEmpty(typeName)) {
										tvGameDes.setText(size);
									} else {
										tvGameDes.setText(typeName + " | "
												+ size);
									}
									// VolleyUtils.setURLImage(mQueue,
									// ivIcon,
									// JsonUtils.getJSONString(
									// gameObj, "icon"),
									// R.drawable.icon_game_loading,
									// R.drawable.icon_game_loading);
									ImageLoaderUtils.loadImgWithConner(ivIcon,
											JsonUtils.getJSONString(gameObj,
													"icon"),
											R.drawable.icon_game_loading,
											R.drawable.icon_game_loading);
								} else {
									rlGame.setVisibility(View.GONE);
								}
								// 友盟计数统计
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("info_id", infoId + "");
								map.put("info_title", title);
								MobclickAgent.onEvent(
										InformationDetailWebViewActivity.this,
										UmengEvent.CLICK_INFORMATION, map);
							} else {
								ToastUtils.toastShow(
										InformationDetailWebViewActivity.this,
										msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// ToastUtils.toastShow(
						// SubjectDetailActivity.this,
						// error.getLocalizedMessage());
					}

				});

	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			mWebView.getClass().getMethod("onResume")
					.invoke(mWebView, (Object[]) null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		try {
			mWebView.getClass().getMethod("onPause")
					.invoke(mWebView, (Object[]) null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onPause();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
