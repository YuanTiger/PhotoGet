package com.my.photoget.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.my.photoget.BuildConfig;
import com.my.photoget.activity.App;
import com.my.photoget.bean.CropBean;
import com.my.photoget.constant.Constant;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author：mengyuan
 * Date  : 2017/7/27下午1:55
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class AppUtils {

    /**
     * 启动相机
     *
     * @param activity
     * @param file
     * @param requestCode
     */
    public static void startCamer(Activity activity, File file, int requestCode) {
        if (!isHaveCame(MediaStore.ACTION_IMAGE_CAPTURE)) {
            Toast.makeText(activity, "该手机没有安装相机", Toast.LENGTH_SHORT).show();
            return;
        }
        //指定相机意图
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //如果TargetSdkVersion >= 24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //getUriForFile(Context context, String authority, File file)
            //authority需要和AndroidManifest中的authorities属性保持一致
            Uri photoUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", file);

            Log.i("mengyuanuri", "SDK>=24相机拍摄存储的uri:" + photoUri.getScheme() + ":" + photoUri.getSchemeSpecificPart());
            //申请权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //设置相片保存的地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        } else {
            //设置相片保存的地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        }
        activity.startActivityForResult(intent, requestCode);


    }

    /**
     * 启动相册
     * <p>
     * <p>
     * Pick:::::::::
     * Uri: content://media/external/images/media/2116
     * Path:/storage/emulated/0/temp/kouliang_avatar.jpg
     * isDocumentUri - false
     * 从所有数据项目中选择一个项目，返回所选的内容
     * <p>
     * <p>
     * <p>
     * GET_CONTENT::
     * Uri: content://com.android.providers.media.documents/document/image:2116
     * Path:/storage/emulated/0/temp/kouliang_avatar.jpg
     * isDocumentUri - true     MediaDocument
     *
     * @param activity
     * @param requestCode
     */
    public static void startAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_PICK);
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        // 设置文件类型
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 启动裁剪
     *
     * @param activity
     * @param cropBean
     * @param requestCode
     */
    public static void startCrop(Activity activity, CropBean cropBean, int requestCode) {
        if (cropBean == null) {
            Toast.makeText(activity, "参数对象为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cropBean.dataUri == null) {
            Toast.makeText(activity, "请设置裁剪图片的Uri：dataUri", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        //配置一系列裁剪参数
        intent.putExtra("outputX", cropBean.outputX);
        intent.putExtra("outputY", cropBean.outputY);
        intent.putExtra("scale", cropBean.scale);
        intent.putExtra("aspectX", cropBean.aspectX);
        intent.putExtra("aspectY", cropBean.aspectY);
        intent.putExtra("outputFormat", cropBean.outputFormat);
        intent.putExtra("return-data", cropBean.isReturnData);

        //如果TargetSdkVersion >= 24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //申请权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //将数据Uri转化成FileProvider的Uri
            File dataFile = new File(UriUtil.getPath(cropBean.dataUri));
            cropBean.dataUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", dataFile);

            Log.i("mengyuanuri", "SDK>=24裁剪的Uri:" + cropBean.dataUri.getScheme() + ":" + cropBean.dataUri.getSchemeSpecificPart());
            Log.i("mengyuanuri", "SDK>=24裁剪保存的Uri:" + cropBean.saveUri.getScheme() + ":" + cropBean.saveUri.getSchemeSpecificPart());
        }
        //设置要裁剪的图片Uri
        intent.setDataAndType(cropBean.dataUri, "image/*");
        // 如果不需要返回Btimap，则需要指定图片保存的Uri
        if (!cropBean.isReturnData) {
            if (cropBean.saveUri == null) {
                Toast.makeText(activity, "请指定保存裁剪图片地址：saveUri", Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropBean.saveUri);
        }

        activity.startActivityForResult(intent, requestCode);

    }

    /**
     * 根据requestCode 转化 权限名称
     *
     * @param requestCode
     * @return
     */
    public static String getPermissionNameByCode(int requestCode) {
        switch (requestCode) {
            case Constant.PREMISSION_SENT_SMS:
                return "读取短信";
            case Constant.PREMISSION_USER_LOCATION:
                return "获取地理位置";
            case Constant.PREMISSION_READ_PHONE_STATE:
                return "读取手机硬件状态";
            case Constant.PREMISSION_READ_CONTACTS:
                return "读取联系人";
            case Constant.PREMISSION_CAMERA:
                return "相机";
            case Constant.PREMISSION_WRITE_EXTERNAL_STORAGE:
                return "保存数据";
            default:
                return "";
        }
    }

    /**
     * 判断某个意图是否存在
     */
    public static boolean isHaveCame(String intentName) {
        PackageManager packageManager = App.context.getPackageManager();
        Intent intent = new Intent(intentName);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 检测权限是否开启
     *
     * @param perms
     * @return
     */
    public static boolean isOpenPremission(@NonNull String... perms) {
        return EasyPermissions.hasPermissions(App.context, perms);
    }
}
