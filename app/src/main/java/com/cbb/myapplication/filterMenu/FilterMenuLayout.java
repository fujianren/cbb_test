package com.cbb.myapplication.filterMenu;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;

import com.cbb.myapplication.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义的菜单容器
 */
public class FilterMenuLayout extends ViewGroup {
    public static final String TAG = "FilterMenuLayout";

    public static final int STATE_COLLAPSE = 0x1;
    public static final int STATE_EXPAND = 0x2;

    public static final int DURATION = 400;
    private static final int DURATION_BETWEEN_ITEM = 50;
    /**
     * menu items position start angle*
     */
    double fromAngle;
    /**
     * menu items position end angle *
     */
    double toAngle;
    Point touchPoint = new Point();
    boolean inChild = false;
    FilterMenu.Item touchedItem;
    boolean isExpand = false;
    /**
     * arc radius when menu is collapsed *
     */
    private int collapsedRadius;        // 属性：收缩后的半径
    /**
     * arc radius when menu is expanded *
     */
    private int expandedRadius;         // 属性：展开后的半径
    private int primaryColor;           // 属性：原生颜色
    /**
     * color of inner circle when menu expanded *
     */
    private int primaryDarkColor;       // 属性：原生颜色深
    /**
     * center of circle
     */
    private Point center;
    private int state = STATE_COLLAPSE;
    private Paint primaryPaint;
    private Paint primaryDarkPaint;
    private OvalOutline outlineProvider;
    /**
     * the expanded circle bounds*
     */
    private Rect menuBounds;
    /**
     * set the circle position, base on its center , the menu will auto align.You should only set two directions at most.
     * 属性：view在父容器中的间距，只能用2个方向确定唯一位置，如选取左、上方向确定时，再使用右、下方向是错误的
     */
    private int centerLeft, centerRight, centerTop, centerBottom;
    /**
     * If true, centers the circle horizontally.
     */
    private boolean centerHorizontal;
    /**
     * If true, centers the circle vertically. *
     */
    private boolean centerVertical;
    /**
     * all intersect points *
     */
    private List<Point> intersectPoints = new ArrayList<>();
    /**
     * expand progress *
     */
    private float expandProgress = 0;
    /**
     * the center drawable
     * TODO: add more drawable
     */
    private FilterMenuDrawable drawable;        // 自定义的drawable
    private ObjectAnimator circleAnimator;
    private ValueAnimator colorAnimator;
    private FilterMenu menu;

    public FilterMenuLayout(Context context) {
        super(context);
        init(context, null);
    }

