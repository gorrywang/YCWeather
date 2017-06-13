package xyz.abug.www.ycweather.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.abug.www.ycweather.R;
import xyz.abug.www.ycweather.db.SelectListDb;
import xyz.abug.www.ycweather.fragment.ListFragment;
import xyz.abug.www.ycweather.fragment.MainFragment;
import xyz.abug.www.ycweather.gson.Weather;
import xyz.abug.www.ycweather.utils.HttpUtils;
import xyz.abug.www.ycweather.utils.Utility;
import xyz.abug.www.ycweather.utils.Utils;

import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_KEY;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_SEARCH_1;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_SEARCH_2;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_WEATHER_1;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_WEATHER_2;

public class MainActivity extends AppCompatActivity {
    //有无网络
    private boolean isNetWorkBool = true;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private MyAdapter myAdapter;
    FragmentManager mManager;
    private ImageView mImageView;
    private FloatingActionButton mActionButton;
    private Fragment mListFragment;
    private FrameLayout mFrame;
    private static final int LEVEL_HOME = 1;
    private static final int LEVEL_MENU = 2;
    private static final int LEVEL_ADD = 3;
    private int mSelectLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindID();
        mManager = getSupportFragmentManager();
        myAdapter = new MyAdapter(mManager);
        mViewPager.setAdapter(myAdapter);
        //判断有无网络
        isNetWork();
        //程序第一次运行
        isFirstRun();
    }

    private void bindID() {
        mViewPager = (ViewPager) findViewById(R.id.main_pager_load);
        mImageView = (ImageView) findViewById(R.id.main_img_background);
        mActionButton = (FloatingActionButton) findViewById(R.id.main_float_menu);
        mFrame = (FrameLayout) findViewById(R.id.main_frame_menu);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mSelectLevel) {
                    case LEVEL_ADD:
                        //添加页面
                        break;
                    case LEVEL_HOME:
                        //主页
                        //切换fragment
                        mFrame.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.GONE);
                        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
                        if (mListFragment == null) {
                            mListFragment = new ListFragment();
                        }

                        fragmentTransaction.replace(R.id.main_frame_menu, mListFragment);
                        fragmentTransaction.commit();
                        //圆角图片
                        mActionButton.setImageResource(R.drawable.add);
                        //设置等级
                        mSelectLevel = LEVEL_MENU;
                        break;
                    case LEVEL_MENU:
                        //菜单
                        break;
                }
            }
        });
    }

    /**
     * 判断网络
     */
    private void isNetWork() {
        isNetWorkBool = HttpUtils.isNetworkAvailable(MainActivity.this);
        if (!isNetWorkBool) {
            Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Glide.with(this).load("http://bing.ioliu.cn/v1?w=480&h=640").into(mImageView);
        }
    }

    /**
     * 运行
     */
    private void isFirstRun() {
        boolean firstRun = Utils.isFirstRun(MainActivity.this);
        if (firstRun) {
            //第一次运行
            getIP();
        } else {
            //非第一次运行
            getFragment();
        }
    }

    /**
     * 获取IP地址
     */
    private void getIP() {
        HttpUtils.sendQuestBackResponse(Utils.URL_YY_GETIP, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.logData("错误获取Ip地址");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String notJsonData = response.body().string();
                //打印数据
                Utils.logData(notJsonData);
                //解析城市
                String city = Utility.analyticFirstIp(notJsonData);
                //搜索城市
                searchCity(city);
                //打印城市
                Utils.logData("第一次运行:" + city);

            }
        });
    }

    /**
     * 搜索城市，子线程中,获取代码
     */
    private void searchCity(String city) {
        HttpUtils.sendQuestBackResponse(URL_HEWEATHER_SEARCH_1 + city + URL_HEWEATHER_SEARCH_2 + URL_HEWEATHER_KEY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                //打印数据
                Utils.logData("第一次搜索城市数据:" + jsonData);
                List<SelectListDb> dbs = Utility.analyticSearchCity(jsonData);
                //保存数据
                SelectListDb db = dbs.get(0);
                Utility.saveSelectCity(db);
                //保存数据
                SelectListDb dd = new SelectListDb();
                //******************************测试***********************************
                dd.setmWeatherStatus("---");
                dd.setmWeatherTemp("---");
                dd.setmWeatherId("CN101010100");
                dd.setmCityname("北京");
                Utility.saveSelectCity(dd);
//                String weatherId = db.getmWeatherId();
//                Utils.logData("第一次获取天气id：" + weatherId);
                //根据id获取天气
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getFragment();
                    }
                });
            }
        });
    }

    /**
     * 获取天气
     *
     * @param weatherId 天气代码
     */
    private void getWeather(final String weatherId) {
        HttpUtils.sendQuestBackResponse(URL_HEWEATHER_WEATHER_1 + weatherId + URL_HEWEATHER_WEATHER_2 + URL_HEWEATHER_KEY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Utils.logData("天气数据：" + data);
                final Weather weather = Utility.analyticWeather(data);
                //显示数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //保存搜索内容
//                        SelectListDb db = new SelectListDb();
//                        db.setmWeatherStatus(weather.now.cond.txt);
//                        db.setmWeatherTemp(weather.now.tmp);
//                        db.setmCityname(weather.basic.city);
//                        db.setmWeatherId(weather.basic.weatherId);
//                        Utility.upDateSelectCity(db);
                        //获取fragment
                    }
                });
            }
        });
    }

    /**
     * 获取fragment
     */
    List<SelectListDb> selectCity;
    int a = 1;

    private void getFragment() {
        selectCity = Utility.findSelectCity();
        for (SelectListDb db : selectCity) {
            db.getmWeatherId();
            Fragment fragment = new MainFragment(db.getmWeatherId());
            fragmentList.add(fragment);
        }
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter == null) {
            mViewPager.setAdapter(myAdapter);
        } else {
            myAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 滑动适配器
     */
    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (mSelectLevel) {
                case LEVEL_ADD:
                    break;
                case LEVEL_HOME:
                    break;
                case LEVEL_MENU:
                    mFrame.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                    mActionButton.setImageResource(R.drawable.other);
                    mSelectLevel = LEVEL_HOME;
                    break;
            }
        }
        return true;
    }
}
