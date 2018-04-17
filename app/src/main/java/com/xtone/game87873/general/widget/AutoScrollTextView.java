package com.xtone.game87873.general.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * AutoScrollTextView.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-12-31 上午9:59:27
 */
public class AutoScrollTextView extends TextView {

	public AutoScrollTextView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AutoScrollTextView(Context context) {
		super(context);
		init();
	}

	public void init() {
		setSingleLine(true);
		setEllipsize(TextUtils.TruncateAt.MARQUEE);
		// setMarqueeRepeatLimit()
	}

	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// return super.isFocused();
		return true;
	}
}
