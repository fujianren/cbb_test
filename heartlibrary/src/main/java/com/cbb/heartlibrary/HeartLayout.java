/*
 * Copyright (C) 2015 tyrantgit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cbb.heartlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 自定义的父容器
 * 对外提供的方法
 * clearAnimation{@link #clearAnimation()}： 清除动画，并移除容器内的所有子控件
 * addHeart{@link #addHeart(int)}, {@link #addHeart(int, int, int)}：添加heartView到容器内
 */
public class HeartLayout extends RelativeLayout {

    private AbstractPathAnimator mAnimator;

    //************************************** 构造函数及初始化 *************************************/
    public HeartLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public HeartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HeartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0);

        mAnimator = new PathAnimator(AbstractPathAnimator.Config.fromTypeArray(a));

        a.recycle();
    }

    //************************************** getter & setter *************************************/
    public AbstractPathAnimator getAnimator() {
        return mAnimator;
    }

    public void setAnimator(AbstractPathAnimator animator) {
        clearAnimation();
        mAnimator = animator;
    }

    /**
     * 移除每个子控件上动画，然后移除每个子控件
     */
    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

    /**
     * 添加对应颜色的子控件heartView，并开始动画
     * @param color 颜色
     */
    public void addHeart(@ColorInt int color) {
        HeartView heartView = new HeartView(getContext());
        heartView.setColor(color);
        mAnimator.start(heartView, this);
    }

    /**
     * 重载的方法，添加对应颜色的子控件heartView，并开始动画
     * @param color     子控件heartView的颜色
     * @param heartResId    子控件对应drawable的资源id
     * @param heartBorderResId      子控件对应drawable的资源id
     */
    public void addHeart(@ColorInt int color, @DrawableRes int heartResId, @DrawableRes int heartBorderResId) {
        HeartView heartView = new HeartView(getContext());
        heartView.setColorAndDrawables(color, heartResId, heartBorderResId);
        mAnimator.start(heartView, this);
    }

}
