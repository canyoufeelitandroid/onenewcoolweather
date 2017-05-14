package com.example.weather.coolweather.model;

import android.content.Context;

/**
 * Created by 64088 on 2017/3/20.
 * 功能描述：弹窗内部子类项（绘制标题和图标）
 */

public class ActionItem {
    //定义图片对象
   // public Drawable mDrawable;
    //定义文本对象
    public CharSequence mTitle;

    public ActionItem(CharSequence title){
        this.mTitle = title;
    }

    public ActionItem(Context context, int titleId){
        this.mTitle = context.getResources().getText(titleId);

    }

    public ActionItem(Context context, CharSequence title) {
        this.mTitle = title;
    }
}
