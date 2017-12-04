package com.cbb.myapplication.QQ_listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * @author chenbb
 * @create 2017/11/30
 * @desc
 */

public class VerticalLinearLayout extends ViewGroup {

    private int mScreenHeight;
    private Scroller mScroller;
    private boolean isScrolling;
    private int mScrollStart;
    private int mLastY;
    private int mScrollYEnd;

    public VerticalLinearLayout(Context context) {
        this(context, null);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScroller = new Scroller(context);
        // 获取屏幕高度
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, mScreenHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            // 设置主布局的高度
            int childCount = getChildCount();
            MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
            layoutParams.height = mScreenHeight * childCount;
            setLayoutParams(layoutParams);

            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != GONE){
                    child.layout(1, i * mScreenHeight, r, (i + 1) * mScreenHeight);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isScrolling)
            return super.onTouchEvent(event);
        int action = event.getAction();
        int y = (int) event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                mScrollStart = getScrollY();
                mLastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                int dy = mLastY - y;
                int scrollY = getScrollY();
                // 下拉操作，且已经回到顶部（即手势距离>控件的滑动距离），则调整手势距离
                if (dy < 0 && scrollY + dy < 0)
                    dy = -scrollY;
                // 上拉操作，
                if (dy > 0 && scrollY + dy > getHeight() - mScreenHeight)
                    dy = getHeight() - mScreenHeight - scrollY;

                scrollBy(0, dy);
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
                mScrollYEnd = getScrollY();
                int dSrollY = mScrollYEnd - mScrollStart;
                if (wantScrollToNext()){
                    if (shouldScrollToNext()){
                        mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - dSrollY);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -dSrollY);
                    }
                }

                if (wantScrollToPre()){
                    if (shouldScrollToPre()){
                        mScroller.startScroll(0, getScrollY(), 0, -mScreenHeight - dSrollY);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -dSrollY);
                    }
                }
                isScrolling = true;
                postInvalidate();
                recycleVelocity();
                break;
        }
        return true;
    }

    private boolean shouldScrollToPre() {
        return -mScrollYEnd + mScrollStart > mScreenHeight / 2 || Math.abs(getVelocity()) > 600;
    }

    private boolean wantScrollToPre() {
        return mScrollYEnd < mScrollStart;
    }

    private boolean shouldScrollToNext() {
        return mScrollYEnd - mScrollStart > mScreenHeight / 2 || Math.abs(getVelocity()) > 600;
    }

    private boolean wantScrollToNext() {
        return mScrollYEnd > mScrollStart;
    }


    private void recycleVelocity() {

    }

    private int getVelocity() {
        return 0;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()){
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        } else {
            int position = getScrollY() / mScreenHeight;
            if (position != 0){

            }
        }
    }
}
