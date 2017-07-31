package com.jiahuan.timelyanimation.sample;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jiahuan.timelyanimation.NumberSwitchView;

/**
 * 自定义的容器，左右相邻的2个数字控件
 */
public class DoubleNumberView extends LinearLayout {

    public DoubleNumberView(Context context) {
        super(context);
        init(context);
    }

    public DoubleNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }



    private int tenNumber = -1;

    private NumberSwitchView mNumberSwitchViewTen;      // 十位数的view
    private NumberSwitchView mNumberSwitchView;         // 个位数的View
    private void init(Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.double_item_view, null, false);
        addView(view);


        mNumberSwitchViewTen =  (NumberSwitchView)view.findViewById(R.id.numberswitchview_ten);

        mNumberSwitchView = (NumberSwitchView)view.findViewById(R.id.numberswitchview_one);
    }

    public void setText(int number){
        if(number < 100){
            if(number<10){

                if(0 !=tenNumber) {
                    tenNumber = 0;
                    mNumberSwitchViewTen.setNumberColor(Color.rgb(10, 10, 10));
                    mNumberSwitchViewTen.setNumberBGColor(Color.WHITE);
                    mNumberSwitchViewTen.animateTo(0);
                }

                mNumberSwitchView.setNumberColor(Color.rgb(10, 10, 10));
                mNumberSwitchView.setNumberBGColor(Color.WHITE);
                mNumberSwitchView.animateTo(number);
            }else{

                String numString = String.valueOf(number);
                int tenIndex = Integer.valueOf(numString.substring(0, 1));
                int oneIndex = Integer.valueOf(numString.substring(1, 2));

                if(tenIndex !=tenNumber) {
                    tenNumber = tenIndex;
                    mNumberSwitchViewTen.setNumberColor(Color.rgb(10, 10, 10));
                    mNumberSwitchViewTen.setNumberBGColor(Color.WHITE);
                    mNumberSwitchViewTen.animateTo(tenIndex);
                }

                mNumberSwitchView.setNumberColor(Color.rgb(10, 10, 10));
                mNumberSwitchView.setNumberBGColor(Color.WHITE);
                mNumberSwitchView.animateTo(oneIndex);

            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
