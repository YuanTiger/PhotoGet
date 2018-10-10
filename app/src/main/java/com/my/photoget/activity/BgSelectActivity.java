package com.my.photoget.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.my.photoget.R;
import com.my.photoget.constant.Constant;
import com.my.photoget.utils.AppUtils;
import com.my.photoget.utils.UriUtil;

import java.io.File;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author：mengyuan
 * Date  : 2017/7/27下午1:46
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class BgSelectActivity extends BaseActivity implements View.OnClickListener {


    private final int REQUEST_CODE_ALBUM = 101;//相册回调
    private final int REQUEST_CODE_CAMER = 102;//相机回调


    private ImageView iv_bg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg);

        iv_bg = (ImageView) findViewById(R.id.iv_bg);

        findViewById(R.id.bt_change_bg).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_change_bg://切换背景
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请选择图片获取方式");
                builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openPremissionAblum();
                    }
                });
                builder.setNeutralButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openPremissionCamera();
                    }
                });
                builder.create().show();

                break;
        }
    }


    //@AfterPermissionGranted：权限授权回调，当用户在授权之后，会回调带有AfterPermissionGranted对应权限的方法
    @AfterPermissionGranted(Constant.PREMISSION_CAMERA)
    public void openPremissionCamera() {
        if (AppUtils.isOpenPremission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AppUtils.startCamera(BgSelectActivity.this, Constant.bgFile, REQUEST_CODE_CAMER);
        } else {
            EasyPermissions.requestPermissions(BgSelectActivity.this, "您需要打开拍照权限以及读取相册权限", Constant.PREMISSION_CAMERA, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(Constant.PREMISSION_WRITE_EXTERNAL_STORAGE)
    public void openPremissionAblum() {
        if (AppUtils.isOpenPremission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AppUtils.startAlbum(BgSelectActivity.this, REQUEST_CODE_ALBUM);
        } else {
            EasyPermissions.requestPermissions(BgSelectActivity.this, "您需要打开读取相册权限", Constant.PREMISSION_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_ALBUM://相册
                Uri dataUri = data.getData();
                Log.i("mengyuanuri", "相册uri:" + dataUri.getScheme() + ":" + dataUri.getSchemeSpecificPart());
                Bitmap bitmapAlbum = BitmapFactory.decodeFile(UriUtil.getPath(dataUri));
                iv_bg.setImageBitmap(bitmapAlbum);
                break;
            case REQUEST_CODE_CAMER://相机
                File bgPath = Constant.bgFile;
                Log.i("mengyuanuri", "回调的Path:" + bgPath.getPath());
                Bitmap bitmapCamer = BitmapFactory.decodeFile(bgPath.getPath());
                iv_bg.setImageBitmap(bitmapCamer);
                break;
        }
    }
}
