package com.example.weather.coolweather.model;

import org.litepal.crud.DataSupport;

/**
 * Created by 64088 on 2017/3/17.
 */

public class County extends DataSupport{
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public void setId(int id){
        this.id=id;
    }

    public void setCountyName(String countyName){
        this.countyName=countyName;
    }

    public void setCountyCode(String countyCode){
        this.countyCode=countyCode;
    }

    public void setCityId(int cityId){
        this.cityId=cityId;
    }

    public int getId(){return id;}
    public String getCountyName(){return countyName;}
    public String getCountyCode(){return countyCode;}
    public int getCityId(){return cityId;}
}
