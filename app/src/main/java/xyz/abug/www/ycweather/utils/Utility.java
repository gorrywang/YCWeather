package xyz.abug.www.ycweather.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import xyz.abug.www.ycweather.db.SelectListDb;
import xyz.abug.www.ycweather.gson.Weather;

/**
 * Created by Dell on 2017/6/13.
 * 解析json
 */

public class Utility {
    /**
     * 解析第一次运行ip
     *
     * @param mData 数据
     *              实例：var returnInfo = {"cip":"58.59.7.190","cname":"\u4e2d\u56fd,\u5c71\u4e1c,\u6d4e\u5357,\u7535\u4fe1","country":"\u4e2d\u56fd","province":"\u5c71\u4e1c","city":"\u6d4e\u5357","isp":"\u7535\u4fe1"};
     */
    public static String analyticFirstIp(String mData) {
        String mData1 = mData.split("var returnInfo = ")[1];
        String data = mData1.split(";")[0];
        try {
            JSONObject jsonData = new JSONObject(data);
            String city = jsonData.getString("city");
            return city;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析搜索城市结果
     *
     * @param mData 数据
     *              实例：{"HeWeather5":[{"basic":{"city":"济南","cnty":"中国","id":"CN101120101","lat":"36.67580795","lon":"117.00092316","prov":"山东"},"status":"ok"}]}
     * @return List<SelectListDb>
     */
    public static List<SelectListDb> analyticSearchCity(String mData) {
        List<SelectListDb> selectListDbList = new ArrayList<>();
        try {
            JSONObject jsonData = new JSONObject(mData);
            JSONArray heWeather5 = jsonData.getJSONArray("HeWeather5");
            SelectListDb selectListDb;
            for (int i = 0; i < heWeather5.length(); i++) {
                JSONObject jsonObject = heWeather5.getJSONObject(i);
                JSONObject basic = jsonObject.getJSONObject("basic");
                selectListDb = new SelectListDb();
                selectListDb.setmCityname(basic.getString("city"));
                selectListDb.setmWeatherId(basic.getString("id"));
                selectListDb.setmWeatherTemp("----");
                selectListDb.setmWeatherStatus("----");
                selectListDbList.add(selectListDb);
                selectListDb = null;
            }
            return selectListDbList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析天气数据
     *
     * @param mData
     * @return
     */
    public static Weather analyticWeather(String mData) {
        try {
            JSONObject jsonObject = new JSONObject(mData);
            JSONArray heWeather = jsonObject.getJSONArray("HeWeather");
            String tianqi = heWeather.getJSONObject(0).toString();
            return new Gson().fromJson(tianqi, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 保存搜索后列表数据
     */
    public static void saveSelectCity(SelectListDb db) {
        String city = db.getmCityname();
        //查询数据是否有该城市
        List<SelectListDb> listDbs = DataSupport.where("mCityname = ?", String.valueOf(city)).find(SelectListDb.class);
        if (listDbs.size() == 0) {
            db.save();
            Utils.logData("保存数据：" + db.getmCityname());
        } else {
            Utils.logData("已存在数据：" + db.getmCityname());
        }
    }

    /**
     * 修改列表数据
     */
    public static void upDateSelectCity(SelectListDb db) {
        String city = db.getmCityname();
        //查询数据是否有该城市
        List<SelectListDb> listDbs = DataSupport.where("mCityname = ?", String.valueOf(city)).find(SelectListDb.class);
        SelectListDb db1 = listDbs.get(0);
        db1.setmWeatherTemp(db.getmWeatherTemp());
        db1.setmWeatherStatus(db.getmWeatherStatus());
        db1.save();
        Utils.logData("修改数据：" + db.getmCityname());

    }
}
