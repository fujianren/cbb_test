package com.cbb.myapplication.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author chenbb
 * @create 2017/12/8
 * @desc
 */

public class RotateYTransformer extends BasePageTransformer {
    private static final float DEFAULT_MAX_ROTATE = 35f;
    private float mMaxRotate = DEFAULT_MAX_ROTATE;

    public RotateYTransformer() {}

    public RotateYTransformer(float maxRotate) {
        this(maxRotate, NonPageTransformer.INSTANCE);
    }

    public RotateYTransformer( ViewPager.PageTransformer pageTransformer) {
        this(DEFAULT_MAX_ROTATE, pageTransformer);
    }

    public RotateYTransformer(float maxRotate, ViewPager.PageTransformer pageTransformer) {
        mMaxRotate = maxRotate;
        mPageTransformer = pageTransformer;
    }

    @Override
    protected void pageTransform(View page, float position) {
        page.setPivotY(page.getHeight() / 2);
        if (position < -1){
            page.setRotationY(-1 * mMaxRotate);
            page.setPivotX(page.getWidth());
        } else if (position <= 1){
            page.setRotationY(position * mMaxRotate);
            if (position < 0){
                page.setPivotX(page.getWidth() * (DEFAULT_CENTER - DEFAULT_CENTER * position));
                page.setPivotY(page.getHeight());
            } else {
                page.setPivotX(page.getWidth() * DEFAULT_CENTER * (1 - position));
                page.setPivotX(0);
            }
        } else {
            page.setRotationY(1 * mMaxRotate);
            page.setPivotX(0);
        }
    }
}
