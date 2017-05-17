package com.example.weather.coolweather.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
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
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null)!=null){
            Intent i=new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(i);
            finish();
        }
    }
}
