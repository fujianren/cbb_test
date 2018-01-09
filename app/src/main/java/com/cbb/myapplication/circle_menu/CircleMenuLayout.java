package com.cbb.myapplication.circle_menu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cbb.myapplication.R;


/**
 * @author chenbb注解
 * @create 2017/12/1
 * @desc   圆环菜单
 * 思路:
 * 1.整view分成中心图标和围绕的子菜单，固定子控件id，暴露设置子控件样式的接口
 * 2.一个动态的起点{@link #startAngle}，确定的子菜单个数，重写onMeasure，onLayout
 * 3.触摸监听，判断手势，变化startAngle值，并requestLayout()，达到一个动画随手指进行的效果
 */

public class CircleMenuLayout extends ViewGroup{
    private static final String TAG = "CircleMenuLayout";

    /* 默认容器内child的尺寸比例 */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    /* 默认菜单中心处child的尺寸比例 */
    private float RADIO_DEFAULT_CENTER_ITEM_DIMENSION = 1 / 3f;
    /* 容器默认的内边距 */
    private static final float RADIO_PADDING_LAYOUT = 1 / 12f;
    /* 若移动角度达到该值，屏蔽点击 */
    private static final float NOCLICK_VALUE = 3;
    /* 若每秒移动速度达到该值，则认为是快速移动 */
    private static final int FLINGABLE_VALUE = 300;

    private int mRadius;        // 圆环半径
    private float mPadding;     // 圆环与child的间距

    private double startAngle = 0;     // 菜单item的起始布置处角度，默认为0
    private float mLastX;
    private float mLastY;
    private long mDownTime;
    private float mTmpAngle;
    private boolean isFling = false;

    /* 默认的快速移动临界值 */
    private float flingableValue;
    private AutoFlingRunnable flingRunnable;
    private int[] mItemImgs;
    private String[] mItemTexts;
    private int mMenuItemCount;


    private int menuItemLayoutId = R.layout.circle_menu_item;

