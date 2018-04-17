package com.xtone.game87873.section;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

import com.xtone.game87873.R;
import com.xtone.game87873.general.base.BaseActivity;

public class SampleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				finish();
			}
		};
		timer.schedule(task, 1000);
	}
	
}
