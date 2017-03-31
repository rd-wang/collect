package com.master.ui.viewer;

import com.master.bean.TableContext;
import com.master.contract.MvpView;

import java.util.List;

import rx.Observable;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on  2016/12/15
 */
public interface ExportandWorkView extends MvpView {
    Observable<List<TableContext>> getTableEntityValue();

    void showDialog(String msg);
    void hintDialog(String msg);
}
