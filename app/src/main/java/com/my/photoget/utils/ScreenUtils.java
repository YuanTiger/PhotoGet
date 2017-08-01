package com.my.photoget.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.my.photoget.activity.App;

/**
 * Author：mengyuan
 * Date  : 2017/8/1上午10:52
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class ScreenUtils {

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = getDisplayMetrics(App.context).density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static DisplayMetrics mDm;

    private static DisplayMetrics getDisplayMetrics(Context context) {
        if (mDm == null) {
            if (context != null) {
                mDm = context.getResources().getDisplayMetrics();
            }
            return mDm;
        }
        return mDm;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px
     */
    public static int dp2Px( int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(App.context)) + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp(字体) 的单位 转成为 px
     */
    public static int sp2Px( int spValue) {
        final float fontScale = getDisplayMetrics(App.context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 当前屏幕高度
     */
    public static int getCurrentScreenHeight() {
        return getDisplayMetrics(App.context).heightPixels;
    }

    /**
     * 当前屏幕宽度
     */
    public static int getCurrentScreenWidth() {
        return getDisplayMetrics(App.context).widthPixels;
    }

}
