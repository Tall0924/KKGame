package com.xtone.game87873.section.game;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.xtone.game87873.R;
import com.xtone.game87873.general.base.BaseActivity;

@EActivity(R.layout.activity_web)
public class WebActivity extends BaseActivity {

	@ViewById
	ImageView iv_headLeft;
	@ViewById
	WebView webView;

	@AfterViews
	void afterView() {
		String strUrl = (String) getIntent().getExtras().get("url");
		//重新设置websettings  
		WebSettings s = webView.getSettings();     
		s.setBuiltInZoomControls(true);     
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);     
		s.setUseWideViewPort(true);    
		s.setLoadWithOverviewMode(true);    
//		s.setSavePassword(true);     
		s.setSaveFormData(true);    
//		s.setJavaScriptEnabled(true);    
		// enable navigator.geolocation     
		s.setGeolocationEnabled(true);     
		s.setGeolocationDatabasePath(getFilesDir().getPath()+"/data/org.itri.html5webview/databases/");     
		// enable Web Storage: localStorage, sessionStorage     
		s.setDomStorageEnabled(true);  
		webView.requestFocus();  
		webView.setScrollBarStyle(0);
		if (strUrl != null) {
			webView.loadUrl(strUrl);
			// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// TODO Auto-generated method stub
					// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
					view.loadUrl(url);
					return true;
				}
			});
		}
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}
	
	public boolean onKeyDown(int keyCoder,KeyEvent event){  
        if(webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK){  
        	webView.goBack();   //goBack()表示返回webView的上一页面  
        	return true;  
        }  
        return false;  
	} 
}
