package com.xtone.game87873.section.dialog;

import com.xtone.game87873.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 对话框
 * @author huangzx
 * **/
public class CommonDialog extends Dialog implements View.OnClickListener {
	public static final int CLICK_CANCLE = 101;
	public static final int CLICK_OK = 102;
	private Button btn_cancle,btn_ok;
	private TextView tv_content;
	private OnClickListener mOnClickLister;
	
	public CommonDialog(Context context, OnClickListener lister) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		mOnClickLister = lister;
		setContentView(R.layout.dialog_common);
		initViews();
		initEvents();
		setCancelable(true);
		setCanceledOnTouchOutside(false);
	}
	
	private void initViews(){
		tv_content = (TextView) findViewById(R.id.tv_content);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
	}
	
	private void initEvents(){
		btn_cancle.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancle:
			mOnClickLister.onClick(this, CLICK_CANCLE);
			break;
		case R.id.btn_ok:
			mOnClickLister.onClick(this, CLICK_OK);
			break;
		}
	}
	
	public void setContent(String content){
		tv_content.setText(content);
	}
	
	public void setOkBtnText(String text){
		btn_ok.setText(text);
	}
	
	public void setCancleBtnText(String text){
		btn_cancle.setText(text);
	}

	public Button getBtn_cancle() {
		return btn_cancle;
	}

	public Button getBtn_ok() {
		return btn_ok;
	}

	public TextView getTv_content() {
		return tv_content;
	}

}
