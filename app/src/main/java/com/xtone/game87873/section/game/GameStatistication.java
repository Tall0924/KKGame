package com.xtone.game87873.section.game;

import java.util.HashMap;

import android.content.Context;

import com.android.volley.VolleyError;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;

/**
 * 游戏单击和下载统计
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-22 下午4:28:49
 */
public class GameStatistication {

	public static final int TYPE_CLICK = 1;
	public static final int TYPE_DOWNLOAD = 2;

	public static void AddGameStatistics(final Context context,
			long gameid, int type) {
		if (context != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("gameid", gameid + "");
			params.put("client", ApiUrl.SOURCE_APP+"");// 1:pc 2:app （必填）
			if (type == TYPE_CLICK) {
				params.put("type", "1");// 1：点击 2：下载（必填）
			} else if (type == TYPE_DOWNLOAD) {
				params.put("type", "2");
			}
			VolleyUtils.requestString(context, ApiUrl.GAME_STAT, params, context,
					new VolleyCallback<String>() {
						@Override
						public void onResponse(String response) {
							// try {
							AppLog.redLog("-----gameClick___", "gameClick"
									+ response);
							// JSONObject responseJson = new JSONObject(
							// response);

							// int status = responseJson.getInt("status");
							// String msg =
							// responseJson.getString("message");
							// } catch (JSONException e) {
							// e.printStackTrace();
							// }
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							// ToastUtils.toastShow(context,
							// error.getLocalizedMessage());
						}

					});
		}
	}

}
