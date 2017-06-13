package xyz.abug.www.ycweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tuyenmonkey.mkloader.MKLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xyz.abug.www.ycweather.R;
import xyz.abug.www.ycweather.activity.MainActivity;
import xyz.abug.www.ycweather.db.SelectListDb;
import xyz.abug.www.ycweather.utils.HttpUtils;
import xyz.abug.www.ycweather.utils.Utility;
import xyz.abug.www.ycweather.utils.Utils;

import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_KEY;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_SEARCH_1;
import static xyz.abug.www.ycweather.utils.Utils.URL_HEWEATHER_SEARCH_2;

/**
 * Created by Dell on 2017/6/13.
 */

public class SearchFragment extends Fragment {
    private View mView;
    private EditText mEditText;
    private ListView mListView;
    private ImageView mImageView, mImgBack;
    private List<SelectListDb> mList = new ArrayList<>();
    private List<String> mListString = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private MKLoader mMkLoader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, null);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindID();
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, mListString);
        mListView.setAdapter(mAdapter);
    }

    /**
     * id
     */
    private void bindID() {
        mMkLoader = mView.findViewById(R.id.frag_search_loader_load);
        mEditText = mView.findViewById(R.id.frag_search_edit_search);
        mListView = mView.findViewById(R.id.frag_search_list);
        mImageView = mView.findViewById(R.id.frag_search_img_ok);
        mImgBack = mView.findViewById(R.id.frag_list_imgbtn_back);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backMenu();
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mEditText.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(getContext(), "请输入搜索信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                //搜索
                mMkLoader.setVisibility(View.VISIBLE);
                searchCity(s);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SelectListDb db = mList.get(i);
                Toast.makeText(getContext(), "已保存：" + db.getmCityname(), Toast.LENGTH_SHORT).show();
                mList.clear();
                mListString.clear();
                mEditText.setText("");
                //保存数据
                Utility.saveSelectCity(db);
                //返回到菜单
                MainActivity.backSearch();
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                        mMkLoader.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                //打印数据
                Utils.logData("搜索城市数据:" + jsonData);
                List<SelectListDb> dbs = Utility.analyticSearchCity(jsonData);
                if (dbs == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMkLoader.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "请输入正确的城市", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //有数据
                    mListString.clear();
                    mList.clear();
                    mList = dbs;
                    for (SelectListDb db : mList) {
                        mListString.add(db.getmCityname());
                    }
                    //刷新适配器
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMkLoader.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
    }

}
