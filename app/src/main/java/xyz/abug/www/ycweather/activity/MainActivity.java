package xyz.abug.www.ycweather.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

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
import xyz.abug.www.ycweather.fragment.SearchFragment;
import xyz.abug.www.ycweather.gson.Weather;
import xyz.abug.www.ycweather.utils.HttpUtils;
import xyz.abug.www.ycweather.utils.Utility;
import xyz.abug.www.ycweather.utils.Utils;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_KEY;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_SEARCH_1;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_SEARCH_2;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_WEATHER_1;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_WEATHER_2;

public class MainActivity extends AppCompatActivity {
    //有无网络
    private boolean isNetWorkBool = true;
    private static ViewPager mViewPager;
    private static List<Fragment> fragmentList = new ArrayList<>();
    private static MyAdapter myAdapter;
    FragmentManager mManager;
    private static ImageView mImageView;
    private static FloatingActionButton mActionButton;
    private Fragment mListFragment, mSearchFragment;
    private static FrameLayout mFrame;
    private static final int LEVEL_HOME = 1;
    private static final int LEVEL_MENU = 2;
    private static final int LEVEL_ADD = 3;
    private static int mSelectLevel = 1;
    private MyBroadcast mMyBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //注册广播
        reg();
        bindID();
        mManager = getSupportFragmentManager();
        myAdapter = new MyAdapter(mManager);
        mViewPager.setAdapter(myAdapter);
        //判断有无网络，在广播中
//        isNetWork();
    }

    /**
     * 注册广播
     */
    private void reg() {
        Intent intent = new Intent();
        mMyBroadcast = new MyBroadcast();
        IntentFilter intentFilter = new IntentFilter(CONNECTIVITY_ACTION);
        registerReceiver(mMyBroadcast, intentFilter);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMyBroadcast != null)
            unregisterReceiver(mMyBroadcast);
    }

    private void bindID() {
        mViewPager = (ViewPager) findViewById(R.id.main_pager_load);
        mImageView = (ImageView) findViewById(R.id.main_img_background);
        mActionButton = (FloatingActionButton) findViewById(R.id.main_float_menu);
        mFrame = (FrameLayout) findViewById(R.id.main_frame_menu);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //是否有网络
                if (!isNetWorkBool) {
                    TSnackbar.make(mImageView, "网络未连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.WARNING).show();
                }
                switch (mSelectLevel) {
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
                        mActionButton.setVisibility(View.VISIBLE);
                        //设置等级
                        mSelectLevel = LEVEL_MENU;
                        break;
                    case LEVEL_MENU:
                        //菜单
                        //切换fragment
                        mFrame.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.GONE);
                        FragmentTransaction tran2 = mManager.beginTransaction();
                        if (mSearchFragment == null) {
                            mSearchFragment = new SearchFragment();
                        }

                        tran2.replace(R.id.main_frame_menu, mSearchFragment);
                        tran2.commit();
                        //圆角图片
                        mActionButton.setVisibility(View.GONE);
                        //设置等级
                        mSelectLevel = LEVEL_MENU;
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
                if (city.length() == 0) {
                    city = "北京";
                    TSnackbar.make(mImageView, "IP有误,默认地点更换为北京", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.WARNING).show();

                }
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
//                dd.setmWeatherStatus("---");
//                dd.setmWeatherTemp("---");
//                dd.setmWeatherId("CN101010100");
//                dd.setmCityname("北京");
//                Utility.saveSelectCity(dd);
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
    static List<SelectListDb> selectCity;
    int a = 1;

    private static void getFragment() {
        selectCity = Utility.findSelectCity();
        fragmentList.clear();
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
                    mFrame.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.GONE);
                    mActionButton.setImageResource(R.drawable.add);
                    mActionButton.setVisibility(View.VISIBLE);
                    mSelectLevel = LEVEL_MENU;
                    break;
                case LEVEL_HOME:
                    break;
                case LEVEL_MENU:
                    mFrame.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                    mActionButton.setImageResource(R.drawable.other);
                    mActionButton.setVisibility(View.VISIBLE);
                    mSelectLevel = LEVEL_HOME;
                    break;
            }
        }
        return true;
    }

    /**
     * 从菜单返回
     */
    public static void backMenu() {
        mSelectLevel = LEVEL_HOME;
        mFrame.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mActionButton.setImageResource(R.drawable.other);
        mActionButton.setVisibility(View.VISIBLE);
    }

    /**
     * 从搜索返回
     */
    public static void backSearch() {


        backMenu();
        //刷新数据
        getFragment();

    }

    /**
     * 跳转页面
     */
    public static void jump(int adapterPosition) {
        backMenu();
        Log.e("tag", adapterPosition + "");
        mViewPager.setCurrentItem(adapterPosition);
    }


    //广播
    class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CONNECTIVITY_ACTION)) {
                //监听网络
                //得到网络连接管理器
                ConnectivityManager connectionManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                //通过管理器得到网络实例
                NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
                //判断是否连接
                if (networkInfo != null && networkInfo.isAvailable()) {
//                    Toast.makeText(MainActivity.this, "联网成功",
//                            Toast.LENGTH_SHORT).show();
                    TSnackbar.make(mImageView, "网络已连接", TSnackbar.LENGTH_SHORT, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.SUCCESS).show();
                    Glide.with(MainActivity.this).load("https://bing.ioliu.cn/v1?w=480&h=640").into(mImageView);
                    isNetWorkBool = true;
                    //程序第一次运行
                    isFirstRun();
                } else {
//                    Toast.makeText(MainActivity.this, "请检查网络状态",
//                            Toast.LENGTH_SHORT).show();
                    TSnackbar.make(mImageView, "网络未连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.WARNING).show();
                    mImageView.setImageResource(R.drawable.glidebackground);
                    isNetWorkBool = false;
                }
            }
        }
    }

    /**
     * 提示
     */
    public static void showTips(String tips, Prompt photo) {
        //添加数据成功
        TSnackbar.make(mImageView, tips, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(photo).show();
    }


}
