package com.cbb.myapplication.water_wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author chenbb
 * @create 2017/12/7
 * @desc    没啥卵用的水波纹
 * 利用handler操纵时间，圆环随时间透明度变小，半径增大
 *
 */

public class WaterWave extends View {

    private static final int MAX_ALPHA = 225;

    private static final String TAG = "WaterWave";
    private List<Wave> mWaveList;

    public WaterWave(Context context) {
        this(context, null);
    }

    public WaterWave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterWave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWaveList = Collections.synchronizedList(new ArrayList<Wave>());    // 为什么使用线程安全的
    }

    private Paint initPaint(int alpha, int width) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(alpha);
        paint.setColor(Color.RED);      // 来一发随机颜色
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i= 0; i < mWaveList.size(); i++){
            Wave wave = mWaveList.get(i);
            canvas.drawCircle(wave.xDown, wave.yDown, wave.radius, wave.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                addWave(event);

                invalidate();

                // 启动动画绘制
                mHandler.sendEmptyMessage(0);
                break;

            case MotionEvent.ACTION_MOVE:
                addWave(event);
                break;

        }
        return true;
    }

    /* 添加一个wave */
    private void addWave(MotionEvent event){
        Wave wave = new Wave();
        wave.radius = 0;
        wave.alpha = MAX_ALPHA;
        wave.width = wave.radius / 4;
        wave.xDown = (int) event.getX();
        wave.yDown = (int) event.getY();
        wave.paint = initPaint(wave.alpha, wave.width);
        mWaveList.add(wave);
    }

    /* 刷新状态的方法 */
    private void fluchState(){
        for (int i = 0; i < mWaveList.size(); i++) {
            Wave wave = mWaveList.get(i);
            // 透明度为0，则移除该wave
            if (wave.alpha == 0){
                mWaveList.remove(i);
                wave.paint = null;
                wave = null;
                continue;
            }
            wave.radius += 5;
            wave.alpha -= 10;
            if (wave.alpha < 0) wave.alpha = 0;
            wave.width = wave.radius / 4;
            wave.paint.setAlpha(wave.alpha);
            wave.paint.setStrokeWidth(wave.width);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    fluchState();
                    invalidate();
                    if (mWaveList != null && mWaveList.size() > 0) {
                        mHandler.sendEmptyMessageDelayed(0, 50);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
