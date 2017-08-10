package com.cbb.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.cbb.writetextlibrary.PathTextView;

public class WriteTextActivity extends AppCompatActivity {
    private PathTextView mPathTextView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_text);
        mPathTextView = (PathTextView) findViewById(R.id.path);
        mEditText = (EditText) findViewById(R.id.edit);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPathTextView.init(mEditText.getText().toString());
            }
        });
    }
}
