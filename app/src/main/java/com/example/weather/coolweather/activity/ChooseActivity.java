package com.example.weather.coolweather.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.coolweather.R;
import com.example.weather.coolweather.model.City;
import com.example.weather.coolweather.model.County;
import com.example.weather.coolweather.model.Province;
import com.example.weather.coolweather.util.HttpUtil;
import com.example.weather.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 64088 on 2017/3/17.
 */

public class ChooseActivity extends Fragment {

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private boolean isFromWeatherActivity;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView lv;
    private ArrayAdapter<String> adapter;
    //private CoolWeatherDB db;
    private List<String> dataList=new ArrayList<String>();
    private Button backButton;

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的等级
     */
    private int currentLevel;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.choose_area,container,false);
        backButton=(Button)view.findViewById(R.id.back_button);
        lv=(ListView)view.findViewById(R.id.lv);
        titleText=(TextView)view.findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        lv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(i);
                    queryCity();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(i);
                    queryCounty();
                }else if(currentLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get(i).getWeatherId();
                    Log.i("data","weatherId is "+weatherId);
                    Intent intent=new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        //返回键绑定点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_CITY){
                    queryProvince();
                }else if(currentLevel==LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
        queryProvince();
    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);
//        //SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
////        if(prefs.getBoolean("city_selected",false)&&!isFromWeatherActivity){
////            Intent i=new Intent(ChooseActivity.this,WeatherActivity.class);
////            startActivity(i);
////            finish();
////            return;
////        }
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.choose_area);
//        //db=CoolWeatherDB.getInstance(this);
//        initUI();
//        queryProvince(); //加载省级数据
//
//    }

//    private void initUI(){
//        backButton=(Button)findViewById(R.id.back_button);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        lv=(ListView)findViewById(R.id.lv);
//        titleText=(TextView)findViewById(R.id.title_text);
//        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
//        lv.setAdapter(adapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(currentLevel==LEVEL_PROVINCE){
//                    selectedProvince=provinceList.get(i);
//                    queryCity();
//                }else if(currentLevel==LEVEL_CITY){
//                    selectedCity=cityList.get(i);
//                    queryCounty();
//                }else if(currentLevel==LEVEL_COUNTY){
//                    String countyCode=countyList.get(i).getCountyCode();
//                    Log.i("data","countyCode is "+countyCode);
//                    Intent intent=new Intent(ChooseActivity.this,WeatherActivity.class);
//                    intent.putExtra("county_code",countyCode);
//                    startActivity(intent);
//                }
//            }
//        });
//    }

    /**
     * 查询全国所有的省，优先从数据库查询，如无则去数据库查询
     */
    private void queryProvince(){
        titleText.setText(getText(R.string.china));
        backButton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询选中省下的所有的市，优先从数据库查询，如无则去数据库查询
     */
    private void queryCity(){
        backButton.setVisibility(View.VISIBLE);
        titleText.setText(selectedProvince.getProvinceName());
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询选中市下的所有的县，优先从数据库查询，如无则去数据库查询
     */
    private void queryCounty(){
        backButton.setVisibility(View.VISIBLE);
        titleText.setText(selectedCity.getCityName());
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);

        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县的数据
     */
    private void queryFromServer(final String address,final String type){

//        if(!TextUtils.isEmpty(code)){
//            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
//        }else{
//            address="http://www.weather.com.cn/data/list3/city.xml";
//        }
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(),R.string.fail,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCity();
                            }else if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }

            }
        });


//        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                boolean result=false;
//                if("province".equals(type)){
//                    result= Utility.handleProvinceResponse(db,response);
//                }else if("city".equals(type)){
//                    result=Utility.handleCityResponse(db,response,selectedProvince.getId());
//                }else if("county".equals(type)){
//                    result=Utility.handleCountyResponse(db,response,selectedCity.getId());
//                }
//                if(result){
//                    //通过runOnUiThread()方法回到主线程处理逻辑
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            closeProgressDialog();
//                            if("province".equals(type)){
//                                queryProvince();
//                            }else if("city".equals(type)){
//                                queryCity();
//                            }else if("county".equals(type)){
//                                queryCounty();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                //通过runOnUiThread()方法回到主线程处理逻辑
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        Toast.makeText(ChooseActivity.this,R.string.fail,Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//        });
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage(getText(R.string.loading));
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    private void closeProgressDialog(){
       if(progressDialog!=null){
           progressDialog.dismiss();
       }
    }

//    @Override
//    public void onBackPressed() {
//        if(currentLevel==LEVEL_COUNTY){
//            queryCity();
//        }else if(currentLevel==LEVEL_CITY){
//            queryProvince();
//        }else{
//            if(isFromWeatherActivity){
//                Intent intent=new Intent(getActivity(),WeatherActivity.class);
//                startActivity(intent);
//            }
//            getActivity().finish();
//        }
//    }
}
