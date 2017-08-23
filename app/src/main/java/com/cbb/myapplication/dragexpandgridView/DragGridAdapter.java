package com.cbb.myapplication.dragexpandgridView;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbb.myapplication.R;
import com.cbb.myapplication.dragexpandgridView.Model.DragIconInfo;
import com.cbb.myapplication.dragexpandgridView.view.CustomBehindView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author chenbb
 * @create 2017/8/17
 * @desc
 */

public class DragGridAdapter extends BaseAdapter {

    private static final int INVALID_POSITION = -100;
    private Context mContext;
    /* 要适配的数据集 */
    private ArrayList<DragIconInfo> mList;
    /* 编辑模式下的gridview */
    private CustomBehindView mCustomBehindView;
    /* 修改的position,默认不存在 */
    private int modifyPosition = INVALID_POSITION;
    /* 隐藏item对应的position */
    private int mHidePosition = INVALID_POSITION;
    /*  */
    private boolean hasModifyedOrder = false;
    /* 用于通知取消编辑模式 */
    private Handler mHandler = new Handler();

    public DragGridAdapter(Context context, ArrayList<DragIconInfo> list, CustomBehindView customBehindView) {

        this.mContext = context;
        this.mList = list;
        this.mCustomBehindView = customBehindView;
    }
    
    /*
    * --------------------------------------------
    *  setter & getter 
    * --------------------------------------------
    */
    public void setModifyPosition(int position) {
        modifyPosition = position;
    }
    
    public boolean isHasModifyedOrder() {
        return hasModifyedOrder;
    }
    
    public void setHasModifyedOrder(boolean hasModifyedOrder) {
        this.hasModifyedOrder = hasModifyedOrder;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        if (convertView == null) {
            viewHold = new ViewHold();
            convertView = View.inflate(mContext, R.layout.gridview_behind_itemview, null);
            viewHold.llContainer = (LinearLayout) convertView.findViewById(R.id.edit_ll);
            viewHold.ivIcon = (ImageView) convertView.findViewById(R.id.icon_iv);
            viewHold.tvName = (TextView) convertView.findViewById(R.id.name_tv);
            viewHold.ivDelete = (ImageButton) convertView.findViewById(R.id.delet_iv);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        DragIconInfo iconInfo = mList.get(position);
        viewHold.ivIcon.setImageResource(iconInfo.getResIconId());
        viewHold.tvName.setText(iconInfo.getName());
        // 删除图标被点击时，该处无效化，删除该处的数据
        viewHold.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPosition = INVALID_POSITION;
//                mCustomBehindView.
            }
        });
        // 显示修改位置上的删除图标
        if (modifyPosition == position) {
            viewHold.llContainer.setBackgroundColor(Color.parseColor("#e5e5e5"));
            viewHold.ivDelete.setVisibility(View.VISIBLE);
        } else {
            viewHold.llContainer.setBackgroundColor(Color.parseColor("#ffffff"));
            viewHold.ivDelete.setVisibility(View.GONE);
        }
        // item的监听，若当前不是处于可修改的位置，取消编辑模式
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != modifyPosition) {
                    modifyPosition = INVALID_POSITION;
                    mCustomBehindView.cancleEditModel();
                }
            }
        });
        
        // 隐藏位置上的item要隐藏起来
        if (position == mHidePosition) {        
            convertView.setVisibility(View.INVISIBLE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
    
    /*
    * --------------------------------------------
    *  public 
    * --------------------------------------------
    */

    /**
     * 将原位置上的数据插入到新位置上，同时他们之间的其他数据依次进位排列或退位排列
     * @param oldPosition   原位置
     * @param newPosition   新位置
     */
    public void reOrderItems(int oldPosition, int newPosition) {
        DragIconInfo temp = mList.get(oldPosition);
        // 依次调换2个位置之间所有的数据
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        mList.set(newPosition, temp);
        modifyPosition = newPosition;       // 切换修改pos到新pos
        hasModifyedOrder = true;            // 已修改指令为true
    }

    public void resetModifyPosition() {
        modifyPosition = INVALID_POSITION;
    }

    public void setHideItem (int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }
    
    public void deleteItem(int deletePosition) {
        mList.remove(deletePosition);
        notifyDataSetChanged();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hasModifyedOrder = true;
                mCustomBehindView.cancleEditModel();
            }
        }, 500);
    }


    /*
    * --------------------------------------------
    *  viewholder
    * --------------------------------------------
    */
    class ViewHold {
        public LinearLayout llContainer;
        public ImageView ivIcon;
        public ImageView ivDelete;
        public TextView tvName;
    }
}
