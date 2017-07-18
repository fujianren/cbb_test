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

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class PathAnimator extends AbstractPathAnimator {
    private final AtomicInteger mCounter = new AtomicInteger(0);
    private Handler mHandler;

    public PathAnimator(Config config) {
        super(config);
        mHandler = new Handler(Looper.getMainLooper()); // 通知主线程的handler
    }

    /**
     * 重写父类的该方法，开启动画
     * @param child     做动画的子控件
     * @param parent    做动画的控件所在的父控件
     */
    @Override
    public void start(final View child, final ViewGroup parent) {
        parent.addView(child, new ViewGroup.LayoutParams(mConfig.heartWidth, mConfig.heartHeight));
        // 自定义的动画对象
        FloatAnimation anim = new FloatAnimation(createPath(mCounter, parent, 2), randomRotation(), parent, child);
        anim.setDuration(mConfig.animDuration);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeView(child);     // 动画结束，通知主线程移除子控件
                    }
                });
                mCounter.decrementAndGet();         // mcounter依照原子能衰变率下的值
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
                mCounter.incrementAndGet();         // 依照原子能增加率下的值
            }
        });
        anim.setInterpolator(new LinearInterpolator());
        child.startAnimation(anim);
    }

    //************************************** 静态内部类 *************************************/

    /**
     * 自定义的Animation，重写applyTransformation方法
     */
    static class FloatAnimation extends Animation {
        private PathMeasure mPm;
        private View mView;     // 动画执行的view
        private float mDistance;
        private float mRotation;

        public FloatAnimation(Path path, float rotation, View parent, View child) {
            mPm = new PathMeasure(path, false);
            mDistance = mPm.getLength();
            mView = child;
            mRotation = rotation;
            parent.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        /**
         * 重写，动画的具体实现方法
         * @param factor        插值器的内插值，0~1.0
         * @param transformation    动画在某个时间点的变化对象
         */
        @Override
        protected void applyTransformation(float factor, Transformation transformation) {
            Matrix matrix = transformation.getMatrix();     // 获取动画变化的实时矩阵
            mPm.getMatrix(mDistance * factor, matrix, PathMeasure.POSITION_MATRIX_FLAG);    // 路径下是否有矩阵
            mView.setRotation(mRotation * factor);      // view设置旋转度
            float scale = 1F;
            if (3000.0F * factor < 200.0F) {        // 1/15进度前
                scale = scale(factor, 0.0D, 0.06666667014360428D, 0.20000000298023224D, 1.100000023841858D);
            } else if (3000.0F * factor < 300.0F) { // 1/15~1/10进度之间
                scale = scale(factor, 0.06666667014360428D, 0.10000000149011612D, 1.100000023841858D, 1.0D);
            }
            mView.setScaleX(scale);     // view设置缩放比例
            mView.setScaleY(scale);
            transformation.setAlpha(1.0F - factor);
        }
    }

    private static float scale(double a, double b, double c, double d, double e) {
        return (float) ((a - b) / (c - b) * (e - d) + d);
    }
}

