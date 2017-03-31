package com.master.app.manager;

import com.master.app.tools.ContextUtils;
import com.master.app.tools.JsonUtils;
import com.master.bean.CodesInfo;
import com.master.bean.CodesList;
import com.master.bean.Fields;
import com.master.bean.FieldsList;
import com.master.bean.Table;
import com.master.bean.TableContext;
import com.master.bean.TableContextList;
import com.master.bean.TableList;
import com.master.bean.Xzqh;
import com.master.bean.XzqhList;

import android.content.Context;

import java.util.List;


public class ConfigManager {

    private static ConfigManager cfg;

    String json_Catsic_Codes = "Catsic_Codes.json";
    String json_Catsic_Fields = "Catsic_Fields.json";
    String json_Catsic_Tables = "Catsic_Tables.json";
    String json_Catsic_TablesContext = "Catsic_TablesContext.json";
    String json_Catsic_xzqh = "Catsic_xzqh.json";
    private List<CodesInfo> codeList;
    private List<Fields> fieldList;
    private List<Table> tableList;
    private List<TableContext> tablecontextList;
    private List<Xzqh> xzqhList;


    private ConfigManager() {
    }


    public static synchronized ConfigManager create() {
        if (cfg == null) {
            cfg = new ConfigManager();
        }
        return cfg;
    }

    //初始化配置,只能执行一次
    public void initVariablesCfg(Context context) {
        codeList = JsonUtils.toObject(ContextUtils.loadJSONFromAsset(context, json_Catsic_Codes), CodesList.class).getContent();
        fieldList = JsonUtils.toObject(ContextUtils.loadJSONFromAsset(context, json_Catsic_Fields), FieldsList.class).getContent();
        tableList = JsonUtils.toObject(ContextUtils.loadJSONFromAsset(context, json_Catsic_Tables), TableList.class).getContent();
        tablecontextList = JsonUtils.toObject(ContextUtils.loadJSONFromAsset(context, json_Catsic_TablesContext), TableContextList.class).getContent();
        xzqhList = JsonUtils.toObject(ContextUtils.loadJSONFromAsset(context, json_Catsic_xzqh), XzqhList.class).getContent();
    }

    public List<CodesInfo> getCodeList() {
        return codeList;
    }


    public List<Fields> getFieldList() {
        return fieldList;
    }


    public List<Table> getTableList() {
        return tableList;
    }


    public List<TableContext> getTablecontextList() {
        return tablecontextList;
    }

    public List<Xzqh> getXzqhList() {
        return xzqhList;
    }

    //返回Table
    public Table fetchTable(String tname) {
        List<Table> tableList = ConfigManager.create().getTableList();
        for (Table t : tableList) {
            if (t.getTName().equals(tname)) {
                return t;
            }
        }
        return null;
    }


}


