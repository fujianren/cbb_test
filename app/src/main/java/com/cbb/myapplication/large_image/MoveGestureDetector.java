package com.cbb.myapplication.large_image;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * @author chenbb
 * @create 2017/12/4
 * @desc    仿写系统的手势处理ScaleGestureDetector
 */

public class MoveGestureDetector extends BaseGestureDetector{
    private static final String TAG = "MoveGestureDetector";

    private PointF mCurrentPointer;
    private PointF mPrePointer;
    // 仅仅为了减少创建内存
    private PointF mDeltaPointer = new PointF();
    // 用于记录最终结果，并返回
    private PointF mExtenalPointer = new PointF();

    private OnMoveGesturListener mListener;
    private MotionEvent mPreMotionEvent;

    public MoveGestureDetector(Context context, OnMoveGesturListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected void handleStartProgressEvent(MotionEvent event) {
        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;   // 多点触控，二进制的与运算

        switch (actionCode){
            case MotionEvent.ACTION_DOWN:
                resetState();       // 防止没有接收到CANCEL or UP
                mPreMotionEvent = MotionEvent.obtain(event);
                updateStateByEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mGestureInProgress = mListener.onMoveBegin(this);
                break;
        }
    }

    @Override
    protected void handleInProgressEvent(MotionEvent event) {
        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;   // 多点触控
        switch (actionCode){
            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);
                boolean update = mListener.onMove(this);
                if (update) {
                    mPreMotionEvent.recycle();
                    mPreMotionEvent = MotionEvent.obtain(event);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mListener.onMoveEnd(this);
                resetState();
                break;

        }
    }

    @Override
    protected void updateStateByEvent(MotionEvent event) {

        final MotionEvent prev = mPreMotionEvent;
        mPrePointer = caculateFocalPointer(prev);
        mCurrentPointer = caculateFocalPointer(event);

        boolean skipThisMoveEvent = prev.getPointerCount() != event.getPointerCount();

        mExtenalPointer.x = skipThisMoveEvent ? 0 : mCurrentPointer.x - mPrePointer.x;
        mExtenalPointer.y = skipThisMoveEvent ? 0 : mCurrentPointer.y - mPrePointer.y;
    }

    /**
     * 根据event计算多指中心
     * @param event
     * @return
     */
    private PointF caculateFocalPointer(MotionEvent event) {
        int count = event.getPointerCount();
        float x = 0, y = 0;
        for (int i = 0; i < count; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= count;
        y /= count;
        return new PointF(x, y);
    }

    public float getMoveX(){
        return mExtenalPointer.x;
    }

    public float getMoveY(){
        return mExtenalPointer.y;
    }


    public interface OnMoveGesturListener{
        public boolean onMoveBegin(MoveGestureDetector detector);

        public boolean onMove(MoveGestureDetector detector);

        public void onMoveEnd(MoveGestureDetector detector);
    }

    /**
     * 实现接口的抽象类，可以看做是一个baseAdapter
     */
    public abstract static class SimpleMoveGestureDetector implements OnMoveGesturListener{
        @Override
        public boolean onMoveBegin(MoveGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onMove(MoveGestureDetector detector) {
            return false;
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {
        }
    }
}
