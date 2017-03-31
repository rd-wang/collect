package com.master.app.tools;

import java.util.List;

/**
 * Created by rd on 2017/3/3.
 */

public class DataUtils {
    public static boolean isNotEmpty(List list) {
        return !list.isEmpty() && list != null;
    }
}
