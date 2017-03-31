package com.master.model;

import com.master.bean.Fields;
import com.master.contract.MvpModel;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * @param
 * @author Litao-pc on 2016/10/27.
 *         ~
 */

public interface MainModel extends MvpModel {

    List<Fields> getArgsParms(String tname, @NonNull List<Fields> list);


}
