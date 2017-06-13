package xyz.abug.www.ycweather.gson;

/**
 * Created by Dell on 2017/6/13.
 * 建议信息
 */

public class Suggestion {
    public Cw cw;
    public Sport sport;
    public Comf comf;

    public class Comf {
        public String txt;//舒适度
    }

    public class Sport {
        public String txt;//运动指数
    }

    public class Cw {
        public String txt;//洗车指数
    }
}
