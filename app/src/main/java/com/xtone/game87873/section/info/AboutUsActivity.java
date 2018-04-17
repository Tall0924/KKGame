package com.xtone.game87873.section.info;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtone.game87873.R;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.utils.AppUtil;

/**
 * 关于我们
 * 
 * @author huangzx
 * */

@EActivity(R.layout.activity_about_us)
public class AboutUsActivity extends SwipeBackActivity {
	
	@ViewById(R.id.tv_headTitle)
	TextView tv_headTitle;
	@ViewById(R.id.iv_headLeft)
	ImageView iv_headLeft;
	@ViewById(R.id.tv_app_version)
	TextView tv_app_version;

	@AfterViews
	public void afterViews(){
		initView();
	}
	
	private void initView(){
		tv_headTitle.setText(R.string.about_us);
		try {
			tv_app_version.setText(AppUtil.getAppVersionName(this));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Click(R.id.iv_headLeft)
	void backClick(){
		finish();
	}

}
