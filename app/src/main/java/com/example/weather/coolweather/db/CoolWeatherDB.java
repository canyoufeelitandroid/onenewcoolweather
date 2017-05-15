package com.example.weather.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.weather.coolweather.model.City;
import com.example.weather.coolweather.model.County;
import com.example.weather.coolweather.model.Province;
import com.example.weather.coolweather.model.WeatherItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 64088 on 2017/3/17.
 */

public class CoolWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME="cool_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION=2;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }

    /**
     * 获取CoolWeatherDB的实例
     * synchronized放在private之后，void之前，即一次只能有一个线程进入该方法
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB==null){
            coolWeatherDB=new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 从数据库取出省份信息
     */
    public List<Province> loadProvince(){
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.rawQuery("select * from Province",null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getInt(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city){
        if(city!=null){
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /**
     * 从数据库取出城市信息
     */
    public List<City> loadCity(int provinceId){
        List<City> list=new ArrayList<City>();
        Cursor cursor=db.rawQuery("select * from City where province_id="+provinceId,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getInt(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }

    /**
     * 将County实例存储到数据库
     */
    public void saveCounty(County county){
        if(county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getWeatherId());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }

    /**
     * 从数据库取出县区信息
     */
    public List<County> loadCounty(int cityId){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.rawQuery("select * from County where city_id="+cityId,null);
        if(cursor.moveToFirst()){
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setWeatherId(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            }while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }

    /**
     * 将 城市管理 数据存储到数据库
     */
    public void saveWeatherItem(WeatherItem item,boolean addOrDelete){
        String countyName=item.getCounty_name();
        if(item!=null&&addOrDelete==true){
            ContentValues values=new ContentValues();
            values.put("county_name",item.getCounty_name());
            values.put("weather_desp",item.getWeather_desp());
            values.put("temp1",item.getTemp1());
            values.put("temp2",item.getTemp2());
            values.put("publish_time",item.getTime());
            values.put("weather_code",item.getWeather_code());
            int count=db.update("City_Manager",values,"county_name=?",new String[]{countyName});
            if(count==0) {
                db.insert("City_Manager", null, values);
            }
        }else if(item!=null&&addOrDelete==false){
            db.delete("City_Manager","county_name=?",new String[]{countyName});
        }

    }


    /**
     * 从数据库取出城市管理信息
     */
    public List<WeatherItem> loadWeatherItem(){
        List<WeatherItem> list=new ArrayList<WeatherItem>();
        Cursor cursor=db.rawQuery("select * from City_Manager order by id desc",null);
        if(cursor.moveToFirst()){
            do {
                String countyName=cursor.getString(cursor.getColumnIndex("county_name"));
                String weatherDesp=cursor.getString(cursor.getColumnIndex("weather_desp"));
                String temp1=cursor.getString(cursor.getColumnIndex("temp1"));
                String temp2=cursor.getString(cursor.getColumnIndex("temp2"));
                String publishTime=cursor.getString(cursor.getColumnIndex("publish_time"));
                String weatherCode=cursor.getString(cursor.getColumnIndex("weather_code"));
                WeatherItem item = new WeatherItem(countyName,weatherDesp,temp1,temp2,publishTime,weatherCode);
                list.add(item);
            }while (cursor.moveToNext());

            if(cursor!=null){
                cursor.close();
            }
        }
        return list;
    }

}
