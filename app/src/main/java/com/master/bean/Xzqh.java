package com.master.bean;

import java.io.Serializable;

/**
 * @param
 * @author ma on 2016/11/29.
 *         ~
 */

public class Xzqh implements Serializable{
    private String code;

    private String name;//中文名

    private String pcode;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPcode() {
        return this.pcode;
    }
}
