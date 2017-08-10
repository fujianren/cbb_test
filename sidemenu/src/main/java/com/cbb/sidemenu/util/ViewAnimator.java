package com.cbb.sidemenu.util;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.cbb.sidemenu.R;
import com.cbb.sidemenu.animation.FlipAnimation;
import com.cbb.sidemenu.interfaces.Resourceble;
import com.cbb.sidemenu.interfaces.ScreenShotable;

import java.util.ArrayList;
import java.util.List;


/**
 * Resourceble接口实现类的属性动画
 * {@link #showMenuContent()} 调用实现带动画特效的菜单展开
 */
public class ViewAnimator<T extends Resourceble> {
    private final int ANIMATION_DURATION = 175;
    public static final int CIRCULAR_REVEAL_ANIMATION_DURATION = 500;

    private ActionBarActivity actionBarActivity;
    private List<T> list;                       // 菜单item的集合

    private List<View> viewList = new ArrayList<>();    // 菜单item的集合
    private ScreenShotable screenShotable;              // 截屏接口的实现类
    private DrawerLayout drawerLayout;
    private ViewAnimatorListener animatorListener;

    public ViewAnimator(ActionBarActivity activity,
                        List<T> items,
                        ScreenShotable screenShotable,
                        final DrawerLayout drawerLayout,
                        ViewAnimatorListener animatorListener) {
        this.actionBarActivity = activity;
        this.list = items;
        this.screenShotable = screenShotable;
        this.drawerLayout = drawerLayout;
        this.animatorListener = animatorListener;
    }

    /**显示菜单内容*/
    public void showMenuContent() {
        setViewsClickable(false);   // 所有view全部无效化
        viewList.clear();           // 清空viewList集合
        double size = list.size();
        for (int i = 0; i < size; i++) {
            // 填充
            View viewMenu = actionBarActivity.getLayoutInflater().inflate(R.layout.menu_list_item, null);
            final int finalI = i;
            // 设置监听事件
            viewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] location = {0, 0};
                    v.getLocationOnScreen(location);    // 获取该菜单item的坐标
                    switchItem(list.get(finalI), location[1] + v.getHeight() / 2);
                }
            });
            // 设置菜单图标
            ((ImageView) viewMenu.findViewById(R.id.menu_item_image)).setImageResource(list.get(i).getImageRes());
            viewMenu.setVisibility(View.GONE);  // 初始化不可见
            viewMenu.setEnabled(false);
            viewList.add(viewMenu);             // 添加到集合
            animatorListener.addViewToContainer(viewMenu);  // 子类实现的方法
            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);    // 线程通信的延迟
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (position < viewList.size()) {
                        animateView((int) position);    // 翻转展出
                    }
                    if (position == viewList.size() - 1) {  // 所有条目遍历后
                        screenShotable.takeScreenShot();    // 所有条目展示完，屏幕一闪，截屏
                        setViewsClickable(true);            // 全部可点击
                    }
                }
            }, (long) delay);
        }

    }

    /**隐藏菜单内容*/
    private void hideMenuContent() {
        setViewsClickable(false);
        double size = list.size();
        for (int i = list.size(); i >= 0; i--) {    // 由下往上隐藏
            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (position < viewList.size()) {
                        animateHideView((int) position);
                    }
                }
            }, (long) delay);
        }

    }

    /**
     * 遍历viewList，设置可点击的要求
     * @param clickable 是否可点击
     */
    private void setViewsClickable(boolean clickable) {
        animatorListener.disableHomeButton();
        for (View view : viewList) {
            view.setEnabled(clickable);
        }
    }

    /**
     * 为指定的view做翻转动画
     * @param position
     */
    private void animateView(int position) {
        final View view = viewList.get(position);
        view.setVisibility(View.VISIBLE);
        FlipAnimation rotation =
                new FlipAnimation(90, 0, 0.0f, view.getHeight() / 2.0f);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotation);
    }

    /**
     * 隐藏某个位置的菜单item的动画
     * @param position
     */
    private void animateHideView(final int position) {
        final View view = viewList.get(position);       // 执行的动画的view
        FlipAnimation rotation =                        // 翻转动画
                new FlipAnimation(0, 90, 0.0f, view.getHeight() / 2.0f);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);                    // 动画结束时固定
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            /**动画结束时清楚动画，隐藏view*/
            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
                // 若被隐藏的view正好是最后一个
                if (position == viewList.size() - 1) {
                    animatorListener.enableHomeButton();
                    drawerLayout.closeDrawers();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotation);
    }
    /**被选中的菜单条目会发生什么事，交个接口回调去解决*/
    private void switchItem(Resourceble slideMenuItem, int topPosition) {
        this.screenShotable = animatorListener.onSwitch(slideMenuItem, screenShotable, topPosition);
        hideMenuContent();
    }

    public interface ViewAnimatorListener {

        public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position);

        public void disableHomeButton();

        public void enableHomeButton();

        public void addViewToContainer(View view);

    }
}
