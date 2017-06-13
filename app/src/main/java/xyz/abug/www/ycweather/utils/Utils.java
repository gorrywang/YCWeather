package xyz.abug.www.ycweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Dell on 2017/6/13.
 * 工具类
 */

public class Utils {
    //Log开关
    public static final boolean LOG_BOOL = true;
    //key
    public static final String URL_HEWEATHER_KEY = "aac11d46b15448b5984151cb5e1f4814";
    //获取天气url
    public static final String URL_HEWEATHER_ADDRESS = "";
    //查询城市
    public static final String URL_HEWEATHER_SEARCH_1 = "https://api.heweather.com/v5/search?city=";
    public static final String URL_HEWEATHER_SEARCH_2 = "&key=";
    //获取天气数据
    public static final String URL_HEWEATHER_WEATHER_1 = "http://guolin.tech/api/weather?cityid=";
    public static final String URL_HEWEATHER_WEATHER_2 = "&key=";
    //IP地址获取库
    public static final String URL_YY_GETIP = "https://ipip.yy.com/get_ip_info.php";
    //sp配置文件
    public static final String SP_USER_INFO = "USER_INFO";

    /**
     * 打印数据
     */
    public static void logData(String mData) {
        if (LOG_BOOL) {
            Log.e("data", mData);
        }
    }

    /**
     * 判断程序是否第一次运行
     *
     * @param context
     * @return
     */
    public static boolean isFirstRun(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, context.MODE_PRIVATE);
        boolean isFirstRun = sp.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            //第一次运行
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }
        return isFirstRun;
    }
}