    public CircleMenuLayout(Context context) {
        this(context, null);
    }

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int resWidth = 0;
        int resHeight = 0;
        // 若测量模式非精准
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY){
            // 若未设置背景图，则默认设置为屏幕宽高
            resWidth = getSuggestedMinimumWidth();
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;
            resHeight = getSuggestedMinimumHeight();
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else {
            // 若是精准模式，则取最小值
            resWidth = resHeight = Math.min(width, height);
        }
        // 容器布局确定
        setMeasuredDimension(resWidth, resHeight);

        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        final int count = getChildCount();

        // 菜单child的尺寸
        int childsize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        int childMode = MeasureSpec.EXACTLY;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int makeMeasureSpec = -1;
            // 中心child
            if (child.getId() == R.id.id_circle_menu_item_center){
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (int) (mRadius * RADIO_DEFAULT_CENTER_ITEM_DIMENSION),
                        childMode);
            } else {    //
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childsize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        mPadding = RADIO_PADDING_LAYOUT * mRadius;
    }


    /**
     * 核心方法，会被requestLayout（）多次调用
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;
        final int childCount = getChildCount();

        int left, top;
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);  // child默认尺寸

        float angleDelay = 360 / (getChildCount() - 1);     // 单位角度
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getId() == R.id.id_circle_menu_item_center) continue;

            if (child.getVisibility() == GONE) continue;

            startAngle %= 360;
            // 容器中心与菜单child中心的距离
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;
            left = (int) (layoutRadius / 2 + Math.round(tmp * Math.cos(Math.toRadians(startAngle)) - 1 / 2f * cWidth));
            top = (int) (layoutRadius / 2 + Math.round(tmp * Math.sin(Math.toRadians(startAngle)) - 1 / 2f * cWidth));

            child.layout(left, top, left + cWidth, top + cWidth);
            // 叠加角度
            startAngle += angleDelay;
        }

        View cView = findViewById(R.id.id_circle_menu_item_center);
        if (cView != null){
            cView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null){
                        mOnMenuItemClickListener.itemCenterClick(v);
                    }
                }
            });
            int c1 = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = c1 + cView.getMeasuredWidth();
            cView.layout(c1, c1, cr, cr);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;

                // 若当前在快速滚动,移除快速滚动的回调
                if (isFling){
                    removeCallbacks(flingRunnable);
                    isFling = false;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float start = getAngle(mLastX, mLastY);
                float end = getAngle(x, y);

                // 若是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4){
                    startAngle += end - start;
                    mTmpAngle += end - start;
                } else {
                    startAngle += start - end;
                    mTmpAngle += start - end;
                }
                Log.d(TAG, "dispatchTouchEvent: ==" + startAngle);
                // 重新布局
                requestLayout();
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
                // 每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000 / (System.currentTimeMillis() - mDownTime);

                // 若是快速移动
                if (Math.abs(anglePerSecond) > flingableValue && !isFling){
                    post(flingRunnable = new AutoFlingRunnable(anglePerSecond));
                    return true;
                }
                // 旋转角度越过无效点击值
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /* 根据当前位置计算象限 */
    private int getQuadrant(float x, float y) {
        int temX = (int) (x - mRadius / 2);
        int temY = (int) (y - mRadius / 2);
        if (temX >= 0)
            return temY >= 0 ? 4 : 1;
        else
            return temY >= 0 ? 3 : 2;
    }

    /**
     * 返回触摸点对应中心点的角度，单位为60进制的°
     * @param xTouch    触摸点x坐标
     * @param yTouch    触摸点y坐标
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) ((float) Math.asin(y / Math.hypot(x, y)) * (180 / Math.PI));
    }

    /* 获取layout的默认尺寸 */
    private int getDefaultWidth() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /*
    * --------------------------------------------
    *  暴露的公共方法
    * --------------------------------------------
    */
    public void setMenuItemLayoutId(int menuItemLayoutId){
        this.menuItemLayoutId = menuItemLayoutId;
    }

    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts){
        mItemImgs = resIds;
        mItemTexts = texts;
        if (resIds == null && texts == null){
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }
        mMenuItemCount = resIds == null ? texts.length : resIds.length;
        if (resIds != null && texts != null){
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }
        addMenuItems();
    }

    public void setPadding(float padding){
        this.mPadding = padding;
    }

    public void setFlingableValue(int flingableValue){
        this.flingableValue = flingableValue;
    }



    /* 添加菜单项 */
    private void addMenuItems() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View root = inflater.inflate(menuItemLayoutId, this, false);
            ImageView iv = (ImageView) root.findViewById(R.id.id_circle_menu_item_image);
            TextView tv = (TextView) root.findViewById(R.id.id_circle_menu_item_text);
            if (iv != null){
                iv.setVisibility(VISIBLE);
                iv.setImageResource(mItemImgs[i]);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnMenuItemClickListener != null){
                            mOnMenuItemClickListener.itemClick(v, j);
                        }
                    }
                });
            }
            if (tv != null){
                tv.setVisibility(VISIBLE);
                tv.setText(mItemTexts[i]);
            }
            addView(root);
        }
    }


    /*
        * --------------------------------------------
        *  接口回调
        * --------------------------------------------
        */
    private OnMenuItemClickListener mOnMenuItemClickListener;
    public interface OnMenuItemClickListener{

        void itemClick(View view, int pos);

        void itemCenterClick(View view);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener){
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }



    private class AutoFlingRunnable implements Runnable{
        private float anglePerSecond;

        public AutoFlingRunnable(float veloity){
            this.anglePerSecond = veloity;
        }
        @Override
        public void run() {
            if (Math.abs(anglePerSecond) < 20){
                isFling = false;
                return;
            }
            isFling = true;
            startAngle += (anglePerSecond / 30);
            anglePerSecond /= 1.0666f;
            postDelayed(this, 30);
            requestLayout();
        }
    }

}
