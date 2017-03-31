package com.master.ui.viewer;

import android.view.View;

import com.master.bean.Fields;
import com.master.contract.MvpView;

import java.util.List;

/**
 * @param
 * @author Litao-pc on 2016/10/27.
 *         ~
 */

public interface MainView extends MvpView {

    void showExtra();

    void hideExtra();

    void addFormElements(List<View> views);

    void showToast(String message);

    List<Fields> getArguments(String fname, List<Fields> list);

    void show(View v);

    void hideKeyBoard();

    void showAirPanel();

}
