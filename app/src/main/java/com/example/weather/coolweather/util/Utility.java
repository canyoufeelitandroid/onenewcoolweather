package com.example.weather.coolweather.util;

import android.text.TextUtils;

import com.example.weather.coolweather.model.City;
import com.example.weather.coolweather.model.County;
import com.example.weather.coolweather.model.Province;
import com.example.weather.coolweather.model.gsonofweather.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 64088 on 2017/3/17.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */

    public synchronized  static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)) {
            try{
                JSONArray allProvince=new JSONArray(response);
                for(int i=0;i<allProvince.length();i++){
                    JSONObject jsonObject=allProvince.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
//    public synchronized static boolean handleProvinceResponse(CoolWeatherDB db,String response){
//        if(!TextUtils.isEmpty(response)){
//            String[] allProvince=response.split(",");
//            if(allProvince!=null&&allProvince.length>0){
//                for(String p:allProvince){
//                    String[] array=p.split("\\|");
//                    Province province=new Province();
//                    province.setProvinceCode(array[0]);
//                    province.setProvinceName(array[1]);
//                    //将解析出的数据存储到数据库
//                    db.saveProvince(province);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject jsonObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setProvinceId(provinceId);
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

//    public synchronized static boolean handleCityResponse(CoolWeatherDB db,
//           String response,int provinceId){
//        if(!TextUtils.isEmpty(response)){
//            String[] allCity=response.split(",");
//            if(allCity!=null&&allCity.length>0){
//                for(String p:allCity){
//                    String[] array=p.split("\\|");
//                    City city=new City();
//                    city.setCityCode(array[0]);
//                    city.setCityName(array[1]);
//                    city.setProvinceId(provinceId);
//                    //将解析出的数据存储到数据库
//                    db.saveCity(city);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties=new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject jsonObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCityId(cityId);
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

//    public synchronized static boolean handleCountyResponse(CoolWeatherDB db,
//        String response,int cityId){
//        if(!TextUtils.isEmpty(response)){
//            String[] allCounty=response.split(",");
//            if(allCounty!=null&&allCounty.length>0){
//                for(String p:allCounty){
//                    String[] array=p.split("\\|");
//                    County county=new County();
//                    county.setCountyCode(array[0]);
//                    county.setCountyName(array[1]);
//                    county.setCityId(cityId);
//                    //将解析出的数据存储到数据库
//                    db.saveCounty(county);
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 解析服务器返回的天气JSON数据，并将解析成Weather实体类
     */

    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析服务器返回的天气JSON数据，并将解析出的数据存储到本地
     */
//    public static void handleWeatherResponse(Context context,String response){
//        try{
//            JSONObject jsonObject=new JSONObject(response);
//            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
//            String cityName=weatherInfo.getString("city");
//            String weatherCode=weatherInfo.getString("cityid");
//            String temp1=weatherInfo.getString("temp1");
//            String temp2=weatherInfo.getString("temp2");
//            String weatherDesp=weatherInfo.getString("weather");
//            String publishTime=weatherInfo.getString("ptime");
//            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * 将服务器返回的天气数据存储到SharedPreferences文件中(及城市管理数据库)
     */
//    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,
//                                       String temp2,String weatherDesp,String publishTime){
//
//        java.text.SimpleDateFormat adf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
//        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
//        editor.putBoolean("city_selected",true);
//        editor.putString("city_name",cityName);
//        editor.putString("weather_code",weatherCode);
//        editor.putString("temp1",temp1);
//        editor.putString("temp2",temp2);
//        editor.putString("weather_desp",weatherDesp);
//        editor.putString("publish_time",publishTime);
//        editor.putString("current_data",adf.format(new Date()));
//        editor.commit();
//
//        //城市管理 的数据存储
//        CoolWeatherDB db=CoolWeatherDB.getInstance(context);
//        WeatherItem item=new WeatherItem(cityName,weatherDesp,temp1,temp2,publishTime,weatherCode);
//        db.saveWeatherItem(item,true);
//
//    }
}
