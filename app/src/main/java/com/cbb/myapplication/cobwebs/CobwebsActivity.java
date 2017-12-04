package com.cbb.myapplication.cobwebs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cbb.myapplication.R;

public class CobwebsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobwebs);
        CobwebsView cobwebsView = (CobwebsView) findViewById(R.id.view);

    }
}
