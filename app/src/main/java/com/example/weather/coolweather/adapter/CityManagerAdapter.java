package com.example.weather.coolweather.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weather.coolweather.R;
import com.example.weather.coolweather.model.WeatherItem;

import java.util.List;

/**
 * Created by 64088 on 2017/3/23.
 */

public class CityManagerAdapter extends RecyclerView.Adapter {
    private List<WeatherItem> list;
    private Context context;

    public static interface OnRecyclerViewListener{
        void onItemClickListener(int position);
        void onItemLongClickListener(int position);
    }
    private OnRecyclerViewListener onRecyclerViewListener;
    public void setOnRecyclerViewListener(OnRecyclerViewListener m_onRecyclerViewListener){
        this.onRecyclerViewListener=m_onRecyclerViewListener;
    }

    public CityManagerAdapter(Context mContext,List<WeatherItem> mList){
        this.context=mContext;
        this.list=mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_manager,null);
        ViewGroup.LayoutParams lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        WeatherItem weatherItem=list.get(position);
        MyViewHolder vh=(MyViewHolder)holder;
        vh.cityName.setText(weatherItem.getCounty_name());
        vh.weatherDesp.setText("天气："+weatherItem.getWeather_desp());
        vh.temp.setText("温度："+weatherItem.getTemp());
        vh.updateTime.setText(weatherItem.getTime());
        vh.weatherCode.setText(weatherItem.getWeather_code());
        switch(position%3) {
            case 0:
                vh.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            case 1:
                vh.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.darkGreen));
                break;
            case 2:
                vh.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.violet));
                break;
        }
        if(onRecyclerViewListener!=null){
            vh.itemLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=holder.getLayoutPosition();
                    onRecyclerViewListener.onItemClickListener(pos);
                }
            });
            vh.itemLinear.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos=holder.getLayoutPosition();
                    onRecyclerViewListener.onItemLongClickListener(pos);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView cityName;
        TextView weatherDesp;
        TextView temp;
        TextView updateTime;
        TextView weatherCode;
        LinearLayout itemLinear;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            cityName=(TextView)itemView.findViewById(R.id.item_city_name);
            weatherDesp=(TextView)itemView.findViewById(R.id.item_weather);
            temp=(TextView)itemView.findViewById(R.id.item_temp);
            updateTime=(TextView)itemView.findViewById(R.id.item_date);
            weatherCode=(TextView)itemView.findViewById(R.id.item_weather_code);
            itemLinear=(LinearLayout) itemView.findViewById(R.id.item_linear);
            cardView=(CardView)itemView.findViewById(R.id.cd_1);
        }
    }
}
