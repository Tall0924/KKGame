package com.xtone.game87873.section.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.UploadUtil;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.CircleImageView;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.ChoiceImageDialogActivity;
import com.xtone.game87873.section.dialog.DateChoiceDialog;
import com.xtone.game87873.section.dialog.UserProgressDialog;
import com.xtone.game87873.section.entity.UserInfo;

@EActivity(R.layout.activity_user_info)
public class UserInfoActivity extends SwipeBackActivity {

	@ViewById
	ImageView iv_headLeft;
	@ViewById
	TextView tv_headTitle, tv_birthday, tv_modifyIcon;
	@ViewById
	CircleImageView iv_userIcon;
	@ViewById
	EditText edt_nickName;
	@ViewById
	RadioGroup rg_sex;
	@ViewById
	RadioButton rBtn_boy, rBtn_girl;
	@ViewById
	Button btn_save;

	private PreferenceManager pm;
	private UserInfo userInfo, newUser;
	private DateChoiceDialog dateChoicedialog;
	private String mCurrentPhotoPath;
	private File mTempDir;
	private String picPath;
	private Uri picUri;
	private UpLoadHandler handler;
	private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
	private static final int FLAG_CHOOSE_IMG = 1459;
	private static final int FLAG_CROP_IMG = 1460;
	private static final int FLAG_DIALOG_CHOICE = 1461;

	@AfterViews
	void afterViews() {
		tv_headTitle.setText(R.string.user_info);
		pm = new PreferenceManager(this);
		newUser = new UserInfo();
		mTempDir = new File(Environment.getExternalStorageDirectory(), "Temp");
		if (!mTempDir.exists()) {
			mTempDir.mkdirs();
		}
		String fileName = "userIcon.jpg";
		File cropFile = new File(mTempDir, fileName);
		picUri = Uri.fromFile(cropFile);
		handler = new UpLoadHandler(this);
		getBirthday();
		getSex();
		loadInfo();
	}

	private void loadInfo() {
		userInfo = new UserInfo(pm.getUserId(), pm.getUserNick(),
				pm.getUserFigureUrl());
		if (userInfo.getId() != -1) {
			edt_nickName.setText(userInfo.getNick());
			edt_nickName.setSelection(userInfo.getNick().length());
			if (userInfo.getFigureUrl() != null
					&& userInfo.getFigureUrl().length() > 0
					&& !userInfo.getFigureUrl().equals("null")) {
				VolleyUtils.setURLImage(this, iv_userIcon,
						userInfo.getFigureUrl(),
						R.drawable.icon_user_default_mine,
						R.drawable.icon_user_default_mine);
			}
		} else {
			iv_userIcon.setImageResource(R.drawable.icon_user_default_mine);
		}
	}

