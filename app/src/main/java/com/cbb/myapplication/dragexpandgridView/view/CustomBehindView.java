package com.cbb.myapplication.dragexpandgridView.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.cbb.myapplication.R;
import com.cbb.myapplication.dragexpandgridView.DragGridAdapter;
import com.cbb.myapplication.dragexpandgridView.Model.DragIconInfo;

import java.util.ArrayList;
import java.util.LinkedList;

import static android.graphics.PixelFormat.RGBA_8888;

/**
 * @author chenbb
 * @create 2017/8/18
 * @desc   镜像类
 */

public class CustomBehindView extends GridView {
    private static final String TAG = "CustomBehindView";

    private Context mContext;
    /* 操作类 */
    private CustomGroup mCustomGroup;
    /* gridview适配器 */
    private DragGridAdapter mDragAdapter;
    /* 适配的数据集合 */
    private ArrayList<DragIconInfo> mIconInfoList = new ArrayList<DragIconInfo>();

    /* 窗口管理器 */
    private final WindowManager mWindowManager;
    /* 状态栏的高度 */
    private final int mStatusHeight;
    /* gridview中的列数,默认-1 */
    private int mNumColumns = AUTO_FIT;


    /* 是否可以拖拽，默认不可以 */
    private boolean isDrag = false;
    /* 长按响应时间，默认1秒 */
    private long dragResponseMS = 1000;
    /* 正在拖拽的position */
    private int mDragPosition;
    /* 触摸时的坐标 */
    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;

    /* 刚开始拖拽时item对应的view */
    private View mStartDragItemView = null;
    /* 用于拖拽的镜像，这里直接用一个ImageView */
    private ImageView mDragImageView;
    /** 我们拖拽的item对应的Bitmap **/
    private Bitmap mDragBitmap;
    /** item镜像的布局参数 **/
    private WindowManager.LayoutParams mWindowLayoutParams;

    /* 按下的点到所在item的上边缘的距离 */
    private int mPoint2ItemTop;
    /* 按下的点到所在item的左边缘的距离 */
    private int mPoint2ItemLeft;
    /* DragGridView距离屏幕顶部的偏移量 */
    private int mOffset2Top;
    /* DragGridView距离屏幕左边的偏移量 */
    private int mOffset2Left;
    /** DragGridView自动向下滚动的边界值 **/
    private int mDownScrollBorder;
    /** DragGridView自动向上滚动的边界值 **/
    private int mUpScrollBorder;
    /** DragGridView自动滚动的速度 **/
    private static final int speed = 20;



    private boolean isFirstLongDrag;
    private boolean hasFirstCalculate = false;


