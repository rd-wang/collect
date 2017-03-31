package com.master.bean;

import java.util.List;

/**
 * @param
 * @author Litao-pc on 2016/11/29.
 *         ~
 */
public class TableContext {
    private String tName;

    private List<Fields> fields;

    public void setTName(String tName) {
        this.tName = tName;
    }

    public String getTName() {
        return this.tName;
    }

    public void setFields(List<Fields> fields) {
        this.fields = fields;
    }

    public List<Fields> getFields() {
        return this.fields;
    }
}
