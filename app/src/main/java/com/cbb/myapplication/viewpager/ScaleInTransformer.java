package com.cbb.myapplication.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author chenbb
 * @create 2017/12/8
 * @desc
 */

public class ScaleInTransformer extends BasePageTransformer {

    private static final float DEFAULT_MIN_SCALE = 0.85f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    public ScaleInTransformer() {}

    public ScaleInTransformer(float minScale) {
        this(minScale, NonPageTransformer.INSTANCE);
    }

    public ScaleInTransformer(ViewPager.PageTransformer pageTransformer) {
        this(DEFAULT_MIN_SCALE, pageTransformer);
    }

    public ScaleInTransformer(float minScale, ViewPager.PageTransformer pageTransformer) {
        mMinScale = minScale;
        mPageTransformer = pageTransformer;
    }

    @Override
    protected void pageTransform(View page, float position) {
        int width = page.getWidth();
        int height = page.getHeight();
        page.setPivotX(width);
        page.setPivotY(height);
        if (position < -1){
            page.setPivotX(mMinScale);
            page.setScaleY(mMinScale);
            page.setPivotX(width);
        } else if (position <= 1){
            if (position < 0) {
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(width * (DEFAULT_CENTER - (DEFAULT_CENTER * position)));
            } else {
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setPivotX(width * (DEFAULT_CENTER * (1 - position)));
            }
        } else {
            page.setPivotX(0);
            page.setScaleX(mMinScale);
            page.setScaleY(mMinScale);
        }
    }
}
