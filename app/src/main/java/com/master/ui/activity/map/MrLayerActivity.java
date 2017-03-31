package com.master.ui.activity.map;

import com.master.R;
import com.master.app.tools.ActionBarManager;
import com.master.contract.BaseActivity;
import com.rey.material.widget.CheckBox;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;

import static com.master.ui.fragment.map.LayerFragment.S_LayerFragment;

public class MrLayerActivity extends BaseActivity {

    Context mContent = null;

    ArrayList data = null;

    double mapscale = 0;

    MapLayerInfoAdapter myAdapter;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.listview)
    ListView mListView;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("图层管理");
        Bundle bundle = this.getIntent().getExtras();
        data = bundle.getStringArrayList("maplayerinfo");
        //当前比例尺
        mapscale = bundle.getDouble("mapscale");
        myAdapter = new MapLayerInfoAdapter();
        mListView.setAdapter(myAdapter);

    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_maplayerinfo;
    }

    @Override
    public void finish() {
        ArrayList arrayList = new ArrayList();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                ArrayList item = (ArrayList) data.get(i);
                arrayList.add(item.get(3));
            }
            S_LayerFragment.relaodMaplayers(arrayList);
        }
        super.finish();
    }

    class MapLayerInfoAdapter extends BaseAdapter {

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

            View convertView = View.inflate(mContext, R.layout.maplayerinfo_list_item, null);
            TextView tvlayername = (TextView) convertView.findViewById(R.id.tvlayername);
            TextView tvscale = (TextView) convertView.findViewById(R.id.tvscale);
            CheckBox chekbox_tuceng = (CheckBox) convertView.findViewById(R.id.chekbox_tuceng);
            ArrayList item = (ArrayList) getItem(i);
            tvlayername.setText((String) item.get(0));
            tvscale.setText((String) item.get(1));
            if ((double) item.get(2) < mapscale) {
//                chekbox_tuceng.setEnabled(false);
            }
            if ((boolean) item.get(3)) {
                chekbox_tuceng.setChecked(true);
            }
            chekbox_tuceng.setTag(i);
            chekbox_tuceng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int p = (Integer) buttonView.getTag();
                    ArrayList item = (ArrayList) data.get(p);
                    item.add(3, !Boolean.valueOf(item.get(3).toString()));
//                    myAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
}
