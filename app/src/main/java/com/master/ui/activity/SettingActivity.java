package com.master.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.master.R;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.PreferencesUtils;
import com.master.app.tools.ToastUtils;
import com.master.contract.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

import static com.master.app.Constants.OUT_COLLECTION_CAR;
import static com.master.app.Constants.OUT_COLLECTION_KIND;
import static com.master.app.Constants.OUT_COLLECTION_SPACE;
import static com.master.app.Constants.OUT_COLLECTION_SWICTH;
import static com.master.app.Constants.OUT_COLLECTION_TIME;
import static com.master.app.Constants.default_null_tag;

/**
 * Created by Litao-pc on 2016/9/12.
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.sc_timeInterval)
    SwitchCompat scTimeInterval;

    @BindView(R.id.et_timeInterval)
    AppCompatEditText etTimeInterval;

    @BindView(R.id.sw_spaceInterval)
    SwitchCompat swSpaceInterval;

    @BindView(R.id.et_spaceInterval)
    AppCompatEditText etSpaceInterval;

    @BindView(R.id.sc_speed)
    SwitchCompat scSpeed;

    @BindView(R.id.et_speed)
    AppCompatEditText etSpeed;


    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("采集设置");

        boolean speed_switch = PreferencesUtils.getBoolean(this, OUT_COLLECTION_SWICTH, false);
        if (speed_switch){
            etSpeed.setEnabled(true);
            scSpeed.setChecked(true);
        }


        String collection_model = PreferencesUtils.getString(this, OUT_COLLECTION_KIND, default_null_tag);
        if (collection_model.equals(OUT_COLLECTION_TIME)){
            scTimeInterval.setChecked(true);

        }else if (collection_model.equals(OUT_COLLECTION_SPACE)){
            swSpaceInterval.setChecked(true);
        }
        int anInt1 = PreferencesUtils.getInt(this, OUT_COLLECTION_TIME, 5);
        int anInt2 = PreferencesUtils.getInt(this, OUT_COLLECTION_SPACE, 100);
        int anInt = PreferencesUtils.getInt(this, OUT_COLLECTION_CAR, 10);
        etTimeInterval.setText(String.valueOf(anInt1));
        etSpaceInterval.setText(String.valueOf(anInt2));
        etSpeed.setText(String.valueOf(anInt));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (R.id.save==itemId){
            String space = etSpaceInterval.getText().toString();
            String time = etTimeInterval.getText().toString();
            String speed = etSpeed.getText().toString();
            PreferencesUtils.putInt(this,OUT_COLLECTION_TIME,Integer.parseInt(time));
            PreferencesUtils.putInt(this,OUT_COLLECTION_SPACE,Integer.parseInt(space));
            PreferencesUtils.putInt(this,OUT_COLLECTION_CAR,Integer.parseInt(speed));
            super.hideSoftKeyboard();
            ToastUtils.showToast("保存设置成功");
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.sc_timeInterval, R.id.et_timeInterval, R.id.sw_spaceInterval,
        R.id.et_spaceInterval, R.id.sc_speed, R.id.et_speed})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sc_timeInterval:
                if (scTimeInterval.isChecked()){
                    PreferencesUtils.putString(this, OUT_COLLECTION_KIND,
                        OUT_COLLECTION_TIME);
                    etTimeInterval.setEnabled(true);
                    etSpaceInterval.setEnabled(false);
                    swSpaceInterval.setChecked(false);

                }else {
                    PreferencesUtils.putString(this, OUT_COLLECTION_KIND, default_null_tag);
                    etTimeInterval.setEnabled(false);
                }
                break;
            case R.id.sw_spaceInterval:
                if (swSpaceInterval.isChecked()){
                    PreferencesUtils.putString(this, OUT_COLLECTION_KIND,
                        OUT_COLLECTION_SPACE);
                    etSpaceInterval.setEnabled(true);
                    etTimeInterval.setEnabled(false);
                    scTimeInterval.setChecked(false);
                }else {
                    PreferencesUtils.putString(this, OUT_COLLECTION_KIND, default_null_tag);
                    etTimeInterval.setEnabled(false);
                }
                break;
            case R.id.sc_speed:
                if (scSpeed.isChecked()){
                    PreferencesUtils.putBoolean(this, OUT_COLLECTION_SWICTH,true);
                    etSpeed.setEnabled(true);
                }else {
                    PreferencesUtils.putBoolean(this, OUT_COLLECTION_SWICTH,false);
                    etSpeed.setEnabled(false);
                }
                break;

        }
    }
}
