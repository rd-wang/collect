package com.master.app.tools;

import android.content.Context;
import android.widget.Toast;

import com.master.app.SynopsisObj;

/**
 * Created by rd on 2017/2/28.
 */
public class ToastUtils {
    private static Context context = SynopsisObj.getAppContext();

    public static void showToast(CharSequence string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
