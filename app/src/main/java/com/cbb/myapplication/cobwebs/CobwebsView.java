package com.cbb.myapplication.cobwebs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author chenbb
 * @create 2017/11/28
 * @desc    一个6 × 6 边形的蜘蛛网
 */

public class CobwebsView extends View{

    private int count = 6;  // 方向上蜘蛛网的节点数
    private float angle;
    private float mRadius;
    private int mCenterY;
    private int mCenterX;

    private String[] titles = {"a", "b", "c", "d", "e", "f"};
    private double[] data = {100, 60, 60, 60, 100, 50, 10, 20}; // 各维度分值

    private float maxValue = 100;           // 数据最大值
    private Paint mainPaint;                // 雷达区画笔
    private Paint valuePaint;               // 数据区画笔
    private Paint textPaint;                // 文本画笔

    public CobwebsView(Context context) {
        this(context, null);
    }

    public CobwebsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CobwebsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setColor(Color.parseColor("#00ff00"));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#ff0000"));
        textPaint.setTextSize(16);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(Color.parseColor("#0000ff"));
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        angle = (float) (Math.PI * 2 / count);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRadius = Math.min(h, w) / 2 * 0.9f;
        // 中心坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPolygon(canvas);
        drawLines(canvas);
        drawText(canvas);
        drawRegion(canvas);
    }

    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        for (int i = 0; i < count; i++) {
            double parcent = data[i] / maxValue;
            float x = (float) (mCenterX + mRadius * Math.cos(angle * i) * parcent);
            float y = (float) (mCenterY + mRadius * Math.sin(angle * i) * parcent);
            if (i == 0) path.moveTo(x, mCenterY);
            else path.lineTo(x, y);

            canvas.drawCircle(x, y, 10, valuePaint);
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }


    /* 绘制层层多边形 */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        float r = mRadius / (count - 1);
        for (int i = 0; i < count; i++) {
            float curRadius = r * i;
            path.reset();
            for (int j = 0; j < count; j++) {
                if (j == 0)
                    path.moveTo(mCenterX + curRadius, mCenterY);
                else {
                    // 根据半径，计算出蜘蛛丝上每个点的坐标
                    float x = (float) (mCenterX + curRadius * Math.cos(angle * j));
                    float y = (float) (mCenterY + curRadius * Math.sin(angle * j));
                    path.lineTo(x, y);
                }
            }
            path.close();       // 闭合路径
            canvas.drawPath(path, mainPaint);
        }
    }


    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            path.reset();
            path.moveTo(mCenterX, mCenterY);
            float x = (float) (mCenterX + mRadius * Math.cos(angle * i));
            float y = (float) (mCenterY + mRadius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /* 绘制文字 */
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < count; i++) {
            float x = (float) (mCenterX + (mRadius + fontHeight / 2) * Math.cos(angle * i));
            float y = (float) (mCenterY + (mRadius + fontHeight / 2) * Math.sin(angle * i));

            if (angle * i >= 0 && angle * i <= Math.PI / 2)      // 第四象限
                canvas.drawText(titles[i], x, y, textPaint);

            else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2)   // 第三象限
                canvas.drawText(titles[i], x, y, textPaint);

            else if (angle * i > Math.PI / 2 && angle * i <= Math.PI){      // 第二象限
                float dis = textPaint.measureText(titles[i]);
                canvas.drawText(titles[i], x - dis, y, textPaint);
            } else if (angle *  i > Math.PI && angle * i < 3 * Math.PI / 2){    // 第一象限
                float dis = textPaint.measureText(titles[i]);
                canvas.drawText(titles[i], x - dis, y, textPaint);
            }
        }
    }


}
