package com.master.presenter;

import android.util.Log;

import com.master.app.orm.DbHelperDbHelper;
import com.master.contract.MvpPresenter;
import com.master.model.BaseModel;
import com.master.ui.activity.map.OptionsWorkActivity;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @param
 * @author Litao-pc on 2016/12/1.
 *         ~
 */
public class OptionsWorkPresenter extends MvpPresenter<OptionsWorkActivity, BaseModel> {

    public OptionsWorkPresenter(BaseModel mModel) {
        super(mModel);
    }

    public void getMapFormData() {

        Observable.create(new Observable.OnSubscribe<List>() {
            @Override
            public void call(Subscriber<? super List> subscriber) {
                List list = DbHelperDbHelper.open().getAllMap();
                if (list.size() > 0)
                    subscriber.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (list != null && list.size() > 0) {
                        getView().initPortalItemView(list);

                    }
                }, throwable -> {
                    Log.d(mTag, "xxxxxxxxxxxxxxxxxxxxxxxxxxxThrowablexxxxxxxxxxxxx");
                });


    }

}
