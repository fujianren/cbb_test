package com.cbb.sidemenu.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 *  自定义的一般动画
 */
public class FlipAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private Camera mCamera;

    public FlipAnimation(float fromDegrees, float toDegrees,
                         float centerX, float centerY) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
    }

    /**
     * 重写的初始化
     * @param width
     * @param height
     * @param parentWidth
     * @param parentHeight
     */
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();     // 来个相机
    }

    /**
     * 具体的动画实现方法，动画执行时的每个瞬间
     * @param interpolatedTime  瞬间比值
     * @param t                 点在动画中某个时间的转化类型
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;     // 基于的中心点
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();        // 获取此时的动画矩阵

        camera.save();              // 保存此时的相机

        camera.rotateY(degrees);    // 相机设置当前y轴的旋转角度

        camera.getMatrix(matrix);   // 拷贝一个和transformation下matrix一样的矩阵
        camera.restore();           // 返回先前相机保存的样子

        matrix.preTranslate(-centerX, -centerY);    // 坐标点右乘矩阵
        matrix.postTranslate(centerX, centerY);     // 坐标点左乘矩阵

    }

}