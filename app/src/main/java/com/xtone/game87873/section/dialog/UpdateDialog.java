package com.xtone.game87873.section.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xtone.game87873.R;

/**
 * 更新对话框
 * @author ywj
 */
public class UpdateDialog extends Dialog implements View.OnClickListener {
	private Button btn_cancle,btn_ok;
	private TextView tv_content;
	private String downloadUrl;
	private Context mContext;
	
	public UpdateDialog(Context context, String downloadUrl,String updateLog) {
		super(context, R.style.custom_dialog);
		setContentView(R.layout.dialog_update);
		this.downloadUrl =downloadUrl;
		this.mContext = context;
		initViews();
		initEvents();
		setContent(updateLog);
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
			dismiss();
			break;
		case R.id.btn_ok:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(downloadUrl));
			mContext.startActivity(intent);
			dismiss();
			break;
		}
	}
	
	public void setContent(String content){
		tv_content.setText(content);
	}

}
