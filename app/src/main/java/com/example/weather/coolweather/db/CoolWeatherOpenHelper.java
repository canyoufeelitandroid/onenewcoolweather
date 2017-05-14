package com.example.weather.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 64088 on 2017/3/17.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    /**
     * Province表建表语句
     */
    public static final String CREATE_PROVINCE="create table Province (" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    /**
     * City表建表语句
     */
    public static final String CREATE_CITY="create table City(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";

    /**
     * County表建表语句
     */
    public static final String CREATE_COUNTY="create table County(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer)";

    /**
     * 城市管理 表建表语句
     *
     */
    public static final String CREATE_CITY_MANAGER="create table City_Manager(" +
            "id integer primary key autoincrement," +
            "weather_code text," +
            "county_name text," +
            "weather_desp text," +
            "temp1 text," +
            "temp2 text," +
            "publish_time text)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        db.execSQL(CREATE_CITY_MANAGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        switch(i){
            case 1:
                sqLiteDatabase.execSQL(CREATE_CITY_MANAGER);
                break;
            default:
                break;
        }

    }
}
