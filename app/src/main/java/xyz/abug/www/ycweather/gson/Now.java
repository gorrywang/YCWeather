package xyz.abug.www.ycweather.gson;

/**
 * Created by Dell on 2017/6/13.
 * 当前信息
 */

public class Now {
    public String tmp;//当前气温
    public Cond cond;

    public class Cond{
        public String txt;//天气，阵雨
    }
}
