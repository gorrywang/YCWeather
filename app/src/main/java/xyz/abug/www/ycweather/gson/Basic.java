package xyz.abug.www.ycweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dell on 2017/6/13.
 * 城市信息
 */

public class Basic {
    public String city;
    @SerializedName("id")
    public String weatherId;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String lastTime;
    }

}
