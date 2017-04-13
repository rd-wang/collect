package com.master.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.tools.CheckSeriesNoUtils;
import com.master.app.tools.ClipboardUtils;
import com.master.app.tools.CommonUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.ObjectUtils;
import com.master.app.tools.StringUtils;
import com.master.app.tools.ToastUtils;
import com.master.contract.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A login screen that offers login organiz /password.
 */
public class LoginActivity extends BaseActivity {

    private String mOrignaz = "";

    private String mCaptcha = "";

    @BindView(R.id.btn_submit)
    AppCompatButton btnSubmit;

    @BindView(R.id.ev_imei)
    TextView evImei;

    private String deviceIMEI;

    @BindView(R.id.dw_name)
    EditText dwName;

    @BindView(R.id.seriesNo)
    EditText seriesNo;

    private String dwmc;
    //    @BindView(R.id.iv_copy)
//    ImageView ivCopy;


    @Override
    public void bindView(Bundle savedInstanceState) {

//        ivCopy.setOnClickListener(v -> {
//            copyMsgStr(this, tvCaptcha.getText().toString());
//        });
        deviceIMEI = getDeviceIMEI(this);
        evImei.setText(deviceIMEI);


    }

    @OnClick({R.id.send_email, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_email:
                dwmc = dwName.getText().toString().trim();
                if (StringUtils.isEmpty(dwmc)) {
                    ToastUtils.showToast("请输入单位名称");
                    return;
                }
                String msg = "标识码:" + deviceIMEI + "\n" + "单位名称:" + dwmc + "\n" + "用于APP注册";
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", msg);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast("复制成功");
                break;
            case R.id.btn_submit:
                String trim = seriesNo.getText().toString().trim();
                dwmc = dwName.getText().toString().trim();
                if (TextUtils.isEmpty(dwmc) || CheckSeriesNoUtils.checkSeriesNo(dwmc, deviceIMEI, trim)) {
                    startMainView();
                } else {
                    Toast.makeText(SynopsisObj.getAppContext(), "请输入合法验证码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }


    public void startMainView() {
        CommonUtils.toActivity(this, new Intent(this, MainActivity.class));
//        finish();
    }

    public void showMsgTitle(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void copyMsgStr(Context c, CharSequence text) {
        if (ObjectUtils.isNullOrEmptyString(text) || c == null) {
            return;
        }
        ClipboardUtils.copyText(c, text);
        showMsgTitle(c.getString(R.string.titlemsg));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LoggerUtils.d(sClassName, "onKeyDown");
            finish();
        }

        return false;

    }

    public static String getDeviceIMEI(Context context) {

        String SerialNumber = android.os.Build.SERIAL;

        return SerialNumber;
    }

}




