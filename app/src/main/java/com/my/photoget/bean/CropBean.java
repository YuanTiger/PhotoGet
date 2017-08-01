package com.my.photoget.bean;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;

/**
 * Author：mengyuan
 * Date  : 2017/8/1上午10:10
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class CropBean {

    //要裁剪的图片Uri
    public Uri dataUri;

    //裁剪宽度
    public int outputX;
    //裁剪高度
    public int outputY;

    //X方向上的比例
    public int aspectX;
    //Y方向上的比例
    public int aspectY;

    //是否保留比例
    public boolean scale;

    //是否将数据保存在Bitmap中返回
    public boolean isReturnData;
    //相应的Bitmap数据
    public Parcelable returnData;

    //如果不需要将图片在Bitmap中返回，需要传递保存图片的Uri
    public Uri saveUri;

    //圆形裁剪区域
    public String circleCrop;

    //图片输出格式，默认JPEG
    public String outputFormat = Bitmap.CompressFormat.JPEG.toString();

    //是否取消人脸识别
    public boolean noFaceDetection;

    /**
     * 根据宽高计算裁剪比例
     */
    public void caculateAspect() {

        scale = true;

        if (outputX == outputY) {
            aspectX = 1;
            aspectY = 1;
            return;
        }
        float proportion = (float) outputX / (float) outputY;

        aspectX = (int) (proportion * 100);
        aspectY = 100;
    }
}
