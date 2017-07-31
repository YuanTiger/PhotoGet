package com.my.photoget.constant;

import android.os.Environment;

import java.io.File;

/**
 * Author：mengyuan
 * Date  : 2017/7/27下午2:52
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class Constant {


    //所有私有权限
    public static final int PREMISSION_SENT_SMS = 34311;
    public static final int PREMISSION_USER_LOCATION = 34312;
    public static final int PREMISSION_READ_PHONE_STATE = 34313;
    public static final int PREMISSION_READ_CONTACTS = 34314;
    public static final int PREMISSION_CAMERA = 34315;
    public static final int PREMISSION_WRITE_EXTERNAL_STORAGE = 34316;



    public static File bgPath = new File(Environment.getExternalStorageDirectory(), "photo_bg.jpg");

}
