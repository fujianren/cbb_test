package com.cbb.myapplication.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.cbb.myapplication.R;

/**
 * @author chenbb
 * @create 2017/8/16
 * @desc
 */

public class LoadingButton extends View {
    private static final String TAG = "LoadingButton";

    /** 圆环背景的画笔 **/
    private Paint mCirclePaint;
    /** 圆环进度条的画笔 **/
    private Paint mArcPaint;
    /** 圆形实心背景的画笔 **/
    private Paint mFillCirclePaint;
    /** View的宽度 **/
    private int width;
    /** View的高度，这里View应该是正方形，所以宽高是一样的 **/
    private int height;
    /** View的中心坐标x **/
    private int centerX;
    /** View的中心坐标y **/
    private int centerY;
    /** 是否显示旋转的圆环 **/
    private boolean isShowArc = false;
    /** 是否为首次绘制 **/
    private boolean isDefaultDraw = true;
    private int drawableWidth;
    /** 是否在绘制completeDrawable **/
    private boolean isDrawableStart = false;
    /** 是否处于完成状态 **/
    private boolean isCompleted = false;
    /** 用于展示icon **/
    private Drawable mCommonDrawable;
    /** 完成时的Drawable **/
    private Drawable mCompleteDrawable;
    /** 展示icon的默认宽度 **/
    private int defaultCommonDrawableWidth;

    private boolean isRotate;
    private int radius;
    private RectF oval;

    public LoadingButton(Context context) {
        this(context, null);
    }

    public LoadingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Log.d(TAG, "init: 初始化");
        mCirclePaint = new Paint();
        mCirclePaint.setColor(0x1fffffff);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(10);
        mCirclePaint.setAntiAlias(true);

        mArcPaint = new Paint();
        mArcPaint.setColor(0xff4bb390);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(10);
        mArcPaint.setAntiAlias(true);

        mFillCirclePaint = new Paint();
        mFillCirclePaint.setColor(0xff4bb390);
        mFillCirclePaint.setStyle(Paint.Style.FILL);
        mFillCirclePaint.setStrokeWidth(10);
        mFillCirclePaint.setAntiAlias(true);

        mCommonDrawable = getResources().getDrawable(R.drawable.download_text);
        mCompleteDrawable = getResources().getDrawable(R.drawable.complete);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCompleted) return;
                Log.d(TAG, "onClick: 点击了动画");
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(500);
                scaleAnimation.setFillAfter(true);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isDefaultDraw = false;
                        isShowArc = true;
                        invalidate();
                        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        scaleAnimation.setDuration(500);
                        scaleAnimation.setFillAfter(true);
                        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                isRotate = true;
                                invalidate();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                LoadingButton.this.startAnimation(scaleAnimation);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        centerX = width / 2;
        centerY = width / 2;

        defaultCommonDrawableWidth = (int) (width * 0.5);
        mCommonDrawable.setBounds(0, 0, defaultCommonDrawableWidth, defaultCommonDrawableWidth);
        mCompleteDrawable.setBounds(0, 0, defaultCommonDrawableWidth, defaultCommonDrawableWidth);
        radius = centerX - 10;
        oval = new RectF(centerX - radius, centerX - radius, centerX + radius, centerX + radius);
        Log.d(TAG, "onLayout: 布局完成");
    }

    int arcStartAngle = -90;
    float currentProgress = 10;
    float targetProgress = currentProgress;
    float totalProgress = 360;
    int arcStartAngleOffset = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isKeepDraw = false;
        if (isKeepDraw) {
            canvas.drawCircle(centerX, centerY, radius, mFillCirclePaint);
            canvas.translate(centerX - defaultCommonDrawableWidth/2, centerY - defaultCommonDrawableWidth/2);
            mCompleteDrawable.draw(canvas);
            Log.d(TAG, "onDraw: 在画画");
        } else if (isDrawableStart) {
            defaultCommonDrawableWidth = Math.min(defaultCommonDrawableWidth + 10, width);
            mCompleteDrawable.setBounds(0, 0, defaultCommonDrawableWidth, defaultCommonDrawableWidth);
            canvas.drawCircle(centerX, centerY, Math.min(defaultCommonDrawableWidth, radius), mFillCirclePaint);
            canvas.translate(centerX - defaultCommonDrawableWidth/2, centerX - defaultCommonDrawableWidth/2);
            mCompleteDrawable.draw(canvas);
            Log.d(TAG, "onDraw: 在画画2");
            if (defaultCommonDrawableWidth >= width*0.5f) {
                isDrawableStart = false;
                isCompleted = true;
                invokeComplete();
            }
            invalidate();
        } else if (isRotate) {
            isKeepDraw = true;
            if (currentProgress != targetProgress) {
                isKeepDraw = true;
                float addedValue = 2;
                currentProgress = Math.min(currentProgress + addedValue, targetProgress);
                if (currentProgress == totalProgress) {
                    startDrawable();
                }
            }
            canvas.drawCircle(centerX, centerX, radius, mCirclePaint);
            canvas.drawArc(oval, arcStartAngle + getArcStartOffset(), currentProgress / targetProgress * 360, false, mArcPaint);
        } else if (isShowArc) {
            canvas.drawCircle(centerX, centerY, radius, mCirclePaint);
            isShowArc = false;
        } else if (isCompleted){
            canvas.drawCircle(centerX, centerY, radius, mCirclePaint);
            canvas.translate(centerX - defaultCommonDrawableWidth/ 2, centerX - defaultCommonDrawableWidth/2);
            mCompleteDrawable.draw(canvas);
        }
        if (isKeepDraw) invalidate();
        Log.d(TAG, "onDraw: " + currentProgress);
        Log.d(TAG, "onDraw: 绘制结束");
    }

    private void startDrawable() {
        defaultCommonDrawableWidth = 0;
        isRotate = false;
        isDrawableStart = true;
        invalidate();
    }

    Callback callback;
    public interface  Callback{
        void complete();
    }

    private void invokeComplete() {
        if(callback!=null){
            callback.complete();
            Log.d(TAG, "invokeComplete: 绘画结束");
        }
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    private int getArcStartOffset() {
        if(arcStartAngleOffset>=360){
            arcStartAngleOffset = 0;
        }else{
            arcStartAngleOffset+=2;
        }
        return arcStartAngleOffset;
    }

    public void setTargetProgress(float targetProgress){
        Log.d(TAG, "setTargetProgress: 设置总进度");
        this.targetProgress = targetProgress;
        invalidate();
    }
}
