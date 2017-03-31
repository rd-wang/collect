package com.master.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.lody.turbodex.TurboDex;
import com.master.app.manager.ConfigManager;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class SynopsisObj extends Application {

    private static SynopsisObj sContext;

    public static String chrootDir = Environment.getExternalStorageDirectory().toString();

    private boolean isOpenLocalServer;

    private static Handler mHandler = new Handler();

    private int mGpsSatellite;//卫星数


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println(chrootDir);
        sContext = this;
        Logger.init()
            .setMethodCount(3)
            .hideThreadInfo()
            .setLogLevel(LogLevel.FULL);
//        /**
//         * 每次登陆检查sd卡的文件目录，如不存在，重新创建
//         */
//        FileManager.init(this);

        ConfigManager.create().initVariablesCfg(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        TurboDex.enableTurboDex();
        super.attachBaseContext(base);
    }

    public static SynopsisObj getAppContext() {
        if (sContext == null) {
            Log.e(TAG, "Global context not set");
        }
        return sContext;
    }

    public static String getVersion() {
        Context context = getAppContext();
        String packageName = context.getPackageName();
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            AirbrakeNotifier.notify(e);
            Log.e(TAG, "Unable to find the name " + packageName + " in the package");
            return null;
        }
    }


    public static void post(Runnable r) {
        mHandler.post(r);
    }

    public boolean isOpenLocalServer() {
        return isOpenLocalServer;
    }


    public void setOpenLocalServer(boolean openLocalServer) {
        isOpenLocalServer = openLocalServer;
    }

    public int getGpsSatellite() {
        return mGpsSatellite;
    }

    public void setGpsSatellite(int gpsSatellite) {
        mGpsSatellite = gpsSatellite;
    }

}
