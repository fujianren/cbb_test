package com.cbb.myapplication.large_image;


import android.content.Context;
import android.view.MotionEvent;


/**
 * @author chenbb
 * @create 2017/12/4
 * @desc
 */

public abstract class BaseGestureDetector {

    private static final String TAG = "BaseGestureDetector";

    /* 手势是否正在进行 */
    protected boolean mGestureInProgress;
    protected MotionEvent mPreMotionEvent;
    protected MotionEvent mCurrentMotionEvent;
    protected Context mContext;

    public BaseGestureDetector(Context context){
        mContext = context;
    }

    public boolean onTouchEvent(MotionEvent event){
        if (!mGestureInProgress){
            handleStartProgressEvent(event);
        } else {
            handleInProgressEvent(event);
        }
        return true;
    }

    protected void resetState(){
        if (mPreMotionEvent != null){
            mPreMotionEvent.recycle();
            mPreMotionEvent = null;
        }
        if (mCurrentMotionEvent != null){
            mCurrentMotionEvent.recycle();
            mCurrentMotionEvent = null;
        }
        mGestureInProgress = false;
    }

    protected abstract void handleStartProgressEvent(MotionEvent event);

    protected abstract void handleInProgressEvent(MotionEvent event);

    protected abstract void updateStateByEvent(MotionEvent event);
}
