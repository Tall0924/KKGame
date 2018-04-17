package com.xtone.game87873.general.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.xtone.game87873.R;

public class ToastUtils {
    /**
     * 弹出toast
     *
     * @param context
     * @param msg     toast显示的信息
     */
    public static void toastShow(Context context, String msg) {
        // View toastRoot =
        // LayoutInflater.from(context).inflate(R.layout.my_toast, null);
        // TextView mTextView = (TextView) toastRoot.findViewById(R.id.tv_msg);
        // mTextView.setText(msg);
        // Toast mToast = new Toast(context);
        // mToast.setView(toastRoot);
        // mToast.show();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出toast
     *
     * @param context
     * @param resId   toast显示的信息字符串资源id
     */
    public static void toastShow(Context context, int resId) {
        // View toastRoot = LayoutInflater.from(context).inflate(R.layout.my_toast, null);
        // TextView mTextView = (TextView) toastRoot.findViewById(R.id.tv_msg);
        // mTextView.setText(resId);
        // Toast mToast = new Toast(context);
        // mToast.setView(toastRoot);
        // mToast.show();
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT)
                .show();
    }
}
