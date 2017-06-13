package xyz.abug.www.ycweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Dell on 2017/6/13.
 * 已选择的城市列表数据库
 */

public class SelectListDb extends DataSupport {
    //id
    private int mId;
    //城市名字
    private String mCityname;
    //城市天气代码
    private String mWeatherId;
    //温度
    private String mWeatherTemp;
    //状态
    private String mWeatherStatus;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmCityname() {
        return mCityname;
    }

    public void setmCityname(String mCityname) {
        this.mCityname = mCityname;
    }

    public String getmWeatherId() {
        return mWeatherId;
    }

    public void setmWeatherId(String mWeatherId) {
        this.mWeatherId = mWeatherId;
    }

    public String getmWeatherTemp() {
        return mWeatherTemp;
    }

    public void setmWeatherTemp(String mWeatherTemp) {
        this.mWeatherTemp = mWeatherTemp;
    }

    public String getmWeatherStatus() {
        return mWeatherStatus;
    }

    public void setmWeatherStatus(String mWeatherStatus) {
        this.mWeatherStatus = mWeatherStatus;
    }
}
