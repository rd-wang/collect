package com.master.server;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.master.app.Constants;
import com.master.app.RestricMsg;
import com.master.app.SynopsisObj;
import com.master.app.inter.CallBackLocation;
import com.master.app.inter.LocationHelper;
import com.master.app.tools.GPSUtils;
import com.master.app.tools.LocalUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.PreferencesUtils;
import com.master.bean.LocaDate;

import java.util.ArrayList;
import java.util.List;

import static com.master.app.Constants.OUT_COLLECTION_TIME;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2017/1/17
 */
public class LocalServer extends Service implements LocationHelper {

    private LocalUtils mLocalUtils;

    private List<LocaDate> mLocaDates;

    private static LocalServer mLocalServer;


    //开关，默认关闭
    private boolean on_off = false;

    //LocalUtils 初始化状态
    private static boolean initializationIsComplete = false;

    private static boolean isBind = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (GPSUtils.hasGPSDevice(getApplicationContext())) {
            mLocalUtils = LocalUtils.newCreate().configuration(this);
            mLocaDates = new ArrayList();
            SynopsisObj.getAppContext().setOpenLocalServer(true);
            mLocalServer = this;
        } else {
            SynopsisObj.getAppContext().setOpenLocalServer(false);
            this.onDestroy();
        }

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    //开始采集
    public boolean start(CallBackLocation callBack) {

        if (initializationIsComplete) {
            on_off = true;
            this.mCallBack = callBack;
            return true;
        } else {
            // TODO: 2017/2/28   第二个参数应该是读取设置得到的.

            Boolean aBoolean = mLocalUtils.initLocationListener(getApplication(), PreferencesUtils.getInt(this, OUT_COLLECTION_TIME, 5));
            if (aBoolean) {
                this.mCallBack = callBack;
                on_off = true;
                initializationIsComplete = true;
                return true;
            } else {
                on_off = false;
            }
        }

        return false;
    }

    //停止采集
    public void stop() {
        SynopsisObj.getAppContext().setOpenLocalServer(false);
        mLocalUtils.removeLocationListener();
        initializationIsComplete = false;
        on_off = false;
        this.mCallBack = null;
    }

    //暂停采集
    public void pause() {
        on_off = false;
    }

    public void resume(){
        on_off = true;
    }


    public class LocationBinder extends Binder {

        public LocalServer getService() {
            return LocalServer.this;
        }
    }


    @Override
    public void updateLocation(Location location) {
        //允许为空，为空返回一个损坏的locadate对象
        if (location == null) {
            // TODO: 2017/2/21 采集点无效 ，发出通知 
            RestricMsg.get().post(Constants.MSG_DEFUALT_ERRER, "errer");
        } else {
            LocaDate locaDte = new LocaDate.Builder().initSite(location).builder();
            if (on_off) {
                LoggerUtils.d("LocaDate", locaDte.toString());
                mLocaDates.add(locaDte);
                mCallBack.call(mLocaDates, locaDte);
            }
        }
    }

    @Override
    public void updateStatus(String provider, int status, Bundle extras) {
        LoggerUtils.d("mainactivity", "GPS updateStatus  status: " + provider + status);
    }

    @Override
    public void updateGPSStatus(int event, GpsStatus pGpsStatus) {
//        int mGpsSatellite = mLocalUtils.getGpsSatellite(pGpsStatus);
//        SynopsisObj.getAppContext().setGpsSatellite(mGpsSatellite);
//        switch (event) {
//            case GpsStatus.GPS_EVENT_FIRST_FIX:
//                //第一次定位时间UTC gps可用
//                int i = pGpsStatus.getTimeToFirstFix();
//                LoggerUtils.d("Localutils", "GPS 第一次可用  " + i);
//                break;
//            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                //周期的报告卫星状态
//                //得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
//                if (mGpsSatellite < 3) {
//                    LoggerUtils.d("Localutils", "***卫星少于3颗***");
//                } else if (mGpsSatellite > 7) {
//                    LoggerUtils.d("Localutils", "***卫星大于7颗***");
//                }
//                break;
//            case GpsStatus.GPS_EVENT_STARTED: {
//                LoggerUtils.d("Localutils", "GPS start Event");
//                break;
//            }
//            case GpsStatus.GPS_EVENT_STOPPED: {
//                LoggerUtils.d("Localutils", "GPS **stop*** Event");
//                break;
//            }
//            default:
//                break;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocalUtils != null) {
            mLocalUtils.removeLocationListener();
            mLocalUtils = null;
        }
        if (mLocaDates != null) {
            mLocaDates.clear();
            mLocaDates = null;
        }
        mMLocationService = null;
        initializationIsComplete = false;
        isBind = false;
        this.mCallBack = null;
        mLocalServer=null;
        LoggerUtils.d("LocalServer", "LocalServer drop");
    }

    private static LocalServer mMLocationService;

    public static boolean bindServer(Context context
    ) {

        return context
            .bindService(new Intent(context, LocalServer.class),
                new LocalServiceConnection(),
                Context.BIND_AUTO_CREATE);
    }

    private CallBackLocation mCallBack;


    public static LocalServer getServer() {
        if (LocalServer.isBind) {
            return mMLocationService;
        } else {
            return mMLocationService;
        }
    }

    static class LocalServiceConnection implements ServiceConnection {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMLocationService = ((LocalServer.LocationBinder) service).getService();
            LocalServer.isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LocalServer.isBind = false;
            LoggerUtils.d("isBind", "onServiceDisconnected");
        }
    }

    public List<LocaDate> fetchLocaDatas() {
        return mLocaDates;
    }

    public void clearLocaDates() {
        mLocaDates.clear();
    }


    public static void closeLocalServer() {
        if (mLocalServer != null) {
            mLocalServer.onDestroy();
            LoggerUtils.d("Localserver","关闭定位服务");
        }
    }
}