    public FilterMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FilterMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterMenuLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;     // 屏幕密度
        int defaultCollapsedRadius = (int) (65 / 2.f * density + 0.5);  // 默认的收缩半径
        int defaultExpandedRadius = (int) (65 * 2 * density + 0.5);     // 默认的展开半径
        // 该自定义控件的属性集，并获取对应的属性值
        TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.FilterMenuLayout);
        collapsedRadius = ta.getDimensionPixelSize(R.styleable.FilterMenuLayout_fm_collapsedRadius, defaultCollapsedRadius);
        expandedRadius = ta.getDimensionPixelSize(R.styleable.FilterMenuLayout_fm_expandedRadius, defaultExpandedRadius);
        centerLeft = ta.getDimensionPixelSize(R.styleable.FilterMenuLayout_fm_centerLeft, 0);
        centerRight = ta.getDimensionPixelSize(R.styleable.FilterMenuLayout_fm_centerRight, 0);
        centerTop = ta.getDimensionPixelSize(R.styleable.FilterMenuLayout_fm_centerTop, 0);
        centerBottom = ta.getDimensionPixelSize(R.styleable.FilterMenuLayout_fm_centerBottom, 0);
        centerHorizontal = ta.getBoolean(R.styleable.FilterMenuLayout_fm_centerHorizontal, false);
        centerVertical = ta.getBoolean(R.styleable.FilterMenuLayout_fm_centerVertical, false);

        primaryColor = ta.getColor(R.styleable.FilterMenuLayout_fm_primaryColor, getResources().getColor(android.R.color.holo_blue_bright));
        primaryDarkColor = ta.getColor(R.styleable.FilterMenuLayout_fm_primaryDarkColor, getResources().getColor(android.R.color.holo_blue_dark));
        ta.recycle();

        if (!centerHorizontal) {    // 若不是水平居中，计算centerLeft和centerRight
            // centerLeft与collapsedRadius比较，区较大值作为centerLeft
            centerLeft = centerLeft != 0 && centerLeft < collapsedRadius ? collapsedRadius : centerLeft;
            centerRight = centerRight != 0 && centerRight < collapsedRadius ? collapsedRadius : centerRight;
            // 若左右都未写明dp，默认centerLeft取collapsedRadius的值
            if (centerLeft == 0 && centerRight == 0) {
                centerLeft = collapsedRadius;
            }
        }
        // 若不是垂直居中，计算出centerTop和centerBottom的值
        if (!centerVertical) {
            // centerTop与collapsedRadius比较，取较大值
            centerTop = centerTop != 0 && centerTop < collapsedRadius ? collapsedRadius : centerTop;
            centerBottom = centerBottom != 0 && centerBottom < collapsedRadius ? collapsedRadius : centerBottom;
            if (centerTop == 0 && centerBottom == 0) {
                centerTop = collapsedRadius;
            }
        }
        // 创建一个点center，临界坐标坐标
        center = new Point();
        center.set(collapsedRadius, expandedRadius);

        if (collapsedRadius > expandedRadius) {
            throw new IllegalArgumentException("expandedRadius must bigger than collapsedRadius");
        }
        // 浅色的背景画笔
        primaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryPaint.setColor(primaryColor);
        primaryPaint.setStyle(Paint.Style.FILL);
        // 深色的背景画笔
        primaryDarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        primaryDarkPaint.setColor(primaryColor);
        primaryDarkPaint.setStyle(Paint.Style.FILL);

        setWillNotDraw(false);      // 去除viewGroup的ondraw保护，使得我们可以重写onDraw
        if (Build.VERSION.SDK_INT >= 21) {      // 版本21以上，添加个阴影效果
            outlineProvider = new OvalOutline();
        }
        // 白色，收缩半径，的drawable
        drawable = new FilterMenuDrawable(ctx, Color.WHITE, collapsedRadius);
        menuBounds = new Rect();    // 开辟区域
        // expandProgress的属性动画
        circleAnimator = ObjectAnimator.ofFloat(this, "expandProgress", 0, 0);
        circleAnimator.setInterpolator(new OvershootInterpolator());    // 向前惯性之后回到最终状态的插值器
        circleAnimator.setDuration(DURATION);
        // 渐变色的属性动画
        colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), primaryColor, primaryDarkColor);
        colorAnimator.setDuration(DURATION);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                primaryDarkPaint.setColor((Integer) animation.getAnimatedValue());
            }
        });
        setSoundEffectsEnabled(true);   // 开启点击声音
    }

    //************************************** 工具方法 *************************************/
    /**
     * calculate arc angle between point a and point b
     * 计算2点之间的扇形角度
     * @param center
     * @param a
     * @param b
     * @param area
     * @param radius
     * @return
     */
    private static double arcAngle(Point center, Point a, Point b, Rect area, int radius) {
        double angle = threePointsAngle(center, a, b);
        Point innerPoint = findMidnormalPoint(center, a, b, area, radius);
        Point midInsectPoint = new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
        double distance = pointsDistance(midInsectPoint, innerPoint);
        if (distance > radius) {
            return 360 - angle;
        }
        return angle;
    }

    /**
     * find the middle point of two intersect points in circle,only one point will be correct
     * 2个交叉点的中心点，仅有一个点会被显示
     * @param center
     * @param a
     * @param b
     * @param area
     * @param radius
     * @return
     */
    private static Point findMidnormalPoint(Point center, Point a, Point b, Rect area, int radius) {
        if (a.y == b.y) {
            //top
            if (a.y < center.y) {
                return new Point((a.x + b.x) / 2, center.y + radius);
            }
            //bottom
            return new Point((a.x + b.x) / 2, center.y - radius);
        }
        if (a.x == b.x) {
            //left
            if (a.x < center.x) {
                return new Point(center.x + radius, (a.y + b.y) / 2);
            }
            //right
            return new Point(center.x - radius, (a.y + b.y) / 2);
        }
        //slope of line ab
        double abSlope = (a.y - b.y) / (a.x - b.x * 1.0);
        //slope of midnormal
        double midnormalSlope = -1.0 / abSlope;

        double radian = Math.tan(midnormalSlope);
        int dy = (int) (radius * Math.sin(radian));
        int dx = (int) (radius * Math.cos(radian));
        Point point = new Point(center.x + dx, center.y + dy);
        if (!inArea(point, area, 0)) {
            point = new Point(center.x - dx, center.y - dy);
        }
        return point;
    }

    private static double pointAngleOnCircle(Point center, Point point, Point coor) {
        double angle = threePointsAngle(center, point, coor);
        if (point.y < center.y) {
            angle = 360 - angle;
        }
        return angle;
    }

    /**
     * judge if an point in the area or not
     * 判断1个点是否落在指点的区域内
     * @param point
     * @param area
     * @param offsetRatio
     * @return
     */
    public static boolean inArea(Point point, Rect area, float offsetRatio) {
        int offset = (int) (area.width() * offsetRatio);
        return point.x >= area.left - offset && point.x <= area.right + offset &&
                point.y >= area.top - offset && point.y <= area.bottom + offset;
    }

    /**
     * calculate the  point a's angle of rectangle consist of point a,point b, point c;
     * 计算出3点之间的角度
     * @param vertex
     * @param A
     * @param B
     * @return
     */
    private static double threePointsAngle(Point vertex, Point A, Point B) {
        double b = pointsDistance(vertex, A);
        double c = pointsDistance(A, B);
        double a = pointsDistance(B, vertex);

        return Math.toDegrees(Math.acos((a * a + b * b - c * c) / (2 * a * b)));

    }

    /**
     * calculate distance of two points
     * 计算出两点之间的距离
     * @param a
     * @param b
     * @return
     */
    private static double pointsDistance(Point a, Point b) {
        int dx = b.x - a.x;
        int dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


    //************************************** 流程方法 *************************************/

    /**
     * 完成布局填充之后，调用该方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            throw new IllegalStateException("should not add any child view to FilterMenuLayout ");
        }
    }

    public float getExpandProgress() {
        return expandProgress;
    }

    public void setExpandProgress(float progress) {
        this.expandProgress = progress;
        primaryPaint.setAlpha(Math.min(255, (int) (progress * 255)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            invalidateOutline();
        }
        drawable.setExpandProgress(progress);
        invalidate();
    }

    /**
     * 触发配置事件时，如改变屏幕方向、弹出软件盘和隐藏软键盘等，不走oncreate，直接调用该方法
     * @param newConfig
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 带动画的收缩
     * @param animate
     */
    void collapse(boolean animate) {
        state = STATE_COLLAPSE;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(View.GONE);
        }
        invalidate();
        if (animate) {
            startCollapseAnimation();
        }
        if (menu != null && menu.getListener() != null) {
            menu.getListener().onMenuCollapse();
        }
    }

    /**
     * 带动画的展开
     * @param animate
     */
    void expand(boolean animate) {
        state = STATE_EXPAND;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(View.VISIBLE);
        }
        invalidate();
        if (animate) {
            startExpandAnimation();
        } else {
            setItemsAlpha(1f);
        }
        if (menu != null && menu.getListener() != null) {
            menu.getListener().onMenuExpand();
        }
    }

    /**
     * 带动画地触发
     * @param animate
     */
    void toggle(boolean animate) {
        if (state == STATE_COLLAPSE) {
            expand(animate);
        } else if (state == STATE_EXPAND) {
            collapse(animate);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        setMeasuredDimension(width, height);
        measureChildren(widthMeasureSpec, heightMeasureSpec);   // 测量所有子控件的宽高

    }

    /**
     * 为了根据不同的state来进行切换以及刷新UI,必须重写该方法
     * @param who 需要核实的drawable
     * @return
     */
    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == drawable || super.verifyDrawable(who);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (getChildCount() == 0) {
            return;
        }
        calculateMenuItemPosition();
        for (int i = 0; i < getChildCount(); i++) {
            FilterMenu.Item item = (FilterMenu.Item) getChildAt(i).getTag();
            item.setBounds(
                    l + item.getX(),
                    t + item.getY(),
                    l + item.getX() + item.getView().getMeasuredWidth(),
                    t + item.getY() + item.getView().getMeasuredHeight()
            );
            Rect bounds = item.getBounds();
            item.getView().layout(bounds.left, bounds.top, bounds.right, bounds.bottom);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPoint.set((int) event.getX(), (int) event.getY());
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                isExpand = false;
                double distance = pointsDistance(touchPoint, center);
                if (distance > (collapsedRadius + (expandedRadius - collapsedRadius) * expandProgress)) {
                    if (state == STATE_EXPAND) {
                        collapse(true);
                        return true;
                    }
                    return false;
                } else {
                    if (state == STATE_COLLAPSE) {
                        expand(true);
                        isExpand = true;
                    }
                    return true;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if (inChild) {
                    if (!inArea(touchPoint, touchedItem.getBounds(), .2f)) {
                        touchedItem.getView().setPressed(false);
                        inChild = false;
                    }
                } else {
                    for (int i = 0; i < getChildCount(); i++) {
                        View child = getChildAt(i);
                        FilterMenu.Item item = (FilterMenu.Item) getChildAt(i).getTag();
                        if (inArea(touchPoint, item.getBounds(), .2f)) {
                            touchedItem = item;
                            inChild = true;
                            child.dispatchTouchEvent(event);
                            child.setPressed(true);
                            break;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (inChild) {
                    if (menu != null) {
                        if (menu.getListener() != null) {
                            collapse(true);
                            menu.getListener().onMenuItemClick(touchedItem.getView(), touchedItem.getPosition());
                        }
                    }
                    touchedItem.getView().setPressed(false);
                    inChild = false;
                }
                if (!isExpand) {
                    collapse(true);
                    return true;
                }
                double distance = pointsDistance(touchPoint, center);
                if (distance > (collapsedRadius + (expandedRadius - collapsedRadius) * expandProgress)) {
                    collapse(true);
                    return true;
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            FilterMenu.Item item = (FilterMenu.Item) getChildAt(i).getTag();
//            if(inArea(touchPoint, item.getBounds())){
//                return false;
//            }
//        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 当view的大小发生变化时触发
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + w + ", " + h);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(outlineProvider);    // 设置阴影的轮廓
        }
        int x, y;
        if (centerHorizontal) {
            x = w / 2 + centerLeft - centerRight;
        } else {
            x = centerLeft != 0 ? centerLeft : w - centerRight;
        }
        if (centerVertical) {
            y = h / 2 + centerTop - centerBottom;
        } else {
            y = centerTop != 0 ? centerTop : h - centerBottom;
        }
        center.set(x, y);

        int left = Math.max(getPaddingLeft(), center.x - expandedRadius);
        int top = Math.max(getPaddingTop(), center.y - expandedRadius);
        int right = Math.min(w - getPaddingRight(), center.x + expandedRadius);
        int bottom = Math.min(h - getPaddingBottom(), center.y + expandedRadius);

        menuBounds.set(left, top, right, bottom);

        calculateIntersectPoints();
        drawable.setBounds(center.x - drawable.getIntrinsicWidth() / 2,
                center.y - drawable.getIntrinsicHeight() / 2,
                center.x + drawable.getIntrinsicWidth() / 2,
                center.y + drawable.getIntrinsicHeight() / 2
        );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (expandProgress > 0f) {      // 展开过程中的浅色圆
            canvas.drawCircle(center.x, center.y, collapsedRadius + (expandedRadius - collapsedRadius) * expandProgress, primaryPaint);
        }
        // 中心的暗色圆
        canvas.drawCircle(center.x, center.y, collapsedRadius + (collapsedRadius * .2f * expandProgress), primaryDarkPaint);
        drawable.draw(canvas);
    }

    /* 开启展开的动画 */
    void startExpandAnimation() {
        //animate circle
        circleAnimator.setFloatValues(getExpandProgress(), 1f);
        circleAnimator.start();

        //animate color
        colorAnimator.setObjectValues(colorAnimator.getAnimatedValue() == null ? primaryColor : colorAnimator.getAnimatedValue(), primaryDarkColor);
        colorAnimator.start();
        //animate menu item  菜单item的动画
        int delay = DURATION_BETWEEN_ITEM;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).animate()
                    .setStartDelay(delay)
                    .setDuration(DURATION)
                    .alphaBy(0f)
                    .scaleXBy(0.5f)
                    .scaleX(1f)
                    .scaleYBy(0.5f)
                    .scaleY(1.f)
                    .alpha(1f)
                    .start();
            delay += DURATION_BETWEEN_ITEM;
        }
    }

    /* 开启收缩的动画 */
    void startCollapseAnimation() {
        //animate circle
        circleAnimator.setFloatValues(getExpandProgress(), 0f);
        circleAnimator.start();

        //animate color
        colorAnimator.setObjectValues(colorAnimator.getAnimatedValue() == null ? primaryDarkColor : colorAnimator.getAnimatedValue(), primaryColor);
        colorAnimator.start();

        //animate menu item
        int delay = DURATION_BETWEEN_ITEM;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).animate()
                    .setStartDelay(delay)
                    .setDuration(DURATION)
                    .alpha(0)
                    .scaleX(0)
                    .scaleY(0)
                    .start();
            delay += DURATION_BETWEEN_ITEM;
        }

    }

    void setItemsAlpha(float alpha) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setAlpha(alpha);
        }
    }

    /**
     * calculate and set position to menu items
     * 计算并设置菜单item的Position
     */
    private void calculateMenuItemPosition() {

        float itemRadius = (expandedRadius + collapsedRadius) / 2, f;
        RectF area = new RectF(
                center.x - itemRadius,
                center.y - itemRadius,
                center.x + itemRadius,
                center.y + itemRadius);
        Path path = new Path();
        path.addArc(area, (float) fromAngle, (float) (toAngle - fromAngle));
        PathMeasure measure = new PathMeasure(path, false);
        float len = measure.getLength();
        int divisor = getChildCount();
        float divider = len / divisor;

        for (int i = 0; i < getChildCount(); i++) {
            float[] coords = new float[2];
            measure.getPosTan(i * divider + divider * .5f, coords, null);
            FilterMenu.Item item = (FilterMenu.Item) getChildAt(i).getTag();
            item.setX((int) coords[0] - item.getView().getMeasuredWidth() / 2);
            item.setY((int) coords[1] - item.getView().getMeasuredHeight() / 2);
        }
    }

    /**
     * find all intersect points, and calculate menu items display area;
     * 找到所有的交叉点，并计算菜单条目显示的区域
     */
    private void calculateIntersectPoints() {
        intersectPoints.clear();

        /** order intersect points clockwise **/
        //left edge 左边缘
        if (center.x - menuBounds.left < expandedRadius) {
            int dy = (int) Math.sqrt(Math.pow(expandedRadius, 2) - Math.pow(center.x - menuBounds.left, 2));
            if (center.y - dy > menuBounds.top) {
                intersectPoints.add(new Point(menuBounds.left, center.y - dy));
            }

            if (center.y + dy < menuBounds.bottom) {
                intersectPoints.add(new Point(menuBounds.left, center.y + dy));
            }

        }
        //top edge 顶边缘
        if (center.y - menuBounds.top < expandedRadius) {
            int dx = (int) Math.sqrt(Math.pow(expandedRadius, 2) - Math.pow(center.y - menuBounds.top, 2));
            if (center.x + dx < menuBounds.right) {
                intersectPoints.add(new Point(center.x + dx, menuBounds.top));
            }
            if (center.x - dx > menuBounds.left) {
                intersectPoints.add(new Point(center.x - dx, menuBounds.top));
            }
        }

        //right edge 右边缘
        if (menuBounds.right - center.x < expandedRadius) {
            int dy = (int) Math.sqrt(Math.pow(expandedRadius, 2) - Math.pow(menuBounds.right - center.x, 2));

            if (center.y - dy > menuBounds.top) {
                intersectPoints.add(new Point(menuBounds.right, center.y - dy));
            }
            if (center.y + dy < menuBounds.bottom) {
                intersectPoints.add(new Point(menuBounds.right, center.y + dy));
            }

        }
        //bottom edge 底部边缘
        if (menuBounds.bottom - center.y < expandedRadius) {
            int dx = (int) Math.sqrt(Math.pow(expandedRadius, 2) - Math.pow(menuBounds.bottom - center.y, 2));
            if (center.x + dx < menuBounds.right) {
                intersectPoints.add(new Point(center.x + dx, menuBounds.bottom));
            }
            if (center.x - dx > menuBounds.left) {
                intersectPoints.add(new Point(center.x - dx, menuBounds.bottom));
            }
        }


        //find the maximum arc in menuBounds获取菜单界限内，条目数对应的弧度
        int size = intersectPoints.size();
        if (size == 0) {
            fromAngle = 0;
            toAngle = 360;
            return;
        }
        int indexA = size - 1;
        double maxAngle = arcAngle(center, intersectPoints.get(0), intersectPoints.get(indexA), menuBounds, expandedRadius);
        for (int i = 0; i < size - 1; i++) {
            Point a = intersectPoints.get(i);
            Point b = intersectPoints.get(i + 1);
            double angle = arcAngle(center, a, b, menuBounds, expandedRadius);
            Point midnormalPoint = findMidnormalPoint(center, a, b, menuBounds, expandedRadius);

            //if the arc(a->midnormalPoint->b) is in menuBounds and the angle is bigger, select it
            int pointerIndex = i;
            int endIndex = indexA + 1;
            if (!isClockwise(center, a, midnormalPoint)) {
                int tmpIndex = pointerIndex;
                pointerIndex = endIndex;
                endIndex = tmpIndex;
            }
            if (pointerIndex == intersectPoints.size() - 1) {
                pointerIndex = 0;
            } else {
                pointerIndex++;
            }

            if (pointerIndex == endIndex && angle > maxAngle) {
                indexA = i;
                maxAngle = angle;
            }
        }

        Point a = intersectPoints.get(indexA);
        Point b = intersectPoints.get(indexA + 1 >= size ? 0 : indexA + 1);
        Point midnormalPoint = findMidnormalPoint(center, a, b, menuBounds, expandedRadius);

        Point x = new Point(menuBounds.right, center.y);
        if (!isClockwise(center, a, midnormalPoint)) {
            Point tmp = a;
            a = b;
            b = tmp;
        }

        fromAngle = pointAngleOnCircle(center, a, x);
        toAngle = pointAngleOnCircle(center, b, x);
        toAngle = toAngle <= fromAngle ? 360 + toAngle : toAngle;
    }

    /**
     * judge a->b is ordered clockwise
     * 若a点到b点是不是顺时针的顺序
     * @param center
     * @param a
     * @param b
     * @return
     */
    private boolean isClockwise(Point center, Point a, Point b) {
        double cross = (a.x - center.x) * (b.y - center.y) - (b.x - center.x) * (a.y - center.y);
        return cross > 0;
    }



    /*
    * --------------------------------------------
    *  非正常退出时的save
    * --------------------------------------------
    */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.setExpandProgress(ss.expandProgress);
        this.setPrimaryColor(ss.primaryColor);
        this.setPrimaryDarkColor(ss.primaryDarkColor);
        this.setCollapsedRadius(ss.collapsedRadius);
        this.setExpandedRadius(ss.expandedRadius);
        if (ss.state == STATE_COLLAPSE) {
            collapse(false);
        } else {
            expand(false);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.expandProgress = getExpandProgress();
        ss.primaryColor = getPrimaryColor();
        ss.primaryDarkColor = getPrimaryDarkColor();
        ss.collapsedRadius = getCollapsedRadius();
        ss.expandedRadius = getExpandedRadius();
        ss.state = getState();

        return ss;
    }

    /*
    * --------------------------------------------
    *  setter & getter
    * --------------------------------------------
    */
    public int getState() {
        return state;
    }

    public void setMenu(FilterMenu menu) {
        this.menu = menu;
    }

    public int getExpandedRadius() {
        return expandedRadius;
    }

    public void setExpandedRadius(int expandedRadius) {
        this.expandedRadius = expandedRadius;
        requestLayout();
    }

    public int getCollapsedRadius() {
        return collapsedRadius;
    }

    public void setCollapsedRadius(int collapsedRadius) {
        this.collapsedRadius = collapsedRadius;
        requestLayout();
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int color) {
        this.primaryColor = color;
        primaryPaint.setColor(primaryColor);
        invalidate();
    }

    public int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public void setPrimaryDarkColor(int color) {
        this.primaryDarkColor = color;
        primaryDarkPaint.setColor(color);
        invalidate();
    }

    /*
    * --------------------------------------------
    *  内部类，继承View的静态内部类savestate，记录view需要保存哪些数据
    * --------------------------------------------
    */
    static class SavedState extends BaseSavedState {

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public float expandProgress;
        public int primaryColor;
        public int primaryDarkColor;
        public int collapsedRadius;
        public int expandedRadius;
        public int state;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.expandProgress = in.readFloat();
            this.primaryColor = in.readInt();
            this.primaryDarkColor = in.readInt();
            this.collapsedRadius = in.readInt();
            this.expandedRadius = in.readInt();
            this.state = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(this.expandProgress);
            dest.writeInt(this.primaryColor);
            dest.writeInt(this.primaryDarkColor);
            dest.writeInt(this.collapsedRadius);
            dest.writeInt(this.expandedRadius);
            dest.writeInt(this.state);
        }

    }

    /**
     * 21版本以上的，轮廓辅助类
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class OvalOutline extends ViewOutlineProvider {
        public OvalOutline() {
            super();
        }

        /**
         * 获取轮廓
         * @param view
         * @param outline
         */
        @Override
        public void getOutline(View view, Outline outline) {
            int radius = (int) (collapsedRadius + (expandedRadius - collapsedRadius) * expandProgress);
            Rect area = new Rect(
                    center.x - radius,
                    center.y - radius,
                    center.x + radius,
                    center.y + radius);
            outline.setRoundRect(area, radius);
        }
    }


}