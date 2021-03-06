package com.example.weather.coolweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.weather.coolweather.R;
import com.example.weather.coolweather.model.gsonofweather.Weather;
import com.example.weather.coolweather.receiver.AutoUpdateReceiver;
import com.example.weather.coolweather.util.HttpUtil;
import com.example.weather.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
        boolean update_auto=sharedPreferences.getBoolean("auto_update",false);
        int hourIndex=sharedPreferences.getInt("update_hour_index",2);
        if(hourIndex==0){
            updateHour=2*60*60*1000;
        }else if(hourIndex==1){
            updateHour=4*60*60*1000;
        }
        if(update_auto) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateWeather();
                    updateBingPic();
                }
            }).start();

           NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            builder.setContentTitle("Notification");
            //builder.setContentText("自定义通知栏示例");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            //Notification noti=new Notification(R.drawable.cloudy,"Cool",SystemClock.currentThreadTimeMillis());
            //noti.flags = Notification.FLAG_INSISTENT;
            RemoteViews remoteView=new RemoteViews(this.getPackageName(), R.layout.front_server);
            //remoteView.setImageViewResource(R.id.front_pic,R.drawable.cloudy);
            remoteView.setTextViewText(R.id.front_city_name,"成都");
            remoteView.setTextViewText(R.id.front_city_weather,"晴转多云");
            remoteView.setTextViewText(R.id.front_city_temp,"24℃");
            remoteView.setTextViewText(R.id.front_city_time,"更新时间：19：10");
            //noti.contentView=remoteView;
            builder.setContent(remoteView);





            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long everyTime = SystemClock.elapsedRealtime() +updateHour;
            //long everyTime= SystemClock.elapsedRealtime()+5*1000;
            Intent i = new Intent(this, AutoUpdateReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, everyTime, pi);

//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//            builder.setSmallIcon(R.drawable.add);
//            builder.setContentTitle("前台服务");
//            builder.setContentText("这是前台服务");
//            builder.setContentIntent(pi);
//            Notification notification = builder.build();
//           //启动到前台
//            startForeground(1, notification);
            builder.setContentIntent(pi);
            Notification notification = builder.build();
            startForeground(1,notification);


        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prfs= PreferenceManager.getDefaultSharedPreferences(this);
        Log.i("data","更新天气一次");
        String weatherString=prfs.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析天气信息
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+
                    "&key=ccfb286742e249c0a354d1eeb531bef0";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(responseText);
                    if(weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor=PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }

                }
            });
        }

    }

    private void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

            }
        });
    }
}
