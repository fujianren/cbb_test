package com.cbb.loadtoastlibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * @author chenbb
 * @create 2017/7/18
 * @desc   自定义的加载吐司
 */

public class LoadToastView extends View{

    /*画笔对象*/
    private Paint textPaint = new Paint();
    private Paint bgPaint = new Paint();
    private Paint iconBgPaint = new Paint();
    private Paint loaderPaint = new Paint();
    private Paint successPaint = new Paint();
    private Paint errorPaint = new Paint();
    /*dp值*/
    private int MAX_TEXT_WIDTH  = 100;  // 最大的文本宽度
    private int BASE_TEXT_SIZE  = 20;   // 基本文本size
    private int IMAGE_WIDTH     = 40;   // image的宽度
    private int TOAST_HEIGHT    = 48;   // toast的高度
    private int MARQUE_STEP = 1;        //
    // 宽度的缩放比,做加载的效果时是不会改变的，保持为0f，在结束动画是会渐渐增长到2，该值越大，toast宽度越小，增长到1时，toast宽度固定为圆大小
    private float WIDTH_SCALE = 0f;
    private Rect iconBounds;
    private Rect mTextBounds = new Rect();  // 文本所占的区域
    private RectF spinnerRect = new RectF();

    private ValueAnimator mAnimator;
    private ValueAnimator cmp;

    private boolean success = true;         // 是否加载成功
    private boolean outOfBounds = false;    // 是否文本越界

    private String mText = "";              // 文本内容
    private long prevUpdate = 0;
    private Path toastPath = new Path();    // view绘制的path
    private AccelerateDecelerateInterpolator easeinterpol = new AccelerateDecelerateInterpolator();
    private final Drawable mCompleteIcon;   // 加载成功的drawable
    private final Drawable mFailedIcon;     // 加载失败的drawable

    //************************************** 构造函数 & 初始化 *************************************/
    @SuppressWarnings("WrongConstant")
    public LoadToastView(Context context) {
        super(context);

        textPaint.setTextSize(15);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);

        bgPaint.setColor(Color.WHITE);
        bgPaint.setAntiAlias(true);

        iconBgPaint.setColor(Color.BLUE);
        iconBgPaint.setAntiAlias(true);

        loaderPaint.setStrokeWidth(dpToPx(4));
        loaderPaint.setAntiAlias(true);
        loaderPaint.setColor(fetchPrimaryColor());
        loaderPaint.setStyle(Paint.Style.STROKE);

        successPaint.setColor(getResources().getColor(R.color.color_success));
        successPaint.setAntiAlias(true);

        errorPaint.setColor(getResources().getColor(R.color.color_error));
        errorPaint.setAntiAlias(true);

        MAX_TEXT_WIDTH = dpToPx(MAX_TEXT_WIDTH);
        BASE_TEXT_SIZE = dpToPx(BASE_TEXT_SIZE);
        IMAGE_WIDTH = dpToPx(IMAGE_WIDTH);
        TOAST_HEIGHT = dpToPx(TOAST_HEIGHT);
        MARQUE_STEP = dpToPx(MARQUE_STEP);

        // 图片所占的区域
        int padding = (TOAST_HEIGHT - IMAGE_WIDTH) / 2;     // 图片在toast中的padding
        iconBounds = new Rect(TOAST_HEIGHT + MAX_TEXT_WIDTH - padding,
                padding,
                TOAST_HEIGHT + MAX_TEXT_WIDTH - padding + IMAGE_WIDTH,
                IMAGE_WIDTH + padding);

        mCompleteIcon = getResources().getDrawable(R.drawable.ic_navigation_check);
        mCompleteIcon.setBounds(iconBounds);

        mFailedIcon = getResources().getDrawable(R.drawable.ic_error);
        mFailedIcon.setBounds(iconBounds);

