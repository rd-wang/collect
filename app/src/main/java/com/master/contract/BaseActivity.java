package com.master.contract;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.master.R;
import com.master.app.inter.IBase;
import com.master.app.tools.AppManager;
import com.master.app.tools.KeyboardUtils;
import com.master.app.tools.SystemBarTintManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Litao-pc on 2016/9/7.
 */
public abstract class BaseActivity extends AppCompatActivity implements MvpView, IBase {
    protected View mRootView;
    protected final String sClassName = getClass().getSimpleName();
    private Unbinder unbinder;
    protected Context mContext;

    protected int enterAnim = R.anim.left_push_out;

    protected int exitAnim = R.anim.slide_in_bottom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        createView(null, null, savedInstanceState);
        mContext = this;
        hintStatusBar();
        bindView(savedInstanceState);

    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutRes = getLayoutResId();
        if (layoutRes == 0) {
            throw new IllegalArgumentException("implements getLayoutRes not 0");
        } else {
            mRootView = View.inflate(this, layoutRes, null);
        }
        super.setContentView(mRootView);
        unbinder = ButterKnife.bind(this);
        return mRootView;
    }


    //是否开启称沉浸状态栏
    public void hintStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#303F9F"));//通知栏所需颜
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    protected void onDestroy() {
        unbinder.unbind();
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(true);
        }
        return super.onOptionsItemSelected(item);
    }


    protected View toGetWindowTokenView = null;


    public void finish(boolean b) {
        if (toGetWindowTokenView != null) {
            KeyboardUtils.hideKeyboard(BaseActivity.this, toGetWindowTokenView);
        }
        finish();
        if (b) {
            overridePendingTransition(R.anim.left_push_in, R.anim.right_push_out);
        }

    }

    @Override
    public void finish() {
        if (toGetWindowTokenView != null) {
            KeyboardUtils.hideKeyboard(BaseActivity.this, toGetWindowTokenView);
        }
        super.finish();
    }

    public View getRootView() {
        return mRootView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish(true);
        }
        return false;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    public void hideSoftKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(
                Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
