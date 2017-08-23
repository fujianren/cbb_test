package com.cbb.myapplication.dragexpandgridView.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cbb.myapplication.R;
import com.cbb.myapplication.dragexpandgridView.Model.DragChildInfo;
import com.cbb.myapplication.dragexpandgridView.Model.DragIconInfo;

import java.util.ArrayList;

/**
 * @author chenbb
 * @create 2017/8/17
 * @desc
 */

public class CustomGroup extends ViewGroup {
    private static final String TAG = "CustomGroup";
    private Context mContext;
    /* 默认的每一行显示的item个数 */
    public static final int COLUMMUN = 3;
    /* 可展开的网格 */
    private CustomeAboveView mCustomeAboveView;
    /* 有镜像的网格 */
    private CustomBehindParent mCustomBehindParent;
    /* 是不是编辑模式 */
    private boolean isEditModel = false;

    //所有以的list
    private ArrayList<DragIconInfo> allInfoList = new ArrayList<DragIconInfo>();
    /**显示的带more的list*/
    private ArrayList<DragIconInfo> homePageInfoList = new ArrayList<DragIconInfo>();
    /**可展开的list*/
    private ArrayList<DragIconInfo> expandInfoList = new ArrayList<DragIconInfo>();
    /**不可展开的list*/
    private ArrayList<DragIconInfo> onlyInfoList = new ArrayList<DragIconInfo>();


    private InfoEditModelListener editModelListener;
    public interface InfoEditModelListener {
        public void onModleChanged(boolean isEditModel);
    }

    /*
    * --------------------------------------------
    *  初始化
    * --------------------------------------------
    */
    public CustomGroup(Context context) {
        this(context, null);
    }

    public CustomGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutParams upParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mCustomeAboveView = new CustomeAboveView(context, this);
        addView(mCustomeAboveView, upParams);

        LayoutParams downParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mCustomBehindParent = new CustomBehindParent(context, this);
        addView(mCustomBehindParent, downParams);

