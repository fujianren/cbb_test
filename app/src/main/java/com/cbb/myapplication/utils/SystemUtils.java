package com.zdst.sxcsapp.common.utils.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.zdst.sxcsapp.SxcsApplication;

import java.io.File;

import static android.R.attr.versionCode;

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
            cachePath = SxcsApplication.applicationContext.getExternalCacheDir().getPath();
        } else {
            cachePath = SxcsApplication.applicationContext.getCacheDir().getPath();
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

    /**
     * 获取文件在SD卡中的根路径(一般放一些长时间保存的数据)
     */
    public static String getFilesDir(){
        String path = SxcsApplication.applicationContext.getFilesDir().getPath();
        if (TextUtils.isEmpty(path) && SDCardUtils.isSDCardEnable()){
            path = FileUtils.getExternalStoragePath()+File.separator+"zdst"+File.separator+"sxcs";
        }

        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }

        return path;
    }

    /**
     * 获取图片文件存放在SD卡中的路径
     */
    public static String getImageDir(){
        String filesDir = getFilesDir();
        String imageDir = filesDir + File.separator + "image";
        File file = new File(imageDir);
        if (!file.exists()){
            file.mkdirs();
        }

        return imageDir;
    }

    /**
     * 获取app版本信息对象
     */
    public static PackageInfo getVersionInfo(){

        PackageInfo packageInfo = null;
        try {
            Context applicationContext = SxcsApplication.applicationContext;

            String packageName = applicationContext.getPackageName();

            packageInfo = applicationContext.getPackageManager().getPackageInfo(packageName, 0);

            LogUtils.d("获取版本信息对象成功："+packageInfo);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        }

        return packageInfo;

    }

    /**
     * 获取app当前版本号
     */
    public static int getVersionCode(){
        PackageInfo versionInfo = getVersionInfo();
        return versionInfo == null ? 0 :versionInfo.versionCode;
    }

    /**
     * 获取app当前版本号
     */
    public static String getVersionName(){
        return getVersionInfo().versionName;
    }

}
