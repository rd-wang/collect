package com.master.ui.activity.collect;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.master.R;
import com.master.app.inter.Type;
import com.master.app.tools.ActionBarManager;
import com.master.bean.Envelope;
import com.master.bean.Fields;
import com.master.contract.BaseActivity;
import com.master.interactors.JsonformInteractor;
import com.master.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ModifActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.ll_container)
    LinearLayout llContainer;

    private List<View> mViews;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        this.title.setText("xiugai");
        ActionBarManager.initBackTitle(getSupportActionBar());
        Map<String, String> data = (Map<String, String>) ((Envelope) getIntent()
            .getSerializableExtra("data")).getT();

        String tname = getIntent().getStringExtra("tname");
        //添加编码信息
        String def_TBM = data.get("def_TBM");
        String def_TBMLM = data.get("def_TBMLM");
        data.put(def_TBMLM, def_TBM);
        //让data记录同数据库一致
        data.remove("def_TBM");
        data.remove("def_TBMLM");

        List<Fields> arguments = MainActivity.S_MainActivity.getArguments(tname, new ArrayList<>());
        mViews = JsonformInteractor.getInstance().fetchFormElements(this, arguments, null);
        llContainer.removeAllViews();

        for (int i = 0; i < arguments.size(); i++) {
            View view = mViews.get(i);
            llContainer.addView(view);
            Fields fields = arguments.get(i);
            String fName = fields.getFName();

            String s = data.get(fName);
            if (fields.getFmodify() == 0) {
                view.setEnabled(false);
            }

            if (view instanceof TextView) {
                ((TextView) view).setText(s);
            }
        }

    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_modif;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                update();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    //修改保存
    private void update() {
        super.hideSoftKeyboard();
        MainActivity.S_MainActivity.getPresenter().writeValue(mViews, Type.UPDATE);
    }
}
