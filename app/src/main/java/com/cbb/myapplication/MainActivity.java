package com.cbb.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cbb.myapplication.QrCodeScan.CaptureActivity;
import com.cbb.myapplication.share.ShareActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void elasticDownload(View view){
        Intent intent = new Intent(this, ElasticActivity.class);
        startActivity(intent);
    }

    public void heartLayout(View view) {
        Intent intent = new Intent(this, HeartLayoutActivity.class);
        startActivity(intent);
    }

    public void signature(View view) {
        Intent intent = new Intent(this, SignNameActivity.class);
        startActivity(intent);
    }

    public void loadToast(View view) {
        Intent intent = new Intent(this, LoadToastActivity.class);
        startActivity(intent);
    }

    public void writeText(View view) {
        start(WriteTextActivity.class);
    }

    private void start(Class<?> activityClass){
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    public void percentage(View view){
        start(PercentageBarActivity.class);
    }

    public void goToZbar(View view) {
        start(CaptureActivity.class);
    }

    public void goToShare(View view) {
        start(ShareActivity.class);
    }

    public void goToSideMenu(View view) {
        start(SideMenuActivity.class);
    }

    public void timelyAnimation(View view) {
        start(TimelyAnimationActivity.class);
    }

    public void dotProgress(View view) {
        start(DotProgressActivity.class);
    }
}
