package xyz.abug.www.ycweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.abug.www.ycweather.R;
import xyz.abug.www.ycweather.gson.Weather;

/**
 * Created by Dell on 2017/6/13.
 */

public class MainFragment extends Fragment {
    private View mView;
    private static TextView mTextCityName, mTextTime, mTextTemp, mTextStatus, mTextAqi, mTextPm25, mTextCosy, mTextCar, mTextSport;

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
    }

    /**
     * 绑定控件
     */
    private void bindID() {
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


    }

    /**
     * 刷新数据
     */
    public static void showData(Weather weather) {
        mTextCityName.setText(weather.basic.city);
        mTextTime.setText(weather.basic.update.lastTime.split(" ")[1]);
        mTextTemp.setText(weather.now.tmp + "°C");
        mTextStatus.setText(weather.now.cond.txt);
        mTextAqi.setText(weather.aqi.city.aqi);
        mTextPm25.setText(weather.aqi.city.pm25);
        mTextCosy.setText("舒适度:" + weather.suggestion.comf.txt);
        mTextCar.setText("洗车指数:" + weather.suggestion.cw.txt);
        mTextSport.setText("运动建议:" + weather.suggestion.sport.txt);
    }


}
