package com.cbb.myapplication.taiji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author chenbb
 * @create 2017/11/1
 * @desc   一个太极图案
 * 设计核心，每次把画布旋转，然后重新绘制图案，实现动画效果
 */

public class Taiji extends View{

    private Paint mWhitePaint;
    private Paint mBlackPaint;
    private float degrees = 0;

    public Taiji(Context context) {
        this(context, null);
    }

    public Taiji(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setColor(Color.WHITE);

        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.translate(width/2, height/2);    // 画布平移,使得画布中心点到原点

        canvas.drawColor(Color.GRAY);
        canvas.rotate(degrees);                 // 旋转画布，绘制一遍

        int radius = Math.min(width, height) / 2 - 100;

        RectF rectF = new RectF(-radius, -radius, radius, radius);
        canvas.drawArc(rectF, 90, 180, true, mBlackPaint);          // 黑色半圆
        canvas.drawArc(rectF, -90, 180, true, mWhitePaint);         // 白色半圆

        int smallRadius = radius / 2;
        canvas.drawCircle(0, -smallRadius, smallRadius, mBlackPaint);
        canvas.drawCircle(0, smallRadius, smallRadius, mWhitePaint);

        canvas.drawCircle(0, -smallRadius, smallRadius / 4, mWhitePaint);
        canvas.drawCircle(0, smallRadius, smallRadius / 4, mBlackPaint);
    }

    /**
     * 暴露的接口，供外部触发旋转动画
     * @param degrees
     */
    public void setRotate(float degrees){
        this.degrees = degrees;
        invalidate();
    }
}
