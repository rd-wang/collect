package com.master.bean;

/**
 * @author Litao-pc on 2016/11/29.
 *         ~
 */
public class Fields {

    private String fName;

    private String fNameCHS;

    private String fType;

    private String fLen;

    private String fDec;

    private String fKey;

    private String frequired;

    private String fcode;

    private boolean fShow;

    private int fmodify;

    public int getFmodify() {
        return fmodify;
    }

    public void setFmodify(int fmodify) {
        this.fmodify = fmodify;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getFName() {
        return this.fName;
    }

    public void setFNameCHS(String fNameCHS) {
        this.fNameCHS = fNameCHS;
    }

    public String getFNameCHS() {
        return this.fNameCHS;
    }

    public void setFType(String fType) {
        this.fType = fType;
    }

    public String getFType() {
        return this.fType;
    }

    public void setFLen(String fLen) {
        this.fLen = fLen;
    }

    public String getFLen() {
        return this.fLen;
    }

    public void setFDec(String fDec) {
        this.fDec = fDec;
    }

    public String getFDec() {
        return this.fDec;
    }

    public void setFKey(String fKey) {
        this.fKey = fKey;
    }

    public String getFKey() {
        return this.fKey;
    }

    public void setFrequired(String frequired) {
        this.frequired = frequired;
    }

    public String getFrequired() {
        return this.frequired;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getFcode() {
        return this.fcode;
    }

    public boolean isfShow() {
        return fShow;
    }

    public void setfShow(boolean fShow) {
        this.fShow = fShow;
    }
}
