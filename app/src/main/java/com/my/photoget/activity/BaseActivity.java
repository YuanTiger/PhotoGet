package com.my.photoget.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.my.photoget.utils.AppUtils;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author：mengyuan
 * Date  : 2017/7/27下午2:48
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    //--------------------------------API 23权限升级--------------------------------
    //--------------------------------API 23权限升级--------------------------------
    //--------------------------------API 23权限升级--------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("mengyuanrequest", "权限成功:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i("mengyuanrequest", "权限获取失败:" + requestCode + ":" + perms.size());
        //如果权限已经被拒绝，则弹出提示框提示用户打开权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限被拒绝啦")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消")
                    .setRequestCode(requestCode)
                    .build()
                    .show();
        }
    }

}
