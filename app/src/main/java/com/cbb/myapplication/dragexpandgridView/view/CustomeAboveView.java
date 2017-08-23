package com.cbb.myapplication.dragexpandgridView.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbb.myapplication.R;
import com.cbb.myapplication.dragexpandgridView.Model.DragChildInfo;
import com.cbb.myapplication.dragexpandgridView.Model.DragIconInfo;

import java.util.ArrayList;

/**
 * @author chenbb
 * @create 2017/8/17
 * @desc 一个有展开内层功能的网格效果的view
 */

public class CustomeAboveView extends LinearLayout {
    private static final String TAG = "CustomeAboveView";
    private Context mContext;
    /*  */
    private CustomGroup mCustomCroup;
    /* 要填充item的数据集合 */
    private ArrayList<DragIconInfo> mIconInfoList = new ArrayList<>();
    /* 网格线的宽度 */
    private final int verticalViewWidth = 1;

    /* 加载更多 */
    public static final int MORE = 9999;
    /* 触摸事件 */
    private MotionEvent firstEvent;
    /* 外层item的点击监听，本类中已实现 */
    ItemViewClickListener mItemViewClickListener;

    /* 外层item之后可否展开时的回调接口，交由其他实现类去实现 */
    private CustomAboveViewClickListener gridViewclickListener;


    public CustomAboveViewClickListener getGridViewclickListener() {
        return gridViewclickListener;
    }

    public void setGridViewclickListener(CustomAboveViewClickListener gridViewclickListener) {
        this.gridViewclickListener = gridViewclickListener;
    }

    public MotionEvent getFirstEvent() {
        return firstEvent;
    }

    public interface CustomAboveViewClickListener {
        /* 点击外层item，没有内层数据供其展开时执行该方法 */
        void onSignleClicked(DragIconInfo dragChildInfo);

        /* 内部item被点击时，由该方法执行 */
        void onChildClicked(DragChildInfo dragChildInfo);
    }


    public CustomeAboveView(Context context, CustomGroup customCroup) {
        super(context, null);
        this.mContext = context;
        this.mCustomCroup = customCroup;
        setOrientation(VERTICAL);
        initData();
    }

    private void initData() {
        mChildClickListener = new CustomGridView.CustomChildClickListener() {

            @Override
            public void onChildClicked(DragChildInfo chilidInfo) {
                if (gridViewclickListener != null) {
                    gridViewclickListener.onChildClicked(chilidInfo);
                }
            }
        };
    }

    /* 分发触摸事件 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.firstEvent = ev;
        if (mCustomCroup.isEditModel()) {
            // 去执行behindView对象触摸事件
            mCustomCroup.sendEventBehind(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
        * --------------------------------------------
        *  setter & getter
        * --------------------------------------------
        */
    public ArrayList<DragIconInfo> getIconInfoList() {
        return mIconInfoList;
    }

    public void setIconInfoList(ArrayList<DragIconInfo> mIconInfoList) {
        this.mIconInfoList = mIconInfoList;
    }


    /*
    * --------------------------------------------
    *  public
    * --------------------------------------------
    */

    /**
     * 刷新数据集，并唤醒UI刷新
     *
     * @param iconInfos
     */
    public void refreshIconInfoList(ArrayList<DragIconInfo> iconInfos) {

        mIconInfoList.clear();
        mIconInfoList.addAll(iconInfos);
        refreshViewUI();
    }

