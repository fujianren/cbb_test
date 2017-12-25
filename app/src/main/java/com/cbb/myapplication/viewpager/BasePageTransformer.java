package com.cbb.myapplication.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author chenbb
 * @create 2017/12/8
 * @desc
 */

public abstract class BasePageTransformer implements ViewPager.PageTransformer {

    ViewPager.PageTransformer mPageTransformer = NonPageTransformer.INSTANCE;
    public static final float DEFAULT_CENTER = 0.5f;

    @Override
    public void transformPage(View page, float position) {
        if (mPageTransformer != null) {
            mPageTransformer.transformPage(page, position);
        }
        pageTransform(page, position);
    }

    protected abstract void pageTransform(View page, float position);

}
