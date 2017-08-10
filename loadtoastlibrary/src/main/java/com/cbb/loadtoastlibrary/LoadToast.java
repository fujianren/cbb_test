package com.cbb.loadtoastlibrary;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * @author chenbb
 * @create 2017/7/18
 * @desc   ToastView的操作类
 */

public class LoadToast {

    private String mText = "";
    private LoadToastView mView;
    private ViewGroup mParentView;
    private int mTranslationY = 0;
    private boolean mShowCalled = false;        // Toast是否被召唤
    private boolean mToastCanceled = false;     // Toast是否显示结束
    private boolean mInflated = false;          // Toast是否填充就绪

    public LoadToast(Context context){
        mView = new LoadToastView(context);
        mParentView = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);   // 中心内容的view
        // 添加ToastView
        mParentView.addView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHelper.setAlpha(mView, 0);  // 9老的viewhelper
        // 发送消息序列
        mParentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 移动ToastView使其居中在屏幕
                ViewHelper.setTranslationX(mView, (mParentView.getWidth() - mView.getWidth()) / 2);
                ViewHelper.setTranslationY(mView, -mView.getHeight() + mTranslationY);
                mInflated = true;               // 填充就绪
                if(!mToastCanceled && mShowCalled) show();      // 若被召唤且还没到显示结束，show
            }
        },1);
        // 监听视图树，当整体布局发生改变时，就检查toastView覆盖的层次，必须是最上层的
        mParentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                checkZPosition();
            }
        });
    }

    /**
     * 设置Toast在y轴上的偏移量
     * @param pixels
     * @return
     */
    public LoadToast setTranslationY(int pixels){
        mTranslationY = pixels;
        return this;
    }

    public LoadToast setText(String message){
        mText = message;
        mView.setText(mText);
        return this;
    }

    public LoadToast setTextColor(int color){
        mView.setTextColor(color);
        return this;
    }

    public LoadToast setBackgroundColor(int color){
        mView.setBackgroundColor(color);
        return this;
    }

    public LoadToast setProgressColor(int color){
        mView.setProgressColor(color);
        return this;
    }

    /**
     * Toast暴露的公共方法，实现弹出Toast
     * @return
     */
    public LoadToast show(){
        if(!mInflated){     // 未填充好，直接设置已经被召唤，返回
            mShowCalled = true;
            return this;
        }
        mView.show();       // 已填充好，toastView准备
        ViewHelper.setTranslationX(mView, (mParentView.getWidth() - mView.getWidth()) / 2); // 移动居中
        ViewHelper.setAlpha(mView, 0f);         // 透明
        ViewHelper.setTranslationY(mView, -mView.getHeight() + mTranslationY);
        //mView.setVisibility(View.VISIBLE);
        // 属性动画，透明度，y轴平移
        ViewPropertyAnimator.animate(mView).alpha(1f).translationY(25 + mTranslationY)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(300).setStartDelay(0).start();
        return this;
    }

    public void success(){
        if(!mInflated){         // 未填充，直接设置toast结束，结束
            mToastCanceled = true;
            return;
        }
        mView.success();
        slideUp();
    }

    public void error(){
        if(!mInflated){
            mToastCanceled = true;
            return;
        }
        mView.error();
        slideUp();
    }

    private void checkZPosition(){
        int pos = mParentView.indexOfChild(mView);
        int count = mParentView.getChildCount();
        if(pos != count-1){         // 若toastView不是最后一个child
            mParentView.removeView(mView);      // 移除toastView
            mParentView.requestLayout();        // 重新onMeasure和onLayout
            mParentView.addView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));  // 再次把toastview放到容器
        }
    }

    /*成功或失败的drawable，做动画消失*/
    private void slideUp(){
        ViewPropertyAnimator.animate(mView).setStartDelay(1000).alpha(0f)
                .translationY(-mView.getHeight() + mTranslationY)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(300).start();
    }
}
