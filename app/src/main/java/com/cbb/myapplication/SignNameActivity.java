package com.cbb.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cbb.myapplication.utils.SystemUtils;
import com.cbb.sinaturelibrary.LinePathView;

import java.io.File;
import java.io.IOException;

public class SignNameActivity extends AppCompatActivity {

    private LinePathView signView;
    private TextView tvTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_name);
        initView();
    }

    private void initView() {
        signView = (LinePathView) findViewById(R.id.sign_view);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        signView.setTextView(tvTip);
    }

    public void finish(View view) {
        try {
            if (!signView.isTouched()) {
                Toast.makeText(this, "你还没有签名", Toast.LENGTH_SHORT).show();
                return;
            }
            String path = SystemUtils.getCacheImageDir() + File.separator + System.currentTimeMillis() + ".jpg";
            signView.save(path);
            Toast.makeText(this, "结束！！！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset(View view) {
        tvTip.setVisibility(View.VISIBLE);
        signView.clear();
    }
}
