package com.xtone.game87873.general.base;

import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.general.utils.volley.VolleyUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 所有的Fragment的基类，前期先增加一个全局异常捕获，后续增加响应的接口方法来规范代码编写
 * 
 * @author ywj
 * @version v1.0
 * @copyright 2010-2015
 * @create-time 2015-1-20 下午2:55:02
 */
public abstract class BaseFragment extends Fragment {
	/**
	 * 每次viewpager切换触发
	 */
	public abstract void initView();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName()); // 友盟统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		VolleyUtils.getRequestQueue(getActivity()).cancelAll(this);
	}
}
