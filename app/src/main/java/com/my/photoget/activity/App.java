package com.my.photoget.activity;

import android.app.Application;
import android.content.Context;

/**
 * Author：mengyuan
 * Date  : 2017/7/27下午2:56
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
