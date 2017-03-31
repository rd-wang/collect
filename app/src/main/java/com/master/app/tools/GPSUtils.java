package com.master.app.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by Litao-pc on 2016/9/14.
 */
public class GPSUtils {
    //是否打开GPS设置
    public static boolean isOpenGPSsetting(Context context) {
        try {
            LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
            boolean gps = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
            boolean network = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gps || network) {
                return true;
            }
            return false;
        } catch (Exception e) {

        }
        return false;
    }

    //打开系统gps设置
    public static void openGPSSettings(final Context context) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("提示")
                .setContentText("GPS未开启，不能初始化位置服务，是否去设置开启GPS?")
                .setConfirmText("Yes")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    CommonUtils.toActivity((Activity) context, myIntent);
                    sweetAlertDialog.dismiss();
                })
                .show();

    }


    public static void showPromptDialog(final Activity context) {

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setCancelText("No,cancel plx!")
                .setConfirmText("Yes,delete it!")
                .showCancelButton(true)
                .setCancelClickListener(sDialog -> sDialog.cancel())
                .show();
    }

    //检测是否有GPS模块
    public static boolean hasGPSDevice(Context context)
    {
        final LocationManager mgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if ( mgr == null )
            return false;
        final List<String> providers = mgr.getAllProviders();
        if ( providers == null )
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }
}
