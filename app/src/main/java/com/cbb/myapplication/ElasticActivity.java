package com.cbb.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cbb.elasticdownload.ElasticDownloadView;
import com.cbb.elasticdownload.ProgressDownloadView;

public class ElasticActivity extends AppCompatActivity {
    ElasticDownloadView mElasticDownloadView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elastic);
        mElasticDownloadView = (ElasticDownloadView) findViewById(R.id.elasticDownload);
    }

    /**
     * 点击，进行下载成功的演示
     * @param view
     */
    public void success(View view){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.startIntro();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.success();
            }
        }, 2 * ProgressDownloadView.ANIMATION_DURATION_BASE);

    }

    /**
     * 点击，进行下载失败的演示
     * @param view
     */
    public void fails(View view){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.startIntro();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.setProgress(45);
            }
        }, 2 * ProgressDownloadView.ANIMATION_DURATION_BASE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.fail();
            }
        }, 3 * ProgressDownloadView.ANIMATION_DURATION_BASE);

    }
}
