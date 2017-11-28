package com.cbb.myapplication.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.cbb.myapplication.R;

public class PermissionsActivity extends AppCompatActivity {

    public static final int PERMISSIONS_GRANTED = 0;    // 授权
    public static final int PERMISSIONS_DENIED = 1;     // 拒绝
    /* 权限请求码 */
    private static final int PERMISSION_REQUEST_CODE = 0;
    /* intent的传递码 */
    private static final String EXTRA_PERMISSIONS =
            "me.chunyu.clwang.permission.extra_permission";

    private static final String PACKAGE_URL_SCHEME = "package:";    // 方案
    private PermissionChecker mChecker;     // 自定义权限检测器
    private boolean isRequireCheck;         // 是否需要系统权限检测

    /* 启动当前权限页面的公开接口 */
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions){
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)){
            throw new RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动！");
        }

        setContentView(R.layout.activity_permissions);

        mChecker = new PermissionChecker(this);
        isRequireCheck = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            String[] permissions = getPermissions();
            if (mChecker.lacksPermissions(permissions)) {   // 若缺少权限，就请求权限
                requestPermissions(permissions);
            } else {
                allPermissionGranted();     // 全部权限都已获取
            }
        } else {
            isRequireCheck = true;
        }
    }

    /* 全部权限均已获取 */
    private void allPermissionGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    /* 请求权限兼容低版本 */
    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /* 返回传递的权限参数 */
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)){
            isRequireCheck = true;
            allPermissionGranted();
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    /* 显示缺失权限提示 */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("help boy!");
        builder.setMessage("================-0-0-0--0-0-0-");

        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettins();
            }
        });
        builder.show();
    }

    /* 启动应用的设置 */
    private void startAppSettins() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    /* 是否含有所有权限 */
    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults){
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
