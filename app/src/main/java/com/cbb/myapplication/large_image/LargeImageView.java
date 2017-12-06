package com.cbb.myapplication.large_image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author chenbb
 * @create 2017/12/4
 * @desc
 */

public class LargeImageView extends View {
    private static final String TAG = "LargeImageView";
    /* bitmap的区域解码器 */
    private BitmapRegionDecoder mRegionDecoder;
    /* 图片的宽度，高度 */
    private int mImageWidth, mImageHeight;
    /* 绘制的区域,每次使用都要重新读取 */
    private volatile Rect mRect = new Rect();

    /* 配合静态代码块使用的静态变量，bitmap的选项配置 */
    private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();

    static {
        // 将默认的色彩标准ARGB_8888改为RGB_565
        OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    private MoveGestureDetector mDetector;

    public LargeImageView(Context context) {
        this(context, null);
    }

    public LargeImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LargeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int imageWidth = mImageWidth;
        int imageHeight = mImageHeight;

        // 默认直接显示图片的中心区域，可以自行调节
        mRect.left = imageWidth / 2 - width / 2;
        mRect.top = imageHeight / 2 - height / 2;
        mRect.right = mRect.left + width;
        mRect.bottom = mRect.top + height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = mRegionDecoder.decodeRegion(mRect, OPTIONS);
        canvas.drawBitmap(bitmap, 0, 0, null);  // 左上角开始绘制
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    private void init() {
        mDetector = new MoveGestureDetector(getContext(), new MoveGestureDetector.SimpleMoveGestureDetector() {
            @Override
            public boolean onMove(MoveGestureDetector detector) {
                int moveX = (int) detector.getMoveX();
                int moveY = (int) detector.getMoveY();
                if (mImageWidth > getWidth()) {
                    mRect.offset(-moveX, 0);       // 设置区域的偏移量与手势同步
                    checkWidth();
                    invalidate();
                }
                if (mImageHeight > getHeight()) {
                    mRect.offset(0, -moveY);
                    checkHeight();
                    invalidate();
                }
                return true;
            }
        });
    }

    private void checkHeight() {
        Rect rect = mRect;
        int imageWidth = mImageWidth;
        int imageHeight = mImageHeight;
        if (rect.bottom > imageHeight) {
            rect.bottom = imageHeight;
            rect.top = imageHeight - getHeight();
        }

        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = getHeight();
        }
    }

    private void checkWidth() {
        Rect rect = mRect;
        int imageWidth = mImageWidth;
        int imageHeight = mImageHeight;
        if (rect.right > imageWidth){
            rect.right = imageWidth;
            rect.left = imageWidth - getWidth();
        }
        if (rect.left < 0){
            rect.left = 0;
            rect.right = getWidth();
        }
    }


    /**
     * 根据输入流，解读出流中图片的宽高
     * @param inputStream
     */
    public void setInputStream(InputStream inputStream) {
        try {
            mRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);   // true表示软引用，false表示直接拷贝
            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            tmpOptions.inJustDecodeBounds = true;  // true，表示设置为，不生成bitmap，不分配内存直接访问
            BitmapFactory.decodeStream(inputStream, null, tmpOptions);  // 解码输入流，将解码到的数据存在tmpOptions中
            // 解读出tmpOptions中的宽高
            mImageHeight = tmpOptions.outHeight;
            mImageWidth = tmpOptions.outWidth;

            requestLayout();    // 重新onMeasure,onLayout
            invalidate();       // 重新onDraw
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
