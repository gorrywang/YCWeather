package xyz.abug.www.ycweather.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trycatch.mysnackbar.Prompt;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.abug.www.ycweather.R;
import xyz.abug.www.ycweather.activity.MainActivity;
import xyz.abug.www.ycweather.db.SelectListDb;
import xyz.abug.www.ycweather.gson.Forecast;
import xyz.abug.www.ycweather.gson.Weather;
import xyz.abug.www.ycweather.utils.HttpUtils;
import xyz.abug.www.ycweather.utils.Utility;
import xyz.abug.www.ycweather.utils.Utils;

import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_KEY;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_WEATHER_1;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_WEATHER_2;

/**
 * Created by Dell on 2017/6/13.
 */

@SuppressLint("ValidFragment")
public class MainFragment extends Fragment {
    private View mView;
    private TextView mTextCityName, mTextTime, mTextTemp, mTextStatus, mTextAqi, mTextPm25, mTextCosy, mTextCar, mTextSport;
    private LinearLayout mLinear;
    private MKLoader mMkLoader;

    public MainFragment(String weatherId) {
        this.weatherId = weatherId;
    }


    public MainFragment() {

    }
    private String weatherId;
    private SwipeRefreshLayout mSwipe;
    private long mStartTime, mEndTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindID();
        mStartTime = System.currentTimeMillis();
        mMkLoader.setVisibility(View.VISIBLE);
        mSwipe.setVisibility(View.GONE);
        getWeather(weatherId);
    }

    /**
     * 绑定控件
     */
    private void bindID() {
        mMkLoader = mView.findViewById(R.id.frag_main_loader_load);
        mSwipe = mView.findViewById(R.id.frag_main_swipe_refresh);
        //城市名称
        mTextCityName = mView.findViewById(R.id.frag_main_txt_cityname);
        //时间
        mTextTime = mView.findViewById(R.id.frag_main_txt_refreshtime);
        //温度
        mTextTemp = mView.findViewById(R.id.frag_main_txt_temp);
        //天气
        mTextStatus = mView.findViewById(R.id.frag_main_txt_status);
        //aqi
        mTextAqi = mView.findViewById(R.id.frag_main_txt_aqi);
        //pm25
        mTextPm25 = mView.findViewById(R.id.frag_main_txt_pm25);
        //舒适度
        mTextCosy = mView.findViewById(R.id.frag_main_txt_cosy);
        //洗车指数
        mTextCar = mView.findViewById(R.id.frag_main_txt_car);
        //运动指数
        mTextSport = mView.findViewById(R.id.frag_main_txt_sport);
        mLinear = mView.findViewById(R.id.frag_main_linear_prediction);

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipe.setRefreshing(true);
                getWeather(weatherId);
            }
        });
    }

    /**
     * 刷新数据
     */
    public void showData(Weather weather) {
        mTextCityName.setText(weather.basic.city);
        mTextTime.setText(weather.basic.update.lastTime.split(" ")[1]);
        mTextTemp.setText(weather.now.tmp + "°C");
        mTextStatus.setText(weather.now.cond.txt);
        try {
            mTextAqi.setText(weather.aqi.city.aqi);
            mTextPm25.setText(weather.aqi.city.pm25);
        } catch (Exception e) {
            mTextAqi.setText("----");
            mTextPm25.setText("----");
        }
        mTextCosy.setText("舒适度:" + weather.suggestion.comf.txt);
        mTextCar.setText("洗车指数:" + weather.suggestion.cw.txt);
        mTextSport.setText("运动建议:" + weather.suggestion.sport.txt);
        //预报
        mLinear.removeAllViews();
        List<Forecast> list = weather.forecastList;
        for (Forecast forecast : list) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_status, null);
            TextView date = inflate.findViewById(R.id.item_status_date);
            TextView max = inflate.findViewById(R.id.item_status_max);
            TextView min = inflate.findViewById(R.id.item_status_min);
            TextView status = inflate.findViewById(R.id.item_status_status);
            date.setText(forecast.date);
            max.setText(forecast.tmp.max);
            min.setText(forecast.tmp.min);
            status.setText(forecast.cond.txt_d);
            mLinear.addView(inflate);
        }
        mSwipe.setRefreshing(false);
        mEndTime = System.currentTimeMillis();
        final long time = mEndTime - mStartTime;
        if (time - 1500 < 0) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1500 - time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMkLoader.setVisibility(View.GONE);
                            mSwipe.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }.start();
        } else {
            mMkLoader.setVisibility(View.GONE);
            mSwipe.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 获取天气
     *
     * @param weatherId 天气代码
     */
    private void getWeather(final String weatherId) {
        //显示加载，关闭数据
        HttpUtils.sendQuestBackResponse(URL_HEWEATHER_WEATHER_1 + weatherId + URL_HEWEATHER_WEATHER_2 + URL_HEWEATHER_KEY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipe.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Utils.logData("天气数据：" + data);
                final Weather weather = Utility.analyticWeather(data);
                if (weather == null) {
                    MainActivity.showTips("数据获取错误", Prompt.ERROR);
                    return;
                }
                //显示数据
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //保存搜索内容
                        SelectListDb db = new SelectListDb();
                        db.setmWeatherStatus(weather.now.cond.txt);
                        db.setmWeatherTemp(weather.now.tmp);
                        db.setmCityname(weather.basic.city);
                        db.setmWeatherId(weather.basic.weatherId);
                        Utility.upDateSelectCity(db);
                        showData(weather);
                        //获取fragment
                    }
                });
            }
        });
    }


}
