package com.master.ui.activity.map;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.CommonDateParseUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.weight.SearchBar;
import com.master.app.weight.SearchListView;
import com.master.bean.Maps;
import com.master.model.BaseModel;
import com.master.presenter.OptionsWorkPresenter;
import com.master.ui.activity.MvpActivity;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionsWorkActivity extends MvpActivity<OptionsWorkPresenter> {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.listview)
    SearchListView mListView;
    private MyAdapter adapter;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("打开地图");

        SearchBar searchBar = new SearchBar(this);
        mListView.addHeaderView(searchBar);
        View list_item = View.inflate(this, R.layout.list_item_3, null);
        mListView.addHeaderView(list_item);
        adapter = new MyAdapter();
        presenter.getMapFormData();
        mListView.setAdapter(adapter);
        mListView.setEnableRefresh(false);
        mListView.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(mContext, "item", Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_options_work;
    }

    @Override
    protected OptionsWorkPresenter createPresenter() {
        return new OptionsWorkPresenter(new BaseModel());
    }


    public void initPortalItemView(List list) {
        adapter.setData(list);
    }

    class MyAdapter extends BaseAdapter {
        private List<Maps> list;
        private ViewHolder holder;

        public void setData(List<Maps> list) {
            this.list = list;

            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(SynopsisObj.getAppContext(), R.layout.map_list_item2, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            String msg = "cx";
            int id = 10000 + list.get(position).mId;
            holder.tvId.setText(msg + id);
            holder.tvName.setText(list.get(position).mName);
            LoggerUtils.d("adapter", list.get(position).cjsj);

            holder.tvDate.setText(CommonDateParseUtils.date2string(new Date(Long.parseLong(list.get(position).cjsj)), CommonDateParseUtils.YYYY_MM_DD_HH_MM));
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tv_id)
            TextView tvId;
            @BindView(R.id.tv_name)
            TextView tvName;
            @BindView(R.id.tv_date)
            TextView tvDate;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
