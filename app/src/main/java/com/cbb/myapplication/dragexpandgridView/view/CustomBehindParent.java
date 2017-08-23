package com.cbb.myapplication.dragexpandgridView.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.cbb.myapplication.R;
import com.cbb.myapplication.dragexpandgridView.Model.DragIconInfo;

import java.util.ArrayList;

/**
 * @author chenbb
 * @create 2017/8/21
 * @desc   镜像网格的父容器
 */

class CustomBehindParent extends RelativeLayout{


    private Context mContext;
    private CustomBehindView mCustomBehindView;

    public CustomBehindParent(Context context, CustomGroup customGroup) {
        super(context);
        this.mContext = context;
        mCustomBehindView = new CustomBehindView(context, customGroup);
        mCustomBehindView.setHorizontalSpacing(1);
        mCustomBehindView.setVerticalSpacing(1);
        mCustomBehindView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mCustomBehindView.setBackgroundColor(mContext.getResources().getColor(R.color.gap_line));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mCustomBehindView, params);
    }

    /**
     * 刷新数据集，新的适配器重新适配，并刷新ui
     * @param iconInfos
     */
    public void refreshIconInfoList(ArrayList<DragIconInfo> iconInfos){
        mCustomBehindView.refreshIconInfoList(iconInfos);
    }

    /**
     * 原适配器，刷新ui
     * @param dragIconInfos
     */
    public void notifyDataSetChange(ArrayList<DragIconInfo> dragIconInfos) {
        mCustomBehindView.notifyDataSetChange(dragIconInfos);
    }

    /**
     * 在界面上绘制出一个与item一模一样的镜像
     * @param position  要拖拽的item对应的position
     * @param event     触摸事件，可获取触摸点坐标
     */
    public void drawWindowView(int position, MotionEvent event) {
        mCustomBehindView.drawWindowView(position, event);
    }

    public ArrayList<DragIconInfo> getEditList(){
        return mCustomBehindView.getEditList();
    }

    /**
     * 触发子控件的触摸拦截
     * @param event
     */
    public void childDispatchTouchEvent(MotionEvent event){
        mCustomBehindView.dispatchTouchEvent(event);
    }

    /**
     * 是否已经完成拖拽交换
     * @return
     */
    public boolean isModifyOrder(){
        return mCustomBehindView.isModifyedOrder();
    }

    /**
     * 取消编辑模式，停止拖拽
     */
    public void cancleModifyOrderState(){
        mCustomBehindView.cancleModifyedOrderState();
    }

    /**
     * 复位需要隐藏的item的position
     */
    public void resetHidePosition(){
        mCustomBehindView.resetHidePosition();
    }

    /**
     * 滑动的事件是否有效
     * @param event
     * @param scrolly
     * @return
     */
    public boolean isValideEvent(MotionEvent event, int scrolly){
        return mCustomBehindView.isValideEvent(event, scrolly);
    }

    /**
     * 删除镜像
     */
    public void clearDragView(){
        mCustomBehindView.clearDragView();
    }

}
