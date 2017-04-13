package com.master.ui.activity.collect;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.master.R;
import com.master.app.inter.Type;
import com.master.app.manager.RecordingMedium;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.StringUtils;
import com.master.app.tools.ToastUtils;
import com.master.bean.Envelope;
import com.master.bean.Fields;
import com.master.contract.BaseActivity;
import com.master.interactors.JsonformInteractor;
import com.master.ui.activity.MainActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.master.app.orm.DbHelperDbHelper.LX_TABLE;

public class ModifActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.ll_container)
    LinearLayout llContainer;

    @BindView(R.id.delete)
    Button delete;

    private List<View> mViews;
    private String tname;
    private Map<String, String> data;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        this.title.setText("修改");
        ActionBarManager.initBackTitle(getSupportActionBar());
        data = (Map<String, String>) ((Envelope) getIntent()
                .getSerializableExtra("data")).getT();

        tname = getIntent().getStringExtra("tname");
        //添加编码信息
        String def_TBM = data.get("def_TBM");
        String def_TBMLM = data.get("def_TBMLM");
        data.put(def_TBMLM, def_TBM);
        //让data记录同数据库一致
        data.remove("def_TBM");
        data.remove("def_TBMLM");
        //路线是固定表(我觉得应该用固定配置来处理而不是单独处理 不知道为啥这样设计。)
        if (LX_TABLE.equals(tname)) {
            llContainer.removeAllViews();
            List<View> views = getLXTextView(data.get("LXBM"), data.get("LXMC"));
            for (View view : views) {
                llContainer.addView(view);
            }
        } else {
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


    }

    @OnClick({R.id.delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete:
                new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("删除")
                        .setContentText("确定删除此记录?")
                        .setConfirmText("确定")
                        .setCancelText("取消")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            boolean b;
                            if ("T_ld".equals(tname)) {
                                b = deleteLd();
                            } else {
                                b = deleteNormal();
                            }
                            sweetAlertDialog.setContentText("删除" + (b ? "成功" : "失败")).showCancelButton(false)
                                    .setTitleText("")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(b ? SweetAlertDialog.SUCCESS_TYPE : SweetAlertDialog.ERROR_TYPE);
                        }).show();
                break;
        }
    }

    private boolean deleteLd() {
        String LDBM = "";
        String LXBM = "";
        String qd = "";
        String zd = "";
        Set<Map.Entry<String, String>> entries = data.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.endsWithIgnoreCase(key, "LDBM")) {
                LDBM = value;
            }
            if (StringUtils.endsWithIgnoreCase(key, "QDJWDID")) {
                qd = value;
            }
            if (StringUtils.endsWithIgnoreCase(key, "ZDJWDID")) {
                zd = value;
            }
            if (StringUtils.endsWithIgnoreCase(key, "LXBM")) {
               LXBM = value;
            }
        }
        return DbHelperDbHelper.open().deleteLDRecord(tname, LDBM, LXBM, RecordingMedium.getWorkMapId(),qd,zd);
    }

    private boolean deleteNormal() {
        String attrBM = "";
        String stringBM = "";
        Set<Map.Entry<String, String>> entries = data.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isEmpty(attrBM) && StringUtils.endsWithIgnoreCase(key, "BM")) {
                attrBM = key;
                stringBM = value;
            }
        }
        return DbHelperDbHelper.open().deleteRecord(tname, attrBM, stringBM, RecordingMedium.getWorkMapId());

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

    private List<View> getLXTextView(String lxbm, String lxmc) {
        List<View> views = new ArrayList<>();
        views.add(getEditView("路线编码", lxbm, true));
        views.add(getEditView("路线名称", lxmc, true));
        return views;
    }

    private View getEditView(String attr, String text, boolean enable) {
        MaterialEditText editText = (MaterialEditText) LayoutInflater.from(this).inflate(R.layout.item_edit_text, null);
        editText.setMinCharacters(0);
        editText.setFloatingLabelText(attr);
        editText.setHint("输入" + attr);
        editText.setSingleLine();
        editText.setId(ViewUtil.generateViewId());
        editText.setMaxCharacters(20);
        editText.setText(text);
        editText.setEnabled(enable);
        return editText;
    }


    //修改保存
    private void update() {
        super.hideSoftKeyboard();
        if (LX_TABLE.equals(tname)) {
            if (llContainer.getChildCount() != 2) {
                ToastUtils.showToast("系统错误");
                return;
            }
            String lxbm = ((MaterialEditText) llContainer.getChildAt(0)).getText().toString().trim();
            String lxmc = ((MaterialEditText) llContainer.getChildAt(1)).getText().toString().trim();
            if (TextUtils.isEmpty(lxbm) || TextUtils.isEmpty(lxmc)) {
                ToastUtils.showToast("不能为空");
                return;
            }
            if (DbHelperDbHelper.open().updateLXTable(lxbm, lxmc, data.get("CROWID")) > 0) {
                ToastUtils.showToast("修改成功");
            }
        } else {
            MainActivity.S_MainActivity.getPresenter().writeValue(mViews, Type.UPDATE);
        }
    }
}
