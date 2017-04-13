package com.master.ui.activity.collect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.master.R;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.ToastUtils;
import com.master.contract.BaseActivity;
import com.master.ui.activity.map.CollectListActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

public class AddLxbmActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String lx_xzqj = "";

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("新增路线");

        //根据id获取对象
        Spinner spinner = (Spinner) findViewById(R.id.sp_xzdj);
        //显示的数组
        final String arr[] = new String[]{"G", "S", "X", "C", "Z"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        spinner.setAdapter(arrayAdapter);
        //注册事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lx_xzqj = arr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "没有改变的处理", Toast.LENGTH_LONG).show();
            }

        });
        AppCompatButton bt_submit = (AppCompatButton) findViewById(R.id.btn_submit);
        EditText tv_lxbh = (EditText) findViewById(R.id.tv_lxbh);
        EditText tv_lxmc = (EditText) findViewById(R.id.tv_lxmc);
        EditText tv_xzqh = (EditText) findViewById(R.id.tv_xzqh);
        Bundle bundle = this.getIntent().getExtras();
        String activity = bundle.getString("activity");
        if ("XzqhListActivity".equals(activity)) {
            String xzqh = bundle.getString("xzqh");
            tv_xzqh.setText(xzqh);
        }

        tv_xzqh.setClickable(true);
        tv_xzqh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, XzqhListActivity.class));
                AddLxbmActivity.this.finish();
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lxbh = String.valueOf(tv_lxbh.getText());
                if (lxbh.length() > 4) {
                    ToastUtils.showToast("路线编号不能超过4位");
                    return;
                } else if (chineseCount(lxbh) > 0) {
                    ToastUtils.showToast("路线编号不能出现中文");
                    return;
                }
                String lxmc = String.valueOf(tv_lxmc.getText());
                String xzqh = String.valueOf(tv_xzqh.getText());
                String lxbm = lx_xzqj + xzqh + lxbh;
                Map<String, String> map = new HashMap<>();
                map.put("LXBM", lxbm);
                map.put("LXMC", lxmc);
                if (DbHelperDbHelper.open().addTablEntry(DbHelperDbHelper.LX_TABLE, map, true) < 0) {
                    showToast("保存失败，编码已存在");
                } else {
                    startActivity(new Intent(mContext, CollectListActivity.class));
                    AddLxbmActivity.this.finish();
                }

            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_add_lx;
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public int chineseCount(String lxbh) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(lxbh);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }
}
