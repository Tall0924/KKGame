package com.xtone.game87873.section.info;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.BadgeView;
import com.xtone.game87873.general.widget.CircleImageView;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.entity.TypeTagEntity;
import com.xtone.game87873.section.entity.UserInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

@EFragment(R.layout.fragment_info)
public class InfoFragment extends BaseFragment {

    @ViewById
    ImageView iv_download_manager;
    @ViewById
    CircleImageView iv_user_icon;
    @ViewById
    TextView tv_user_name, tv_showBadge;
    @ViewById
    LinearLayout ll_setting, ll_mineGift, ll_mineGame, ll_mineFavorites;
    private PreferenceManager pm;
    private UserInfo userInfo;
    public static int REQUEST_CODE_LOGIN = 1;
    public static int REQUEST_CODE_EDIT = 2;
    public static int REQUEST_CODE_SETTING = 3;

    private BadgeView badge;
    private DownloadManager downloadManager;
    private List<DownloadInfo> downloadingList;
    private BroadcastReceiver receiveBroadCast;

    @Click(R.id.outer)
    void clickOuter() {

    }

    @AfterViews
    void afterView() {
        pm = new PreferenceManager(getActivity());

        // 角标显示当前下载中的游戏有几个
        badge = new BadgeView(getActivity(), tv_showBadge);
        downloadManager = DownloadService.getDownloadManager(getActivity());
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
        getActivity().registerReceiver(receiveBroadCast, filter);
    }

    /**
     * 广播接收器，改变角标的显示
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

    @Override
    public void initView() {
        loadInfo();
    }

    private void loadInfo() {
        userInfo = new UserInfo(pm.getUserId(), pm.getUserNick(), pm.getUserFigureUrl());
        if (userInfo.getId() != -1) {
            getNick();
            getFigure();
        } else {
            tv_user_name.setText(getString(R.string.click_to_login));
            iv_user_icon.setImageResource(R.drawable.icon_user_default_mine);
        }
    }

    private void getFigure() {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", pm.getUserId() + "");
        params.put("field", UserInfo.FINATURE_URL);
        VolleyUtils.requestString(getActivity(), ApiUrl.USER_GET_FIELD, params, this, new VolleyCallback<String>() {

            @Override
            public void onResponse(String response) {
                // System.out.println("--------------response----------------\n response="+ response);
                JSONObject responseJson;
                try {
                    responseJson = new JSONObject(response);
                    int status = responseJson.getInt("status");
                    String msg = responseJson.getString("message");
                    if (status == 200) {
                        if (responseJson.has("data")) {
                            String figure = responseJson.getString("data").trim();
                            pm.setUserFigureUrl(figure);
                            userInfo.setFigureUrl(figure);
                            VolleyUtils.setURLImage(getActivity(),
                                    iv_user_icon,
                                    userInfo.getFigureUrl(),
                                    R.drawable.icon_user_default_mine,
                                    R.drawable.icon_user_default_mine);
                        }
                    } else {
                        ToastUtils.toastShow(getActivity(), msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // System.out.println("--------------error----------------\n error=" + error.getMessage());
            }
        });
    }

    private void getNick() {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", pm.getUserId() + "");
        params.put("field", UserInfo.NICK);
        VolleyUtils.requestString(getActivity(), ApiUrl.USER_GET_FIELD, params,
                this, new VolleyCallback<String>() {

                    @Override
                    public void onResponse(String response) {
                        // System.out.println("--------------response----------------\n response="+ response);
                        JSONObject responseJson;
                        try {
                            responseJson = new JSONObject(response);
                            int status = responseJson.getInt("status");
                            String msg = responseJson.getString("message");
                            if (status == 200) {
                                if (responseJson.has("data")) {
                                    String nick = responseJson .getString("data").trim();
                                    pm.setUserNick(nick);
                                    userInfo.setNick(nick);
                                    tv_user_name.setText(userInfo.getNick());
                                }
                            } else {
                                ToastUtils.toastShow(getActivity(), msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // System.out.println("--------------error----------------\n error="+ error.getMessage());
                    }
                });
    }

    @Click(R.id.ll_setting)
    void settingClick() {
        Intent intent = new Intent(getActivity(), SettingActivity_.class);
        startActivityForResult(intent, REQUEST_CODE_SETTING);
    }

    @Click(R.id.llLogin)
    void loginClick() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            ToastUtils.toastShow(getActivity(), "网络不可用！");
            return;
        }
        if (userInfo.getId() == -1) {
            toLogin();
        } else {
            Intent intent = new Intent(getActivity(), UserInfoActivity_.class);
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        }
    }

    // 提示登录
    private void showLoginDialog() {
        CommonDialog conDialog = new CommonDialog(getActivity(),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        if (arg1 == CommonDialog.CLICK_OK) {
                            toLogin();
                        }
                        dialog.cancel();
                    }
                });
        conDialog.setContent("您还未登录");
        conDialog.show();
    }

    private void toLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity_.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Click(R.id.ll_mineGift)
    void toMineGiftClick() {
        if (userInfo.getId() == -1) {
            showLoginDialog();
            return;
        }
        StartUtils.startMyGift(getActivity());
    }

    @Click(R.id.ll_mineGame)
    void toMineGameClick() {
        Intent intent = new Intent(getActivity(), MyGameActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.ll_mineFavorites)
    void toMineFavorites() {
        if (userInfo.getId() == -1) {
            showLoginDialog();
            return;
        }
        StartUtils.startMyCollect(getActivity());
    }

    @Click(R.id.ll_downloadManager)
    void toDownload() {
        StartUtils.startMyGame(getActivity());
    }

    @Click(R.id.ll_gameRecommend)
    void gameRecommend() {
        TypeTagEntity tag = new PreferenceManager(getActivity()).getTypeTagEntity();
        StartUtils.startClassifyDetailActivity(getActivity(), tag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOGIN) {
                loadInfo();
            } else if (requestCode == REQUEST_CODE_EDIT) {
                loadInfo();
            } else if (requestCode == REQUEST_CODE_SETTING) {
                loadInfo();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiveBroadCast);
    }
}
