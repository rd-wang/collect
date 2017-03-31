package com.master.ui.activity;

import com.master.R;
import com.master.app.tools.ActionBarManager;
import com.master.contract.BaseActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.BindView;

public class AdviceActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("用户反馈");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_advice;
    }
}
