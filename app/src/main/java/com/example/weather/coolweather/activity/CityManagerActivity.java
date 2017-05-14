package com.example.weather.coolweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.weather.coolweather.R;
import com.example.weather.coolweather.adapter.CityManagerAdapter;
import com.example.weather.coolweather.db.CoolWeatherDB;
import com.example.weather.coolweather.model.WeatherItem;

import java.util.List;

/**
 * Created by 64088 on 2017/3/23.
 */

public class CityManagerActivity extends BaseActivity implements View.OnClickListener{
    private List<WeatherItem> list;
    private RecyclerView rv;
    private CityManagerAdapter adapter;
    private Button addBtn;
    private Button backBtn;
    private CoolWeatherDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.city_manager);
        db=CoolWeatherDB.getInstance(this);
        initData();
        initUI();

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter=new CityManagerAdapter(this,list);
        adapter.setOnRecyclerViewListener(new CityManagerAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClickListener(int position) {
                WeatherItem item1=list.get(position);
                String weatherCode=item1.getWeather_code();
                Intent weatherIntent=new Intent(CityManagerActivity.this,WeatherActivity.class);
                weatherIntent.putExtra("weather_code",weatherCode);
                startActivity(weatherIntent);
            }

            @Override
            public void onItemLongClickListener(final int position) {
                final WeatherItem item=list.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.saveWeatherItem(item,false);
                    }
                }).start();
                adapter.removeItem(position);

            }
        });
        rv.setAdapter(adapter);


    }
    private  void initData(){
        list=db.loadWeatherItem();
    }

    private void initUI(){
        rv=(RecyclerView)findViewById(R.id.rv_1);
        addBtn=(Button)findViewById(R.id.add_city_manager);
        backBtn=(Button)findViewById(R.id.back_before_1);
        addBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_city_manager:
                Intent intent=new Intent(CityManagerActivity.this,ChooseActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                break;
            case R.id.back_before_1:
                finish();
                break;
        }
    }
}
