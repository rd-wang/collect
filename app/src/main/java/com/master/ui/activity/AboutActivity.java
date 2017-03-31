package com.master.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.master.R;
import com.master.app.tools.ActionBarManager;
import com.master.contract.BaseActivity;

import butterknife.BindView;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("关于");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_about;
    }
}
