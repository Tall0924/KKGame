package com.xtone.game87873.section.dialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xtone.game87873.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@EActivity(R.layout.activity_shareboard_dialog)
public class CustomShareDialogActivity extends Activity {
	private final UMSocialService mController = UMServiceFactory.getUMSocialService("http://app.87873.cn");
	private String share_content = "精选单机游戏，精品手机网游，精选手游礼包 ，玩手游就用87手游宝app.87873.cn";
	private String share_url = "http://app.87873.cn";
	private String share_title = "87手游宝";
	private int iconDrawableId = R.drawable.ic_launcher;
	private String iconUrl;
	@ViewById
	RelativeLayout exit_layout;
	@ViewById
	LinearLayout ll_wx,ll_wxpyq,ll_qq,ll_qqZone;

	@AfterViews
	void afterView(){
		Window win = getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			share_content = bundle.getString("share_content");
			share_url = bundle.getString("share_url");
			share_title = bundle.getString("share_title");
			iconUrl = bundle.getString("icon");
		}
		setUMengShare();
	}
	
	private void setUMengShare() {
		// 设置分享内容
		mController.setShareContent(share_content);
		// 设置分享图片，参数2为本地图片的资源引用
		UMImage image;
		if (iconUrl != null) {
			image = new UMImage(this, iconUrl);
		}else{
			image = new UMImage(this, iconDrawableId);
		}
		mController.setShareMedia(image);
		// 添加为微信和朋友圈分享
		 String appID = "wxdd6ef67a461fc996";
		 String appSecret = "e733c692de574919e56b15a56b28726e";
		// // 添加微信平台
		 UMWXHandler wxHandler = new UMWXHandler(this,appID,appSecret);
		 wxHandler.addToSocialSDK();
		// 设置微信好友分享内容
		 WeiXinShareContent weixinContent = new WeiXinShareContent();
		// //设置分享文字
		 weixinContent.setShareContent(share_content);
		// //设置title
		 weixinContent.setTitle(share_title);
		// //设置分享内容跳转URL
		 weixinContent.setTargetUrl(share_url);
		// //设置分享图片
		 weixinContent.setShareImage(image);
		 mController.setShareMedia(weixinContent);
		// // 添加微信朋友圈
		 UMWXHandler wxCircleHandler = new UMWXHandler(this,appID,appSecret);
		 wxCircleHandler.setToCircle(true);
		 wxCircleHandler.addToSocialSDK();
		// 设置微信朋友圈分享内容
		 CircleShareContent circleMedia = new CircleShareContent();
		 circleMedia.setShareContent(share_content);
		// //设置朋友圈title
		 circleMedia.setTitle(share_title);
		 circleMedia.setShareImage(image);
		 circleMedia.setTargetUrl(share_url);
		 mController.setShareMedia(circleMedia);
		// 添加QQ分享
		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104725551", "uOKig9Cjqs7kxyG1");
		qqSsoHandler.addToSocialSDK();
		QQShareContent qqShareContent = new QQShareContent();
		// 设置分享文字
		qqShareContent.setShareContent(share_content);
		// 设置分享title
		qqShareContent.setTitle(share_title);
		// 设置分享图片
		qqShareContent.setShareImage(image);
		// 设置点击分享内容的跳转链接
		qqShareContent.setTargetUrl(share_url);
		mController.setShareMedia(qqShareContent);
		// 添加QQ空间分享
		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, "1104725551", "uOKig9Cjqs7kxyG1");
		qZoneSsoHandler.addToSocialSDK();
		QZoneShareContent qzone = new QZoneShareContent();
		// 设置分享文字
		qzone.setShareContent(share_content);
		// 设置点击消息的跳转URL
		qzone.setTargetUrl(share_url);
		// 设置分享内容的标题
		qzone.setTitle(share_title);
		// 设置分享图片
		qzone.setShareImage(image);
		mController.setShareMedia(qzone);
	}
	
	@Click(R.id.exit_layout)
	void exitClick()
	{
		finish();
	}
	
	@Click(R.id.ll_wx)
	void wxClick()
	{
		performShare(SHARE_MEDIA.WEIXIN);
//		finish();
	}
	
	@Click(R.id.ll_wxpyq)
	void wxpyqClick()
	{
		performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
//		finish();
	}
	
	@Click(R.id.ll_qq)
	void qqClick()
	{
		performShare(SHARE_MEDIA.QQ);
//		finish();
	}
	
	@Click(R.id.ll_qqZone)
	void qqZoneClick()
	{
		performShare(SHARE_MEDIA.QZONE);
//		finish();
	}
	
	private void performShare(SHARE_MEDIA platform) {
        mController.postShare(this, platform, new SnsPostListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
            	finish();
//                String showText = platform.toString();
//                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
//                    showText += "平台分享成功";
//                } else {
//                    showText += "平台分享失败";
//                }
//                Toast.makeText(CustomShareDialogActivity.this, showText, Toast.LENGTH_SHORT).show();
            }
        });
    }

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
}
