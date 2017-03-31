package com.master.app.manager;

import com.master.app.Constants;
import com.master.app.SynopsisObj;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.PreferencesUtils;


/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/21
 */
public class RecordingMedium {

    /**
     * @return 检查是否有工作地图
     */
    public static boolean checkWorkMap() {
        return PreferencesUtils.getInt(SynopsisObj.getAppContext(), Constants.CURRENT_USER_MAP,
            Constants.STATUS_DEFAULT_NUM) != -1;
    }

    /**
     * @return 返回工作地图id
     */
    public static int getWorkMapId() {
        if (checkWorkMap()) {
            return PreferencesUtils
                .getInt(SynopsisObj.getAppContext(), Constants.CURRENT_USER_MAP, -1);
        }
        return -1;
    }

    /**
     * @return 返回当前jwd表数
     */
    public static int getJWDMaxId() {
        return PreferencesUtils.getInt(SynopsisObj.getAppContext(), Constants.TABLE_JWD_MAX_, 0);
    }

    /**
     * @return 返回当前使用的jwd表
     */
    public static String getCurrentJwdName() {
        return PreferencesUtils.getString(SynopsisObj.getAppContext(), Constants.CURRENT_JWD_MAP, null);
    }

    /**
     * @return 返回当前使用的jwd表
     */
    public static String getJWBTableByMapId(int mid) {
        return DbHelperDbHelper.JwdPrefix+mid;
    }

}
