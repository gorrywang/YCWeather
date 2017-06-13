package xyz.abug.www.ycweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.abug.www.ycweather.R;
import xyz.abug.www.ycweather.db.SelectListDb;
import xyz.abug.www.ycweather.utils.Utility;

/**
 * Created by Dell on 2017/6/13.
 */

public class ListFragment extends Fragment {
    private View mView;
    private RecyclerView mRecycler;
    private List<SelectListDb> mSelectCity;
    private MyAdapter mMyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindID();
        //遍历数据
        mSelectCity = Utility.findSelectCity();
        mMyAdapter = new MyAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(mMyAdapter);
    }

    private void bindID() {
        mRecycler = mView.findViewById(R.id.frag_list_recycler);
    }

    /**
     * 适配器
     */
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
            ViewHolder holder = new ViewHolder(inflate);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SelectListDb db = mSelectCity.get(position);
            holder.city.setText(db.getmCityname());
            holder.status.setText(db.getmWeatherStatus());
            holder.temp.setText(db.getmWeatherTemp()+"°C");
        }

        @Override
        public int getItemCount() {
            return mSelectCity.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView city, status, temp;

            public ViewHolder(View itemView) {
                super(itemView);
                city = itemView.findViewById(R.id.item_city);
                status = itemView.findViewById(R.id.item_status);
                temp = itemView.findViewById(R.id.item_qw);
            }
        }
    }
}
