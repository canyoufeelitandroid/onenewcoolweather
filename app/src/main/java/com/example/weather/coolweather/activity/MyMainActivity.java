package com.example.weather.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

import com.example.weather.coolweather.R;

import org.litepal.tablemanager.Connector;

/**
 * Created by 64088 on 2017/5/17.
 */

public class MyMainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_my);
        Connector.getDatabase();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null)!=null){
            Intent i=new Intent(MyMainActivity.this,WeatherActivity.class);
            startActivity(i);
            finish();
        }
    }


}
