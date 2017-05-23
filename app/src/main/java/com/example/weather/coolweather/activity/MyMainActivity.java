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

    private boolean isFromWeatherActivity;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Connector.getDatabase();
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_my);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        editor=PreferenceManager.getDefaultSharedPreferences(this).edit();
        if(prefs.getString("weather",null)!=null&&!isFromWeatherActivity){
            Intent i=new Intent(MyMainActivity.this,WeatherActivity.class);
            startActivity(i);
            finish();
        }else{
            editor.putString("weather",null);
            editor.apply();
        }
    }


}
