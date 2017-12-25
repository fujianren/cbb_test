package com.cbb.myapplication.water_wave;

import android.graphics.Paint;

/**
 * @author chenbb
 * @create 2017/12/7
 * @desc
 */

class Wave {
    /* 透明度 */
    public int alpha;
    /* 波痕宽度 */
    public int width;
    /* 水波半径 */
    public int radius;
    /* 对应的画笔 */
    public Paint paint;
    /* 按下时的坐标，即中心点 */
    public int xDown;
    public int yDown;
}
