package com.master.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.inter.Type;
import com.master.app.manager.AcquisitionPara;
import com.master.app.manager.RecordingMedium;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.LocalUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.ObjectUtils;
import com.master.app.tools.StringUtils;
import com.master.bean.Fields;
import com.master.bean.LocaDate;
import com.master.contract.MvpPresenter;
import com.master.interactors.JsonformInteractor;
import com.master.model.MainModel;
import com.master.model.MainModelImpl;
import com.master.ui.activity.MainActivity;
import com.master.ui.viewer.MainView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.master.app.orm.DbHelperDbHelper.LX_LXBM;
import static com.master.ui.activity.MainActivity.S_MainActivity;
import static com.master.ui.activity.MainActivity.mtable;

/**
 * @author Litao-pc on 2016/10/27.
 *         ~
 */

public class MainPresenter extends MvpPresenter<MainView, MainModel> {


    public MainPresenter(MainModelImpl mModel) {
        super(mModel);
    }

    public List<Fields> getFieldsParam(String tname, @NonNull List<Fields> list) {
        return mModel.getArgsParms(tname, list);
    }

    public List<View> builderSheetElement(Context c, List<Fields> fieldsList) {
        List<View> views = JsonformInteractor.getInstance().fetchFormElements(c, fieldsList, null);
        getView().addFormElements(views);
        return views;
    }

    public boolean writeValue(List<View> views, Type type) {
        boolean isSucceed = false;
        Map<String, String> map = new HashMap<>();
        String mSbmc = null;
        String mSbm = null;
        int length = views.size();
        for (int i = 0; i < length; i++) {
            String tag = (String) views.get(i).getTag(R.id.fname);

            if (ObjectUtils.isNullOrEmptyString(tag)) {
                continue;
            }
            if (views.get(i) instanceof EditText) {
                String s = ((EditText) views.get(i)).getText().toString();
                //获取具体编码，隧道编码，桥梁编码?
                if (StringUtils.endsWithIgnoreCase(tag, "BM") && i == 0) {
                    mSbmc = tag;
                    mSbm = s;
                }
                if (ObjectUtils.isNullOrEmptyString(s)) {
                    getView().showToast("录入信息不完整...");
                }
                map.put(tag, s);
            }
            if (views.get(i) instanceof Spinner) {
                TextView v = (TextView) ((Spinner) views.get(i)).getSelectedView();
                String s = v.getText().toString();
                map.put(tag, s);
            }
        }

        if (type == Type.SAVE) {
            //是否为点
            boolean contains = AcquisitionPara.getPointName().contains(mtable.getTName());
            //获取表名字
            String currentJwdName = RecordingMedium.getCurrentJwdName();
            //如果是点且经纬度无效，退出
            if (contains) {
                LocaDate locaDate = new LocaDate.Builder()
                        .initSite(LocalUtils.getLastLocation(SynopsisObj.getAppContext())).builder();
                if (locaDate.isworn
                        || DbHelperDbHelper.open().addTablEntry(mtable.getTName(), map, true) < 0) {
                    getView().showToast("采集点无效");
                }
                LoggerUtils.d("坐标/当前保存的经纬度表", locaDate.toString() + "/" + currentJwdName);

                Map<String, String> m1 = new HashMap<>();
                m1.put(DbHelperDbHelper.JWD_FID_COLUMN, mSbm);
                m1.put(DbHelperDbHelper.JWD_JD_COLUMN, locaDate.lng + "");
                m1.put(DbHelperDbHelper.JWD_WD_COLUMN, locaDate.lat + "");
                m1.put(DbHelperDbHelper.JWD_CJDX_COLUMN, MainActivity.mCJDX);
                m1.put(DbHelperDbHelper.JWD_CJSJ_COLUMN, new Date().getTime() + "");
                if (DbHelperDbHelper.open().addTablEntry(currentJwdName, m1, false) < 0) {
                    getView().showToast("保存失败");
                } else {
                    getView().showToast("采集成功");
                    isSucceed = true;
                }
            } else {
                List<LocaDate> dataList = S_MainActivity.lat_lng_list;
                S_MainActivity.lat_lng_list = new ArrayList<>();
                if (dataList != null) {
                    if (dataList.size() == 0) {
                        getView().showToast("未获取采集位置信息");
                        return isSucceed;
                    }
                    if (DbHelperDbHelper.open().addTablEntry(mtable.getTName(), map, true) < 0) {
                        getView().showToast("采集无效");
                        return isSucceed;
                    }
                    //保存线  线保存时不知道是哪个路段 所以经纬度表中 外键为路线
                    if (DbHelperDbHelper.open().addPointList(dataList, currentJwdName, map.get(LX_LXBM))) {
                        getView().showToast("采集成功");
                        isSucceed = true;
                    }
                }
            }
        } else if (type == Type.UPDATE) {
            long updateTable = DbHelperDbHelper.open()
                    .updateTable(mtable.getTName(), mSbmc, mSbm, map);
            if (0 < updateTable) {
                getView().showToast("更新成功");
                isSucceed = true;
            } else {
                getView().showToast("更新失败");
            }
        }

        return isSucceed;
    }
}
