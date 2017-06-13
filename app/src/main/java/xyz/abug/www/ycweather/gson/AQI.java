package xyz.abug.www.ycweather.gson;

/**
 * Created by Dell on 2017/6/13.
 * api和pm2.5
 * <p>
 * 实例：
 * "aqi":{
 * "city":{
 * "aqi":"93",
 * "co":"1",
 * "no2":"47",
 * "o3":"164",
 * "pm10":"135",
 * "pm25":"65",
 * "qlty":"良",
 * "so2":"46"
 * }
 * }
 */

public class AQI {
    public City city;

    public class City {
        public String aqi;
        public String pm25;
    }
}
