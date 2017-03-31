package com.master.presenter;

import com.master.contract.MvpPresenter;
import com.master.model.LayerModel;
import com.master.ui.viewer.LayerView;

/**
 * @param
 * @author Litao-pc on 2016/11/1.
 *         ~
 */
public class LayerPresenter extends MvpPresenter<LayerView, LayerModel> {
    public LayerPresenter(LayerModel mModel) {
        super(mModel);
    }
}
