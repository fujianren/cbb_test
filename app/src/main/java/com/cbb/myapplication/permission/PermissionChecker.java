package com.cbb.myapplication.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * @author chenbb
 * @create 2017/11/27
 * @desc    权限检查的工具类
 */

public class PermissionChecker {

    private Context mContext;

    public PermissionChecker(Context context){

        mContext = context.getApplicationContext();
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions){
        for (String permission : permissions){
            if (lacksPermission(permission)){
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED;
    }
}


