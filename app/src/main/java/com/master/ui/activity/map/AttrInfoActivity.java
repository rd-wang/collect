package com.master.ui.activity.map;

import com.master.R;
import com.master.app.tools.ActionBarManager;
import com.master.app.weight.SearchListView;
import com.master.contract.BaseActivity;
import com.master.ui.adapter.AttributeListAdapter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;

public class AttrInfoActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.listView)
    SearchListView mListView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        ArrayList arraylist = new ArrayList();
        Bundle bundle = this.getIntent().getExtras();
        String titleName = bundle.getString("title");
        this.title.setText(titleName);
        ArrayList data =(ArrayList) bundle.getStringArrayList("arraylist");
        AttributeListAdapter myAdapter = new AttributeListAdapter(mContext, data);
        mListView.setAdapter(myAdapter);
        mListView.setEnableRefresh(false);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_property_list;
    }

}
