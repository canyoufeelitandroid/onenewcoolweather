package com.example.weather.coolweather.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.coolweather.R;
import com.example.weather.coolweather.model.gsonofweather.Forecast;
import com.example.weather.coolweather.model.gsonofweather.Weather;
import com.example.weather.coolweather.util.HttpUtil;
import com.example.weather.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 64088 on 2017/3/17.
 */

public class WeatherActivity extends BaseActivity {
//    private LinearLayout weatherInfoLayout;
//    private RelativeLayout weatherLayout;
//
//    private TextView cityNameText;
//    private TextView publishText;
//    private TextView weatherDespText;
//    private TextView temp1Text;
//    private TextView temp2Text;
//    private TextView currentDataText;
//    private Button switchCityBtn;
//    private Button refreshWeatherBtn;
//
//    //自定义下拉菜单初始化
//    private TitlePopup titlePopup;
    //记录第一次按下返回的时间（毫秒）
    long firstTime=0;

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private ImageView picImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        initUI();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather=Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器解析数据
            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            //ccfb286742e249c0a354d1eeb531bef0
            requestWeather(weatherId);
        }
        //加载背景图片
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(WeatherActivity.this).load(bingPic).into(picImg);
        }else{
            loadBingPic();
        }
//        String countyCode=getIntent().getStringExtra("county_code");
//        String weatherCode=getIntent().getStringExtra("weather_code");
//        if(!TextUtils.isEmpty(countyCode)){
//            //有县级代号就去查询天气
//            publishText.setText("同步中...");
//            weatherInfoLayout.setVisibility(View.INVISIBLE);
//            cityNameText.setVisibility(View.INVISIBLE);
//            queryWeatherCode(countyCode);
//        }else if(!TextUtils.isEmpty(weatherCode)){
//            //有天气代号就去查询天气
//            publishText.setText("同步中...");
//            weatherInfoLayout.setVisibility(View.INVISIBLE);
//            cityNameText.setVisibility(View.INVISIBLE);
//            queryWeatherInfo(weatherCode);
//        }else{
//            showWeather();
//        }
    }

    private void initUI(){
//        titlePopup=new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        titlePopup.addAction(new ActionItem(this,R.string.choose));
//        titlePopup.addAction(new ActionItem(this,R.string.setting));
//        titlePopup.addAction(new ActionItem(this,R.string.citymanager));
//        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
//            @Override
//            public void onItemClick(ActionItem item, int position) {
//                if(item.mTitle==getText(R.string.choose)){
//                    Intent switchIntent=new Intent(WeatherActivity.this,ChooseActivity.class);
//                    switchIntent.putExtra("from_weather_activity",true);
//                    startActivity(switchIntent);
//                    finish();
//                }else if(item.mTitle==getText(R.string.setting)){
//                    Intent settingIntent=new Intent(WeatherActivity.this,SettingActivity.class);
//                    startActivity(settingIntent);
//                }else {
//                    Intent managerIntent=new Intent(WeatherActivity.this,CityManagerActivity.class);
//                    startActivity(managerIntent);
//                }
//            }
//        });
//
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout_scrollview);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout) findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView) findViewById(R.id.pm25_text);
        comfortText=(TextView) findViewById(R.id.comfort_text);
        carWashText=(TextView) findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);

        picImg=(ImageView)findViewById(R.id.pic_img);
    }

//    private void queryWeatherCode(String countyCode){
//        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
//        queryFromServer(address,"countyCode");
//    }
//
//    private void queryWeatherInfo(String weatherCode){
//        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
//        queryFromServer(address,"weatherCode");
//    }

    /**
     * 根据天气ID查询对应的天气信息
     */

    private void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+
                "&key=ccfb286742e249c0a354d1eeb531bef0";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                Log.i("data","weather.status is "+weather.status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        loadBingPic();
    }

//    private  void queryFromServer(final String address,final String type){
//        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                if("countyCode".equals(type)){
//                    if(!TextUtils.isEmpty(response)){
//                        //从返回的额数据中解析出天气代号
//                        String[] array=response.split("\\|");
//                        if(array!=null&&array.length==2){
//                            String weatherCode=array[1];
//                            queryWeatherInfo(weatherCode);
//                        }
//                    }
//                }else if("weatherCode".equals(type)){
//                    Log.i("data","处理服务器返回的天气信息");
//                    //处理服务器返回的天气信息
//                    Utility.handleWeatherResponse(WeatherActivity.this,response);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            showWeather();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                publishText.setText("同步失败");
//            }
//        });
//    }

    /**
     * 处理并展示weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).
                    inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max+"℃");
            minText.setText(forecast.temperature.min+"℃");
            forecastLayout.addView(view);
        }
            if(weather.aqi!=null){
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }

            String comfort="舒适度："+weather.suggestion.comfort.info;
            String carWash="洗车指数："+weather.suggestion.carWash.info;
            String sport="运动建议"+weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);



//        String desp_weather=prefs.getString("weather_desp","");
//        showBackground(desp_weather);

        //启动服务，用于后台自动更新天气信息
//        Intent serviceIntent=new Intent(this, AutoUpdateService.class);
//        startService(serviceIntent);
    }

    /**
     * 根据天气设置不同的背景图
     *
     */
//    private void showBackground(String desp){
//        switch (desp){
//            case "晴":
//                weatherLayout.setBackgroundResource(R.drawable.sun_day);
//                break;
//            case "小雨":
//                weatherLayout.setBackgroundResource(R.drawable.rain_day);
//                break;
//            case "中雨":
//                weatherLayout.setBackgroundResource(R.drawable.center_rain_day);
//                break;
//            case "阴":
//                weatherLayout.setBackgroundResource(R.drawable.cloudy_more_day);
//                break;
//            case "多云":
//                weatherLayout.setBackgroundResource(R.drawable.cloudy_less_day);
//                break;
//            default:
//                weatherLayout.setBackgroundColor(getResources().getColor(R.color.normalColor));
//                break;
//        }
//
//    }

//    @Override
//    public void onClick(View view) {
//        switch(view.getId()){
//            case R.id.switch_city:
//                titlePopup.show(view);
//                break;
//            case R.id.refresh_weather:
//                //从sharedPreference里获得weatherCode,然后重新获取刷新天气
//                publishText.setText(getText(R.string.loading));
//                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
//                String weatherCode=sharedPreferences.getString("weather_code","");
//                if(!TextUtils.isEmpty(weatherCode)){
//                    queryWeatherInfo(weatherCode);
//                }
//                break;
//            default:
//                break;
//        }
//
//    }

    /**
     * 加载每日背景图片
     */
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(picImg);
                    }
                });

            }
        });
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
