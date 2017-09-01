package com.cbb.myapplication.user;


import android.app.Application;

import com.cbb.butterknifelibrary.BuildConfig;
import com.cbb.butterknifelibrary.ButterKnife;

public class SimpleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    ButterKnife.setDebug(BuildConfig.DEBUG);
  }
}
