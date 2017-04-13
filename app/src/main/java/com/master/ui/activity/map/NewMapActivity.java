package com.master.ui.activity.map;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.master.R;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.DialogUtils;
import com.master.app.tools.StringUtils;
import com.master.contract.BaseActivity;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class NewMapActivity extends BaseActivity implements Animator.AnimatorListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.tv_name)
    EditText tv_name;

    private Animator mAnimator;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("新建地图");
        mAnimator = AnimatorInflater.loadAnimator(this, R.animator.button_anim);
        mAnimator.setTarget(btnSubmit);
        mAnimator.setDuration(200);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_new_map;
    }


    @OnClick(R.id.btn_submit)
    public void onClick() {
        super.hideSoftKeyboard();
        mAnimator.addListener(this);
        mAnimator.start();


    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        String mapName = tv_name.getText().toString();
        if (StringUtils.isNotEmpty(mapName)) {
            int ap = DbHelperDbHelper.open().addMAp(mapName, new Date(), "否");
            DbHelperDbHelper.open().newJWDTable(ap);
            tv_name.setText("");
            new DialogUtils.Builder(this)
                .addMsg("新建成功，请到管理地图查看。").addCancel("确定").build().showDialog();

        } else {
            new DialogUtils.Builder(this)
                .addMsg("新建失败，请重试。").addCancel("确定").build().showDialog();
        }
        btnSubmit.clearAnimation();
        animation.removeListener(this);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
