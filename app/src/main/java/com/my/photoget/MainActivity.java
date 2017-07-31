package com.my.photoget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.my.photoget.activity.BaseActivity;
import com.my.photoget.activity.BgSelectActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.bt_bg).setOnClickListener(this);
        findViewById(R.id.bt_head_portrait).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_bg://选择背景
                startActivity(new Intent(MainActivity.this, BgSelectActivity.class));
                break;
            case R.id.bt_head_portrait://选择头像
                break;
        }
    }
}
