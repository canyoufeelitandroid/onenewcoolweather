package com.example.weather.coolweather.util;

/**
 * Created by 64088 on 2017/3/17.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
