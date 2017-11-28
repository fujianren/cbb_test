package com.cbb.myapplication.recycler_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * @author chenbb
 * @create 2017/11/22
 * @desc
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
    private final Drawable mDivider;


    public DividerGridItemDecoration(Context context){
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    /* 画水平分割线 */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params
                    = (RecyclerView.LayoutParams) child.getLayoutParams();

            int left = child.getLeft() - params.leftMargin; // 左越margin
            int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();   // 右过margin + 线自宽
            int top = child.getBottom() + params.bottomMargin;      // 高顶child底部
            int bottom = top + mDivider.getIntrinsicHeight();   // 底部正好为top + 线自高
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /* 画垂直分割线 */
    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params
                    = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getTop() - params.topMargin;        // 高同child高
            int bottom = child.getBottom() + params.bottomMargin;   // 底同child底
            int left = child.getRight() + params.rightMargin;   // 左贴child右侧
            int right = left + mDivider.getIntrinsicWidth();    // 右为left + 线自宽

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
//        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        // 若是最后一行不需要绘制底部
        if (isLastRaw(parent, itemPosition, spanCount, childCount)){
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);

            // 若是最后一列，不绘制右边
        } else if (isLastColumn(parent, itemPosition, spanCount, childCount)){
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());

        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
        }
    }

    private boolean isLastColumn(RecyclerView parent, int itemPosition, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((itemPosition + 1) % spanCount == 0) return true;   // 最后一列

        } else if (layoutManager instanceof  StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL){
                if ((itemPosition + 1) % spanCount == 0) return true;
            } else {
                childCount = childCount = childCount % spanCount;
                if (itemPosition >= childCount) return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int itemPosition, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager){
            childCount = childCount - childCount % spanCount;
            if (itemPosition >= childCount) return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager){
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();

            if (orientation == StaggeredGridLayoutManager.VERTICAL){
                childCount = childCount - childCount % spanCount;
                if (itemPosition >= childCount) return true;
            } else {
                if ((itemPosition + 1) % spanCount == 0) return true;
            }
        }
        return false;
    }


    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }
}
