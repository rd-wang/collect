package com.master.app.tools;

import android.support.annotation.Nullable;

import com.master.app.Constants;
import com.orhanobut.logger.Logger;

/**
 * Created by Litao-pc on 2016/9/6.
 */
public class LoggerUtils {
    public static void d(@Nullable String clsStr,String args) {
        if (Constants.S_IS_DEBUG) {
            Logger.d(clsStr,  args);
        }
    }



}