        // 在构造函数中直接开启一个循环播放的属性动画，然后通过获取动画过程中属性的变化，来绘制加载的弧
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(6000);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 只要该ToastView对象存在，则动画会一直执行，不断调用重新绘制ToastView，达到无限加载的动画效果
                postInvalidate();
            }
        });
        mAnimator.setRepeatMode(ValueAnimator.INFINITE);
        mAnimator.setRepeatCount(999999);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();

        calculateBounds();
    }
    /** 获取基本颜色 */
    private int fetchPrimaryColor() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){  // 版本21↑
            // 动态类型化数据值容器，支持Resource
            TypedValue typedValue = new TypedValue();
            // 获取属性集合，参数1：风格id，参数2：该风格下包含的各属性id的数组
            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
            int color = a.getColor(0, 0);   // 通过属性id获取对应的color值

            a.recycle();                    // 记得回收
            return color;
        }
        return Color.rgb(155,155,155);
    }

    /** 计算界限 */
    private void calculateBounds() {
        outOfBounds = false;        // 未越界
        prevUpdate = 0;

        textPaint.setTextSize(BASE_TEXT_SIZE);
        textPaint.getTextBounds(mText, 0, mText.length(), mTextBounds); // 文本框要占据的界限大小
        if(mTextBounds.width() > MAX_TEXT_WIDTH){   // 文本框宽度》最大标准
            int textSize = BASE_TEXT_SIZE;          // 调整字体大小依次缩小，直至符合条件
            while(textSize > dpToPx(13) && mTextBounds.width() > MAX_TEXT_WIDTH){
                textSize--;
                //Log.d("bounds", "width " + mTextBounds.width() + " max " + MAX_TEXT_WIDTH);
                textPaint.setTextSize(textSize);
                textPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
            }
            if(mTextBounds.width() > MAX_TEXT_WIDTH){       // 调整之后仍越界，则表示越界
                outOfBounds = true;
                /**
                 float keep = (float)MAX_TEXT_WIDTH / (float)mTextBounds.width();
                 int charcount = (int)(mText.length() * keep);
                 //Log.d("calc", "keep " + charcount + " per " + keep + " len " + mText.length());
                 mText = mText.substring(0, charcount);
                 textPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
                 **/
            }
        }
    }

    //************************************** setter & getter *************************************/
    public void setTextColor(int color){
        textPaint.setColor(color);
    }

    public void setBackgroundColor(int color){
        bgPaint.setColor(color);
        iconBgPaint.setColor(color);
    }

    public void setProgressColor(int color){
        loaderPaint.setColor(color);
    }

    /**
     * 其实只要ToastView对象一创建就会自动做加载动画
     * 该方法只是还原初始值，并实现结束动画和加载动画之间可以无bug互换
     */
    public void show(){
        WIDTH_SCALE = 0f;           //  设置为起始状态，因为成功失败的动画会改变该值，不还原的话无法从结束状态回到加载状态
        if(cmp != null) cmp.removeAllUpdateListeners();     // 若已经有cmp动画，则会出现第一次的加载动画无效，而继续弹出cmp动画
    }

    public void success(){
        success = true;
        done();
    }

    public void error(){
        success = false;
        done();
    }

    /** 带动画效果的重绘 */
    private void done() {
        // 改变WIDTH_SCALE的属性动画
        cmp = ValueAnimator.ofFloat(0,1);
        cmp.setDuration(600);
        cmp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                WIDTH_SCALE = 2f*(valueAnimator.getAnimatedFraction()); // 获取当前动画片段值
                //Log.d("lt", "ws " + WIDTH_SCALE);
                postInvalidate();                           // 通知主线程自身重绘
            }
        });
        cmp.setInterpolator(new DecelerateInterpolator());
        cmp.start();
    }


    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {      // 精准
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = IMAGE_WIDTH + MAX_TEXT_WIDTH + TOAST_HEIGHT;
            if (specMode == MeasureSpec.AT_MOST) {  // 至大
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = TOAST_HEIGHT;
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        float ws = Math.max(1f - WIDTH_SCALE, 0f);
        // If there is nothing to display, just draw a circle
        if(mText.length() == 0) ws = 0;     // 若没有文本，宽度的不缩放

        float translateLoad = (1f-ws)*(IMAGE_WIDTH+MAX_TEXT_WIDTH);
        float leftMargin = translateLoad/2;         // 左间距
        float textOpactity = Math.max(0, ws * 10f - 9f);
        textPaint.setAlpha((int)(textOpactity * 255));
        spinnerRect.set(iconBounds.left + dpToPx(4) - translateLoad/2, iconBounds.top + dpToPx(4),
                iconBounds.right - dpToPx(4) - translateLoad/2, iconBounds.bottom - dpToPx(4));

        int circleOffset = (int)(TOAST_HEIGHT*2*(Math.sqrt(2)-1)/3);
        int th = TOAST_HEIGHT;
        int pd = (TOAST_HEIGHT - IMAGE_WIDTH)/2;
        int iconoffset = (int)(IMAGE_WIDTH*2*(Math.sqrt(2)-1)/3);
        int iw = IMAGE_WIDTH;

        toastPath.reset();
        toastPath.moveTo(leftMargin + th / 2, 0);
        toastPath.rLineTo(ws*(IMAGE_WIDTH + MAX_TEXT_WIDTH), 0);
        toastPath.rCubicTo(circleOffset, 0, th / 2, th / 2 - circleOffset, th / 2, th / 2);

        toastPath.rLineTo(-pd, 0);
        toastPath.rCubicTo(0, -iconoffset, -iw / 2 + iconoffset, -iw / 2, -iw / 2, -iw / 2);
        toastPath.rCubicTo(-iconoffset, 0, -iw / 2, iw / 2 - iconoffset, -iw / 2, iw / 2);
        toastPath.rCubicTo(0, iconoffset, iw / 2 - iconoffset, iw / 2, iw / 2, iw / 2);
        toastPath.rCubicTo(iconoffset, 0, iw / 2, -iw / 2 + iconoffset, iw / 2, -iw / 2);
        toastPath.rLineTo(pd, 0);

        toastPath.rCubicTo(0, circleOffset, circleOffset - th / 2, th / 2, -th / 2, th / 2);
        toastPath.rLineTo(ws*(-IMAGE_WIDTH - MAX_TEXT_WIDTH), 0);
        toastPath.rCubicTo(-circleOffset, 0, -th / 2, -th / 2 + circleOffset, -th / 2, -th / 2);
        toastPath.rCubicTo(0, -circleOffset, -circleOffset + th / 2, -th / 2, th / 2, -th / 2);

        c.drawCircle(spinnerRect.centerX(), spinnerRect.centerY(), iconBounds.height() / 1.9f, bgPaint);
        //loadicon.draw(c);
        c.drawPath(toastPath, bgPaint);
        // 根据属性动画的当前片段
        float prog = mAnimator.getAnimatedFraction() * 6.0f;
        float progrot = prog % 2.0f;            // 弧起点的变化系数
        float proglength = easeinterpol.getInterpolation(prog % 3f / 3f)*3f - .75f;     // 获取增减插值器对应起点系数的插值
        if(proglength > .75f){
            proglength = .75f - (prog % 3f - 1.5f);
            progrot += (prog % 3f - 1.5f)/1.5f * 2f;
        }
        //Log.d("spin", "rot " + progrot + " len " + proglength);

        toastPath.reset();

        if(mText.length() == 0){
            ws = Math.max(1f - WIDTH_SCALE, 0f);
        }
        // 弧形路径
        toastPath.arcTo(spinnerRect, 180 * progrot, Math.min((200 / .75f) * proglength + 1 + 560*(1f-ws),359.9999f));
        loaderPaint.setAlpha((int)(255 * ws));
        c.drawPath(toastPath, loaderPaint);

        if(WIDTH_SCALE > 1f){
            Drawable icon = (success) ? mCompleteIcon : mFailedIcon;
            float circleProg = WIDTH_SCALE - 1f;
            textPaint.setAlpha((int)(128 * circleProg + 127));
            int paddingicon = (int)((1f-(.25f + (.75f * circleProg))) * TOAST_HEIGHT/2);
            int completeoff = (int)((1f-circleProg) * TOAST_HEIGHT/8);
            icon.setBounds((int)spinnerRect.left + paddingicon, (int)spinnerRect.top + paddingicon + completeoff, (int)spinnerRect.right - paddingicon, (int)spinnerRect.bottom - paddingicon + completeoff);
            c.drawCircle(leftMargin + TOAST_HEIGHT/2, (1f-circleProg) * TOAST_HEIGHT/8 + TOAST_HEIGHT/2,
                    (.25f + (.75f * circleProg)) * TOAST_HEIGHT/2, (success) ? successPaint : errorPaint);
            c.save();
            c.rotate(90*(1f-circleProg), leftMargin + TOAST_HEIGHT/2, TOAST_HEIGHT/2);
            icon.draw(c);
            c.restore();

            prevUpdate = 0;
            return;
        }

        int yPos = (int) ((th / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

        if(outOfBounds){
            float shift = 0;
            if(prevUpdate == 0){
                prevUpdate = System.currentTimeMillis();
            }else{
                shift = ((float)(System.currentTimeMillis() - prevUpdate) / 16f) * MARQUE_STEP;

                if(shift - MAX_TEXT_WIDTH > mTextBounds.width()){
                    prevUpdate = 0;
                }
            }
            c.clipRect(th / 2, 0, th/2 + MAX_TEXT_WIDTH, TOAST_HEIGHT);
            c.drawText(mText, th / 2 - shift + MAX_TEXT_WIDTH, yPos, textPaint);
        }else{
            c.drawText(mText, 0, mText.length(), th / 2 + (MAX_TEXT_WIDTH - mTextBounds.width()) / 2, yPos, textPaint);
        }
    }

    public void setText(String text) {
        mText = text;
        calculateBounds();
    }
}
