package com.cbb.myapplication.filterMenu;

/**
 *
 */
public interface IMenu {
    // 是否带动画地收缩
    void collapse(boolean animate);
    // 是否带动画地展开
    void expand(boolean animate);
    // 是否带动画地触发
    void toggle(boolean animate);
    // 设置菜单容器
    void setMenuLayout(FilterMenuLayout layout);
}
