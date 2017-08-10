package com.cbb.myapplication.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.cbb.myapplication.R;

/**
 * 点状的进度加载等待
 */
public class DottedProgressBar extends View {
    private final float mDotSize;
    private final float mSpacing;
    private final int mJumpingSpeed;        // 加载时，每次闪烁的间隔时间
    private int mEmptyDotsColor;
    private int mActiveDotColor;
    private Drawable mActiveDot;
    private Drawable mInactiveDot;

    private boolean isInProgress;           // 是否正处于进度动画当中
    private boolean isActiveDrawable = false;   // 是否使用我们的点图片
    private boolean isInactiveDrawable = false;

    private int mActiveDotIndex;            // 闪烁点的索引

    private int mNumberOfDots;
    private Paint mPaint;
    private int mPaddingLeft;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mNumberOfDots != 0)
                mActiveDotIndex = (mActiveDotIndex + 1) % mNumberOfDots;   // 索引+1,范围内循环
            DottedProgressBar.this.invalidate();            // 重新绘制
            mHandler.postDelayed(mRunnable, mJumpingSpeed); // 当主线程执行到该方法时，重新执行runnable，实现循环
        }
    };


    public DottedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 查询属性类型集合
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DottedProgressBar,
                0, 0);

        isInProgress = false;
        mHandler = new Handler();

        try {
//            mEmptyDotsColor = a.getColor(R.styleable.DottedProgressBar_emptyDotsColor, Color.WHITE);
//            mActiveDotColor = a.getColor(R.styleable.DottedProgressBar_activeDotColor, Color.BLUE);
            // 属性值对象
            TypedValue value = new TypedValue();
            // 从属性集合中获取activityDot的format，并封装到value中
            a.getValue(R.styleable.DottedProgressBar_activeDot, value);
            // 判断是否使用资源中的点drawable
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // 类型是一个颜色值，则不需要activitydrawable，直接获取颜色
                isActiveDrawable = false;
                mActiveDotColor = getResources().getColor(value.resourceId);
            } else if (value.type == TypedValue.TYPE_STRING) {
                // 类型是一个string型的，那么有希望获取的是个图片id
                isActiveDrawable = true;
                mActiveDot = getResources().getDrawable(value.resourceId);
            }
            // inactiveDot的format也有可能是2种中的一种，所以要通过value来判断
            a.getValue(R.styleable.DottedProgressBar_inactiveDot, value);
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // It's a color
                isInactiveDrawable = false;
                mEmptyDotsColor = getResources().getColor(value.resourceId);
            } else if (value.type == TypedValue.TYPE_STRING) {
                // It's a reference, hopefully to a drawable
                isInactiveDrawable = true;
                mInactiveDot = getResources().getDrawable(value.resourceId);
            }
            // dotSize只有一种确定的format，直接精确获取
            mDotSize = a.getDimensionPixelSize(R.styleable.DottedProgressBar_dotSize, 5);
            mSpacing = a.getDimensionPixelSize(R.styleable.DottedProgressBar_spacing, 10);

            mActiveDotIndex = a.getInteger(R.styleable.DottedProgressBar_activeDotIndex, 0);

            mJumpingSpeed = a.getInt(R.styleable.DottedProgressBar_jumpingSpeed, 500);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制出所有的底色点
        for (int i = 0; i < mNumberOfDots; i++) {
            int x = (int) (getPaddingLeft() + mPaddingLeft + mSpacing / 2 + i * (mSpacing + mDotSize));
            if (isInactiveDrawable) {       // 若是直接使用drawable作为点，那么要先设置界限，直接绘制drawable
                mInactiveDot.setBounds(x, getPaddingTop(), (int) (x + mDotSize), getPaddingTop() + (int) mDotSize);
                mInactiveDot.draw(canvas);
            } else {            // 若只获取到背景色的话，则要设置画笔颜色，然后绘制圆
                mPaint.setColor(mEmptyDotsColor);
                canvas.drawCircle(x + mDotSize / 2,
                        getPaddingTop() + mDotSize / 2, mDotSize / 2, mPaint);
            }
        }
        // 绘制出在对应索引上的闪烁点
        if (isInProgress) {         // 若当前处于进度加载中
            int x = (int) (getPaddingLeft() + mPaddingLeft + mSpacing / 2 + mActiveDotIndex * (mSpacing + mDotSize));
            if (isActiveDrawable) {
                mActiveDot.setBounds(x, getPaddingTop(), (int) (x + mDotSize), getPaddingTop() + (int) mDotSize);
                mActiveDot.draw(canvas);
            } else {
                mPaint.setColor(mActiveDotColor);
                canvas.drawCircle(x + mDotSize / 2,
                        getPaddingTop() + mDotSize / 2, mDotSize / 2, mPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 预定的宽高的粗略值
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 计算出不包含间距的宽高
        int widthWithoutPadding = parentWidth - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = parentHeight - getPaddingTop() - getPaddingBottom();

        //setMeasuredDimension(parentWidth, calculatedHeight);
        // 计算出不使用drawable，自定义圆点绘制时的高度
        int calculatedHeight = getPaddingTop() + getPaddingBottom() + (int) mDotSize;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, calculatedHeight);       // 重新定义测量到的宽高
        mNumberOfDots = calculateDotsNumber(widthWithoutPadding);
    }

    /**
     * 根据宽度，求出可以设置几个进度点
     * @param width 进度view所占的不包含间距的宽度
     * @return 进度点的个数
     */
    private int calculateDotsNumber(int width) {
        int number = (int) (width / (mDotSize + mSpacing));
        mPaddingLeft = (int) ((width % (mDotSize + mSpacing)) / 2);  // 居中设置
        //setPadding(getPaddingLeft() + (int) mPaddingLeft, getPaddingTop(), getPaddingRight() + (int) mPaddingLeft, getPaddingBottom());
        return number;
    }

    /**
     * 对外暴露的方法，开启进度加载
     */
    public void startProgress() {
        isInProgress = true;
        mActiveDotIndex = -1;
        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);       // 发送消息，执行循环的runnable
    }

    public void stopProgress() {
        isInProgress = false;
        mHandler.removeCallbacks(mRunnable);
        invalidate();
    }

}
