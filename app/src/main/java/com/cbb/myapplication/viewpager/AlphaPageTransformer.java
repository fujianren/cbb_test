package com.cbb.myapplication.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author chenbb
 * @create 2017/12/8
 * @desc
 */

public class AlphaPageTransformer extends BasePageTransformer {

    private static final float DEFAULT_MIN_ALPHA = 0.5f;
    private float mMinAlpha = DEFAULT_MIN_ALPHA;

    public AlphaPageTransformer(){}

    public AlphaPageTransformer(float minAlpha){
        this(minAlpha, NonPageTransformer.INSTANCE);
    }

    public AlphaPageTransformer(ViewPager.PageTransformer pageTransformer){
        this(DEFAULT_MIN_ALPHA, pageTransformer);
    }

    public AlphaPageTransformer(float minAlpha, ViewPager.PageTransformer pageTransformer){
        mMinAlpha = minAlpha;
        mPageTransformer = pageTransformer;
    }


    @Override
    protected void pageTransform(View page, float position) {
        page.setScaleX(0.999f);
        if (position < -1) page.setAlpha(mMinAlpha);

        else if (position <= 1){
            if (position < 0){
                float factor = mMinAlpha + (1 - mMinAlpha) * (1 + position);
                page.setAlpha(factor);
            } else {
                float factor = mMinAlpha + (1 - mMinAlpha) * (1 - position);
                page.setAlpha(factor);
            }
        } else {
            page.setAlpha(mMinAlpha);
        }
    }
}
