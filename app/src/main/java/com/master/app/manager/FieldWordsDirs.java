package com.master.app.manager;

import com.master.bean.Fields;
import com.master.bean.TableContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/5
 */
public class FieldWordsDirs {

    private static FieldWordsDirs f;

    private Map<String, Fields> map = new HashMap<>();

    private Map<String, TableContext> map1 = new HashMap<>();


    public synchronized static FieldWordsDirs get() {
        if (f == null) {
            f = new FieldWordsDirs();
            f.initFieldGenerateDict();
            f.initTableGenerateDict();
        }
        return f;
    }

    private FieldWordsDirs() {
    }

    //创建字段词典
    private Map<String, Fields> initFieldGenerateDict() {
        map.clear();
        List<Fields> list = ConfigManager.create().getFieldList();
        for (Fields f : list) {
            String fName = f.getFName();
            map.put(fName, f);
        }
        return map;
    }

    /**
     *
     * @param fname
     * @return  查询field对象词典
     */
    public Fields fieldsFormFName(String fname) {
        return map.get(fname);
    }


    //创建对象词典
    private Map<String, TableContext> initTableGenerateDict() {
        map1.clear();
        List<TableContext> list = ConfigManager.create().getTablecontextList();
        for (TableContext f : list) {
            String fName = f.getTName();
            map1.put(fName, f);
        }
        return map1;
    }

    //查询Table对象词典
    public TableContext tableFormFName(String fname) {
        return map1.get(fname);
    }



}
