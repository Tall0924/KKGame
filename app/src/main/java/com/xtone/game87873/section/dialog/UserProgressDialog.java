package com.xtone.game87873.section.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;

import com.xtone.game87873.R;

/**
 * 加载框
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-15 上午11:16:12
 */
public class UserProgressDialog {

	private static UserProgressDialog mInstance;
	private Dialog dialog;

	public static UserProgressDialog getInstane() {
		if (mInstance == null) {
			mInstance = new UserProgressDialog();
		}
		return mInstance;
	}

	private UserProgressDialog() {

	}

	public void show(Context context) {
		try {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}

			dialog = new Dialog(context, R.style.custom_dialog);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.dialog_loading_anim);// 设置布局
			dialog.show();
			((AnimationDrawable) dialog.findViewById(R.id.ivLoading).getBackground()).start();
		} catch (Exception e) {

		}
	}

	public void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			((AnimationDrawable) dialog.findViewById(R.id.ivLoading).getBackground()).stop();
			dialog.dismiss();
		}
	}

	public void setCancelable(boolean cancelable) {
		if (dialog != null) {
			dialog.setCancelable(cancelable);
		}
	}
}
