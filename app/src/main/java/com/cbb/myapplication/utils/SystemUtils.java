package com.cbb.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import java.io.File;

/**
 * Created by ZDST-03 on 2016/11/22 10 : 40
 */
public class SystemUtils {

    /**
     * 判断网络是否可用
     *
     * @param context Context对象
     */
    public static Boolean isNetworkReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.isAvailable());
    }

    //获取系统版本号
    public static int getSDKVersionNumber() {

        return Build.VERSION.SDK_INT;
    }

    //版本系统版本是否大于等于23
    public static boolean isVersionOver23() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 获取缓存文件路径(一般存放临时缓存数据)
     *
     * @return
     */
    public static String getCacheDir(){
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
//            cachePath = SxcsApplication.applicationContext.getExternalCacheDir().getPath();
        } else {
//            cachePath = SxcsApplication.applicationContext.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获取缓存图片文件路径
     */
    public static String getCacheImagePath(String imageName){
        return getCacheImageDir()+ File.separator+imageName;
    }

    /**
     * 获取缓存图片文件夹路径
     */
    public static String getCacheImageDir(){
        String dirPath = getCacheDir() + File.separator + "image";
        File file = new File(dirPath);
        if (!file.exists()){
            file.mkdirs();
        }
        return dirPath;
    }



}
