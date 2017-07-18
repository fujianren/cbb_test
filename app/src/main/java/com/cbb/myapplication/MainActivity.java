package com.cbb.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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


}
