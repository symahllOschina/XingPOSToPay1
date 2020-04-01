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

package com.zijunlin.Zxing.Demo.view;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.wanding.xingpos.R;
import com.zijunlin.Zxing.Demo.camera.CameraManager;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
  private static final long ANIMATION_DELAY = 100L;
  private static final int OPAQUE = 0xFF;

  private final Paint paint;
  private Bitmap resultBitmap;
  private final int maskColor;
  private final int resultColor;
  private final int frameColor;
  private final int laserColor;
  private final int resultPointColor;
  private int scannerAlpha;
  private Collection<ResultPoint> possibleResultPoints;
  private Collection<ResultPoint> lastPossibleResultPoints;
  
  private static final int MIDDLE_LINE_WIDTH = 6;  //扫描框中的中间线的宽度 
  private static final int MIDDLE_LINE_PADDING = 5;  //扫描框中的中间线的与扫描框左右的间隙 
  private static final int SPEEN_DISTANCE = 5;  //中间那条线每次刷新移动的距离 
  private static float density;  //手机的屏幕密度 
  private static final int TEXT_SIZE = 16;  //字体大小 
  private static final int TEXT_PADDING_TOP = 30;  //字体距离扫描框下面的距离 
  private int slideTop;  //中间滑动线的最顶端位置
  private int slideBottom;  //中间滑动线的最底端位置 
  boolean isFirst;  


  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);
    
    density = context.getResources().getDisplayMetrics().density; 
    
    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint();
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.viewfinder_mask);
    resultColor = resources.getColor(R.color.result_view);
    frameColor = resources.getColor(R.color.viewfinder_frame);
    laserColor = resources.getColor(R.color.viewfinder_laser);
    resultPointColor = resources.getColor(R.color.possible_result_points);
    scannerAlpha = 0;
    possibleResultPoints = new HashSet<ResultPoint>(5);
  }

  @Override
  public void onDraw(Canvas canvas) {
    Rect frame = CameraManager.get().getFramingRect();
    if (frame == null) {
      return;
    }
    
  //初始化中间线滑动的最上边和最下边  
    if(!isFirst){  
        isFirst = true;  
        slideTop = frame.top;  
        slideBottom = frame.bottom;  
    }  
    
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面  
    //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边 
    paint.setColor(resultBitmap != null ? resultColor : maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    if (resultBitmap != null) {
      // 在扫描矩形中绘制不透明的结果位图
      paint.setAlpha(OPAQUE);
      canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
    } else {

      // 画出一二像素的黑色固体边界内框架矩形
      paint.setColor(frameColor);
      canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
      canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
      canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
      canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);
      //画出四个角
      paint.setColor(getResources().getColor(R.color.green_30d60a));
      // 左上角
      canvas.drawRect(frame.left, frame.top, frame.left + 15,frame.top+ 5,paint);
      canvas.drawRect(frame.left, frame.top, frame.left + 5,frame.top + 15,paint);
      // 右上角
      canvas.drawRect(frame.right- 15, frame.top, frame.right,frame.top + 5,paint);
      canvas.drawRect(frame.right- 5,frame.top, frame.right,frame.top + 15, paint);
	 //左下角
	 canvas.drawRect(frame.left,frame.bottom - 5,frame.left + 15,frame.bottom, paint);
	 canvas.drawRect(frame.left,frame.bottom - 15,frame.left + 5,frame.bottom, paint);
	 //右下角
	 canvas.drawRect(frame.right - 15,frame.bottom - 5,frame.right,frame.bottom, paint);
	 canvas.drawRect(frame.right- 5,frame.bottom - 15,frame.right,frame.bottom, paint);


      /**画一个红色的“激光扫描仪”线通过中间显示解码是积极的(DEMO默认)
      paint.setColor(laserColor);
      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
      int middle = frame.height() / 2 + frame.top;
      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
         
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
       */

	//绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE  
     slideTop += SPEEN_DISTANCE;  
     if(slideTop >= frame.bottom){  
         slideTop = frame.top;  
     }  
     canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop + MIDDLE_LINE_WIDTH/2,paint);

     
     //画扫描框下面的字  
     paint.setColor(Color.WHITE);  
     paint.setTextSize(TEXT_SIZE * density);  
     paint.setAlpha(0x40);  
     paint.setTypeface(Typeface.create("System", Typeface.BOLD));  
     canvas.drawText(getResources().getString(R.string.scan_text), frame.left/2, (float) (frame.bottom + (float)TEXT_PADDING_TOP *density), paint);
     
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
             canvas.drawCircle(frame.left + point.getX(), frame.top  
                     + point.getY(), 6.0f, paint);  
         }  
     }  
     if (currentLast != null) {  
         paint.setAlpha(OPAQUE / 2);  
         paint.setColor(resultPointColor);  
         for (ResultPoint point : currentLast) {  
             canvas.drawCircle(frame.left + point.getX(), frame.top  
                     + point.getY(), 3.0f, paint);  
         }  
     }  

	 
	 
      //只刷新扫描框的内容，其他地方不刷新 
      postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }
  }

  public void drawViewfinder() {
    resultBitmap = null;
    invalidate();
  }

  /**
   * Draw a bitmap with the result points highlighted instead of the live scanning display.
   *
   * @param barcode An image of the decoded barcode.
   */
  public void drawResultBitmap(Bitmap barcode) {
    resultBitmap = barcode;
    invalidate();
  }

  public void addPossibleResultPoint(ResultPoint point) {
    possibleResultPoints.add(point);
  }

}
