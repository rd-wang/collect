package com.master.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.inter.LocationHelper;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.LocalUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.weight.RadarAnimationView;
import com.master.contract.BaseActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;

public class SlateActivity extends BaseActivity implements LocationHelper {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.tv_status)
    TextView tv_status;

    @BindView(R.id.tv_count)
    TextView tv_count;

    @BindView(R.id.radar_animation_view)
    RadarAnimationView radarAnimationView;

    private LocalUtils mLocal;
    private long oldTime = -1;


    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("GPS状态");
        oldTime = System.currentTimeMillis();
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        mLocal = LocalUtils.newCreate();
                        mLocal.configuration(this);
                        mLocal.initLocationListener(this, 5);
                    } else {
                        Toast.makeText(mContext, "权限不允许获取GPS信息", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_slate;
    }

    @Override
    public void updateLocation(Location location) {

    }

    @Override
    public void updateStatus(String provider, int status, Bundle extras) {

    }

    @Override
    public void updateGPSStatus(int event, GpsStatus pGpsStatus) {
        long newTime = System.currentTimeMillis();
        if (newTime - oldTime <= 5000) {
            return;
        }
        oldTime = newTime;
        int mGpsSatellite = mLocal.getGpsSatellite(pGpsStatus);
        LoggerUtils.d("Localutils", mGpsSatellite + "卫星数量");
        SynopsisObj.getAppContext().setGpsSatellite(mGpsSatellite);
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                //第一次定位时间UTC gps可用
                int i = pGpsStatus.getTimeToFirstFix();
                LoggerUtils.d("Localutils", "GPS 第一次可用  " + i);
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //周期的报告卫星状态
                //得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
                if (mGpsSatellite < 3) {
                    tv_status.setText("GPS信号强度:差");
                    tv_count.setText("可用卫星:" + mGpsSatellite);
                } else if (mGpsSatellite > 7) {
                    tv_status.setText("GPS信号强度:良好");
                    tv_count.setText("可用卫星:" + mGpsSatellite);
                }

                radarAnimationView.setCounterTextSizeDp(40);
                radarAnimationView.setNumberOfItemsToDiscover(mGpsSatellite);
                radarAnimationView.beginAnimation(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                break;
            case GpsStatus.GPS_EVENT_STARTED: {
                LoggerUtils.d("Localutils", "GPS start Event");
                break;
            }
            case GpsStatus.GPS_EVENT_STOPPED: {
                LoggerUtils.d("Localutils", "GPS **stop*** Event");
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mLocal.removeLocationListener();
        super.onDestroy();
    }
}