    /* 是否结束item交换的滑动动画，默认结束 */
    private boolean mAnimationEnd = true;
    /* 通知 */
    private Handler mHandler = new Handler();
    /* 长按执行 */
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            isDrag = true;
            mStartDragItemView.setVisibility(INVISIBLE);    // 原item隐藏
            // 在长按的位置上创建一个可拖拽的镜像
            createDragImage(mDragBitmap, mDownX, mDownY);
        }
    };
    private CustomBehindParent parentView;


    public CustomBehindView(Context context, CustomGroup customGroup) {
        super(context);

        this.mContext = context;
        this.mCustomGroup = customGroup;
        this.setNumColumns(CustomGroup.COLUMMUN);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 获取状态栏高度
        mStatusHeight = getStatusHeight(mContext);
    }

    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    private int getStatusHeight(Context context) {
        int statusHeight = 0;

        // 通过窗口管理服务器获取状态栏高度
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            // 反射R类的实例域获取
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelOffset(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /*
    * --------------------------------------------
    *  setter & getter
    * --------------------------------------------
    */

    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     * @param dragResponseMS
     */
    public void setDragResponseMS(long dragResponseMS) {
        this.dragResponseMS = dragResponseMS;
    }

    public ArrayList<DragIconInfo> getEditList() {
        return mIconInfoList;
    }


    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        this.mNumColumns = numColumns;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof DragGridAdapter) {
            mDragAdapter = (DragGridAdapter) adapter;
        } else {
            throw new IllegalStateException("he adapter must be implements DragGridAdapter");
        }
    }

    /* 重新测量高度 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /* 触摸事件的分发 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                // 获取落下点对应的position
                int tempPos = pointToPosition(mDownX, mDownY);
                // 若落脚对应的Position是正好是操作无效的，继续分发触摸事件
                if (tempPos == AdapterView.INVALID_POSITION) {
                    return true;
                }
                // 若落脚处的不是正在拖拽的item，且处于编辑模式，继续分发触摸事件
                if (mCustomGroup.isEditModel() && tempPos != mDragPosition) {
                    mCustomGroup.setEditModel(false, 0);
                    return true;
                }
                // 1秒之后设置长按的通知执行
                mHandler.postDelayed(mLongClickRunnable, dragResponseMS);
                // 屏幕上要被拖拽的控件
                mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());
                // 触摸点在item控件中的位置
                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();
                // item在屏幕中的偏移量
                mOffset2Top = (int) (ev.getRawY() - mDownY);
                mOffset2Left = (int) (ev.getRawX() - mDownX);
                // 自动滚动的边界值
                mDownScrollBorder = getHeight() / 5;
                mUpScrollBorder = getHeight() * 4 / 5;
                // 拖拽的位图
                mStartDragItemView.setDrawingCacheEnabled(true);
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                mStartDragItemView.destroyDrawingCache();
                break;

            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                // 第一次拖拽，且未进行第一次计算时，重新定义偏移量
                if (isFirstLongDrag && !hasFirstCalculate) {
                    mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());
                    mPoint2ItemTop = moveY - mStartDragItemView.getTop();
                    mPoint2ItemLeft = moveX - mStartDragItemView.getLeft();

                    mOffset2Top = (int) (ev.getRawY() - moveY);
                    mOffset2Left = (int) (ev.getRawX() - moveX);
                    hasFirstCalculate = true;
                }
                // 手指的移动点不处于当前的item之内，移除长按通知
                if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }

                break;

            case MotionEvent.ACTION_UP:
                // 抬手时，可实现若长按没有1秒，就会被移除不会触发长按事件
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 触摸点是否在要拖拽的view之内
     * @param dragView  拖拽的view
     * @param moveX     当前触摸点在父容器中的坐标
     * @param moveY
     * @return
     */
    private boolean isTouchInItem(View dragView, int moveX, int moveY) {
        if (dragView == null) return false;

        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (moveX < leftOffset || moveX > leftOffset + dragView.getWidth()) return false;
        if (moveY < topOffset || moveY > topOffset + dragView.getHeight()) return false;
        return true;
    }

    /* 本view亲自处理的触摸事件 */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 可拖拽，且镜像存在
        if (isDrag && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    // 所有受拖拽影响的view做动画
                    onDragItem(moveX, moveY);
                    break;

                case MotionEvent.ACTION_UP:
                    int dropX = (int) ev.getX();
                    int dropY = (int) ev.getY();
                    onStopDrag(dropX, dropY);
                    isDrag = false;
                    isFirstLongDrag = false;
                    hasFirstCalculate = false;
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据触摸位置，通知ui线程创建一个对应item的镜像图
     * @param position 触摸的位置
     * @param event  触摸事件
     */
    public void drawWindowView(final int position, final MotionEvent event){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDragPosition = position;
                if (mDragPosition != AdapterView.INVALID_POSITION) {
                    isFirstLongDrag = true;
                    mDragAdapter.setModifyPosition(mDragPosition);
                    mDownX = (int) event.getX();
                    mDownY = (int) event.getY();
                    mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());
                    createFirstDragImage();
                }
            }
        }, 100);
    }

    /**
     * 创建一个要拖拽的item同款的镜像，用imageview装载
     */
    private void createFirstDragImage() {
        Log.d(TAG, "createFirstDragImage: 创建first镜像");
        removeDragImage();

        isDrag = true;          // 切换状态为正在拖拽
        // 将原本item上的隐藏信息显示出来
        ImageView ivDelete = (ImageView) mStartDragItemView.findViewById(R.id.delet_iv);
        LinearLayout llContainer = (LinearLayout) mStartDragItemView.findViewById(R.id.edit_ll);
        if (ivDelete != null){
            ivDelete.setVisibility(VISIBLE);
        }
        if (llContainer != null) {
            llContainer.setBackgroundColor(mContext.getResources().getColor(R.color.item_bg));
        }
        // item的同款bitmap
        mStartDragItemView.setDrawingCacheEnabled(true);
        mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
        mStartDragItemView.destroyDrawingCache();

        if (llContainer != null){
            llContainer.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        // item镜像的布局参数
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = RGBA_8888;
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        int[] location = new int[2];
        mStartDragItemView.getLocationOnScreen(location);
        mWindowLayoutParams.x = location[0];
        mWindowLayoutParams.y = location[1] - mStatusHeight;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        // 用imageView装载镜像
        mDragImageView = new ImageView(getContext());
        mDragImageView.setBackgroundColor(mContext.getResources().getColor(R.color.item_bg));
        mDragImageView.setImageBitmap(mDragBitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);        // 往屏幕等级处添加一个镜像控件

        mStartDragItemView.setVisibility(INVISIBLE);            // 隐藏原item
    }


    /**
     * 在界面上创建一个可拖动的镜像dragImageView
     * @param bitmap
     * @param downX     按下的点相对父控件的X坐标
     * @param downY     按下的点相对父控件的Y坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = RGBA_8888;
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setBackgroundColor(mContext.getResources().getColor(R.color.item_bg));
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    /**
     * 从界面上移除拖动的镜像dragImageView
     */
    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * 停止镜像拖动,并将之前隐藏的item显示出来，并将镜像移除
     * @param dropX
     * @param dropY
     */
    private void onStopDrag(int dropX, int dropY) {

        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(VISIBLE);
        }
        mDragAdapter.setHideItem(-1);
        removeDragImage();
    }

    /**
     * 拖动item镜像，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        Log.d(TAG, "onDragItem: 开始交换item");
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - - mStatusHeight;
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);   // 实时更新镜像位置
        // 其他item的平移
        onSwapItem(moveX, moveY);
        // 通知镜像自动滚动
        mHandler.post(mScrollRunnable);
    }

    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollY;
            // 置顶或置尾时
            if (getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getCount() - 1){
                mHandler.removeCallbacks(mScrollRunnable);
            }

            if (moveY > mUpScrollBorder) {
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (moveY < mDownScrollBorder) {
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }
            // 移动速度，每10毫秒移动一下
            smoothScrollBy(scrollY, 10);
        }
    };

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        // 获取我们手指移动到的那个item的position
        final int tempPosition = pointToPosition(moveX, moveY);
        // 假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd) {
            if (tempPosition != mIconInfoList.size() - 1) {
                // 交给适配器完成适配的操作
                mDragAdapter.reOrderItems(mDragPosition, tempPosition);
                mDragAdapter.setHideItem(tempPosition);     // 该方法呼叫重新适配并绘制
                // ui绘制前的开启动画
                final ViewTreeObserver observer = getViewTreeObserver();
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        observer.removeOnPreDrawListener(this);
                        animateReorder(mDragPosition, tempPosition);
                        mDragPosition = tempPosition;
                        return true;
                    }
                });
            }
        }
    }

    /**
     * 位置切换时，前后位置之间所有item的平移动画效果
     * @param oldPosition   原item所处的位置
     * @param newPosition   要被放置的新位置
     */
    private void animateReorder(int oldPosition, int newPosition) {
        Log.d(TAG, "animateReorder: 开启交换动画");
        boolean isForward = newPosition >  oldPosition;
        LinkedList<Animator> resultList = new LinkedList<>();
        if (isForward) {
            // 选中的view前进（即索引数变大），其他view后退
            for (int pos = oldPosition; pos < newPosition; pos++) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                // 每行的最后一个item
                if ((pos + 1) % mNumColumns == 0) {  //
                    resultList.add(createTranslationAniamtions(view, -view.getWidth() * (mNumColumns - 1), 0, view.getHeight(), 0));
                } else {        // 前移一个item的距离
                    resultList.add(createTranslationAniamtions(view, view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            // 选中的view后退，其他view前进
            for (int pos = oldPosition; pos > newPosition; pos--) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                // 每行的第一个item
                if ((pos + mNumColumns) % mNumColumns == 0) {  // 上移到上一行
                    resultList.add(createTranslationAniamtions(view, view.getWidth() * (mNumColumns - 1), 0, -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAniamtions(view, -view.getWidth(), 0, 0, 0));
                }
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(300);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());  // 慢快慢的插值器
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimationEnd = true;
            }
        });
        resultSet.start();
    }

    /**
     * 平移的属性动画
     * @param view      执行动画的view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    private AnimatorSet createTranslationAniamtions(View view, float startX, float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY);
        return animatorSet;
    }
    /*
    * --------------------------------------------
    *  public
    * --------------------------------------------
    */

    /**
     * 更新适配
     * @param iconInfoList
     */
    public void refreshIconInfoList(ArrayList<DragIconInfo> iconInfoList) {
        mIconInfoList.clear();
        mIconInfoList.addAll(iconInfoList);
        mDragAdapter = new DragGridAdapter(mContext, mIconInfoList, this);
        this.setAdapter(mDragAdapter);
        mDragAdapter.notifyDataSetChanged();
    }

    public void notifyDataSetChange(ArrayList<DragIconInfo> iconInfoList) {
        mIconInfoList.clear();
        mIconInfoList.addAll(iconInfoList);
        mDragAdapter.resetModifyPosition();     // 重置修改的位置
        mDragAdapter.notifyDataSetChanged();
    }

    public void deleteInfo(int position, DragIconInfo iconInfo) {
        deleteAnimation(position);
        mCustomGroup.deleteHomePageInfo(iconInfo);
    }

    private void deleteAnimation(final int position) {
        final View view = getChildAt(position);
        view.setDrawingCacheEnabled(true);
        Bitmap dragBitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.destroyDrawingCache();

        final ImageView animView = new ImageView(mContext);
        animView.setImageBitmap(dragBitmap);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // TODO: 2017/8/21
        final int animPos = mIconInfoList.size() - 1;
        AnimatorSet animatorSet = createTranslationAniamtions(position, animPos, view, animView);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(500);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animView.setVisibility(GONE);
                animView.clearAnimation();
                mDragAdapter.reOrderItems(position, animPos);
                mDragAdapter.deleteItem(animPos);
                final ViewTreeObserver observer = getViewTreeObserver();
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        observer.removeOnPreDrawListener(this);
                        animateReorder(position, animPos);
                        return true;
                    }
                });
            }
        });
        animatorSet.start();
    }

    /**
     * 混合属性动画
     * @param position
     * @param animPos   平移终点所在的view
     * @param view      平移起始点所在的view
     * @param animView  执行动画的view
     * @return
     */
    private AnimatorSet createTranslationAniamtions(int position, int animPos, View view, ImageView animView) {
        int startx = view.getLeft();
        int starty = view.getTop();
        View aimView = getChildAt(animPos);
        int endx = aimView.getLeft();
        int endy = aimView.getTop();

        ObjectAnimator animX = ObjectAnimator.ofFloat(animView, "translationX", startx, endx);
        ObjectAnimator animY = ObjectAnimator.ofFloat(animView, "translationY", starty, endy);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(animView, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(animView, "scaleY", 1f, 0.5f);
        ObjectAnimator alpaAnim = ObjectAnimator.ofFloat(animView, "alpha", 1f, 0.0f);

        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY, scaleX, scaleY, alpaAnim);
        return animSetXY;
    }

    public void setDeletAnimView(CustomBehindParent customBehindParent) {
        this.parentView = customBehindParent;
    }

    /**
     * 是否已经完成拖拽后各item的数据交换
     * @return
     */
    public boolean isModifyedOrder() {
        return mDragAdapter.isHasModifyedOrder();
    }


    public void cancleModifyedOrderState() {
        mDragAdapter.setHasModifyedOrder(false);
    }

    public void resetHidePosition() {
        mDragAdapter.setHideItem(-1);
    }

    /**
     * 手指移动到的位置是不是有效的触摸事件
     * @param ev   触摸事件
     * @param scrolly   垂直滚动值
     * @return
     */
    public boolean isValideEvent(MotionEvent ev, int scrolly) {
        int left = ((View)(getParent().getParent())).getLeft();
        int top = ((View)(getParent().getParent())).getTop();
        int x_ = (int) ev.getX();
        int y_ = (int) ev.getY();
        int tempx = x_ - left;
        int tempy = y_ - top + scrolly;
        int position = pointToPosition(tempx,tempy);
        Rect rect = new Rect();
        getHitRect(rect);
        if (position == AdapterView.INVALID_POSITION) {
            return false;
        }else{
            return true;
        }
    }

    public void clearDragView() {
        removeDragImage();
    }

    /**
     * 取消编辑模式
     */
    public void cancleEditModel() {
        removeDragImage();
        mCustomGroup.setEditModel(false, 0);
    }
}
