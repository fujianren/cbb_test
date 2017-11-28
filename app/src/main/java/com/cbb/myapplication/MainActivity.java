package com.cbb.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cbb.myapplication.QrCodeScan.CaptureActivity;
import com.cbb.myapplication.permission.PermissionsStartActivity;
import com.cbb.myapplication.share.ShareActivity;
import com.cbb.myapplication.taiji.EasyCircleActivity;
import com.cbb.myapplication.taiji.TaijiActivity;
import com.cbb.myapplication.user.SimpleActivity;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String s = "1001011,1000101,1011001,111010,1000010,1000100,1000101,110101,110000,111001,110001,1000001,111000,1000011,1000100,110100,1000010,111000,1000010,110000,110010,1000011,1000110,110100,110000,1000011,110101,1000101,110000,1000110,110000,111000,110101,110001,1000011,1000101";

        String[] split = s.split(",");
        byte[] bytes = new byte[split.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Byte.parseByte(split[i], 2);
            Log.d(TAG, "onCreate: " + bytes[i]);
        }
        String s1 = null;
        try {
            s1 = new String(bytes, "GB2312");
        } catch (UnsupportedEncodingException e) {

        }

        Log.d(TAG, "onCreate: " + s1);


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

    public void filterMenu(View view) {
        start(FilterMenuActivity.class);
    }

    public void loading(View view){
        start(LoadingActivity.class);
    }

    public void goDragView(View view){
        start(DragGridViewActivity.class);
    }

    public void disPlayButterknife(View view){
        start(SimpleActivity.class);
    }

    public void goDialogDemo(View view){
        start(DialogDemoActivity.class);
    }

    public void goViewDemo(View view){
        start(ViewDemoActivity.class);
    }

    public void goMapDemo(View view){
        start(BDActivity.class);
    }

    public void goTaijiDemo(View view){
        start(TaijiActivity.class);
    }

    public void goCircleDemo(View view){
        start(EasyCircleActivity.class);
    }

    public void goRecyclerViewDemo(View view) {
        start(EasyRecyclerViewActivity.class);
    }

    public void goPermissionDemo(View view) {
        start(PermissionsStartActivity.class);
    }
}
