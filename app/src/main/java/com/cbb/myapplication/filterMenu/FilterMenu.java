package com.cbb.myapplication.filterMenu;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.cbb.myapplication.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 操作类实现菜单接口
 * 新的接口回调
 * 与FilterMenuLayout控件聚合，实现的菜单逻辑是控件来实现的，同时控件在实现时，遗留问题给操作类的回调接口
 * 包含静态的builder类
 * 自定义的item类
 */
public class FilterMenu implements IMenu {

    private List<Item> items = new ArrayList<>();   // 子菜单集合
    private OnMenuChangeListener listener;          // 回调监听接口
    private FilterMenuLayout layout;                // 自定义的菜单容器
//    /**
//     * add menu item to layout
//     *
//     * @param item
//     * @param listener
//     */
//    public void addItem(Item item, View.OnClickListener listener) {
//        items.add(item);
//    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    //************************************** 回调的监听接口 *************************************/
    public interface OnMenuChangeListener {
        void onMenuItemClick(View view, int position);

        void onMenuCollapse();

        void onMenuExpand();
    }

    public OnMenuChangeListener getListener() {
        return listener;
    }

    public void setListener(OnMenuChangeListener listener) {
        this.listener = listener;
        for (final Item item : getItems()) {
            item.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("FilterMenu", "onClick");
                    if (getListener() != null) {
                        // 具体的处理交给接口实现类完成
                        getListener().onMenuItemClick(item.getView(), item.getPosition());
                    }
                    if (layout != null) {
                        layout.collapse(true);     // 收缩子菜单，true表示带动画效果
                    }
                }
            });
        }
    }

    //************************************** 菜单接口的实现方法 *************************************/
    @Override
    public void collapse(boolean animate) {
        layout.collapse(animate);
    }

    @Override
    public void expand(boolean animate) {
        layout.expand(animate);
    }

    @Override
    public void toggle(boolean animate) {
        layout.toggle(animate);
    }

    /**
     * 设置菜单容器，并将items添加到容器中
     * @param view
     */
    @Override
    public void setMenuLayout(FilterMenuLayout view) {
        this.layout = view;
        if (view == null) {
            return;
        }
        for (final Item item : getItems()) {
            layout.addView(item.getView());
        }
        layout.setMenu(this);    // 为容器设置菜单操作
    }

    //************************************** 经典的builder套路 *************************************/
    public static class Builder {
        OnMenuChangeListener listener;
        private List<Item> items = new ArrayList<>();
        private Context ctx;
        private LayoutInflater inflater;
        private FilterMenuLayout layout;
        // 构造函数
        public Builder(Context ctx) {
            this.ctx = ctx;
            this.inflater = LayoutInflater.from(ctx);
        }
        // 设置监听器
        public Builder withListener(OnMenuChangeListener listener) {
            this.listener = listener;
            return this;
        }
        // 填充布局
        public Builder inflate(int menuResId) {
            PopupMenu popupMenu = new PopupMenu(ctx, null);
            popupMenu.inflate(menuResId);
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                addItem(item.getIcon());
            }
            menu.clear();
            menu = null;
            popupMenu = null;
            return this;
        }
        // 添加子菜单控件
        public Builder addItem(Drawable icon) {
            ImageButton view = (ImageButton) inflater.inflate(R.layout.menu_item, null, false);
            view.setImageDrawable(icon);
//            TypedValue value = new TypedValue();
//            ctx.getTheme().resolveAttribute(R.attr.selectableItemBackgroundBorderless, value, true);
//            view.setBackgroundResource(value.resourceId);
            addItem(view);
            return this;
        }

        public Builder addItem(int iconResId) {
            Drawable icon = ctx.getResources().getDrawable(iconResId);
            addItem(icon);
            return this;
        }

        public Builder addItem(View customView) {
            // 利用item对象添加控件
            Item item = new Item();
            item.setView(customView);       // 控件
            item.setPosition(items.size()); // 控件排列的位置
            item.getView().setTag(item);    // 给该控件打上标记
            items.add(item);                // item添加到集合中

            return this;
        }
        // 父容器
        public Builder attach(FilterMenuLayout view) {
            this.layout = view;
            return this;
        }
        // 构造操作类
        public FilterMenu build() {
            FilterMenu menu = new FilterMenu();
            menu.setItems(items);
            menu.setListener(this.listener);
            menu.setMenuLayout(this.layout);
            return menu;
        }
    }

    public static class Item {
        private View view;  // 子菜单控件
        private int x;
        private int y;
        private int position;   // 所处的排列位置
        private Rect bounds = new Rect(0, 0, 0, 0);

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
            view.setAlpha(0f);
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setBounds(int left, int top, int right, int bottom) {
            this.bounds.set(left, top, right, bottom);
        }

        public Rect getBounds() {
            return bounds;
        }

        public void setBounds(Rect bounds) {
            this.bounds = bounds;
        }
    }
}
