package com.cbb.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cbb.myapplication.progress.LoadingButton;

public class LoadingActivity extends AppCompatActivity {

    private LoadingButton lbtnDefault;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        initView();
    }

    private void initView() {
        lbtnDefault = (LoadingButton) findViewById(R.id.lbtn_default);
        btnStart = (Button) findViewById(R.id.btn_start);
        lbtnDefault.setCallback(new LoadingButton.Callback() {
            @Override
            public void complete() {
                Toast.makeText(LoadingActivity.this, "下载完成,可以在这里写完成的回调方法", Toast.LENGTH_SHORT).show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lbtnDefault.setTargetProgress(360);
            }
        });
        
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
