package com.example.weather.coolweather.model.gsonofweather;

/**
 * Created by 64088 on 2017/5/16.
 */

public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
