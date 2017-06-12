package com.example.weather.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.coolweather.R;
import com.example.weather.coolweather.controls.TitlePopup;
import com.example.weather.coolweather.model.ActionItem;
import com.example.weather.coolweather.model.WeatherItem;
import com.example.weather.coolweather.model.gsonofweather.Forecast;
import com.example.weather.coolweather.model.gsonofweather.HourlyForecast;
import com.example.weather.coolweather.model.gsonofweather.Weather;
import com.example.weather.coolweather.service.AutoUpdateService;
import com.example.weather.coolweather.util.HttpUtil;
import com.example.weather.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 64088 on 2017/3/17.
 */

public class WeatherActivity extends BaseActivity implements View.OnClickListener{

//    //自定义下拉菜单初始化
    private TitlePopup titlePopup;
    //记录第一次按下返回的时间（毫秒）
    long firstTime=0;

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout hourlyForecastLayout;
    private LinearLayout forecastLayout;
    private TextView airText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView dressText;
    private TextView coldText;
    private TextView travelText;
    private TextView uvText;

    private ImageView picImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private Button navBtn;

    SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        initUI();
        prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);


        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather=Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器解析数据
            String weatherId;
            weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            //ccfb286742e249c0a354d1eeb531bef0
            requestWeather(weatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherString=prefs.getString("weather",null);
                Weather weather=Utility.handleWeatherResponse(weatherString);
                String weatherId=weather.basic.weatherId;
                requestWeather(weatherId);
            }
        });

        //加载背景图片
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(WeatherActivity.this).load(bingPic).into(picImg);
        }else{
            loadBingPic();
        }

    }

    private void initUI(){
        titlePopup=new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titlePopup.addAction(new ActionItem(this,R.string.choose));
        titlePopup.addAction(new ActionItem(this,R.string.setting));
        titlePopup.addAction(new ActionItem(this,R.string.citymanager));
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                if(item.mTitle==getText(R.string.choose)){
                    drawerLayout.openDrawer(GravityCompat.START);
                }else if(item.mTitle==getText(R.string.setting)){
                    Intent settingIntent=new Intent(WeatherActivity.this,SettingActivity.class);
                    startActivity(settingIntent);
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                }else {
                    Intent managerIntent=new Intent(WeatherActivity.this,CityManagerActivity.class);
                    startActivity(managerIntent);
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
            }
        });

        weatherLayout=(ScrollView)findViewById(R.id.weather_layout_scrollview);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        hourlyForecastLayout=(LinearLayout)findViewById(R.id.hourly_forecast_layout);
        forecastLayout=(LinearLayout) findViewById(R.id.forecast_layout);
        airText=(TextView)findViewById(R.id.air_quality);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView) findViewById(R.id.pm25_text);
        comfortText=(TextView) findViewById(R.id.comfort_text);
        carWashText=(TextView) findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        dressText=(TextView)findViewById(R.id.dress_text);
        coldText=(TextView)findViewById(R.id.cold_text);
        travelText=(TextView)findViewById(R.id.travel_text);
        uvText=(TextView)findViewById(R.id.uv_text);

        picImg=(ImageView)findViewById(R.id.pic_img);
        //下拉刷新
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.refresh_weather_layout);
        //设置进度条颜色
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        //左滑动菜单切换城市
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navBtn=(Button)findViewById(R.id.nav_button);
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titlePopup.show(view);
            }
        });


    }


    /**
     * 根据天气ID查询对应的天气信息
     */

    public void requestWeather(final String weatherId){
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
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                Log.i("data","responseText is"+responseText);
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);

                            //启动后台自动更新服务
                            Intent intent=new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }


    /**
     * 处理并展示weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;//城市名称
        String updateTime=weather.basic.update.updateTime.split(" ")[1];//更新时间
        String degree=weather.now.temperature+"℃";//当前温度
        String weatherInfo=weather.now.more.info;//天气信息
        String weatherId=weather.basic.weatherId;

        //用于城市管理界面中，已收藏城市的部分数据存储
        WeatherItem weatherItem=new WeatherItem();
        weatherItem.setCounty_name(cityName);
        weatherItem.setTime(updateTime);
        weatherItem.setTemp(degree);
        weatherItem.setWeather_desp(weatherInfo);
        weatherItem.setWeather_code(weatherId);
        //首先进行更新操作
        int  i=weatherItem.updateAll("weather_code=?",weatherId);
        //若该数据为第一次存储，则存储操作
        if(i==0){
            weatherItem.save();
        }

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        //未来几个小时的天气预报
        hourlyForecastLayout.removeAllViews();
        for(HourlyForecast hourlyForecast:weather.hourlyForecastList){
            View view=LayoutInflater.from(this).
                    inflate(R.layout.hourly_forecast_item,hourlyForecastLayout,false);
            TextView hourText=(TextView)view.findViewById(R.id.hour_text);
            TextView hourInfoText=(TextView)view.findViewById(R.id.hour_info_text);
            TextView tmpText=(TextView)view.findViewById(R.id.hourly_tmp_text);

            String hourlyDate=hourlyForecast.date.substring(10);
            hourText.setText(hourlyDate);
            hourInfoText.setText(hourlyForecast.more.more);
            tmpText.setText(hourlyForecast.tmp+"℃");
            hourlyForecastLayout.addView(view);
        }
       //未来几日的天气预报
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).
                    inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.daily_info_text);
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
                airText.setText(weather.aqi.city.airQuality);
            }

            String comfort="舒适度："+weather.suggestion.comfort.info;
            String carWash="洗车指数："+weather.suggestion.carWash.info;
            String sport="运动建议："+weather.suggestion.sport.info;
            String dress="穿衣建议："+weather.suggestion.dress.info;
            String cold="感冒指数："+weather.suggestion.cold.info;
            String travel="出游建议："+weather.suggestion.travel.info;
            String uv="紫外线指数："+weather.suggestion.ultraviolet.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            dressText.setText(dress);
            coldText.setText(cold);
            travelText.setText(travel);
            uvText.setText(uv);
            weatherLayout.setVisibility(View.VISIBLE);

//        String desp_weather=prefs.getString("weather_desp","");
//        showBackground(desp_weather);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.switch_city:
                titlePopup.show(view);
                break;
            default:
                break;
        }

    }

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
                finish();
                ActivityCollector.finishAll();

            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
