package com.cbb.myapplication.QQ_listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cbb.myapplication.R;



/**
 * @author chenbb
 * @create 2017/11/30
 * @desc    仿qq列表左滑删除
 * 获取滑动触摸处对应的item
 * 在item做popupwindow，实现删除按钮出现的视觉效果
 */

public class QQListview extends ListView {
    private static final String TAG = "QQListview";
    private Button mDelBtn;

    /* 触发用户滑动的最小距离 */
    private int mTouchSlop;

    private PopupWindow mPopupWindow;
    private int mPopWindowHeight;
    private int mPopWindowWidth;

    /* 触摸时的坐标点 */
    private int xDown;
    private int yDown;
    private int xMove;
    private int yMove;

    private boolean isSliding;      // 是否响应滑动
    private int mCurrentPos;
    private View mCurrentView;
    private DelButtonClickListener mListener;

    public QQListview(Context context) {
        this(context, null);

    }

    public QQListview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.delete_btn, null);
        // 获取触摸滑动的溢出距离，即能够触发有效滑动的最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mDelBtn = (Button) root.findViewById(R.id.id_item_btn);
        mPopupWindow = new PopupWindow(root, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        mPopupWindow.getContentView().measure(0, 0);     // 先调用measure，否则拿不到宽高
        mPopWindowHeight = mPopupWindow.getContentView().getMeasuredHeight();
        mPopWindowWidth = mPopupWindow.getContentView().getMeasuredWidth();
    }

    /**
     * 对触摸的快速处理事件的响应
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                xDown = x;
                yDown = y;
                // 若pop正在显示，则直接隐藏pop，并屏蔽listView的touch事件
                if (mPopupWindow.isShowing()){
                    dismissPopWindow();
                    return false;
                }
                // 获取当前手指按下时的item
                mCurrentPos = pointToPosition(xDown, yDown);
                View view = getChildAt(mCurrentPos - getFirstVisiblePosition());
                mCurrentView = view;
                break;

            case MotionEvent.ACTION_MOVE:
                xMove = x;
                yMove = y;
                int dx = xMove - xDown;
                int dy = yMove - yDown;

                // 判断是否从右到左滑动，滑动后x小于起始x，x方向触发移动，y方向不能触发移动
                if (xMove < xDown && Math.abs(dx) > mTouchSlop && Math.abs(dy) < mTouchSlop)
                    isSliding = true;       // 恩，确定滑动状态
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (isSliding) {
            switch (action){
                case MotionEvent.ACTION_MOVE:
                    int[] location = new int[2];
                    mCurrentView.getLocationOnScreen(location);
                    // 设置pop的动画
                    mPopupWindow.setAnimationStyle(R.style.popwindow_delete_btn_anim_style);
                    mPopupWindow.update();
                    mPopupWindow.showAtLocation(mCurrentView, Gravity.LEFT | Gravity.TOP,
                            location[0] + mCurrentView.getWidth(),
                            location[1] + mCurrentView.getHeight() / 2 - mPopWindowHeight / 2);

                    mDelBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null){
                                mListener.clickHappend(mCurrentPos);
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                    break;

                case MotionEvent.ACTION_UP:
                    isSliding = false;
                    break;
            }
            return true;    // 声明消费该事件
        }
        return super.onTouchEvent(ev);
    }

    private void dismissPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing())
            mPopupWindow.dismiss();
    }

    /*
    * --------------------------------------------
    *  接口回调
    * --------------------------------------------
    */
    public void setDelButtonClickListener(DelButtonClickListener listener){
        mListener = listener;
    }

    interface DelButtonClickListener{
        public void clickHappend(int position);
    }
}