	// 获取用户资料
	private void getSex() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", pm.getUserId() + "");
		params.put("field", UserInfo.SEX);
		VolleyUtils.requestStringWithLoading(this, ApiUrl.USER_GET_FIELD,
				params, this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("--------------response----------------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								if (responseJson.has("data")) {
									int iSex = responseJson.getInt("data");
									if (iSex == 0 || iSex == 1) {
										rBtn_boy.setChecked(true);
									} else if (iSex == 2) {
										rBtn_girl.setChecked(true);
									}
									userInfo.setSex(iSex);
								}
							} else {
								ToastUtils
										.toastShow(UserInfoActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("--------------error----------------\n error="
						// + error.getMessage());
					}
				});
	}

	private void getBirthday() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", pm.getUserId() + "");
		params.put("field", UserInfo.BIRTH_DAY);
		VolleyUtils.requestString(this, ApiUrl.USER_GET_FIELD, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("--------------response----------------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								if (responseJson.has("data")) {
									String bithDay = responseJson.getString(
											"data").trim();
									int year = Integer.valueOf(bithDay
											.substring(0, 4));
									int month = Integer.valueOf(bithDay
											.substring(5, 7));
									int day = Integer.valueOf(bithDay
											.substring(8, 10));
									if (year != 0 && month != 0 && day != 0) {
										tv_birthday.setText(bithDay
												.subSequence(0, 10));
									} else {
										tv_birthday.setText("1970-01-01");
									}
								} else {
									tv_birthday.setText("1970-01-01");
								}
								userInfo.setBirthday(tv_birthday.getText()
										.toString().trim());
							} else {
								ToastUtils
										.toastShow(UserInfoActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("--------------error----------------\n error="
						// + error.getMessage());

					}
				});
	}

	private void getFigure() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", pm.getUserId() + "");
		params.put("field", UserInfo.FINATURE_URL);
		VolleyUtils.requestString(this, ApiUrl.USER_GET_FIELD, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						UserProgressDialog.getInstane().dismiss();
						// System.out.println("--------------response----------------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								if (responseJson.has("data")) {
									String figure = responseJson.getString(
											"data").trim();
									pm.setUserFigureUrl(figure);
									setResult(RESULT_OK);
									ToastUtils.toastShow(UserInfoActivity.this,
											"头像修改成功！");
									finish();
								}
							} else {
								ToastUtils
										.toastShow(UserInfoActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("--------------error----------------\n error="
						// + error.getMessage());
					}
				});
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	@Click(R.id.tv_birthday)
	void choiceBirdayClick() {
		String date = tv_birthday.getText().toString().trim();
		dateChoicedialog = new DateChoiceDialog(this, Integer.valueOf(date
				.substring(0, 4)), Integer.valueOf(date.substring(5, 7)),
				Integer.valueOf(date.substring(8, 10)), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.cancel();
						int[] solarDate = dateChoicedialog.getDateIntArray();
						String date = makeDateString(solarDate[2],
								solarDate[1], solarDate[0]);
						tv_birthday.setText(date);
					}
				});
		dateChoicedialog.show();
	}

	private String makeDateString(int year, int month, int day) {
		String s = "";
		s += year + "-";
		if (month < 10) {
			s += "0" + month;
		} else {
			s += month;
		}
		s += "-";
		if (day < 10) {
			s += "0" + day;
		} else {
			s += day;
		}
		return s;
	}

	@Click(R.id.iv_userIcon)
	void replaceIconClick() {
		startChoiceDialog();
	}

	@Click(R.id.tv_modifyIcon)
	void modifyIconClick() {
		startChoiceDialog();
	}

	private void startChoiceDialog() {
		StartUtils.startChoiceImageDialogActivity(this, FLAG_DIALOG_CHOICE);
	}

	@Click(R.id.btn_save)
	void saveClick() {
		if (picPath != null && picPath.length() > 0) {
			UserProgressDialog.getInstane().show(this);
			new Thread(new UploadThread()).start();
		} else if (valideUser()) {
			if (userInfo.equals(newUser)) {
				ToastUtils.toastShow(this, "您的信息未做修改！");
			} else {
				submitInfo();
			}
		}
	}

	// 上传头像
	private void uploadFigure() {
		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("sign", MD5Utils.getMd5Str("87873API"));
		params.put("timestamp", System.currentTimeMillis() / 1000L + "");
		params.put("uid", pm.getUserId() + "");
		// sign修改
		try {
			params.put("sign",
					VolleyUtils.getSignature(params, VolleyUtils.SECRET));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		HashMap<String, File> files = new HashMap<String, File>();
		File picFile = new File(picPath);
		files.put("figure", picFile);
		Message mMsg = handler.obtainMessage();
		try {
			String result = UploadUtil.postUploadFile(ApiUrl.UPLOAD_FIGURE,
					params, files);
			// System.out.println("---------------result-------------\n result="
			// + result);
			JSONObject responseJson;
			try {
				responseJson = new JSONObject(result);
				int status = responseJson.getInt("status");
				String msg = responseJson.getString("message");
				if (status == 200) {
					mMsg.what = 1;
					handler.sendMessageDelayed(mMsg, 1000);
				} else {
					mMsg.what = 0;
					mMsg.obj = msg;
					handler.sendMessageDelayed(mMsg, 1000);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				mMsg.what = -1;
				handler.sendMessageDelayed(mMsg, 1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class UploadThread implements Runnable {

		@Override
		public void run() {
			uploadFigure();
		}
	}

	static class UpLoadHandler extends Handler {
		WeakReference<UserInfoActivity> mActivity;

		public UpLoadHandler(UserInfoActivity mActivity) {
			super();
			this.mActivity = new WeakReference<UserInfoActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			UserInfoActivity activity = mActivity.get();
			switch (msg.what) {
			case -1:
				UserProgressDialog.getInstane().dismiss();
				ToastUtils.toastShow(activity, "头像修改异常，请稍候重试！");
				break;
			case 0:
				UserProgressDialog.getInstane().dismiss();
				ToastUtils.toastShow(activity, msg.obj + "");
				break;
			case 1:
				activity.picPath = null;
				activity.getFigure();
				break;
			}
		}

	}

	private boolean valideUser() {
		String nick = edt_nickName.getText().toString().trim();
		if (nick == null || nick.length() == 0) {
			ToastUtils.toastShow(this, "请输入昵称！");
			return false;
		}
		if (nick.length() < 4 || nick.length() > 20) {
			ToastUtils.toastShow(this, "请输4-20个字的昵称！");
			return false;
		}
		newUser.setNick(nick);
		int sex = 0;
		if (rBtn_boy.isChecked()) {
			sex = 1;
		}
		if (rBtn_girl.isChecked()) {
			sex = 2;
		}
		newUser.setSex(sex);
		String birthday = tv_birthday.getText().toString().trim();
		newUser.setBirthday(birthday);
		return true;
	}

	// 提交资料
	private void submitInfo() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("nick", newUser.getNick());
		params.put("birthday", newUser.getBirthday());
		params.put("uid", pm.getUserId() + "");
		params.put("sex", newUser.getSex() + "");
		VolleyUtils.requestStringWithLoading(this, ApiUrl.EDIT_USER, params,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("--------------submit----------------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								pm.setUserNick(edt_nickName.getText()
										.toString().trim());
								ToastUtils.toastShow(UserInfoActivity.this,
										"修改成功！");
								setResult(RESULT_OK);
								userInfo.setBirthday(newUser.getBirthday());
								userInfo.setNick(newUser.getNick());
								userInfo.setSex(newUser.getSex());
							} else {
								ToastUtils
										.toastShow(UserInfoActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("--------------submit----------------\n error="
						// + error.getMessage());
					}
				});
	}

	public void doGoToImg() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, FLAG_CHOOSE_IMG);
	}

	// 选择相机拍照
	public void doGoToPhone() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
		mCurrentPhotoPath = picUri.getPath();
		startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
	}

	private void beginCrop(Uri source) {
		// 调用系统截图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(source, "image/*"); // source要裁剪的图片的url
		intent.putExtra("crop", "true");// 可裁剪
		intent.putExtra("aspectX", 1); // 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300); // 输出图片大小
		intent.putExtra("outputY", 300);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // picUrl裁剪后保存图片的url
		intent.putExtra("return-data", false);// 若为false则表示不返回数据
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, FLAG_CROP_IMG);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == FLAG_CHOOSE_IMG) {
				if (data != null) {
					beginCrop(data.getData());
				}
			}
			if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
				if (mCurrentPhotoPath != null) {
					beginCrop(Uri.fromFile(new File(mCurrentPhotoPath)));
				}
			} else if (FLAG_CROP_IMG == requestCode) {
				try {
					// 拿到剪切数据
					Bitmap bitmap = BitmapFactory
							.decodeStream(UserInfoActivity.this
									.getContentResolver().openInputStream(
											picUri));
					iv_userIcon.setImageBitmap(bitmap);
					picPath = picUri.getPath();
					UserProgressDialog.getInstane().show(this);
					new Thread(new UploadThread()).start();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (FLAG_DIALOG_CHOICE == requestCode) {
				int choice = data.getIntExtra("choice", 0);
				if (choice == ChoiceImageDialogActivity.CHOICE_ABLUM) {
					doGoToImg();
				} else if (choice == ChoiceImageDialogActivity.CHOICE_CAMERA) {
					doGoToPhone();
				}
			}
		}
	}

}
