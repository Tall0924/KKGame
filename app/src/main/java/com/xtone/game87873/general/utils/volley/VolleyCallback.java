package com.xtone.game87873.general.utils.volley;

import com.android.volley.VolleyError;

/**
 * 自定义
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-6-10 下午4:58:27
 */
public interface VolleyCallback<T> {

	public abstract void onResponse(T response);

	public abstract void onErrorResponse(VolleyError error);
}
