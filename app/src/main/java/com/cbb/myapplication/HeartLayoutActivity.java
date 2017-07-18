package com.cbb.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cbb.heartlibrary.HeartLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class HeartLayoutActivity extends AppCompatActivity {
    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private HeartLayout mHeartLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_layout);
        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        // 计时器
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.addHeart(randomColor());
                    }
                });
            }
        }, 500, 200);
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }


}
