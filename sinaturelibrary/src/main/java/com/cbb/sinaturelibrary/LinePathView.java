package com.cbb.sinaturelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author chenbb
 * @create 2017/7/17
 * @desc    利用一个临时canvas封装bitmap，
 * 将path绘制在临时的canvas上，此时bitmap上也记录了每次path的显示
 * 最后把这个bitmap绘制出来
 */

public class LinePathView extends View {

    private Context mContext;
    /*手写画笔*/
    private final Paint mGesturePaint = new Paint();
    /*提示画笔*/
    private final Paint mTipPaint = new Paint();
    /*路径*/
    private final Path mPath = new Path();

    //************************************** 可setter 或 getter的变量 *************************************/
    /*是否已经签名*/
    private boolean isTouched = false;
    /*线条粗细*/
    private int mPaintWidth;
    /*画笔颜色*/
    private int mPaintColor;
    /*背景色*/
    private int mbgColor;

    TextView mTextView;
    private Bitmap mCacheBitmap;        // 临时的bitmap，和view等大，记录着每次绘制的内容
    private Canvas mCacheCanves;        // 封装bitmap的临时画布，将以往的path绘制其上
    private float mDownX;
    private float mDownY;

    public LinePathView(Context context) {
        this(context, null);
    }

    public LinePathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinePathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mPaintWidth = 10;
        mGesturePaint.setStrokeWidth(mPaintWidth);
        mPaintColor = Color.BLACK;
        mGesturePaint.setColor(mPaintColor);
        mbgColor = Color.parseColor("#F1F1F1");     // 默认背景色
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 创建一个跟view一样大小的bitmap，用来保存签名
        mCacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCacheCanves = new Canvas(mCacheBitmap);
        mCacheCanves.drawColor(mbgColor);
        isTouched = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mCacheBitmap, 0, 0, mGesturePaint);   // 把临时画布上的内容绘制出来
        canvas.drawPath(mPath, mGesturePaint);                  // 把本次的轨迹绘制出来
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mCacheCanves.drawPath(mPath, mGesturePaint);   // 停笔，把本次的轨迹绘制到临时画布上
                mPath.reset();
                break;
        }
        invalidate();   // 每次触摸都要ondraw，而移动中的path是叠加的，所以看起来像轨迹写字
        return true;    //消费触摸事件
    }

    /**
     * 触摸手势移动时，调用该方法，绘制赛贝尔曲线的套路
     *
     * @param event
     */
    private void touchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float disX = Math.abs(x - mDownX);
        float disY = Math.abs(y - mDownY);
        // 若移动时，两点之间的距离大于3，则生成贝塞尔曲线
        if (disX >= 3 || disY >= 3) {
            isTouched = true;
            // 贝塞尔曲线的操作点，为前后两点的中心点
            float cX = (x + mDownX) / 2;
            float cY = (y + mDownY) / 2;
            // 二次贝塞尔
            mPath.quadTo(mDownX, mDownY, cX, cY);
            mDownX = x;
            mDownY = y;
        }
    }

    /**
     * 触摸手势为down时调用该方法
     *
     * @param event
     */
    private void touchDown(MotionEvent event) {
        mPath.reset();
        mDownX = event.getX();
        mDownY = event.getY();
        mPath.moveTo(mDownX, mDownY);   // 路径的起点坐标
    }

    //************************************** 暴露的公共方法 *************************************/

    /**
     * 清除画板
     */
    public void clear() {
        if (mCacheCanves != null) {
            isTouched = false;
            mGesturePaint.setColor(mPaintColor);
            mCacheCanves.drawColor(mbgColor);
            invalidate();
        }
    }

    /**
     * 保存画板
     * @param path  保存的路径
     * @throws IOException
     */
    public void save(String path) throws IOException {
        save(path, false, 0);
    }

    /**
     * 保存画板
     *
     * @param path       保存到的路径
     * @param clearBlank 是否清除边缘空白区域
     * @param blank      要保留的边缘空白距离
     */
    public void save(String path, boolean clearBlank, int blank) throws IOException {
        Bitmap bitmap = mCacheBitmap;
        if (clearBlank) {
            bitmap = clearBlank(bitmap, blank);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] buffer = baos.toByteArray();
        if (buffer != null) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        }
    }

    /**
     * 逐行扫描，清除边界空白
     *
     * @param bitmap
     * @param blank  边距留多少个px
     * @return
     */
    private Bitmap clearBlank(Bitmap bitmap, int blank) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int[] pixs = new int[width];
        int top = 0, left = 0, right = 0, bottom = 0;
        Boolean isStop;
        // 扫描上边距不等于背景颜色的第一个点
        for (int y = 0; y < height; y++) {  // 沿高度遍历像素点
            bitmap.getPixels(pixs, 0, width, 0, y, width, 1);
            isStop = false;
            for (int pix : pixs) {   // 沿宽度遍历像素点
                if (pix != mbgColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) break;
        }
        // 扫描下边距不等于背景颜色的第一个点
        for (int y = height - 1; y >= 0; y--) {
            bitmap.getPixels(pixs, 0, width, 0, y, width, 0);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mbgColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) break;
        }

        pixs = new int[height];
        // 扫描左边距不等于背景颜色的第一个点
        for (int x = 0; x < width; x++) {
            bitmap.getPixels(pixs, 0, 1, x, 0, 1, height);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mbgColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) break;
        }
        // 扫描右边距
        for (int x = width - 1; x > 0; x--) {
            bitmap.getPixels(pixs, 0, 1, x, 0, 1, height);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mbgColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) break;
        }
        if (blank < 0) blank = 0;
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > width - 1 ? width - 1 : right + blank;
        bottom = bottom + blank > height - 1 ? height - 1 : bottom + blank;

        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top);
    }
    //************************************** getter & setter *************************************/
    public boolean isTouched() {
        return isTouched;
    }

    public void setTouched(boolean touched) {
        isTouched = touched;
    }

    public int getPaintWidth() {
        return mPaintWidth;
    }

    public void setPaintWidth(int paintWidth) {
        paintWidth = paintWidth > 0 ? paintWidth : 10; // 默认为10
        this.mPaintWidth = paintWidth;
        mGesturePaint.setStrokeWidth(mPaintWidth);
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public void setPaintColor(@ColorInt int paintColor) {
        this.mPaintColor = paintColor;
        mGesturePaint.setColor(paintColor);
    }


    public int getMbgColor() {
        return mbgColor;
    }

    public void setMbgColor(@ColorInt int mbgColor) {
        this.mbgColor = mbgColor;
    }

    /*获取画板的bitmap*/
    public Bitmap getBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        setDrawingCacheEnabled(false);
        return bitmap;
    }

    void drawText(int color){
        mTipPaint.setColor(getResources().getColor(color));
        mTipPaint.setTextSize(60);
        mTipPaint.setAntiAlias(true);
        float x = (getWidth() - getFontlength(mTipPaint, "签名区域")) / 2;
        float y = (getHeight() - getFontHeight(mTipPaint)) / 2 + getFontleading(mTipPaint);
        mCacheCanves.drawText("签名区域", x, y, mTipPaint);
    }

    /**
     * 返回指定笔和指定字符串的长度
     * @param paint
     * @param string
     * @return
     */
    public static float getFontlength(Paint paint, String string) {
        return paint.measureText(string);
    }

    /**
     * 返回指定笔的文字高度
     * @param paint
     * @return
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    /**
     * 返回指定笔，离文字顶部的基准距离
     * @param paint
     * @return
     */
    public static float getFontleading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public void setTextView(TextView textView) {
        this.mTextView = textView;
    }


    public Bitmap getCacheBitmap() {
        return mCacheBitmap;
    }
}
