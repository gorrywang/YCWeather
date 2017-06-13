package xyz.abug.www.ycweather.gson;

/**
 * Created by Dell on 2017/6/13.
 * 最近几天
 */

public class Forecast {
    public String date;//日期
    public Tmp tmp;//最大最小日期
    public Cond cond;//天气，阵雨

    public class Tmp {
        public String max;//最大
        public String min;//最小
    }

    public class Cond {
        public String txt_d;
    }
}
