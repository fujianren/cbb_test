package com.cbb.myapplication.System;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenbb
 * @create 2017/10/20
 * @desc    错误崩溃日志捕获
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {/*  */
    private static final String TAG = "CrashHandler";
    private static final String APP_NAME = "sxcs";

    private Context mContext;
    /* 系统默认的UncaughtException处理类 */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /* 用来存储设备信息和异常信息 */
    private Map<String, String> infos = new HashMap<String, String>();

    /* 用于格式化日期,作为日志文件名的一部分 */
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private String nameString = APP_NAME;

    /* 恶汉式创建实例 */
    private static CrashHandler INSTANCE = new CrashHandler();

    /* 私有化构造函数 */
    private CrashHandler() {
    }

    /* 静态获取对象 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 暴露的初始化方法
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 设备出现未捕获的异常时，调用该方法
     *
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 如果用户没有处理则让系统默认的异常处理器来处理
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                Log.e(TAG, "error : ", e1);
            }
            // 退出程序
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }


    /**
     * 此处进行，自定义的错误处理，错误信息收集，错误报告发送
     *
     * @param e
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable e) {
        if (e == null) return false;

        // 开启另一个子线程处理异常
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出现异常,正在收集日志，即将退出", Toast.LENGTH_SHORT).show();
                Looper.loop();      // 开始轮询
            }
        }.start();

        // 收集设备参数信息
        collectDeviceInfo(mContext);

        // 保存日志文件
        String fileName = saveCrashInfo2File(e);

        return true;
    }



    /* 收集设备参数信息 */
    private void collectDeviceInfo(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);

            if (packageInfo != null) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "an error occured when collect package info", e);
        }

        // 反射获取所有声明的field
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (IllegalAccessException e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /* 保存错误信息到文件中 */
    private String saveCrashInfo2File(Throwable e) {

        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : infos.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);

        Throwable cause = e.getCause();
        while (cause != null){
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        printWriter.close();

        String result = writer.toString();
        Log.e("volley", "crashHandler cause:" + result);
        Log.d("volley", result);

        sb.append(result);

        long timestemp = System.currentTimeMillis();
        String time = formatter.format(new Date());
        String fileName = nameString + "_" + time + "_" + timestemp + ".log";

        //如果当前版本大于23，而且尚未获取到写入SD卡权限，则提示用户
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("volley","CrashHandler 当前版本大于23 并且未获取到SD卡写入权限");
        } else {
            // sd卡已经挂载
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/XXXXXLog/";
                File dir = new File(path);
                if (!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(path + fileName);
                if (!file.exists()){
                    try {
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(sb.toString().getBytes());
                        fos.close();
                    } catch (IOException e1) {
                        Log.e(TAG, "an error occured while writing file...", e1);
                        return null;
                    }
                }
            }
        }
        return fileName;
    }
}
