package com.example.weather.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.coolweather.R;
import com.example.weather.coolweather.controls.TitlePopup;
import com.example.weather.coolweather.model.ActionItem;
import com.example.weather.coolweather.service.AutoUpdateService;
import com.example.weather.coolweather.util.HttpCallbackListener;
import com.example.weather.coolweather.util.HttpUtil;
import com.example.weather.coolweather.util.Utility;

/**
 * Created by 64088 on 2017/3/17.
 */

public class WeatherActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private RelativeLayout weatherLayout;

    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDataText;
    private Button switchCityBtn;
    private Button refreshWeatherBtn;

    //自定义下拉菜单初始化
    private TitlePopup titlePopup;
    //记录第一次按下返回的时间（毫秒）
    long firstTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        initUI();
        String countyCode=getIntent().getStringExtra("county_code");
        String weatherCode=getIntent().getStringExtra("weather_code");
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else if(!TextUtils.isEmpty(weatherCode)){
            //有天气代号就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(weatherCode);
        }else{
            showWeather();
        }
    }

    private void initUI(){
        titlePopup=new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        titlePopup.addAction(new ActionItem(this,R.string.choose));
        titlePopup.addAction(new ActionItem(this,R.string.setting));
        titlePopup.addAction(new ActionItem(this,R.string.citymanager));
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if(item.mTitle==getText(R.string.choose)){
                    Intent switchIntent=new Intent(WeatherActivity.this,ChooseActivity.class);
                    switchIntent.putExtra("from_weather_activity",true);
                    startActivity(switchIntent);
                    finish();
                }else if(item.mTitle==getText(R.string.setting)){
                    Intent settingIntent=new Intent(WeatherActivity.this,SettingActivity.class);
                    startActivity(settingIntent);
                }else {
                    Intent managerIntent=new Intent(WeatherActivity.this,CityManagerActivity.class);
                    startActivity(managerIntent);
                }
            }
        });

        weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText=(TextView)findViewById(R.id.city_name);
        publishText=(TextView)findViewById(R.id.publish_text);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        temp1Text=(TextView)findViewById(R.id.temp1);
        temp2Text=(TextView)findViewById(R.id.temp2);
        currentDataText=(TextView)findViewById(R.id.current_data);
        switchCityBtn=(Button)findViewById(R.id.switch_city);
        refreshWeatherBtn=(Button)findViewById(R.id.refresh_weather);
        weatherLayout=(RelativeLayout)findViewById(R.id.weather_layout_background);
        switchCityBtn.setOnClickListener(this);
        refreshWeatherBtn.setOnClickListener(this);
    }

    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }

    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }

    private  void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //从返回的额数据中解析出天气代号
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length==2){
                            String weatherCode=array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    Log.i("data","处理服务器返回的天气信息");
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                publishText.setText("同步失败");
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取天气信息，并显示在页面上
     */
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        currentDataText.setText(prefs.getString("current_data",""));
        publishText.setText(getText(R.string.today)+prefs.getString("publish_time","")+getText(R.string.publish));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        String desp_weather=prefs.getString("weather_desp","");
        showBackground(desp_weather);

        //启动服务，用于后台自动更新天气信息
        Intent serviceIntent=new Intent(this, AutoUpdateService.class);
        startService(serviceIntent);
    }

    /**
     * 根据天气设置不同的背景图
     *
     */
    private void showBackground(String desp){
        switch (desp){
            case "晴":
                weatherLayout.setBackgroundResource(R.drawable.sun_day);
                break;
            case "小雨":
                weatherLayout.setBackgroundResource(R.drawable.rain_day);
                break;
            case "中雨":
                weatherLayout.setBackgroundResource(R.drawable.center_rain_day);
                break;
            case "阴":
                weatherLayout.setBackgroundResource(R.drawable.cloudy_more_day);
                break;
            case "多云":
                weatherLayout.setBackgroundResource(R.drawable.cloudy_less_day);
                break;
            default:
                weatherLayout.setBackgroundColor(getResources().getColor(R.color.normalColor));
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.switch_city:
                titlePopup.show(view);
                break;
            case R.id.refresh_weather:
                //从sharedPreference里获得weatherCode,然后重新获取刷新天气
                publishText.setText(getText(R.string.loading));
                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=sharedPreferences.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }

    }

    /**
     * 双击退出程序
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            long secondTime=System.currentTimeMillis();
            if(secondTime-firstTime>800){//如果两次按键时间间隔大于800毫秒，则不退出
                Toast toast=Toast.makeText(WeatherActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT);
                toast.getView().setBackgroundColor(Color.parseColor("#FFCC00"));

                toast.show();
                firstTime=secondTime;
                return true;
            }else{
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
