package com.cbb.myapplication.taiji;

import android.support.annotation.NonNull;

/**
 * @author chenbb
 * @create 2017/11/1
 * @desc
 */

public class PieData {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    /* 名称，数值，百分比 */
    private String name;
    private float value;
    private float percentage;

    /* 画笔颜色 */
    private int color = 0;
    private float angle = 0;

    public PieData(@NonNull String name, @NonNull float value){
        this.name = name;
        this.value = value;
    }
}
