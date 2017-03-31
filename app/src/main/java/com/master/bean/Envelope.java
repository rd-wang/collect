package com.master.bean;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @param
 * @author litao
 * @mail llsmpsv@gmail.com
 * @date on 2016/12/22
 */
public class Envelope<T> implements Serializable {
    private T t;
    private Object obj;
    private List param;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public List getParam() {
        return param;
    }

    public void setParam(List param) {
        this.param = param;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
