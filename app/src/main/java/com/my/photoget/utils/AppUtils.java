package com.my.photoget.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.my.photoget.activity.App;
import com.my.photoget.constant.Constant;

import java.io.File;

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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
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
     * MediaDocument  isDocumentUri - true
     *
     *
     * @param activity
     * @param requestCode
     */
    public static void startAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        //        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        // 设置文件类型
        intent.setType("image/*");
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
     * 检测权限是否开启
     *
     * @param perms
     * @return
     */
    public static boolean isOpenPremission(@NonNull String... perms) {
        return EasyPermissions.hasPermissions(App.context, perms);
    }
}
