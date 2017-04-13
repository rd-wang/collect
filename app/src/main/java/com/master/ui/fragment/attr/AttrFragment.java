package com.master.ui.fragment.attr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.master.R;
import com.master.app.RestricMsg;
import com.master.app.manager.AcquisitionPara;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.CommonUtils;
import com.master.app.weight.CustomTextView;
import com.master.app.weight.SearchListView;
import com.master.bean.Table;
import com.master.contract.BaseFragment;
import com.master.ui.activity.PropertyListActivity;
import com.master.ui.adapter.DatabaseInfoAdapter;
import com.master.ui.adapter.MapLayerInfoAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.master.R.id.ct_roadAttr;
import static com.master.ui.fragment.map.LayerFragment.S_LayerFragment;

/**
 * @author Litao-pc on 2016/10/27.
 *         ~
 */
public class AttrFragment extends BaseFragment {

    @BindView(R.id.ct_workAttr)
    CustomTextView ctWorkAttr;

    @BindView(R.id.ct_roadAttr)
    CustomTextView ctRoadAttr;

    @BindView(R.id.toolbar_att)
    Toolbar toolbar_att;

    @BindView(R.id.listview_gl)
    SearchListView mListView;

    @BindView(R.id.listview_work)
    SearchListView listview_work;

    Observable attrFragment = null;

    @Override
    public void bindView(Bundle savedInstanceState) {
        toggleColor(false);
        toolbar_att.setNavigationIcon(null);
        List<Table> tempDate = new ArrayList<>();
        List<Table> lineList = AcquisitionPara.getLineList();
        List<Table> pointList = AcquisitionPara.getPointList();
        Table table = new Table();
        table.setLayerType("23");
        table.setTName(DbHelperDbHelper.LX_TABLE);
        table.setTNameCHS("路线");
        tempDate.add(table);
        tempDate.addAll(lineList);
        tempDate.addAll(pointList);
        DatabaseInfoAdapter myAdapterWk = new DatabaseInfoAdapter(mContext, tempDate);
        listview_work.setAdapter(myAdapterWk);
        listview_work.setEnableRefresh(false);


        attrFragment = RestricMsg.get().register("AttrFragment", String.class);
//        attrFragment.subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
//            @Override
//            public void call(String o) {
//
//            }
//        });
//        RestricMsg.get().post();
        attrFragment.subscribeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            ArrayList data = new ArrayList();
            String filepath = S_LayerFragment.filepath;
            Geodatabase geodatabase = null;
            try {
                geodatabase = new Geodatabase(filepath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (geodatabase != null) {
                List getdatatables = geodatabase.getGeodatabaseTables();
                GeodatabaseFeatureTable geodatabaseFeatureTable = null;
                for (int i = 0; i < getdatatables.size(); i++) {
                    geodatabaseFeatureTable = (GeodatabaseFeatureTable) getdatatables.get(i);
                    String tablename = geodatabaseFeatureTable.getTableName();
                    Log.e("TAG", "tablename" + tablename);
                    long featurecount = geodatabaseFeatureTable.getNumberOfFeatures();
                    ArrayList item = new ArrayList();
                    item.add(0, tablename);
                    item.add(1, featurecount);
                    data.add(item);
                }
            }
            mListView.setOnItemClickListener((parent, view, position, id) -> {
                //点击事件执行
                String tablename = "";
                if (data.size() > 0) {
                    tablename = (String) ((ArrayList) data.get(position)).get(0);
                    Log.e("TAG", "tablename" + tablename);
                }
                Intent intent = new Intent(mContext, PropertyListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tablename", tablename);
                bundle.putString("filepath", filepath);
                intent.putExtras(bundle);
                CommonUtils.toActivity(mContext, intent);

            });
            MapLayerInfoAdapter myAdapter = new MapLayerInfoAdapter(mContext, data);
            mListView.setAdapter(myAdapter);
            mListView.setEnableRefresh(false);
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_attr;
    }


    void toggleColor(boolean tag) {
        if (tag) {
            mListView.setVisibility(View.VISIBLE);
            listview_work.setVisibility(View.INVISIBLE);
            ctWorkAttr.setSolidColor(Color.parseColor("#adadad"));
            ctRoadAttr.setSolidColor(Color.parseColor("#ffffff"));
        } else {
            mListView.setVisibility(View.INVISIBLE);
            listview_work.setVisibility(View.VISIBLE);
            ctRoadAttr.setSolidColor(Color.parseColor("#adadad"));
            ctWorkAttr.setSolidColor(Color.parseColor("#ffffff"));
        }
    }

    @OnClick({R.id.ct_roadAttr, R.id.ct_workAttr})
    public void onClick(View view) {
        switch (view.getId()) {
            case ct_roadAttr:
                toggleColor(true);
                break;
            case R.id.ct_workAttr:
                toggleColor(false);
                break;
        }
    }

    @Override
    public void onDestroy() {
        RestricMsg.get().unregister("AttrFragment", attrFragment);
        super.onDestroy();
    }
}

