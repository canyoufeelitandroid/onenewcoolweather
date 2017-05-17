package com.example.weather.coolweather.model.gsonofweather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 64088 on 2017/5/16.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
