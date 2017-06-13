package xyz.abug.www.ycweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dell on 2017/6/13.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Suggestion suggestion;
    public Now now;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
