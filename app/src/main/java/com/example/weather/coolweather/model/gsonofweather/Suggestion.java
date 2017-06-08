package com.example.weather.coolweather.model.gsonofweather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 64088 on 2017/5/16.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    @SerializedName("drsg")
    public Dress dress;

    @SerializedName("flu")
    public Cold cold;

    @SerializedName("trav")
    public Travel travel;

    @SerializedName("uv")
    public Ultraviolet ultraviolet;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }

    public class Dress{
        @SerializedName("txt")
        public String info;
    }

    public class Cold{
        @SerializedName("txt")
        public String info;
    }

    public class Travel{
        @SerializedName("txt")
        public String info;
    }

    public class Ultraviolet{
        @SerializedName("txt")
        public String info;
    }
}
