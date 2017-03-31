package com.master.app.manager;

import com.master.bean.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/5
 * 采集参数初始化配置
 */

public class AcquisitionPara {

    private static List<String> lineName = new ArrayList();

    //返回layerType=1点对象的集合
    public static List<Table> getPointList() {

        List<Table> list = new ArrayList<>();
        List<Table> tables = ConfigManager.create().getTableList();

        for (Table t : tables) {

            String type = t.getLayerType().charAt(0) + "";
            if (Integer.parseInt(type) == 1) {
                list.add(t);
                lineName.add(t.getTName());
            }
        }
        return list;
    }

    //返回layerType=2线对象的集合
    public static List<Table> getLineList() {
        List<Table> list = new ArrayList<>();
        List<Table> tables = ConfigManager.create().getTableList();
        for (Table t : tables) {
            String type = t.getLayerType().charAt(0) + "";
            if (Integer.parseInt(type) == 2) {
                list.add(t);
            }
        }
        return list;
    }

    public static List<String> getPointName() {
        return lineName;
    }

}
