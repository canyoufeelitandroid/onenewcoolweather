package com.example.weather.coolweather.model;

/**
 * Created by 64088 on 2017/3/23.
 */

public class WeatherItem {
    private String weather_code;
    private String county_name;
    private String weather_desp;
    private String temp1;
    private  String temp2;
    private String time;

    public WeatherItem(String city_name,String weather_desp,String temp1,String temp2,String time,String county_code){
        this.county_name=city_name;
        this.weather_desp=weather_desp;
        this.temp1=temp1;
        this.temp2=temp2;
        this.time=time;
        this.weather_code=county_code;
    }

    public String getWeather_code(){
        return weather_code;
    }
    public String getCounty_name(){
        return county_name;
    }
    public String getWeather_desp(){
        return weather_desp;
    }
    public  String getTemp1(){
        return temp1;
    }
    public  String getTemp2(){
        return temp2;
    }
    public  String getTime(){
        return time;
    }
    public void setWeather_code(String mWeather_code){
        this.weather_code=mWeather_code;
    }
    public void setCounty_name(String mCounty_name){this.county_name=mCounty_name;}
    public void setWeather_desp(String mWeather_desp){this.weather_desp=mWeather_desp;}
    public void setTemp1(String mTemp1){this.temp1=mTemp1;}
    public void setTemp2(String mTemp2){this.temp2=mTemp2;}
    public void setTime(String mTime){this.time=mTime;}

}
