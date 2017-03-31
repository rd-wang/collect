package com.master.ui.activity.map;

import com.master.R;
import com.master.app.inter.OnSearchOperateListener;
import com.master.app.manager.ConfigManager;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.ContextUtils;
import com.master.app.tools.DialogUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.weight.SearchBar;
import com.master.app.weight.SearchListView;
import com.master.bean.Maps;
import com.master.bean.TableContext;
import com.master.model.BaseModel;
import com.master.presenter.ExportandWorkPresenter;
import com.master.ui.activity.MvpActivity;
import com.master.ui.adapter.MapCussorAdapter;
import com.master.ui.viewer.ExportandWorkView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

public class ExportandWorkActivity extends MvpActivity<ExportandWorkPresenter>
    implements ExportandWorkView {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.listview)
    SearchListView mListView;

    private MapCussorAdapter adapter;

    private ProgressDialog myDialog;

    private List<Maps> mSearchRootDate;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());

        SearchBar searchBar = new SearchBar(this);
        adapter = new MapCussorAdapter(this, getIntent().getFlags());
        searchBar.setText("输入地图名称关键字");
        searchBar.setOnSearchOperateListener(new OnSearchOperateListener() {

            @Override
            public void onSearchOperate(String text) {
                mSearchRootDate = adapter.getData();
                LoggerUtils.d("mSearchRootDate", mSearchRootDate.size() + "");
                List<Maps> temp = new ArrayList<>();
                for (Maps m : mSearchRootDate) {
                    boolean contains = m.mName.contains(text);
                    if (contains) {
                        temp.add(m);
                    }
                }
                initPortalItemView(temp);
            }

            @Override
            public void onClearOperate() {

                if (mSearchRootDate != null) {
                    initPortalItemView(mSearchRootDate);
                }

            }
        });
        mListView.addHeaderView(searchBar);
        presenter.getMapFormData();
        mListView.setAdapter(adapter);
        mListView.setEnableRefresh(false);
        if (getIntent().getFlags() == 1) {
            initExporListEvent();
            title.setText("导出地图");
        } else {
            title.setText("管理工作地图");
            View head = ContextUtils.inflate(R.layout.list_item_3);
            head.setEnabled(false);
            mListView.addHeaderView(head);
        }
    }

    private void initExporListEvent() {
        TextView lable = new TextView(this);
        ListView.LayoutParams layoutParams = new ListView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = ContextUtils.dip2px(this, 42);
        lable.setLayoutParams(layoutParams);
        lable.setGravity(Gravity.CENTER);
        lable.setTextColor(getResources().getColor(R.color.red));
        lable.setText("注:地图导出后，将生成json文件，保存到exchange目录  \n 单击条目进行选中");
        lable.setEnabled(false);
        mListView.addHeaderView(lable);

    }

    public void export(int id) {
        presenter.exportJson(id);
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_options_work;
    }

    @Override
    protected ExportandWorkPresenter createPresenter() {
        return new ExportandWorkPresenter(new BaseModel());
    }


    public void initPortalItemView(List list) {
        adapter.setData(list);
    }

    @Override
    public Observable<List<TableContext>> getTableEntityValue() {
        return Observable.just(ConfigManager.create().getTablecontextList());
    }

    @Override
    public void showDialog(String msg) {
        // 获取对象
        myDialog = new ProgressDialog(this);
        myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置样式为圆形样式
        myDialog.setMessage(msg); // 设置进度条的提示信息
        myDialog.setIndeterminate(false); // 设置进度条是否为不明确
        myDialog.setCancelable(true); // 设置进度条是否按返回键取消
        // 为进度条添加确定按钮 ， 并添加单机事件
        myDialog.setButton("确定", (dialog, which) -> {
            dialog.cancel(); // 撤销进度条
        });
        myDialog.show(); // 显示进度条
    }

    @Override
    public void hintDialog(String msg) {
        runOnUiThread(() -> {
            if (myDialog != null) {
                myDialog.cancel(); // 撤销进度条
            }
            if (msg != null) {
                new DialogUtils.Builder(this).addTitle("提示").addMsg(msg).build().showDialog();
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (myDialog != null) {
            myDialog.dismiss();
        }
        super.onDestroy();
    }
}