    /**
     * 刷新ui
     */
    public void refreshViewUI() {
        removeAllViews();
        int rowNum = mIconInfoList.size() / CustomGroup.COLUMMUN + (mIconInfoList.size() % CustomGroup.COLUMMUN > 0 ? 1 : 0);
        // 布局参数，行容器，垂直线，水平线
        LayoutParams rowParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams verticalParams = new LayoutParams(verticalViewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams horizontalParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, verticalViewWidth);

        for (int rowIndex = 0; rowIndex < rowNum; rowIndex++) {
            // 每一行的布局,并找出控件
            View rowView = View.inflate(mContext, R.layout.gridview_rowcontainer_ll, null);
            // 外层网格中每一行的行容器
            LinearLayout llRowContainer = (LinearLayout) rowView.findViewById(R.id.gridview_rowcontainer_ll);
            // 展开的小箭头
            final ImageView ivOpenFlag = (ImageView) rowView.findViewById(R.id.gridview_rowopenflag_iv);
            // 展开时内层的父容器
            LinearLayout llBtm = (LinearLayout) rowView.findViewById(R.id.gridview_rowbtm_ll);
            // 整个内层网格的view
            final CustomGridView gridViewNoScroll = (CustomGridView) rowView.findViewById(R.id.gridview_child_gridview);

            // 内层网格的父容器
            gridViewNoScroll.setParentView(llBtm);
            // 内层item的点击监听
            if (mChildClickListener != null) {
                gridViewNoScroll.setChildClickListener(mChildClickListener);
            }

            // 外层item的布局参数
            LayoutParams itemParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemParam.weight = 1.0f;

            // 外层item的监听实现
            ItemViewClickListener itemClickListener = new ItemViewClickListener(llBtm, ivOpenFlag, new ItemViewClickInterface() {
                @Override
                public boolean shoudInteruptViewAnimtion(ItemViewClickListener listener, int position) {
                    boolean isInterupt = false;
                    mCustomCroup.clearEditDragView();

                    // 若已经设置相同的外层item的点击监听，做收缩的操作
                    if (mItemViewClickListener != null && !mItemViewClickListener.equals(listener)) {
                        mItemViewClickListener.closeExpandView();
                    }
                    mItemViewClickListener = listener;
                    // 获取内层的数据
                    DragIconInfo iconInfo = mIconInfoList.get(position);
                    ArrayList<DragChildInfo> childList = iconInfo.getChildList();
                    if (childList.size() > 0) {
                        // 刷新内层ui
                        gridViewNoScroll.refreshDataSet(childList);
                    } else {
                        // 没有内层数据，关闭内层
                        setViewCollaps();
                        isInterupt = true;
                        if (gridViewclickListener != null) {
                            gridViewclickListener.onSignleClicked(iconInfo);
                        }
                    }
                    return isInterupt;
                }

                @Override
                public void viewUpdateData(int position) {
                    gridViewNoScroll.notifyDataSetChange(true);
                }
            });


            for (int columnIndex = 0; columnIndex < CustomGroup.COLUMMUN; columnIndex++) {
                // 外层网格的item布局
                View itemView = View.inflate(mContext, R.layout.gridview_above_itemview, null);
                ImageView ivIcon = (ImageView) itemView.findViewById(R.id.icon_iv);
                TextView tvName = (TextView) itemView.findViewById(R.id.name_tv);

                // 为对应位置上的控件绑定数据
                int itemInfoIndex = rowIndex * CustomGroup.COLUMMUN + columnIndex;
                if (itemInfoIndex > mIconInfoList.size() - 1) {
                    itemView.setVisibility(INVISIBLE);
                } else {
                    final DragIconInfo iconInfo = mIconInfoList.get(itemInfoIndex);
                    ivIcon.setImageResource(iconInfo.getResIconId());
                    tvName.setText(iconInfo.getName());
                    itemView.setId(itemInfoIndex);
                    itemView.setTag(itemInfoIndex);     // 设置标记
                    // 设置外层item的点击监听
                    itemView.setOnClickListener(itemClickListener);
                    // 设置长按监听(原来系统有自带的)
                    itemView.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (iconInfo.getId() != MORE) {
                                // 让操作类实现长按操作，进入编辑模式，创建第一个镜像
                                int position = (Integer) v.getTag();
                                Log.d(TAG, "onLongClick: 长按。。。。");
                                mCustomCroup.setEditModel(true, position);
                            }
                            return true;
                        }
                    });
                }
                // 添加到行容器中
                llRowContainer.addView(itemView, itemParam);
                // 插入垂直网格线
                View view = new View(mContext);
                view.setBackgroundColor(Color.parseColor("#e2e2e2"));
                llRowContainer.addView(view, verticalParams);
            }
            // 总容器，先插入一条水平网格线
            View view = new View(mContext);
            view.setBackgroundColor(Color.parseColor("#e2e2e2"));
            addView(view, horizontalParams);
            // 把行容器添加到总容器中
            addView(rowView, rowParam);
            // 添加到最后一个行容器了，再插入一个水平线
            if (rowIndex == rowNum - 1) {
                View btmView = new View(mContext);
                btmView.setBackgroundColor(Color.parseColor("#e2e2e2"));
                addView(btmView, horizontalParams);
            }
        }
    }

    /**
     * 调用监听的实现类，关闭内层项
     */
    public void setViewCollaps() {
        if (mItemViewClickListener != null) {
            mItemViewClickListener.closeExpandView();
        }
    }

    /*
    * --------------------------------------------
    *  接口回调
    * --------------------------------------------
    */
    private CustomGridView.CustomChildClickListener mChildClickListener;

    /**
     * 内部接口，作为本类中的外层点击事件的参数，在本类中实现具体操作
     */
    public interface ItemViewClickInterface {

        /**
         * 当外层item被点击时，直接执行该方法
         * 判断是不是没有没有内层数据
         * @param animUtil  item被点击的监听
         * @param position  点击对应的position
         * @return  true表示没有内层数据，false表示有内层数据
         */
        public boolean shoudInteruptViewAnimtion(ItemViewClickListener animUtil, int position);

        /**
         * 更新对应位置上的数据
         * 已有一个展开的item，现点击另外一个item，执行该方法
         * @param position
         */
        public void viewUpdateData(int position);
    }

    /**
     * 点击事件的实现类，实现外层的item被点击时的处理
     */
    public class ItemViewClickListener implements OnClickListener {

        private View mContentParent;
        private View mViewFlag;     // 本处指外层展开时，显示小箭头的view
        private ItemViewClickInterface animationListener;       // 动画接口
        private final int INVALID_ID = -1000;   // 默认无效的item编号
        private int mLastViewID = INVALID_ID;   // 上一次被点击的item，初始化为无效

        private int startX;             // 箭头做平移动画时的起始位置
        private int viewFlagWidth;      // 箭头view的宽
        private int itemViewWidth;      // 外层单个item的宽

        /**
         * 构造函数
         * @param contentParent
         * @param viewFlag
         * @param animationListener    回调的接口
         */
        public ItemViewClickListener(View contentParent, View viewFlag, ItemViewClickInterface animationListener) {
            this.mContentParent = contentParent;
            this.animationListener = animationListener;
            this.mViewFlag = viewFlag;
        }

        /* 重写onClick方法，具体处理方案由该方法提供 */
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            boolean isThesameView = false;      // 是不是上一次的被点中的item
            if (animationListener != null) {
                // 判断该item下有没有内层数据
                if (animationListener.shoudInteruptViewAnimtion(this, viewId)) {
                    return;
                }
            }
            // 若点中的是上一次的item
            if (mLastViewID == viewId) {
                isThesameView = true;       // 本次和上一次点中的item相同
            } else {
                // 若与上一次点中的不是同一个item
                mViewFlag.setVisibility(VISIBLE);       // 显示箭头
                viewFlagWidth = getViewFlagWidth();
                itemViewWidth = v.getWidth();
                // 结束的x坐标居于被点中item的中心
                int endX = v.getLeft() + itemViewWidth / 2 - viewFlagWidth / 2;

                // 第一次点击时，因为默认上一次的点击事件是没有的，执行该判断
                if(mLastViewID == INVALID_ID) {
                    startX = endX;
                    // 箭头做平移动画
                    xAxisMoveAnim(mViewFlag, startX, endX);
                } else {
                    xAxisMoveAnim(mViewFlag, startX, endX);
                }
                // 本次的平移终点作为下一次的平移起点
                startX = endX;
            }

            boolean isVisible = mContentParent.getVisibility() == VISIBLE;
            if (isVisible) {
                // 展开的外层item再次被点中，执行收缩
                if (isThesameView) {
                    animateCollapsing(mContentParent);
                } else {
                    // 已有一个展开的item，现点击另外一个item，执行的操作交由接口完成
                    if (animationListener != null) {
                        // 此处执行内层的数据更新
                        animationListener.viewUpdateData(viewId);
                    }
                }
            } else {
                // 关闭的item再次被点中,重新显示箭头
                if (isThesameView) {
                    mViewFlag.setVisibility(VISIBLE);
                    xAxisMoveAnim(mViewFlag, startX, startX);
                }
                // 展开内层
                animataExpanding(mContentParent);
            }
            // 替换上一次被点中的item
            mLastViewID = viewId;

        }

        /**
         * 提供一个收缩内层的公共方法
         */
        public void closeExpandView(){
            boolean isVisible = mContentParent.getVisibility() == VISIBLE;
            if (isVisible) {
                animateCollapsing(mContentParent);
            }
        }

        /**
         * 展开动画
         * @param contentParent
         */
        private void animataExpanding(View contentParent) {
            contentParent.setVisibility(VISIBLE);
            int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            contentParent.measure(widthSpec, heightSpec);
            ValueAnimator animator = createHeightAnimator(contentParent, 0, contentParent.getMeasuredHeight());
            animator.start();
        }

        /**
         * 收缩动画
         * @param contentParent
         */
        private void animateCollapsing(final View contentParent) {
            int origHeignt = contentParent.getHeight();
            ValueAnimator animator = createHeightAnimator(contentParent, origHeignt, 0);
            // 设置属性动画监听
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    contentParent.setVisibility(GONE);
                    mViewFlag.clearAnimation();
                    mViewFlag.setVisibility(GONE);
                }
            });
            animator.start();      // 开始动画
        }

        /**
         * view做高度线性扩张或收缩的动画
         * @param view
         * @param start
         * @param end
         * @return
         */
        private ValueAnimator createHeightAnimator(final View view, int start, int end) {
            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = value;
                    view.setLayoutParams(layoutParams);
                }
            });
            return animator;
        }

        /**
         * 获取箭头view的宽度
         * @return
         */
        public int getViewFlagWidth() {
            int viewWidth = mViewFlag.getWidth();
            if (viewWidth == 0) {       // 若原viewFlag的宽度为0，将其精度设置为未知，并重新获取测量值作为宽
                int widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                mViewFlag.measure(widthSpec, heightSpec);
                viewWidth = mViewFlag.getMeasuredWidth();
            }
            return viewWidth;
        }

        /**
         * 移动动画
         * @param v     执行动画的view
         * @param startX    动画起始x坐标
         * @param toX   动画结束x坐标
         */
        public void xAxisMoveAnim(View v, int startX, int toX) {
            moveAnim(v, startX, toX, 0, 0, 200);
        }

        /**
         * 开启一个view的平移动画
         * @param v     执行动画的view
         * @param startX    平移动画的起始x坐标
         * @param toX   平移动画的结束x坐标
         * @param startY    平移动画起始y坐标
         * @param toY   平移动画结束y坐标
         * @param during    动画执行时间
         */
        private void moveAnim(View v, int startX, int toX, int startY, int toY, long during) {
            TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
            anim.setDuration(during);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }
    }
}
