package com.xtone.game87873.general.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.xtone.game87873.R;
import com.xtone.game87873.general.widget.SwipeBackLayout;

/**
 * @author ywj
 */
public class SwipeBackActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SwipeBackLayout layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
	}

}
