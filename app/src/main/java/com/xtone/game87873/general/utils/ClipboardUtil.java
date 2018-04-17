package com.xtone.game87873.general.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

/**
 * 剪切板的使用
 * 
 * @author huangzx
 * 
 * */
@SuppressLint("NewApi")
public class ClipboardUtil {
	public static void setClipboardText(Context context, String text) {
		if (Build.VERSION.SDK_INT >= 11) {
			ClipboardManager clipboard = (ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("text", text);
			clipboard.setPrimaryClip(clip);
		} else {
			// 得到剪贴板管理器
			android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(text);
		}
	}

	public String getClipboardText(Context context) {
		if (Build.VERSION.SDK_INT >= 11) {
			ClipboardManager clipboard = (ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
		} else {
			// 得到剪贴板管理器
			android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return cmb.getText().toString().trim();
		}
	}
}
