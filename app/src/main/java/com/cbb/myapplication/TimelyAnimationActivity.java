package com.cbb.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.cbb.myapplication.timelyAnimation.DoubleNumberView;
import com.cbb.myapplication.timelyAnimation.SecondsNumberView;

import java.util.Timer;
import java.util.TimerTask;

public class TimelyAnimationActivity extends AppCompatActivity {

    private DoubleNumberView hourNumberView, minuteNumberView;
    private SecondsNumberView secondsNumberView;

    private int secondsNumber = 0;
    private int minuteNumber = 0;
    private int hourNumber = 0;

    int number = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            secondsNumberView.setText(secondsNumber); //
            minuteNumberView.setText(minuteNumber);
            hourNumberView.setText(hourNumber);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timely_animation);
        hourNumberView = (DoubleNumberView) findViewById(R.id.hour_number_view);
        minuteNumberView = (DoubleNumberView) findViewById(R.id.minute_number_view);
        secondsNumberView = (SecondsNumberView) findViewById(R.id.seconds_number_view);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                number++;
//                if (number > 9)
//                {
//                    number = 0;
//                }

                if (number < 60) {
                    secondsNumber = number;//
                } else if (number == 60) {
                    number = 0;
                    secondsNumber = number;
                    if (minuteNumber < 60) {
                        minuteNumber += 1;
                    } else if (minuteNumber == 60) {
                        minuteNumber = 0;
                        hourNumber += 1;
                    }
                }

                handler.obtainMessage().sendToTarget();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
    }
}
