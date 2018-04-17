package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.utils.BitmapHelper;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.entity.SearchHotWord;
import com.xtone.game87873.section.game.adapter.GameListAdapter;

/***
 * @author huangzx 游戏搜索
 * ***/
@EActivity(R.layout.activity_search)
public class SearchActivity extends SwipeBackActivity implements IXListViewListener {

    @ViewById
    EditText edt_gameName;
    @ViewById
    Button btn_change;
    @ViewById
    XListView lv_gameList;
    @ViewById
    LinearLayout ll_keyWord, ll_searchNone;
    @ViewById
    View v_translucent;
    @ViewById
    LinearLayout ll_root, ll_contain;

    private List<SearchHotWord> wordList;
    private HashMap<String, String> params;
    private int curPage = 1, screenW;
    private List<DownloadInfo> mAppInfos;
    private GameListAdapter gameAdapter;

    @AfterViews
    void afterViews() {
        // 获取屏幕的宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW = dm.widthPixels;
        wordList = new ArrayList<SearchHotWord>();
        params = new HashMap<String, String>();
        mAppInfos = new ArrayList<DownloadInfo>();
        gameAdapter = new GameListAdapter(this, mAppInfos);
        lv_gameList.setXListViewListener(this);
        lv_gameList.setPullRefreshEnable(false);
        lv_gameList.setPullLoadEnable(false);
        lv_gameList.setAdapter(gameAdapter);
        getSearchWord();
        changeSearchKeyWord();
        lv_gameList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position > 0) {
                    Intent intent = new Intent(SearchActivity.this, AppDetailActivity_.class);
                    intent.putExtra(AppDetailActivity.GAME_NAME, mAppInfos.get(position - 1).getAppName());
                    intent.putExtra(AppDetailActivity.GAME_ID, mAppInfos.get(position - 1).getAppId());
                    startActivity(intent);
                }
            }
        });
        // 监听键盘弹出或关闭
        ll_root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = ll_root.getRootView().getHeight() - ll_root.getHeight();
                        if (heightDiff > 100) { // 键盘弹出状态
                            v_translucent.setVisibility(View.VISIBLE);
                        } else {
                            v_translucent.setVisibility(View.GONE);
                        }
                    }
                });
        edt_gameName.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {//keyCode:手机键盘的键盘码 event:键盘事件封装类的对象，包含了事件的详细信息
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    searchClick();
                }
                return false;
            }
        });
    }
    // 获取搜索热词
    private void getSearchWord() {
        VolleyUtils.requestString(this, ApiUrl.SEARCH_HOT_WORD, null, this,
                new VolleyCallback<String>() {

                    @Override
                    public void onResponse(String response) {
                        hideKeyBoard();
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            int status = responseJson.getInt("status");
                            String msg = responseJson.getString("message");
                            if (status == 200) {
                                JSONArray jsonArr = responseJson.getJSONArray("data");
                                if (jsonArr != null && jsonArr.length() > 0) {
                                    int len = jsonArr.length();
                                    for (int i = 0; i < len; i++) {
                                        JSONObject obj = jsonArr.getJSONObject(i);
                                        if (!TextUtils.isEmpty(obj.getString("name_zh"))) {
                                            wordList.add(new SearchHotWord(obj.getLong("game_id"), obj.getString("name_zh"), obj.getString("icon")));
                                        }
                                    }
                                    if (wordList.size() > 0) {
                                        showKeyWord();
                                    }
                                } else {
                                    ToastUtils.toastShow(SearchActivity.this, msg);
                                }
                            } else {
                                ToastUtils.toastShow(SearchActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // System.out
                        // .println("------------error--------------\n error=" +
                        // error.getMessage());
                    }
                });
    }

    // 搜索
    private void gameSearch() {
        VolleyUtils.requestString(this, ApiUrl.SEARCH_GAME, params, this,
                new VolleyCallback<String>() {

                    @Override
                    public void onResponse(String response) {
//						System.out.println("-------------response-------------\n response="+ response.toString());
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            int status = responseJson.getInt("status");
                            String msg = responseJson.getString("message");
                            if (status == 200) {
                                if (!responseJson.has("data")) {
                                    showNoData();
                                    return;
                                }
                                String data = responseJson.getString("data");
                                if (data == null || data.length() <= 0 || data.equals("null")) {
                                    showNoData();
                                    return;
                                }
                                List<DownloadInfo> appInfos = new ArrayList<>();
                                JSONArray bannerArray = responseJson.getJSONArray("data");
                                if (bannerArray != null && bannerArray.length() > 0) {
                                    int len = bannerArray.length();
                                    for (int i = 0; i < len; i++) {
                                        JSONObject infoObj = bannerArray.getJSONObject(i);
                                        DownloadInfo info = new DownloadInfo();
                                        info.setAppId(infoObj.getLong("id"));
                                        info.setApkPackageName(infoObj.getString("apk_package_name"));
                                        if (infoObj.has("apk_version_code") && !infoObj.getString("apk_version_code").equals("null")) {
                                            info.setVersionCode(infoObj.getInt("apk_version_code"));
                                        }
                                        info.setVersionName(infoObj.getString("apk_version_name"));
                                        info.setAppName(infoObj.getString("name_zh"));
                                        info.setApkMark(infoObj.getInt("game_rank"));
                                        info.setAppDes(infoObj.getString("synopsis"));
                                        info.setTypeName(JsonUtils.getJSONString(infoObj, "typename"));
                                        info.setAppSize(infoObj.getString("apk_size"));
                                        info.setApkDownloadUrl(infoObj.getString("apk_url"));
                                        info.setAppIconUrl(infoObj.getString("icon"));
                                        appInfos.add(info);
                                    }
                                }
                                if (curPage == 1) {
                                    mAppInfos.clear();
                                }
                                if (appInfos.size() >= ApiUrl.PAGE_SIZE) {
                                    lv_gameList.setPullLoadEnable(true);
                                } else {
                                    lv_gameList.setPullLoadEnable(false);
                                }
                                if (appInfos.size() == 0) {
                                    showNoData();
                                } else {
                                    ll_keyWord.setVisibility(View.GONE);
                                    ll_searchNone.setVisibility(View.GONE);
                                    lv_gameList.setVisibility(View.VISIBLE);
                                    mAppInfos.addAll(appInfos);
                                    gameAdapter.notifyDataSetChanged();
                                }
                            } else {
                                ToastUtils.toastShow(SearchActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // System.out.println("------------error--------------\n error="
                        // + error.getMessage());
                    }
                });
    }

    private void showNoData() {
        ll_keyWord.setVisibility(View.GONE);
        ll_searchNone.setVisibility(View.VISIBLE);
        lv_gameList.setVisibility(View.GONE);
    }

    @Click(R.id.iv_search)
    void searchClick() {
        hideKeyBoard();
        String strKeyWord = edt_gameName.getText().toString().trim();
        if (strKeyWord == null || strKeyWord.length() == 0) {
            ToastUtils.toastShow(this, "请输入游戏名！");
            return;
        }
        params.clear();
        curPage = 1;
        params.put("keyword", strKeyWord);
        params.put("page", curPage + "");
        params.put("pagesize", ApiUrl.PAGE_SIZE + "");
        gameSearch();
        edt_gameName.requestFocus();
    }

    @Click(R.id.iv_headLeft)
    void backClick() {
        finish();
    }

    @Click(R.id.btn_back)
    void backHome() {
        finish();
    }

    @Click(R.id.btn_change)
    void change() {
        btn_change.setEnabled(false);
        changeSearchKeyWord();
    }

    @Click(R.id.iv_scan)
    void scanClick() {
        StartUtils.startCaptureActivity(this);
    }

    @Click(R.id.v_translucent)
    void traslucentClick() {
        hideKeyBoard();
    }

    // 隐藏键盘
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(edt_gameName.getWindowToken(), 0);
    }
    // 换一批搜索热词
    private void changeSearchKeyWord() {
        VolleyUtils.requestString(this, ApiUrl.ROUND_HOT_WORD, null, this,
                new VolleyCallback<String>() {

                    @Override
                    public void onResponse(String response) {
                        // System.out
                        // .println("-------------response-------------\n response="
                        // + response.toString());
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            int status = responseJson.getInt("status");
                            String msg = responseJson.getString("message");
                            if (status == 200) {
                                JSONArray jsonArr = responseJson
                                        .getJSONArray("data");
                                if (jsonArr != null && jsonArr.length() > 0) {
                                    wordList.clear();
                                    int len = jsonArr.length();
                                    for (int i = 0; i < len; i++) {
                                        JSONObject obj = jsonArr
                                                .getJSONObject(i);
                                        wordList.add(new SearchHotWord(obj
                                                .getLong("game_id"), obj
                                                .getString("name_zh"), obj
                                                .getString("icon")));
                                    }
                                    if (wordList.size() > 0) {
                                        showKeyWord();
                                    }
                                } else {
                                    ToastUtils.toastShow(SearchActivity.this,
                                            msg);
                                }
                            }
                            btn_change.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // System.out.println("------------error--------------\n error="
                        // + error.getMessage());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameAdapter != null) {
            gameAdapter.notifyDataSetChanged();
        }
    }

    // 下拉刷新
    @Override
    public void onRefresh() {

    }

    // 上拉加载更多
    @Override
    public void onLoadMore() {
        curPage++;
        params.remove("page");
        params.put("page", curPage + "");
        gameSearch();
    }

    // 显示搜索热词
    private void showKeyWord() {
        LayoutInflater inflater = LayoutInflater.from(this);
        ll_contain.removeAllViews();
        // 字体颜色
        String[] colors = {"#F9C349", "#39BFA6", "#D255CE", "#5E89E8",
                "#7DAF50", "#54B9DE", "#262A7D", "#504E3E"};
        LinearLayout lLayout = null;
        Random random = new Random();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha_into);
        // 单行内容宽度，当大于屏幕宽度时要换行显示
        int contentW = 0;
        int colum = 1;
        for (int i = 0; i < wordList.size(); i++) {
            if (lLayout == null) {
                lLayout = new LinearLayout(this);
                lLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lLayoutlayoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lLayout.setLayoutParams(lLayoutlayoutParams);
            }

            View vKeyWord = inflater.inflate(R.layout.layout_key_word, null);
            TextView tv_keyword = (TextView) vKeyWord
                    .findViewById(R.id.tv_keyword);
            ImageView iv_icon = (ImageView) vKeyWord.findViewById(R.id.iv_icon);
            VolleyUtils.setURLImage(this, iv_icon, wordList.get(i).getIcon(),
                    R.drawable.icon_game_loading, R.drawable.icon_game_loading);
            tv_keyword.setText(wordList.get(i).getName_zh());
            // 随机颜色
            String ranColor = colors[random.nextInt(colors.length)];
            tv_keyword.setTextColor(Color.parseColor(ranColor));
            vKeyWord.setTag(i);
            vKeyWord.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();
                    Intent intent = new Intent(SearchActivity.this,
                            AppDetailActivity_.class);
                    intent.putExtra(AppDetailActivity.GAME_NAME,
                            wordList.get(index).getName_zh());
                    intent.putExtra(AppDetailActivity.GAME_ID,
                            wordList.get(index).getGame_id());
                    startActivity(intent);
                }
            });
            // 获取宽、高度
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            vKeyWord.measure(w, h);
            contentW = contentW + vKeyWord.getMeasuredWidth();
            colum++;
            if (contentW <= screenW - BitmapHelper.dip2px(this, 10 * colum)) {
                lLayout.addView(vKeyWord);
            } else {
                ll_contain.addView(lLayout);
                lLayout.startAnimation(anim);
                contentW = vKeyWord.getMeasuredWidth();
                colum = 1;
                lLayout = new LinearLayout(this);
                lLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lLayoutlayoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lLayout.setLayoutParams(lLayoutlayoutParams);
                lLayout.addView(vKeyWord);
            }
        }
        ll_contain.addView(lLayout);
        lLayout.startAnimation(anim);
    }

}
