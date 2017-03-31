package com.master.bean;

import java.util.List;

/**
 * @param
 * @author Litao-pc on 2016/11/29.
 *         ~
 */
public class CodesInfo {
    private String cName ;

    private List<Codes> fields ;

    public void setCName (String cName ){
        this. cName = cName ;
    }
    public String getCName (){
        return this.cName ;
    }
    public void setFields (List<Codes> fields ){
        this. fields = fields ;
    }
    public List<Codes> getFields (){
        return this. fields ;
    }
}
