package com.master.bean;

import java.io.Serializable;

/**
 * @param
 * @author Litao-pc on 2016/11/29.
 *         ~
 */

public class Table implements Serializable{
    private String tName;

    private String tNameCHS;//中文名

    private String layerType;

    public void setTName(String tName) {
        this.tName = tName;
    }

    public String getTName() {
        return this.tName;
    }

    public void setTNameCHS(String tNameCHS) {
        this.tNameCHS = tNameCHS;
    }

    public String getTNameCHS() {
        return this.tNameCHS;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public String getLayerType() {
        return this.layerType;
    }

    public Table copy(){
        Table table = new Table();
        table.tName = this.tName;
        table.tNameCHS = this.tNameCHS;
        table.layerType = this.layerType;
        return table;
    }
}
