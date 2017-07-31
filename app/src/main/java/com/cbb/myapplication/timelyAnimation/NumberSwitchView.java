package com.jiahuan.timelyanimation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NumberSwitchView extends View {
    private static final String TAG = "NumberSwitchView";
    //
    private int WIDTH_PRE = 120;   // px  110
    private int HEIGHT_PRE = 180;  // px  170
    //
    private int numberWrapSpace = 1; //dp
    private int numberAnimationDuration = 500; // ms
    private int numberColor = 0XFF1A2634;
    private int numberBGColor = 0xFFABCDEF;
    private float numberStrokeWidth = 4; // px
    //
    private float widthRatio = 1;       // 数字在画布中所占的比例
    private float heightRatio = 1;
    private Paint mPaint;
    private NumberSwitchAnimation switchAnimation;

    private List<float[]> numbers = new ArrayList<float[]>();
    private float[] currentNumberPoints;

    public NumberSwitchView(Context context) {
        this(context, null);
    }

    public NumberSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public void setChangeNumber(int WIDTH_PRE, int HEIGHT_PRE) {
        this.WIDTH_PRE = WIDTH_PRE;
        this.HEIGHT_PRE = HEIGHT_PRE;

    }

    public NumberSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 转换为标准dp
        numberWrapSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, numberWrapSpace, context.getResources().getDisplayMetrics());
        // 对应style下的属性集
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberSwitchView);
        // 获取对应的属性值
        numberAnimationDuration = typedArray.getInt(R.styleable.NumberSwitchView_ta_number_animation_duration, numberAnimationDuration);
        numberBGColor = typedArray.getColor(R.styleable.NumberSwitchView_ta_number_bg_color, numberBGColor);
        numberColor = typedArray.getColor(R.styleable.NumberSwitchView_ta_number_color, numberColor);
        numberStrokeWidth = typedArray.getDimension(R.styleable.NumberSwitchView_ta_number_stroke_width, numberStrokeWidth);
        Log.i(TAG, "numberAnimationDuration = " + numberAnimationDuration);
        Log.i(TAG, "numberBGColor = " + numberBGColor + "");
        Log.i(TAG, "numberColor = " + numberColor + "");
        Log.i(TAG, "numberStrokeWidth = " + numberStrokeWidth);
        typedArray.recycle();
        initialize();
    }

    /**初始化*/
    private void initialize() {
        Log.i(TAG, "init");
        setData();
        // 画笔初始化设置
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(numberColor);
        mPaint.setStrokeWidth(numberStrokeWidth);
        mPaint.setPathEffect(new CornerPathEffect(200));    // 转角处的圆滑程度。
        // 部署动画
        Interpolator interc = AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.accelerate_decelerate);
        switchAnimation = new NumberSwitchAnimation();
        switchAnimation.setDuration(numberAnimationDuration);
        switchAnimation.setInterpolator(interc);
        switchAnimation.setFillAfter(true);
        //

    }

    /**
     * 点集合
     */
    private void setData() {
        numbers.add(NumberData.NUMBER_0);
        numbers.add(NumberData.NUMBER_1);
        numbers.add(NumberData.NUMBER_2);
        numbers.add(NumberData.NUMBER_3);
        numbers.add(NumberData.NUMBER_4);
        numbers.add(NumberData.NUMBER_5);
        numbers.add(NumberData.NUMBER_6);
        numbers.add(NumberData.NUMBER_7);
        numbers.add(NumberData.NUMBER_8);
        numbers.add(NumberData.NUMBER_9);
        //
        currentNumberPoints = Arrays.copyOf(NumberData.NUMBER_0, NumberData.NUMBER_0.length);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();                                                      // 保存
        canvas.translate(numberStrokeWidth / 2, numberStrokeWidth / 2);     // 画布平移
        canvas.scale(this.widthRatio, this.heightRatio);                    // 画布缩放，实现画不同大小的数字
        canvas.drawColor(numberBGColor);
        canvas.drawPath(makeNumberPath(currentNumberPoints), mPaint);       // 写上数字

        canvas.restore();                                                   // 画布恢复到保存时的状态
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.widthRatio = (width + this.numberStrokeWidth) / WIDTH_PRE;
        this.heightRatio = (height + this.numberStrokeWidth) / HEIGHT_PRE;
        setMeasuredDimension((int) (width + this.numberStrokeWidth), (int) (height + this.numberStrokeWidth));
        Log.i(TAG, "width = " + width + ", height = " + height);
    }


    // 0 - 9
    public void animateTo(int number) {
        synchronized (this) {
            if (number >= 0 && number <= 9) {
                Log.i(TAG, numbers.get(number).length + "");
                switchAnimation.setData(currentNumberPoints, numbers.get(number));
                startAnimation(switchAnimation);
            } else {

            }
        }
    }

    /**
     * 继承Animation的自定义动画，实现了数字变化时的过渡效果
     * 本处是直接写成内部类
     */
    class NumberSwitchAnimation extends Animation {
        private float[] to;
        private float[] from;

        /**
         * 构造函数
         * @param from  变化前的数字的点数组
         * @param to    最终显示数字的点数组
         */
        public void setData(float[] from, float[] to) {
            this.from = Arrays.copyOf(from, from.length);
            this.to = to;
        }

        /**
         * 老套路，继承Animation必须继承该方法
         * @param interpolatedTime  转换瞬间
         * @param t   转换类型对象
         */
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime != 1) {        // 动画还没到结束时间
                for (int i = 0, N = from.length; i < N; i++) {      // 遍历每一个执行动画的点，就算出当前时间片刻的运动位置
                    currentNumberPoints[i] = from[i] + (to[i] - from[i]) * interpolatedTime;
                }
                invalidate();                   // 回调，重新绘制
            }
        }
    }

    /**
     * 根据点集，画路径
     * @param points
     * @return
     */
    private Path makeNumberPath(float[] points) {
        Path p = new Path();
        p.moveTo(points[0], points[1]);
        for (int i = 2, N = points.length; i < N; i += 2) {
            p.lineTo(points[i], points[i + 1]);
        }
        return p;
    }

    /**
     * 回调，重新绘制view
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();  // 主线程的轮询，用该方法绘制
        } else {
            postInvalidate();   // 非主线程，用该方法绘制
        }
    }

    // out api
    public int getNumberAnimationDuration() {
        return numberAnimationDuration;
    }

    public void setNumberAnimationDuration(int numberAnimationDuration) {
        this.numberAnimationDuration = numberAnimationDuration;
        switchAnimation.setDuration(numberAnimationDuration);
    }

    public int getNumberColor() {
        return numberColor;
    }

    public void setNumberColor(int numberColor) {
        this.numberColor = numberColor;
        mPaint.setColor(numberColor);
        invalidateView();
    }

    public void setNumberBGColor(int numberBGColor) {
        this.numberBGColor = numberBGColor;
        invalidateView();
    }

    public int getNumberBGColor() {
        return numberBGColor;
    }


}
