package com.cbb.myapplication.dragexpandgridView.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbb.myapplication.R;
import com.cbb.myapplication.dragexpandgridView.Model.DragChildInfo;
import com.cbb.myapplication.utils.CommUtil;

import java.util.ArrayList;

/**
 * @author chenbb
 * @create 2017/8/17
 * @desc   自定义的内层容器
 */

public class CustomGridView extends LinearLayout {
    private static final String TAG = "CustomGridView";

    private Context mContext;
    /*  */
    private LinearLayout mParentView;
    /* 网格线的宽度 */
    private int mVerticalViewWidth;
    /* textView控件（即item）的宽 */
    private int mViewWidth;
    /* textView控件（即item）的高 */
    private int mViewHeight;
    /* 容器内item的数据集合 */
    private ArrayList<DragChildInfo> mPlayList = new ArrayList<>();
    /* 所有item可以排列的行数 */
    private int mRowNum;


    /* 回调的接口 */
    private CustomChildClickListener childClickListener;

    public CustomGridView(Context context) {
        this(context, null);
    }

    public CustomGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mVerticalViewWidth = CommUtil.dip2px(mContext, 1);
        View root = View.inflate(mContext, R.layout.gridview_child_layoutview, null);
        TextView textView = ((TextView) root.findViewById(R.id.gridview_child_name_tv));

        int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);     // 未确认的精准值
        int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);    // 未确认的精准值
        textView.measure(widthSpec, heightSpec);

        mViewWidth = (mContext.getResources().getDisplayMetrics().widthPixels - CommUtil.dip2px(mContext, 2)) / CustomGroup.COLUMMUN;
        mViewHeight = textView.getMeasuredHeight();
    }

    /*
    * --------------------------------------------
    *  public
    * --------------------------------------------
    */

    /**
     * 刷新数据集，同时唤醒ui刷新
     * @param playList
     */
    public void refreshDataSet(ArrayList<DragChildInfo> playList){
        mPlayList.clear();
        mPlayList.addAll(playList);
        notifyDataSetChange(false);
    }

    /**
     * 刷新ui
     * @param needAnim  是否需要动画
     */
    public void notifyDataSetChange(boolean needAnim) {

        removeAllViews();
        // 计算数据集需要分布的行数
        mRowNum = mPlayList.size() / CustomGroup.COLUMMUN + (mPlayList.size() % CustomGroup.COLUMMUN > 0 ? 1 : 0);
        // 布局参数对象，行容器，垂直网格线，水平网格线
        LayoutParams rowParam = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutParams verticalParams = new LayoutParams(mVerticalViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        LayoutParams horizontalParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mVerticalViewWidth);
        // 依次绘制出所有的item
        for (int rowIndex = 0; rowIndex < mRowNum; rowIndex++) {
            // 先绘制每一行的线性容器
            LinearLayout llContainer = new LinearLayout(mContext);
            llContainer.setOrientation(HORIZONTAL);
            LayoutParams itemParam = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            itemParam.width = mViewWidth;
            // 依次往该行容器内填入item
            for (int columnIndex = 0; columnIndex < CustomGroup.COLUMMUN; columnIndex++) {
                int itemInfoIndex = rowIndex * CustomGroup.COLUMMUN + columnIndex;          // 对应的总索引
                boolean isValidateView = true;              // 默认是有效的item
                if (itemInfoIndex >= mPlayList.size()) isValidateView = false;      // 索引越界的view是无效的
                // item控件的处理
                View root = View.inflate(mContext, R.layout.gridview_child_layoutview, null);
                TextView textView = (TextView) root.findViewById(R.id.gridview_child_name_tv);
                if (isValidateView) {
                    final DragChildInfo tempChild = mPlayList.get(itemInfoIndex);
                    textView.setText(tempChild.getName());
                    textView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (childClickListener != null) {
                                childClickListener.onChildClicked(tempChild);
                            }
                        }
                    });
                }
                // 将item控件放置到行容器中
                llContainer.addView(root, itemParam);
                // 添加一个item之后，就添加一条网格线
                if (columnIndex != CustomGroup.COLUMMUN - 1) {
                    View view = new View(mContext);
                    view.setBackgroundResource(R.drawable.ver_line);
                    llContainer.addView(view, verticalParams);
                }
            }
            // 填满一个行容器，将该行容器添加到总容器中
            addView(llContainer, rowParam);
            // 添加水平网格线
            View view = new View(mContext);
            view.setBackgroundResource(R.drawable.hor_line);
            addView(view, horizontalParams);

            if (needAnim) {
                // 动画效果，原始容器高度慢慢展开变化到刷新后的容器高度
                createHeightAnimator(mParentView, CustomGridView.this.getHeight(), mRowNum * mViewHeight);
            }
        }

    }

    /**
     * 容器高度从start慢慢展开到end的动画
     * @param view 执行动画的view
     * @param start 起始属性值
     * @param end   结束属性值
     */
    public void createHeightAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        // 动画更新时，做同步的容器高度展开
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    public void setParentView(LinearLayout llBtm) {
        this.mParentView = llBtm;
    }


    /*
    * --------------------------------------------
    *  接口回调
    * --------------------------------------------
    */
    public void setChildClickListener (CustomChildClickListener childClickListener) {
        this.childClickListener = childClickListener;
    }



    public  CustomChildClickListener getChildClickListener() {
        return childClickListener;
    }


    public interface CustomChildClickListener {
        public void onChildClicked(DragChildInfo chilidInfo);
    }

}
