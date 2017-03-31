package com.master.contract;

/**
 * Created by Litao-pc on 2016/9/7.
 */
public interface BasePresenter<V extends MvpView> {
    void attachView(V view);

    void detachView(boolean retainInstance);
}
