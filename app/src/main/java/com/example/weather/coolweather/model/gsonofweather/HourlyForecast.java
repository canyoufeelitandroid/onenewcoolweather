package com.example.weather.coolweather.model.gsonofweather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 64088 on 2017/6/8.
 */

public class HourlyForecast {
    public String date;
    public String tmp;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String more;
    }


}
