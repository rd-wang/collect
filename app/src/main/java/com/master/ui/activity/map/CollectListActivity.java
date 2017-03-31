package com.master.ui.activity.map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.master.R;
import com.master.app.Constants;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.CommonUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.weight.SearchBar;
import com.master.app.weight.SearchListView;
import com.master.bean.Table;
import com.master.constant.Const;
import com.master.contract.BaseActivity;
import com.master.ui.activity.MainActivity;
import com.master.ui.activity.collect.AddLxbmActivity;

import java.util.ArrayList;

import butterknife.BindView;

public class CollectListActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.listview)
    SearchListView mListView;

    ArrayList data = new ArrayList();

    private Table table = null;
    private int collectLineType;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("路线采集");

        SearchBar searchBar = new SearchBar(this);
        mListView.addHeaderView(searchBar);
        View list_item = View.inflate(this, R.layout.list_item_1, null);
        mListView.addHeaderView(list_item);

        //根据采集路线的type来确认显示的集合
        collectLineType = Const.LineType.NEWLINE;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            table = extras.getParcelable(Constants.SELECT_COLLECT_LABE);
            collectLineType = extras.getInt(Const.COLLECT_LINE_TYPE);
        }


        new AsyncTask<Integer, Void, ArrayList>() {

            @Override
            protected ArrayList doInBackground(Integer... integers) {
                data = (ArrayList) DbHelperDbHelper.open().getAllLX();
                if (integers[0] == Const.LineType.OLDLINE) {

                }
                return data;
            }

            @Override
            protected void onPostExecute(ArrayList arrayList) {
                mListView.setAdapter(new CollectAdapter());
                mListView.setEnableRefresh(false);
            }
        }.execute(collectLineType);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_collect_list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(mContext, AddLxbmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("activity", "CollectListActivity");
                intent.putExtras(bundle);
                CommonUtils.toActivity(this, intent);
                CollectListActivity.this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CollectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View convertView = View.inflate(mContext, R.layout.ac_collect_item, null);
            TextView tvlxbm = (TextView) convertView.findViewById(R.id.tvlxbm);
            TextView tvlxmc = (TextView) convertView.findViewById(R.id.tvlxmc);
            ArrayList item = (ArrayList) getItem(i);
            tvlxbm.setText(String.valueOf(item.get(0)));
            tvlxmc.setText(String.valueOf(item.get(1)));
            //点击定位按钮
            convertView.findViewById(R.id.btcollect).setOnClickListener(v1 -> {

                CollectListActivity.this.finish(true);
                MainActivity.S_MainActivity.showAirPanel();
                //开始采集
                MainActivity.S_MainActivity.setPanelType(MainActivity.DEFUALT_NUM_L_TYPE);
                MainActivity.S_MainActivity.startLocation();
                MainActivity.S_MainActivity.currentLXBM = String.valueOf(item.get(0));
                LoggerUtils.d("CollectListActivity", "currentLXBM:" + MainActivity.S_MainActivity.currentLXBM);
            });
            return convertView;
        }
    }


}
