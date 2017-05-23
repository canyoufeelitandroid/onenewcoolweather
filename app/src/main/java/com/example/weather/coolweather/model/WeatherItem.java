package com.example.weather.coolweather.model;

import org.litepal.crud.DataSupport;

/**
 * Created by 64088 on 2017/3/23.
 */

public class WeatherItem extends DataSupport{
    private String weather_code;
    private String county_name;
    private String weather_desp;
    private String temp;
    private String time;

//    public WeatherItem(String city_name,String weather_desp,String temp,String time,String county_code){
//        this.county_name=city_name;
//        this.weather_desp=weather_desp;
//        this.temp=temp;
//        this.time=time;
//        this.weather_code=county_code;
//    }

    public String getWeather_code(){
        return weather_code;
    }
    public String getCounty_name(){
        return county_name;
    }
    public String getWeather_desp(){
        return weather_desp;
    }
    public  String getTemp(){
        return temp;
    }
    public  String getTime(){
        return time;
    }
    public void setWeather_code(String mWeather_code){
        this.weather_code=mWeather_code;
    }
    public void setCounty_name(String mCounty_name){this.county_name=mCounty_name;}
    public void setWeather_desp(String mWeather_desp){this.weather_desp=mWeather_desp;}
    public void setTemp(String mTemp){this.temp=mTemp;}
    public void setTime(String mTime){this.time=mTime;}

}
