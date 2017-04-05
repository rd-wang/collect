package com.master.presenter;

import android.database.Cursor;

import com.master.app.manager.RecordingMedium;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.FileManager;
import com.master.app.tools.JsonUtils;
import com.master.app.tools.LoggerUtils;
import com.master.bean.TableContext;
import com.master.contract.MvpPresenter;
import com.master.model.BaseModel;
import com.master.ui.activity.map.ExportandWorkActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Litao-pc on 2016/12/1.
 *         ~
 */
public class ExportandWorkPresenter extends MvpPresenter<ExportandWorkActivity, BaseModel> {

    private final String TAG = "ExportandWorkPresenter";

    public ExportandWorkPresenter(BaseModel mModel) {
        super(mModel);
    }

    public void getMapFormData() {

        Observable.create(new Observable.OnSubscribe<List>() {
            @Override
            public void call(Subscriber<? super List> subscriber) {
                List list = DbHelperDbHelper.open().getAllMap();
                if (list.size() > 0) {
                    subscriber.onNext(list);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (list != null && list.size() > 0) {
                        getView().initPortalItemView(list);
                    }
                }, throwable -> {
                });

    }

    /**
     * @param mid t-map主键
     */
    public void exportJson(int mid) {

        getView().getTableEntityValue().doOnNext(tableContexts -> {
            getView().showDialog("导出中...");
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .forEach(tableContexts -> {

                    //表记录
                    Map<String, List> resMap = new HashMap<>();
                    for (TableContext t : tableContexts) {
                        ArrayList<Map<String, String>> list1 = new ArrayList();
                        resMap.put(t.getTName(), list1);
                        Cursor cursor = DbHelperDbHelper.open().excuteExporSql(t.getTName(), mid + "");
                        String[] columnNames = cursor.getColumnNames();
                        Map m = null;
                        while (cursor.moveToNext()) {
                            m = new HashMap();
                            for (int i = 0; i < columnNames.length; i++) {
                                String columnstring = cursor
                                        .getString(cursor.getColumnIndex(columnNames[i]));
                                LoggerUtils.d(TAG, "列名:" + columnNames[1] + "值:" + columnstring);
                                //根据fid查询经纬度表
                                m.put(columnNames[i], columnstring);
                            }
                            list1.add(m);
                        }
                    }

                    //获取工作地图绑定的经纬度表，并查
                    String jwbTableByMapId = RecordingMedium.getJWBTableByMapId(mid);
                    Cursor cursor1 = DbHelperDbHelper.open().excuteExporSql(jwbTableByMapId);
                    String[] columnNames = cursor1.getColumnNames();
                    ArrayList<Map<String, String>> temp = new ArrayList();
                    resMap.put(jwbTableByMapId, temp);
                    Map m = null;
                    while (cursor1.moveToNext()) {
                        m = new HashMap();
                        for (int i = 0; i < columnNames.length; i++) {
                            String columnstring = cursor1
                                    .getString(cursor1.getColumnIndex(columnNames[i]));
                            LoggerUtils.d(TAG, "列名:" + columnNames[1] + "值:" + columnstring);
                            //根据fid查询经纬度表
                            m.put(columnNames[i], columnstring);
                        }
                        temp.add(m);
                    }

                    String toJson = JsonUtils.toJson(resMap);
                    LoggerUtils.d(TAG, toJson);
                    boolean b = FileManager.saveIntoDirs(toJson);
                    if (b) {
                        getView().hintDialog("导出成功");
                    } else {
                        getView().hintDialog("导出失败");
                    }
                }, throwable -> System.out.println("Throwable"));

    }


}
