package com.cbb.myapplication.utils;

import android.content.Context;

/**
 * @author chenbb
 * @create 2017/8/17
 * @desc
 */

public class CommUtil {
    /**
     * 获取屏幕密度
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        // 获取屏幕密度
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
