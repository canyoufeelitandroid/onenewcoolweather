package com.example.weather.coolweather.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by 64088 on 2017/3/20.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
