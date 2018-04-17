/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xtone.game87873.zxing.view;

import java.util.Collection;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.xtone.game87873.R;
import com.xtone.game87873.general.utils.BitmapHelper;
import com.xtone.game87873.zxing.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	private static final long ANIMATION_DELAY = 100L;
	private static final int OPAQUE = 0xFF;
	private Bitmap resultBitmap;
	/**
	 * 四个绿色边角对应的长度
	 */
	private int screenRate;
	/**
	 * 四个绿色边角对应的宽度
	 */
	private int corner_whith;
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;
	private final int maskColor;	
	private final int resultColor;
	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		possibleResultPoints = new HashSet<ResultPoint>(5);
		screenRate = BitmapHelper.dip2px(context, 16);
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		possibleResultPoints = new HashSet<ResultPoint>(5);
		corner_whith = BitmapHelper.dip2px(context, 4);
		resultColor = resources.getColor(R.color.result_view);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@SuppressLint("DrawAllocation") 
	@Override
	public void onDraw(Canvas canvas) {
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		
		// Draw the exterior (i.e. outside the framing rect) darkened
		// 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		// 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {
			// 画扫描框边上的角，总共8个部分
			paint.setColor(getResources().getColor(R.color.corner_color));
			canvas.drawRect(frame.left, frame.top, frame.left + screenRate, frame.top + corner_whith, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + corner_whith, frame.top + screenRate, paint);
			canvas.drawRect(frame.right - screenRate, frame.top, frame.right, frame.top + corner_whith, paint);
			canvas.drawRect(frame.right - corner_whith, frame.top, frame.right, frame.top + screenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - corner_whith, frame.left + screenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - screenRate, frame.left + corner_whith, frame.bottom, paint);
			canvas.drawRect(frame.right - screenRate, frame.bottom - corner_whith, frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - corner_whith, frame.bottom - screenRate, frame.right, frame.bottom, paint);

			// Draw a red "laser scanner" line through the middle to show
			// decoding is active
			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
				}
			}

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
		
//		if (resultBitmap != null) {
//
//		} else {
//			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
//		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
