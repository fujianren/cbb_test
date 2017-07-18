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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class AbstractPathAnimator {
    private final Random mRandom;
    protected final Config mConfig;


    public AbstractPathAnimator(Config config) {
        mConfig = config;
        mRandom = new Random();
    }

    /**
     * 返回一个随机的旋转值
     * @return
     */
    public float randomRotation() {
        return mRandom.nextFloat() * 28.6F - 14.3F;
    }

    /**
     * 创建一条随机的塞贝尔曲线
     * @param counter
     * @param view
     * @param factor
     * @return
     */
    public Path createPath(AtomicInteger counter, View view, int factor) {
        Random r = mRandom;
        int x = r.nextInt(mConfig.xRand);
        int x2 = r.nextInt(mConfig.xRand);
        int y = view.getHeight() - mConfig.initY;
        int y2 = counter.intValue() * 15 + mConfig.animLength * factor + r.nextInt(mConfig.animLengthRand);
        factor = y2 / mConfig.bezierFactor;
        x = mConfig.xPointFactor + x;
        x2 = mConfig.xPointFactor + x2;
        int y3 = y - y2;
        y2 = y - y2 / 2;
        Path p = new Path();
        p.moveTo(mConfig.initX, y);
        p.cubicTo(mConfig.initX, y - factor, x, y2 + factor, x, y2);       // 双控制点的赛贝尔曲线
        p.moveTo(x, y2);
        p.cubicTo(x, y2 - factor, x2, y3 + factor, x2, y3);
        return p;
    }

    /**
     * 子类需要重写的开始方法
     * @param child     做动画的子控件
     * @param parent    做动画的控件所在的父控件
     */
    public abstract void start(View child, ViewGroup parent);

    /**
     * 用Config类存储HeartLayout的属性集
     */
    public static class Config {
        public int initX;
        public int initY;
        public int xRand;
        public int animLengthRand;
        public int bezierFactor;
        public int xPointFactor;
        public int animLength;
        public int heartWidth;
        public int heartHeight;
        public int animDuration;

        static Config fromTypeArray(TypedArray typedArray) {
            Config config = new Config();
            Resources res = typedArray.getResources();
            config.initX = (int) typedArray.getDimension(R.styleable.HeartLayout_initX,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_init_x));
            config.initY = (int) typedArray.getDimension(R.styleable.HeartLayout_initY,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_init_y));
            config.xRand = (int) typedArray.getDimension(R.styleable.HeartLayout_xRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_bezier_x_rand));
            config.animLength = (int) typedArray.getDimension(R.styleable.HeartLayout_animLength,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length));
            config.animLengthRand = (int) typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length_rand));
            config.bezierFactor = typedArray.getInteger(R.styleable.HeartLayout_bezierFactor,
                    res.getInteger(R.integer.heart_anim_bezier_factor));
            config.xPointFactor = (int) typedArray.getDimension(R.styleable.HeartLayout_xPointFactor,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_x_point_factor));
            config.heartWidth = (int) typedArray.getDimension(R.styleable.HeartLayout_heart_width,
                    res.getDimensionPixelOffset(R.dimen.heart_size_width));
            config.heartHeight = (int) typedArray.getDimension(R.styleable.HeartLayout_heart_height,
                    res.getDimensionPixelOffset(R.dimen.heart_size_height));
            config.animDuration = typedArray.getInteger(R.styleable.HeartLayout_anim_duration,
                    res.getInteger(R.integer.anim_duration));
            return config;
        }


    }


}

