package com.cbb.elasticdownload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.cbb.elasticdownload.VectorCompat.AnimatedVectorDrawable;


/**
 * Created by thibaultguegan on 15/03/15.
 */
@SuppressLint("AppCompatCustomView")
public class IntroView extends ImageView {

    private static final String LOG_TAG = IntroView.class.getSimpleName();

    public interface EnterAnimationListener {
        public void onEnterAnimationFinished();
    }

    private EnterAnimationListener mListener;

    public IntroView(Context context) {
        this(context,null);
    }

    /**
     * MARK: Constructor
     */



    public IntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setImageResource(R.drawable.avd_start);
        } else {
            AnimatedVectorDrawable drawable = AnimatedVectorDrawable.getDrawable(context, R.drawable.avd_start);
            setImageDrawable(drawable);
        }
    }

    /**
     * MARK: Getters/setters
     */

    public void setListener(EnterAnimationListener listener) {
        mListener = listener;
    }

    /**
     * MARK: Public functions
     */

    public void startAnimation() {


        Drawable drawable = getDrawable();     // 此处获取null
        Animatable animatable = (Animatable) drawable;  // animatable也是null

        AVDWrapper.Callback callback = new AVDWrapper.Callback() {
            @Override
            public void onAnimationDone() {
                Log.d(LOG_TAG, "Enter animation finished");
                mListener.onEnterAnimationFinished();
            }

            @Override
            public void onAnimationStopped() {

            }
        };

        AVDWrapper wrapper = new AVDWrapper(animatable, new Handler(), callback);
        wrapper.start(getContext().getResources().getInteger(R.integer.enter_animation_duration));
    }

}
