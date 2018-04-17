package com.xtone.game87873.section.dialog;

import com.xtone.game87873.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 提示弹出框
 */
public class HintDialog extends Dialog implements View.OnClickListener {

	private Context mContext;
	private Button btn_down;
	private TextView tv_content;
	private OnClickListener mOnClickLister;
	
	public HintDialog(Context context,String showText,OnClickListener lister) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		mContext = context;
		mOnClickLister = lister;
		setContentView(R.layout.dialog_hint);
		initViews();
		initEvents();
		setContent(showText);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}
	
	private void initViews()
	{
		tv_content = (TextView) findViewById(R.id.tv_content);
		btn_down = (Button) findViewById(R.id.btn_down);
	}
	
	private void initEvents()
	{
		btn_down.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_down:
			if (mOnClickLister != null) {
				mOnClickLister.onClick(this, 0);
				this.cancel();
			}
			break;
		}
	}
	
	public void setContent(String content)
	{
		tv_content.setText(content);
	}
	
}
