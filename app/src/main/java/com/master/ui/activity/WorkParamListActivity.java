package com.master.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.master.R;
import com.master.app.inter.OnSearchOperateListener;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.ContextUtils;
import com.master.app.tools.StringUtils;
import com.master.app.weight.SearchBar;
import com.master.app.weight.SearchListView;
import com.master.contract.BaseActivity;
import com.master.ui.adapter.WorkParaListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/20
 */
public class WorkParamListActivity extends BaseActivity implements OnSearchOperateListener {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.listView)
    SearchListView mListView;

    private WorkParaListAdapter workParaListAdapter;

    private String mTname;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        mTname = intent.getStringExtra("tname");
        String chsname = intent.getStringExtra("chsname");
        this.title.setText(chsname);
        ActionBarManager.initBackTitle(getSupportActionBar());
        SearchBar searchBar = new SearchBar(this);
        searchBar.setText("输入地图名称关键字");
        mListView.addHeaderView(searchBar);
        mListView.pullRefreshEnable(false);
        View head = ContextUtils.inflate(R.layout.list_item_2);
        TextView textView = (TextView) head.findViewById(R.id.tv_bm);
        textView.setText(chsname + "编码");
        mListView.addHeaderView(head);
        workParaListAdapter = new WorkParaListAdapter(mTname, mContext);
        mListView.setAdapter(workParaListAdapter);
        searchBar.setOnSearchOperateListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initDatafromDatabase(mTname);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_work_para;
    }

    public void initDatafromDatabase(String tname) {
        new Thread(() -> {
            Cursor cursor = DbHelperDbHelper.open().queryAttrTablebyTName(tname);
            String[] columnNames = cursor.getColumnNames();
            List list = new ArrayList();
            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap();
                for (int i = 0; i < columnNames.length; i++) {
                    if (i == 0 && StringUtils.endsWithIgnoreCase(columnNames[i], "BM")) {
                        String columnstring = cursor
                                .getString(cursor.getColumnIndex(columnNames[i]));

                        //手动保存编码信息
                        map.put("def_TBM", columnstring);
                        map.put("def_TBMLM", columnNames[i]);
                    } else {
                        String columnstring = cursor
                                .getString(cursor.getColumnIndex(columnNames[i]));
                        map.put(columnNames[i], columnstring);
                    }
                }
                list.add(map);
            }
            runOnUiThread(() ->
                    workParaListAdapter.deviceDataSource(list)
            );

        }).start();
    }

    @Override
    public void onSearchOperate(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClearOperate() {
        Toast.makeText(mContext, "clear", Toast.LENGTH_SHORT).show();
    }
}
