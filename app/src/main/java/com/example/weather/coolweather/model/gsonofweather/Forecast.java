package com.example.weather.coolweather.model.gsonofweather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 64088 on 2017/5/16.
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature tempearture;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("tex_d")
        public String info;
    }
}
