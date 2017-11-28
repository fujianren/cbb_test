package com.cbb.myapplication.permission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cbb.butterknifelibrary.Bind;
import com.cbb.butterknifelibrary.ButterKnife;
import com.cbb.myapplication.R;


/**
 * 在页面显示前，即onResume时，检测权限，
 * 如果缺少，则进入权限获取页面，接收返回值
 * 若拒绝权限时，直接关闭
 */
public class PermissionsStartActivity extends AppCompatActivity {

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

    @Bind(R.id.toolbar)
    Toolbar mTToolbar;

    private static final int REQUEST_CODE = 0; // 请求码
    private PermissionChecker mPermissionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        ButterKnife.bind(this);

        setSupportActionBar(mTToolbar);
        mPermissionChecker = new PermissionChecker(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时，进入权限配置页面
        if(mPermissionChecker.lacksPermissions(PERMISSIONS)){
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时，关闭页面，缺少主权限，无法运行
        if(requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED){
            finish();
        }
    }
}
