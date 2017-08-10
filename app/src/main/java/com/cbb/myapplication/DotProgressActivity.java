package com.cbb.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cbb.myapplication.progress.DottedProgressBar;

public class DotProgressActivity extends AppCompatActivity {
    DottedProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dot_progress);
        bar = (DottedProgressBar) findViewById(R.id.progress);
        Runnable run = new Runnable(){

            @Override
            public void run() {
                bar.startProgress();      // 开启进度动画
            }
        };
        Handler han = new Handler();
        han.postAtTime(run, 100);       // 100毫秒之后推送到消息队列中
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void stopProgress(View view) {
        bar.stopProgress();
    }

    public void startProgress(View view) {
        bar.startProgress();
    }
}
