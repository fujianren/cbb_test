package com.cbb.sidemenu.interfaces;

import android.graphics.Bitmap;

/**
 *
 */
public interface ScreenShotable {
    // 截屏的操作
    public void takeScreenShot();
    // 获取截屏图对应的bitmap
    public Bitmap getBitmap();
}
