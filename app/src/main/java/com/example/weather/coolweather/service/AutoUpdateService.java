package com.example.weather.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.weather.coolweather.receiver.AutoUpdateReceiver;

/**
 * Created by 64088 on 2017/3/18.
 */

public class AutoUpdateService extends Service {
    private long updateHour=8*60*60*1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        boolean updata_auto=sharedPreferences.getBoolean("auto_update",false);
        int hourIndex=sharedPreferences.getInt("update_hour_index",2);
        if(hourIndex==0){
            updateHour=2*60*60*1000;
        }else if(hourIndex==1){
            updateHour=4*60*60*1000;
        }
        if(updata_auto) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateWeather();
                }
            }).start();
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long everyTime = SystemClock.elapsedRealtime() +updateHour;
            //long everyTime= SystemClock.elapsedRealtime()+5*1000;
            Intent i = new Intent(this, AutoUpdateReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, everyTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prfs= PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("data","更新天气一次");
        String weatherCode=prfs.getString("weather_code","");
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
//        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                Utility.handleWeatherResponse(AutoUpdateService.this,response);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                e.printStackTrace();
//            }
//        });
    }
}
