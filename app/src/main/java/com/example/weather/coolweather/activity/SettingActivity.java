package com.example.weather.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.weather.coolweather.R;
import com.example.weather.coolweather.service.AutoUpdateService;

/**
 * Created by 64088 on 2017/3/20.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private Switch aSwitch;
    private Spinner spinner;
    private Button exitBtn;
    private Button backBtn;
    private int updateHourIndex=0;
    private boolean autoUpdate;
    private SharedPreferences pfs;
    //SharedPreferences.Editor必须初始化，不能直接用pfs.eidt()代替
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        pfs= PreferenceManager.getDefaultSharedPreferences(this);
        editor=pfs.edit();
        if(!(pfs.getInt("update_hour_index",-1)==-1)){
            updateHourIndex=pfs.getInt("update_hour_index",0);
        }else {
            editor.putInt("update_hour_index",0);
            editor.commit();
        }
        autoUpdate=pfs.getBoolean("auto_update",false);
        setContentView(R.layout.setting_layout);
        Log.i("data","create");
        initUI();

    }

    private void initUI(){
        aSwitch=(Switch)findViewById(R.id.auto_update);
        spinner=(Spinner)findViewById(R.id.update_hour);
        spinner.setDropDownVerticalOffset(55);
        exitBtn=(Button)findViewById(R.id.exit);
        backBtn=(Button)findViewById(R.id.back_before);
        aSwitch.setChecked(autoUpdate);
        spinner.setSelection(updateHourIndex,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("update_hour_index",i);
                editor.commit();
                Log.i("data",""+i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        exitBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        boolean update_auto=aSwitch.isChecked();
        editor.putBoolean("auto_update",update_auto);
        editor.commit();
        Intent serviceIntent=new Intent(SettingActivity.this, AutoUpdateService.class);
        startService(serviceIntent);
        SettingActivity.this.finish();
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_before:
                SettingActivity.this.finish();
                break;
            case R.id.exit:
                ActivityCollector.finishAll();
                finish();
                break;
        }
    }
}
