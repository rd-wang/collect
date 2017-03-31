package com.master.app.inter;

import com.master.bean.LocaDate;

import java.util.List;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2017/1/18
 */
public interface CallBackLocation {
    void call(List<LocaDate> locaDates, LocaDate locaDte);
}
