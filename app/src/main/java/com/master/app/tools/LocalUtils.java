package com.master.app.tools;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.master.app.inter.LocationHelper;
import com.master.ui.activity.MainActivity;

import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/19
 */
public class LocalUtils {

    private LocationHelper mLocationHelper;

    private MyLocationListener myLocationListener;

    private LocationManager locationManager;

    private MyGpsStatusListener myGpsStatusListener;

    private String mBestProvider;

    public LocationHelper getmLocationHelper() {
        return mLocationHelper;
    }

    private LocalUtils() {
    }

    public static LocalUtils newCreate() {
        return new LocalUtils();
    }

    public LocalUtils configuration(LocationHelper helper) {
        if (helper == null) {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }
        mLocationHelper = helper;
        return this;
    }

    //初始化监听
    public Boolean initLocationListener(Context pContext, int time) {

        if (mLocationHelper == null) {
            return null;
        }

        myLocationListener = new MyLocationListener();
        myGpsStatusListener = new MyGpsStatusListener();
        locationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            throw new UnsupportedOperationException("Cannot initialize the locationmanager...");
        }
        Criteria criteria = getCriteria();
        mBestProvider = locationManager.getBestProvider(criteria, true);

        LoggerUtils.d("位置供应模式", "provider=" + mBestProvider);
        if (GPSUtils.hasGPSDevice(pContext)) {
            locationManager
                    .requestLocationUpdates(mBestProvider, time * 1000, 10f,
                            myLocationListener);
            LoggerUtils.d("LocalUtils：", "采集间隔：" + (time * 1000));
            locationManager.addGpsStatusListener(myGpsStatusListener);
            return true;
        }
        return false;
    }

    //移除监听
    public void removeLocationListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(myLocationListener);
            locationManager.removeGpsStatusListener(myGpsStatusListener);
            myLocationListener = null;
            myGpsStatusListener = null;
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mLocationHelper.updateLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            mLocationHelper.updateStatus(provider, status, extras);
            switch (status) {
                case LocationProvider.AVAILABLE:
                    LoggerUtils.d("onStatusChanged", "当前GPS状态为可见状态" + provider);
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    LoggerUtils.d("onStatusChanged", "当前GPS状态为服务区外状态" + provider);
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    LoggerUtils.d("onStatusChanged", "当前GPS状态为暂停服务状态" + provider);
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            LoggerUtils.d("onProviderEnabled", "当前provider状态激活" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            LoggerUtils.d("onProviderEnabled", "当前provider状态失效" + provider);
        }

    }

    private class MyGpsStatusListener implements GpsStatus.Listener {

        @Override
        public void onGpsStatusChanged(int event) {
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            mLocationHelper.updateGPSStatus(event, gpsStatus);
        }
    }

    private Criteria getCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);//高精度
        c.setAltitudeRequired(false);//包含高度信息
        c.setBearingRequired(false);//包含方位信息
        c.setSpeedRequired(true);//包含速度信息
        c.setCostAllowed(true);//允许付费
        c.setPowerRequirement(Criteria.POWER_LOW);//耗电
        return c;
    }

    /**
     * 判断Gps是否可用
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //返回卫星个数
    public int getGpsSatellite(GpsStatus gpsStatus) {
        int count = 0;
        Iterable<GpsSatellite> iterable = gpsStatus.getSatellites();
        for (GpsSatellite satellite : iterable) {
            //只有信躁比不为0的时候才算搜到了星
            if (satellite.getSnr() != 0) {
                count++;
            }
        }
        return count;
    }

    public void close() {
        locationManager = null;
        mLocationHelper = null;
    }


    public static Location getLastLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location myLocation = lm
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null) {
            myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (myLocation != null) {
                double lat1 = myLocation.getLatitude();
                double lng1 = myLocation.getLongitude();
            }
        }
        return myLocation;
    }
}
