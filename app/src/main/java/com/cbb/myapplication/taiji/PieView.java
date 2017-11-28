package com.cbb.myapplication.taiji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * @author chenbb
 * @create 2017/11/1
 * @desc
 */

public class PieView extends View {

    // 颜色表 (注意: 此处定义颜色使用的是ARGB，带Alpha通道的)
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636,
            0xFF800000, 0xFF808000, 0xFFFF8C69,
            0xFF808080, 0xFFE6B800, 0xFF7CFC00};
    /* 适配的数据 */
    private ArrayList<PieData> mDatas;

    /* 画笔 */
    private Paint mPaint = new Paint();
    /* 宽高 */
    private int mWidth, mHeight;
    /* 起始配置 */
    private float mStartAngle;

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDatas == null) return;

        float currentStartAngle = mStartAngle;
        canvas.translate(mWidth / 2, mHeight / 2);  // 老套路，将画布坐标原点移动到中心位置

        float radius = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
        RectF rectF = new RectF(-radius, -radius, radius, radius);

        // 依次绘制扇形
        for (int i = 0; i < mDatas.size(); i++) {
            PieData pieData = mDatas.get(i);
            mPaint.setColor(pieData.getColor());
            canvas.drawArc(rectF, currentStartAngle, pieData.getAngle(), true, mPaint);
            currentStartAngle += pieData.getAngle();
        }
    }

    /* 设置起始的角度 */
    public void setStartAngle(int startAngle){
        this.mStartAngle = startAngle;
        invalidate();
    }


    /* 设置数据 */
    public void setDatas(ArrayList<PieData> datas){
        this.mDatas = datas;
        initData(mDatas);
        invalidate();
    }

    /* 初始化数据 */
    private void initData(ArrayList<PieData> datas) {
        if (datas == null || datas.size() == 0) return;

        float sumValue = 0;
        for (int i = 0; i < datas.size(); i++) {
            PieData pieData = datas.get(i);
            sumValue += pieData.getValue();

            int j = i % mColors.length;     // 取颜色
            pieData.setColor(mColors[j]);
        }
        float sumAngle = 0;
        for (int i = 0; i < datas.size(); i++) {
            PieData pieData = mDatas.get(i);
            float percentage = pieData.getValue() / sumValue;
            float angle = percentage * 360;

            pieData.setPercentage(percentage);
            pieData.setAngle(angle);
            sumAngle += angle;
            Log.i("angle", "" + pieData.getAngle());
        }
    }
}
