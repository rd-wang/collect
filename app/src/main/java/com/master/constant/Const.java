package com.master.constant;

/**
 * Created by rd on 2017/3/2.
 */
public class Const {

    public static final String SHOW_ATTR_TYPE = "type";
    public static final String SHOW_ATTR_DATA_POINT = "Location";
    public static final String SHOW_ATTR_DATA_POINT_LIST = "LocationList";
    /**
     * 地图上划线的类型。
     */
    public interface DrawType {
        String POINT = "POINT";
        String POLYGON = "POLYGON";
        String POLYLINE = "POLYLINE";
    }

    public static final String COLLECT_LINE_TYPE = "COLLECT_LINE_TYPE";

    /**
     * 采集时采集线类型
     * 新路线 ，续采集。
     */
    public interface LineType {
        int OLDLINE = 0;
        int NEWLINE = 1;
    }
}
