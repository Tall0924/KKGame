package com.xtone.game87873.section.dialog;

import com.xtone.game87873.R;
import com.xtone.game87873.general.utils.BitmapHelper;
import com.xtone.game87873.general.utils.DateUtil;
import com.xtone.game87873.general.utils.MyDateHelper;
import com.xtone.game87873.general.widget.wheel.ArrayWheelAdapter;
import com.xtone.game87873.general.widget.wheel.OnWheelChangedListener;
import com.xtone.game87873.general.widget.wheel.WheelView;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class DateChoiceDialog extends Dialog implements android.view.View.OnClickListener{
	private WheelView yearWheel;
	private WheelView monthWheel;
	private WheelView dayWheel;
	private Button btn_ok, btn_cancle;
	private OnClickListener onClikListener;
	private int[] date = new int[3];
	private static int START_YEAR = 1970, END_YEAR = Integer.valueOf(DateUtil.getNowDate().substring(0, 4))+1;
	private String[] yearArray = new String[END_YEAR - START_YEAR];
	private String[] monthArray = new String[] { "01月", "02月", "03月", "04月", "05月", "06月", "07月", "08月", "09月", "10月", "11月", "12月" };
	private String[] dayArray = new String[] { "01日", "02日", "03日", "04日", "05日", "06日", "07日", "08日", "09日", "10日", "11日", "12日", "13日", "14日", "15日", "16日", "17日", "18日", "19日", "20日", "21日",
			"22日", "23日", "24日", "25日", "26日", "27日", "28日", "29日", "30日", "31日" };
	
	public DateChoiceDialog(Context context, int year, int month, int day, OnClickListener onClikListener) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		setContentView(R.layout.dialog_choose_date);
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		this.onClikListener = onClikListener;
		this.date[0] = day;
		this.date[1] = month;
		this.date[2] = year;
		dayWheel = (WheelView) findViewById(R.id.day);
		dayWheel.setLabel("");
		dayWheel.setCyclic(true);
		dayWheel.setVisibleItems(3);
		dayWheel.TEXT_SIZE = BitmapHelper.sp2px(context, 18);

		monthWheel = (WheelView) findViewById(R.id.month);
		monthWheel.setLabel("");
		monthWheel.setAdapter(new ArrayWheelAdapter<String>(monthArray, 8));
		monthWheel.setCyclic(true);
		monthWheel.setVisibleItems(3);
		monthWheel.TEXT_SIZE = BitmapHelper.sp2px(context, 18);

		// Year picker
		yearWheel = (WheelView) findViewById(R.id.year);
		yearWheel.setLabel("");// 添加文字
		for (int i = START_YEAR; i < END_YEAR; i++) {
			yearArray[i - START_YEAR] = i + "年";
		}
		yearWheel.setAdapter(new ArrayWheelAdapter<String>(yearArray, 8));// 设置"年"的显示数据
		yearWheel.setCyclic(true);// 可循环滚动
		yearWheel.setVisibleItems(3);
		yearWheel.TEXT_SIZE = BitmapHelper.sp2px(context, 18);

		dayWheel.setCurrentItem(date[0] - 1);
		monthWheel.setCurrentItem(date[1] - 1);
		yearWheel.setCurrentItem(date[2] - START_YEAR);
		OnWheelChangedListener wheelListenerYear = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				date[2] = newValue + START_YEAR;
				updateDay();
			}

		};
		OnWheelChangedListener wheelListenerMonth = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				date[1] = newValue + 1;
				updateDay();
			}

		};
		OnWheelChangedListener wheelListenerDay = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				date[0] = newValue + 1;
			}

		};
		yearWheel.addChangingListener(wheelListenerYear);
		monthWheel.addChangingListener(wheelListenerMonth);
		dayWheel.addChangingListener(wheelListenerDay);
		updateDay();
		btn_cancle.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
	}
	
	public void updateDay() {
		int sMonth = date[1], sYear = date[2];
		int daysLength = MyDateHelper.countSolarDates(sMonth, sYear);
		String subSolarDay[] = new String[daysLength];
		System.arraycopy(dayArray, 0, subSolarDay, 0, MyDateHelper.countSolarDates(sMonth, sYear));
		dayWheel.setAdapter(new ArrayWheelAdapter<String>(subSolarDay, 6));
		if (daysLength < date[0]) {
			dayWheel.setCurrentItem(daysLength-1);
		} else {
			dayWheel.setCurrentItem(date[0]-1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			onClikListener.onClick(this, 0);
			break;
		case R.id.btn_cancle:
			this.cancel();
		break;
		}
	}
	
	public int[] getDateIntArray() {
		return date;
	}

}
