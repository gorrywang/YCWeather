package xyz.abug.www.ycweather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Dell on 2017/6/13.
 * 联网获取数据
 */

public class HttpUtils {
    /**
     * 获取数据
     *
     * @param mStrURL   链接地址
     * @param mCallback 回掉接口
     */
    public static void sendQuestBackResponse(String mStrURL, Callback mCallback) {
        //客户端
        OkHttpClient client = new OkHttpClient();
        //请求
        Request request = new Request.Builder().url(mStrURL).build();
        //发送请求
        client.newCall(request).enqueue(mCallback);
    }

    /**
     * 判断有无网络
     *
     * @param context 上下文
     * @return true成功 false失败
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(cm == null)) {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
