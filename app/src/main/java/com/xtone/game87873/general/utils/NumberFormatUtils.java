package com.xtone.game87873.general.utils;

import java.text.DecimalFormat;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;


/**
 * 设置数字格式的工具类
 * 
 * @author 杨伟锦
 * @e-mail 1147953072@qq.com
 * @version v1.0
 * @copyright 2010-2015
 * @create-time 2014-9-22 下午9:47:27
 */
public class NumberFormatUtils
{
	/**
	 * 保留小数点后两位
	 * 
	 * @param price
	 * @return
	 */
	public static String getNumFormat(double price)
	{
		return new DecimalFormat("0.00").format(price);
	}

}
