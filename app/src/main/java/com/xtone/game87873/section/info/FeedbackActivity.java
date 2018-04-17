package com.xtone.game87873.section.info;

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

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.entity.AdviceMessage;

@EActivity(R.layout.activity_feedback)
public class FeedbackActivity extends SwipeBackActivity {

	@ViewById
	ImageView iv_headLeft;
	@ViewById
	TextView tv_headTitle;
	@ViewById
	EditText edt_mobileOrEmail, edt_content;
	@ViewById
	Button btn_submit;
	@ViewById
	ListView lv_message;
	private HashMap<String, String> params;
	private List<AdviceMessage> mList;
	private AdviceMessage fistMessage;
	private MessageAdapter mAdapter;
	private PreferenceManager pm;

	@AfterViews
	void afterViews() {
		pm = new PreferenceManager(this);
		params = new HashMap<String, String>();
		tv_headTitle.setText(R.string.suggestion_feedback);
		mList = new ArrayList<AdviceMessage>();
		fistMessage = new AdviceMessage(
				"感谢您使用87手游宝，如有任何问题或建议可以再次给我们留言，我们将尽快给予您答复。",
				AdviceMessage.MESSAGE_TYPE_LEFT);
		mList.add(fistMessage);

		mAdapter = new MessageAdapter();
		lv_message.setAdapter(mAdapter);
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	@Click(R.id.btn_submit)
	void submitClick() {
		String content = edt_content.getText().toString().trim();
		String contact = edt_mobileOrEmail.getText().toString();
		params.clear();
		if (pm.getUserId() != -1) {
			params.put("user_id", pm.getUserId() + "");
		} else {
			showLoginDialog();
			return;
		}
		if (content != null && content.length() > 0) {
			params.put("content", content);
			if (contact != null && contact.length() > 0) {
				params.put("contact", contact);
			}
			submitFeedback();
		} else {
			ToastUtils.toastShow(this, "请留下您的宝贵意见！");
			return;
		}
	}

	// 提示登录
	private void showLoginDialog() {
		CommonDialog conDialog = new CommonDialog(FeedbackActivity.this,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						if (arg1 == CommonDialog.CLICK_OK) {
							StartUtils.startLogin(FeedbackActivity.this);
						}
						dialog.cancel();
					}
				});
		conDialog.setContent("您还未登录");
		conDialog.show();
	}

	private void submitFeedback() {
		VolleyUtils.requestStringWithLoading(this, ApiUrl.ADD_FEEDBACK, params,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("-----------response-----------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								edt_content.setText("");
								long uid = pm.getUserId();
								if (uid == -1) {
									ToastUtils.toastShow(FeedbackActivity.this,
											"提交成功，感谢您的反馈！");
								} else {
									getAdviceMessage();
								}
							} else {
								ToastUtils
										.toastShow(FeedbackActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("-----------error-----------\n error="
						// + error.getMessage());

					}
				});
	}

	private void getAdviceMessage() {
		params.clear();
		long uid = pm.getUserId();
		if (uid == -1) {
			return;
		}
		params.put("user_id", "" + uid);
		VolleyUtils.requestStringWithLoading(this, ApiUrl.GET_FEEDBACK, params,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("-----------getAdviceMessage-----------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONArray dataArr = responseJson
										.getJSONArray("data");
								mList.clear();
								mList.add(fistMessage);
								for (int i = 0; i < dataArr.length(); i++) {
									String content = dataArr.getJSONObject(i)
											.getString("content");
									String reply = dataArr.getJSONObject(i)
											.getString("reply");
									if (content != null && content.length() > 0
											&& !content.equals("null")) {
										mList.add(new AdviceMessage(
												content,
												AdviceMessage.MESSAGE_TYPE_RIGHT));
									}
									if (reply != null && reply.length() > 0
											&& !reply.equals("null")) {
										mList.add(new AdviceMessage(reply,
												AdviceMessage.MESSAGE_TYPE_LEFT));
									}
								}
								mAdapter.notifyDataSetChanged();
								lv_message.smoothScrollToPosition(mList.size() - 1);
							} else {
								ToastUtils
										.toastShow(FeedbackActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("-----------getAdviceMessage-----------\n error="
						// + error.getMessage());

					}
				});
	}

	private class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			ViewHolder holder;
			if (v == null) {
				v = LayoutInflater.from(FeedbackActivity.this).inflate(
						R.layout.list_item_message, null);
				holder = new ViewHolder();
				holder.tv_messageLeft = (TextView) v
						.findViewById(R.id.tv_messageLeft);
				holder.tv_messageRight = (TextView) v
						.findViewById(R.id.tv_messageRight);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			AdviceMessage am = mList.get(position);
			if (am.getType() == AdviceMessage.MESSAGE_TYPE_LEFT) {
				holder.tv_messageLeft.setVisibility(View.VISIBLE);
				holder.tv_messageLeft.setText(am.getContent());
				holder.tv_messageRight.setVisibility(View.GONE);
			} else {
				holder.tv_messageRight.setVisibility(View.VISIBLE);
				holder.tv_messageRight.setText(am.getContent());
				holder.tv_messageLeft.setVisibility(View.GONE);
			}
			return v;
		}

		class ViewHolder {
			TextView tv_messageLeft, tv_messageRight;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		String mobile = pm.getUserMobile();
		if (mobile != null) {
			edt_mobileOrEmail.setText(pm.getUserMobile());
			edt_mobileOrEmail.setSelection(pm.getUserMobile().length());
		}
		getAdviceMessage();
	}

}
