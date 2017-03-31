package com.master.ui.fragment.home;

import com.master.R;
import com.master.app.tools.CommonUtils;
import com.master.contract.BaseFragment;
import com.master.ui.activity.map.ExportandWorkActivity;
import com.master.ui.activity.map.NewMapActivity;
import com.master.ui.activity.map.UncapMapActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.master.app.Constants.STATUS_DEFAULT_ORNUM;
import static com.master.app.Constants.STATUS_DEFAULT_UNNUM;

/**
 * @author Litao-pc on 2016/10/27.
 *         ~
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void bindView(Bundle savedInstanceState) {
        toolbar.setNavigationIcon(null);
        title.setText("地图");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragmeng_home;
    }


    @OnClick({R.id.atv1, R.id.atv2, R.id.atv3, R.id.atv4, R.id.atv5, R.id.act_export})
    public void onClick(View view) {
        Intent intent = new Intent(mContext, UncapMapActivity.class);
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.atv1:
                CommonUtils.toActivity(mContext, new Intent(mContext, ExportandWorkActivity.class)
                    .setFlags(STATUS_DEFAULT_UNNUM));
                break;
            case R.id.atv2:
                CommonUtils.toActivity(mContext, new Intent(mContext, NewMapActivity.class));
                break;
            case R.id.atv3:
                bundle.putString("maptype", "bg");
                intent.putExtras(bundle);
                CommonUtils.toActivity(mContext, intent);
                break;
            case R.id.atv4:
                bundle.putString("maptype", "gl");
                intent.putExtras(bundle);
                CommonUtils.toActivity(mContext, intent);
                break;
            case R.id.atv5:
                bundle.putString("maptype", "online");
                intent.putExtras(bundle);
                CommonUtils.toActivity(mContext, intent);
                break;
            case R.id.act_export:
                CommonUtils.toActivity(mContext, new Intent(mContext, ExportandWorkActivity.class)
                    .setFlags(STATUS_DEFAULT_ORNUM));
                break;
        }
    }
}
