package com.cbb.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.cbb.loadtoastlibrary.LoadToast;

public class LoadToastActivity extends AppCompatActivity {

    int delay = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_toast);
        final String text = "德玛西亚";
        final LoadToast lt = new LoadToast(this).setText(text).setTranslationY(100);
        //lt.success();
        final ViewGroup root = (ViewGroup) findViewById(android.R.id.content);

        View v = new View(this);
        v.setBackgroundColor(Color.RED);
        root.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lt.show();
            }
        });
        findViewById(R.id.error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lt.error();
            }
        });
        findViewById(R.id.success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lt.success();
            }
        });
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = new View(LoadToastActivity.this);
                v.setBackgroundColor(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
                root.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
            }
        });
    }
}