        initData();
    }

    private void initData() {
        setCustonViewClickListener(new CustomeAboveView.CustomAboveViewClickListener() {
            @Override
            public void onSignleClicked(DragIconInfo dragIconInfo) {
                dispatchSingle(dragIconInfo);
            }

            @Override
            public void onChildClicked(DragChildInfo dragChildInfo) {
                dispatchChild((dragChildInfo));
            }
        });
        initIconInfo();
    }


    private void initIconInfo() {
        allInfoList.clear();
        allInfoList.addAll(initAllOriginalInfo());
        getPageInfoList();

        refreshIconInfo();
    }

    private void getPageInfoList() {
        homePageInfoList.clear();
        int count = 0;
        for (DragIconInfo info : allInfoList) {
            if (count < 9) {
                homePageInfoList.add(info);
                count++;
            } else {
                break;
            }
        }
    }

    private ArrayList<DragIconInfo> initAllOriginalInfo() {
        ArrayList<DragIconInfo> iconInfoList = new ArrayList<DragIconInfo>();
        ArrayList<DragChildInfo> childList = initChildList();
        iconInfoList.add(new DragIconInfo(1, "第一个单独", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_ONLY, new ArrayList<DragChildInfo>()));
        iconInfoList.add(new DragIconInfo(2, "第二个单独", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_ONLY, new ArrayList<DragChildInfo>()));
        iconInfoList.add(new DragIconInfo(3, "第三个单独", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_ONLY, new ArrayList<DragChildInfo>()));
        iconInfoList.add(new DragIconInfo(4, "第一个可展开", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList));
        iconInfoList.add(new DragIconInfo(5, "第二个可展开", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList));
        iconInfoList.add(new DragIconInfo(6, "第三个可展开", R.mipmap.ic_launcher, DragIconInfo.CATEGORY_EXPAND, childList));

        return iconInfoList;
    }

    private ArrayList<DragChildInfo> initChildList() {
        ArrayList<DragChildInfo> childList = new ArrayList<DragChildInfo>();
        childList.add(new DragChildInfo(1, "Item1"));
        childList.add(new DragChildInfo(2, "Item2"));
        childList.add(new DragChildInfo(3, "Item3"));
        childList.add(new DragChildInfo(4, "Item4"));
        childList.add(new DragChildInfo(5, "Item5"));
        childList.add(new DragChildInfo(6, "Item6"));
        childList.add(new DragChildInfo(7, "Item7"));
        return childList;
    }

    private void refreshIconInfo() {
        judeHomeInfoValid();

        ArrayList<DragIconInfo> moreInfo = getMoreInfoList(allInfoList, homePageInfoList);
        expandInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_EXPAND);
        onlyInfoList = getInfoByType(moreInfo, DragIconInfo.CATEGORY_ONLY);
        setIconInfoList(homePageInfoList);
    }

    private ArrayList<DragIconInfo> getInfoByType(ArrayList<DragIconInfo> moreInfo, int categoryExpand) {
        ArrayList<DragIconInfo> typeList = new ArrayList<DragIconInfo>();
        for (DragIconInfo info : moreInfo) {
            if (info.getCategory() == categoryExpand) {
                typeList.add(info);
            }
        }
        return typeList;
    }

    private void judeHomeInfoValid() {
        boolean hasMoreInfo = false;
        int posit = 0;
        for(int index = 0;index<homePageInfoList.size();index++){
            DragIconInfo tempInfo = homePageInfoList.get(index);
            if(tempInfo.getId()== CustomeAboveView.MORE){
                hasMoreInfo = true;
                posit = index;
                break;
            }
        }
        if(!hasMoreInfo){
            //没有更多 增加
            homePageInfoList.add(new DragIconInfo(CustomeAboveView.MORE, "更多", R.mipmap.icon_home_more, 0, new ArrayList<DragChildInfo>()));
        }else{
            if(posit!=homePageInfoList.size()-1){
                //不是最后一个
                DragIconInfo moreInfo = homePageInfoList.remove(posit);
                homePageInfoList.add(moreInfo);
            }
        }
    }

    private ArrayList<DragIconInfo> getMoreInfoList(ArrayList<DragIconInfo> allInfoList, ArrayList<DragIconInfo> homePageInfoList) {
        ArrayList<DragIconInfo> moreInfoList = new ArrayList<DragIconInfo>();
        moreInfoList.addAll(allInfoList);
        moreInfoList.removeAll(homePageInfoList);
        return moreInfoList;
    }

    /*
    * --------------------------------------------
    *  自定义三步骤
    * --------------------------------------------
    */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasure = 0;
        int heightMeasure = 0;

        if (isEditModel){
            mCustomBehindParent.measure(widthMeasureSpec, heightMeasureSpec);
            widthMeasure = mCustomBehindParent.getMeasuredWidth();
            heightMeasure = mCustomBehindParent.getMeasuredHeight();
        } else {
            mCustomeAboveView.measure(widthMeasureSpec, heightMeasureSpec);
            widthMeasure = mCustomeAboveView.getMeasuredWidth();
            heightMeasure = mCustomeAboveView.getMeasuredHeight();
        }
        setMeasuredDimension(widthMeasure, heightMeasure);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isEditModel){
            int behindHeight = mCustomBehindParent.getMeasuredHeight();
            mCustomBehindParent.layout(1, 0, r, behindHeight + t);
        } else {
            int aboveHeight = mCustomeAboveView.getMeasuredHeight();
            mCustomeAboveView.layout(1, 0, r, aboveHeight + t);
        }
    }

    /*
    * --------------------------------------------
    *  public
    * --------------------------------------------
    */

    public void setIconInfoList(ArrayList<DragIconInfo> iconInfoList) {
        mCustomeAboveView.refreshIconInfoList(iconInfoList);
        mCustomBehindParent.refreshIconInfoList(iconInfoList);
    }

    public void setEditModel(boolean isEditModel, int position) {
        this.isEditModel = isEditModel;
        if (isEditModel) {
            Log.d(TAG, "setEditModel: 进入编辑模式");
            mCustomeAboveView.setViewCollaps();
            mCustomeAboveView.setVisibility(View.GONE);
            mCustomBehindParent.notifyDataSetChange(mCustomeAboveView.getIconInfoList());
            mCustomBehindParent.setVisibility(View.VISIBLE);
            mCustomBehindParent.drawWindowView(position, mCustomeAboveView.getFirstEvent());
        } else {
            Log.d(TAG, "setEditModel: 退出编辑模式");
            homePageInfoList.clear();
            homePageInfoList.addAll(mCustomBehindParent.getEditList());
            mCustomeAboveView.refreshIconInfoList(homePageInfoList);
            mCustomeAboveView.setVisibility(View.VISIBLE);
            mCustomBehindParent.setVisibility(View.GONE);
            if(mCustomBehindParent.isModifyOrder()){
                mCustomBehindParent.cancleModifyOrderState();
            }
            mCustomBehindParent.resetHidePosition();
        }
        if(editModelListener!=null){
            editModelListener.onModleChanged(isEditModel);
        }
    }

    public boolean isEditModel(){
        return isEditModel;
    }

    public void cancleEditModel(){
        setEditModel(false, 0);
    }

    public void deleteHomePageInfo(DragIconInfo iconInfo) {
        homePageInfoList.remove(iconInfo);
        mCustomeAboveView.refreshIconInfoList(homePageInfoList);
        int category = iconInfo.getCategory();
        switch (category) {
            case DragIconInfo.CATEGORY_ONLY:
                onlyInfoList.add(iconInfo);
                break;
            case DragIconInfo.CATEGORY_EXPAND:
                expandInfoList.add(iconInfo);
                break;
            default:
                break;
        }
        allInfoList.remove(iconInfo);
        allInfoList.add(iconInfo);
    }

    /**
     * 设置点击监听
     * @param custonViewClickListener
     */
    public void setCustonViewClickListener(CustomeAboveView.CustomAboveViewClickListener custonViewClickListener){
        mCustomeAboveView.setGridViewclickListener(custonViewClickListener);
    }

    public void dispatchSingle(DragIconInfo dragInfo) {
        if (dragInfo == null) return;
        Toast.makeText(mContext, "点击了icon"+dragInfo.getName(), Toast.LENGTH_SHORT).show();
    }

    protected void dispatchChild(DragChildInfo childInfo) {
        if (childInfo == null) return;
        Toast.makeText(mContext, "点击了item" + childInfo.getName(), Toast.LENGTH_SHORT).show();
    }

    public void sendEventBehind(MotionEvent ev) {
        mCustomBehindParent.childDispatchTouchEvent(ev);
    }

    public boolean isValideEvent(MotionEvent ev, int scrolly) {
        return mCustomBehindParent.isValideEvent(ev,scrolly);
    }

    public void clearEditDragView() {
        mCustomBehindParent.clearDragView();
    }
}
