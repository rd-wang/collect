package com.master.app;

/**
 * Created by Litao-pc on 2016/9/12.
 */

public class Constants {

    public static final boolean S_IS_DEBUG = true;

    /**
     * @param :COLLECT_METHED 采集方式
     * 0 按时间采集
     * 1 按距离采集   default
     */
    public static final int COLLECT_METHED = 0;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    /**
     * shareprefrence 持久化配置
     *
     * @param :持久化文件名
     */
    public static final String CONFIG = "config";


    public static final String SELECT_COLLECT_TAG = "cellect";


    public static final String SELECT_COLLECT_LABE = "select_tab";

    //屏幕常亮
    public static final String SC_WAKE_LOCK = "SC_WAKE_LOCK";

    public static String ConfigSqliteDB = "config.sqlite";


    public static final String FIRST_STEP_NAME = "step1";

    public static final String EDIT_TEXT = "edit_text";

    public static final String CHECK_BOX = "check_box";

    public static final String RADIO_BUTTON = "radio";

    public static final String LABEL = "label";

    public static final String SPINNER = "spinner";

    public static final String CHOOSE_IMAGE = "choose_image";

    public static final String OPTIONS_FIELD_NAME = "options";


    public static final String CURRENT_USER_MAP = "CURRENT_USER_MAP";

    public static final int STATUS_DEFAULT_NUM = -1;

    public static final int STATUS_DEFAULT_UNNUM = 0;

    public static final int STATUS_DEFAULT_ORNUM = 1;

    public static final String MSG_DEFUALT_ERRER = "msg-errer";
    //经纬度表最大的编号，02，代表有两张经纬度表
    public static final String TABLE_JWD_MAX_ = "TABLE_JWD_MAX_";

    public static final String CURRENT_JWD_MAP = "TABLE_CURRENT_JWD";

    public static final String default_null_tag = "";


    //路线分段
    public static final String DEFAULT_FD_LD = "T_ld";

    //采集模式
    public static String OUT_COLLECTION_KIND = default_null_tag;

    //时间间隔
    public static final String OUT_COLLECTION_TIME = "colloction_time";

    //距离间隔
    public static final String OUT_COLLECTION_SPACE = "colloction_space";

    //车速
    public static final String OUT_COLLECTION_CAR = "colloction_CAR";

    //车速开关
    public static final String OUT_COLLECTION_SWICTH = "colloction_SWITCH";

    //自动保存
    public static final String OUT_COLLECTION_AUTO_SAVE = "colloction_AUTOSAVE";
    //自动保存数量
    public static final String OUT_COLLECTION_AUTO_SAVE_POINT_NUMBER = "colloction_AUTOPOINT";
}
