package com.master.ui.activity;

import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.query.QueryParameters;
import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.CommonUtils;
import com.master.app.weight.SearchBar;
import com.master.app.weight.SearchListView;
import com.master.contract.BaseActivity;
import com.master.ui.activity.map.AttrInfoActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;

import static com.master.ui.activity.MainActivity.S_MainActivity;
import static com.master.ui.fragment.map.LayerFragment.S_LayerFragment;

public class PropertyListActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.listView)
    SearchListView mListView;

    PropertyAdapter myAdapter;

    ArrayList data = null;

    static int vcount = 20;

    int counti = vcount;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        SearchBar searchBar = new SearchBar(this);
        mListView.addHeaderView(searchBar);
        mListView.pullRefreshEnable(false);
        //mListView.setAutoFetchMore(true);
        ArrayList arraylist = new ArrayList();
        final FeatureResult[] vfeatureSet = new FeatureResult[1];
        Bundle bundle = this.getIntent().getExtras();
        String tablename = bundle.getString("tablename");
        String filepath = bundle.getString("filepath");
        this.title.setText(tablename + "信息");
        Geodatabase geodatabase = null;

        try {
            geodatabase = new Geodatabase(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (geodatabase != null) {
            QueryParameters query = new QueryParameters();
            query.setReturnGeometry(true);
            query.setOutFields(new String[]{"*"});
            Log.e("dd", geodatabase.getPath());
            List getdatatables = geodatabase.getGeodatabaseTables();
            GeodatabaseFeatureTable geodatabaseFeatureTable = null;
            for (int i = 0; i < getdatatables.size(); i++) {
                geodatabaseFeatureTable = (GeodatabaseFeatureTable) getdatatables.get(i);
                if (geodatabaseFeatureTable.getTableName().equalsIgnoreCase(tablename)) {
                    geodatabaseFeatureTable
                        .queryFeatures(query, new CallbackListener<FeatureResult>() {
                            public void onCallback(FeatureResult featureSet) {
                                Log.e("TAG", "Feature featureCount=" + featureSet.featureCount());
                                Feature feature = null;
                                Iterator iter = featureSet.iterator();
                                int i = 0;
                                data = new ArrayList();
                                while (iter.hasNext() && i < vcount) {
                                    ArrayList attrlist = new ArrayList();
                                    feature = (Feature) iter.next();
                                    Map attr = feature.getAttributes();
                                    attrlist.add(0, feature.getAttributeValue("LXBM"));
                                    attrlist.add(1, feature.getAttributeValue("LXMC"));
                                    attrlist.add(2, attr);
                                    attrlist.add(3, tablename);
                                    attrlist.add(4, feature);
                                    data.add(attrlist);
                                    i++;
                                }
                                runOnUiThread(() -> {
                                    myAdapter = new PropertyAdapter();
                                    mListView.setAdapter(myAdapter);
                                    // show the editor dialog.
                                    mListView.setOnLastItemVisibleListener(
                                        () -> {
                                            int i1 = 0;
                                            while (iter.hasNext() && i1 < vcount) {
                                                ArrayList attrlist = new ArrayList();
                                                Feature feature1 = (Feature) iter.next();
                                                Map attr = feature1.getAttributes();
                                                attrlist.add(0,
                                                    feature1.getAttributeValue("LXBM"));
                                                attrlist.add(1,
                                                    feature1.getAttributeValue("LXMC"));
                                                attrlist.add(2, attr);
                                                attrlist.add(3, tablename);
                                                attrlist.add(4, feature1);
                                                data.add(attrlist);
                                                i1++;
                                            }
                                            myAdapter.notifyDataSetChanged();
                                            mListView.setAdapter(myAdapter);
                                            // mListView.setEnableRefresh(false);
                                            mListView.onRefreshComplete();

                                        });
                                });
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }
                        });
                }
            }
        }

    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_property_list;
    }

    private class PropertyAdapter extends BaseAdapter {

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
            View convertView = View
                .inflate(SynopsisObj.getAppContext(), R.layout.ac_property_item, null);
            TextView tv_code = (TextView) convertView.findViewById(R.id.tv_code);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ArrayList item = (ArrayList) getItem(i);
            tv_code.setText((String) item.get(0));
            tv_name.setText((String) item.get(1));
            //点击详细按钮查看详细信息
            convertView.findViewById(R.id.ctv_info).setOnClickListener(v12 -> {
                ArrayList arraylist = new ArrayList();
                Map attrs = (Map) item.get(2);
                Set set = attrs.entrySet();
                Iterator keyiter = set.iterator();
                while (keyiter.hasNext()) {
                    ArrayList arraylist1 = new ArrayList();
                    Map.Entry entry = (Map.Entry) keyiter.next();
                    arraylist1.add(0, entry.getKey());
                    arraylist1.add(1, entry.getValue().toString());
                    arraylist.add(arraylist1);
                }
                Intent intent = new Intent(mContext, AttrInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("arraylist", arraylist);
                bundle.putString("title", "详细信息");
                intent.putExtras(bundle);
                CommonUtils.toActivity(PropertyListActivity.this, intent);
            });
            //点击定位按钮
            convertView.findViewById(R.id.ctv_localtion).setOnClickListener(v1 -> {
                Feature feature = (Feature) item.get(4);
                S_LayerFragment.localMap(feature);
                S_MainActivity.changetabs(0);
                PropertyListActivity.this.finish();
            });
            return convertView;
        }


    }

}
