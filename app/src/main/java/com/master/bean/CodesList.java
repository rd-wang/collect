package com.master.bean;

import java.util.List;

/**
 * @param
 * @author Litao-pc on 2016/11/28.
 *         ~
 */

public class CodesList {
    private List<CodesInfo> content ;
    
    public void setContent(List<CodesInfo> content){
        this.content = content;
    }
    public List<CodesInfo> getContent(){
        return this.content;
    }
}
