package com.example.weather.coolweather.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Window;

import com.example.weather.coolweather.R;

/**
 * Created by 64088 on 2017/5/15.
 */

public class MainActivity extends BaseActivity {
    private Fragment fragment;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }
}
