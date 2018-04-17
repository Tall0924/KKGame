package com.xtone.game87873.section;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xtone.game87873.general.base.BaseActivity;
import com.xtone.game87873.section.dialog.ChoiceImageDialogActivity_;
import com.xtone.game87873.section.dialog.CustomShareDialogActivity_;
import com.xtone.game87873.section.entity.GameGiftEntity;
import com.xtone.game87873.section.entity.SubjectEntity;
import com.xtone.game87873.section.entity.TypeTagEntity;
import com.xtone.game87873.section.game.AppDetailActivity;
import com.xtone.game87873.section.game.AppDetailActivity_;
import com.xtone.game87873.section.game.ClassifyDetailActivity;
import com.xtone.game87873.section.game.ClassifyDetailActivity_;
import com.xtone.game87873.section.game.InformationDetailWebViewActivity;
import com.xtone.game87873.section.game.InformationDetailWebViewActivity_;
import com.xtone.game87873.section.game.SubjectDetailActivity;
import com.xtone.game87873.section.game.SubjectDetailActivity_;
import com.xtone.game87873.section.game.WebActivity_;
import com.xtone.game87873.section.gift.GiftDetailActivity_;
import com.xtone.game87873.section.gift.MyGiftActivity_;
import com.xtone.game87873.section.info.LoginActivity_;
import com.xtone.game87873.section.info.game.DownloadManagerActivity_;
import com.xtone.game87873.section.info.game.MyCollectionActivity_;
import com.xtone.game87873.zxing.CaptureActivity_;

/**
 * AtartUtils.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-21 上午10:21:01
 */
public class StartUtils {

	public static void startHomeActivity(Context context) {
		context.startActivity(new Intent(context, HomeActivity_.class));
	}

	public static void startLogin(Context context) {
		context.startActivity(new Intent(context, LoginActivity_.class));
	}

	public static void startLoginForResult(Fragment fragment, int requestCode) {
		fragment.startActivityForResult(new Intent(fragment.getActivity(),
				LoginActivity_.class), requestCode);
	}

	public static void startMyGame(Context context) {
		Intent intent = new Intent(context, DownloadManagerActivity_.class);
		context.startActivity(intent);
	}

	public static void startMyCollect(Context context) {
		Intent intent = new Intent(context, MyCollectionActivity_.class);
		context.startActivity(intent);
	}

	public static void startMyGift(Context context) {
		Intent intent = new Intent(context, MyGiftActivity_.class);
		context.startActivity(intent);
	}

	public static void startMyGiftForResult(Fragment fragment, int requestCode) {
		fragment.startActivityForResult(new Intent(fragment.getActivity(),
				MyGiftActivity_.class), requestCode);
	}

	public static void startAppDetail(Context context, String gameName,
			long gameId) {
		Intent intent = new Intent(context, AppDetailActivity_.class);
		intent.putExtra(AppDetailActivity.GAME_NAME, gameName);
		intent.putExtra(AppDetailActivity.GAME_ID, gameId);
		context.startActivity(intent);
	}

	public static void startTypeDetail(Context context, String typeName, long id) {
		Intent intent = new Intent(context, ClassifyDetailActivity_.class);
		intent.putExtra(ClassifyDetailActivity.TYPE_NAME, typeName);
		intent.putExtra(ClassifyDetailActivity.TYPE_ID, id);
		context.startActivity(intent);
	}

	public static void startTagDetail(Context context, String tagName,
			long tagId) {
		Intent intent = new Intent(context, ClassifyDetailActivity_.class);
		intent.putExtra(ClassifyDetailActivity.TAG_NAME, tagName);
		intent.putExtra(ClassifyDetailActivity.TAG_ID, tagId);
		context.startActivity(intent);
	}

	public static void startInformationDetail(Context context, long id,
			String title) {
		Intent intent = new Intent(context,
				InformationDetailWebViewActivity_.class);
		intent.putExtra(InformationDetailWebViewActivity.INFO_ID, id);
		intent.putExtra(InformationDetailWebViewActivity.INFO_TITLE, title);
		context.startActivity(intent);
	}

	public static void startAppDetailForResult(Fragment fragment,
			String gameName, long gameId, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(),
				AppDetailActivity_.class);
		intent.putExtra(AppDetailActivity.GAME_NAME, gameName);
		intent.putExtra(AppDetailActivity.GAME_ID, gameId);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void startGiftDetail(Context context, GameGiftEntity gift) {
		Intent intent = new Intent(context, GiftDetailActivity_.class);
		intent.putExtra("giftId", gift.getId());
		context.startActivity(intent);
	}

	public static void startGiftDetail(Context context, long giftId) {
		Intent intent = new Intent(context, GiftDetailActivity_.class);
		intent.putExtra("giftId", giftId);
		context.startActivity(intent);
	}

	public static void startSubjectDetail(Context context, SubjectEntity subject) {
		Intent intent = new Intent(context, SubjectDetailActivity_.class);
		intent.putExtra(SubjectDetailActivity.SUBJECT_ID, subject.getId());
		intent.putExtra(SubjectDetailActivity.SUBJECT_TITLE, subject.getTitle());
		context.startActivity(intent);
	}

	public static void startSubjectDetail(Context context, long subjectId,
			String title) {
		Intent intent = new Intent(context, SubjectDetailActivity_.class);
		intent.putExtra(SubjectDetailActivity.SUBJECT_ID, subjectId);
		intent.putExtra(SubjectDetailActivity.SUBJECT_TITLE, title);
		context.startActivity(intent);
	}

	public static void startSubjectDetailForResult(Fragment fragment,
			SubjectEntity subject, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(),
				SubjectDetailActivity_.class);
		intent.putExtra(SubjectDetailActivity.SUBJECT_ID, subject.getId());
		intent.putExtra(SubjectDetailActivity.SUBJECT_TITLE, subject.getTitle());
		fragment.startActivityForResult(intent, requestCode);
	}

	// 分享dialog
	public static void startCustomShareDialogActivity(Context context) {
		Intent intent = new Intent(context, CustomShareDialogActivity_.class);
		context.startActivity(intent);
	}

	public static void startCustomShareDialogActivity(Context context,Bundle bundle) {
		Intent intent = new Intent(context, CustomShareDialogActivity_.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	// 选择照片来源dialog
	public static void startChoiceImageDialogActivity(BaseActivity context,
			int reqCode) {
		Intent intent = new Intent(context, ChoiceImageDialogActivity_.class);
		context.startActivityForResult(intent, reqCode);
	}

	// 打开网页
	public static void startWebActivity(Context context, Bundle bundle) {
		Intent intent = new Intent(context, WebActivity_.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	// 打开扫描页面
	public static void startCaptureActivity(Context context) {
		Intent intent = new Intent(context, CaptureActivity_.class);
		context.startActivity(intent);
	}
	
	public static void startClassifyDetailActivity(Context cxt, TypeTagEntity tag){
		Intent intent = new Intent(cxt,ClassifyDetailActivity_.class);
		intent.putExtra(ClassifyDetailActivity.TAG_NAME, tag.getName());
		intent.putExtra(ClassifyDetailActivity.TAG_ID, tag.getId());
		cxt.startActivity(intent);
	}
}
