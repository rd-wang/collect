package com.master.ui.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.master.R;
import com.master.app.Constants;
import com.master.app.SynopsisObj;
import com.master.app.manager.ConfigManager;
import com.master.app.manager.RecordingMedium;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.CommonDateParseUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.PreferencesUtils;
import com.master.bean.Maps;
import com.master.ui.activity.map.ExportandWorkActivity;

import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.master.app.orm.DbHelperDbHelper.JwdPrefix;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/13
 */
public class MapCussorAdapter extends BaseSwipeAdapter {

    private List<Maps> maps;


    private ExportandWorkActivity mContext;

    private boolean mIsSwipe = true;


    public MapCussorAdapter(Context mContext, int flag) {
        this.mContext = (ExportandWorkActivity) mContext;
        mIsSwipe = (flag == 0);
    }


    public void setData(List<Maps> list) {
        this.maps = list;
        notifyDataSetChanged();
    }

    public List<Maps> getData() {
        return maps;
    }


    @Override
    public int getCount() {
        return maps == null ? 0 : maps.size();
    }

    @Override
    public Object getItem(int position) {
        return maps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        View v = View.inflate(SynopsisObj.getAppContext(), R.layout.map_list_item2, null);
        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        if (mIsSwipe) {

            v.findViewById(R.id.delete).setOnClickListener(
                v12 -> {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
                    dlg.setTitle("提示");
                    dlg.setMessage("确定删除 \"" + maps.get(position).mName + "\"?");
                    dlg.setPositiveButton("确定", (dialog, which) -> {
                        mContext.showDialog("删除中...");

                        new Thread() {
                            DbHelperDbHelper dbHelperDbHelper = null;

                            @Override
                            public void run() {
                                try {
                                    dbHelperDbHelper = DbHelperDbHelper.open()
                                        .beginTransation();
                                    dbHelperDbHelper.deleteTableByTableName(
                                        DbHelperDbHelper.JwdPrefix + maps.get(position).mId);
                                    dbHelperDbHelper
                                        .deleteaRecordByTableName(maps.get(position).mId,
                                            ConfigManager.create().getTablecontextList());
                                    dbHelperDbHelper.removeMapByid(maps.get(position).mId);
                                    dbHelperDbHelper.setTransactionSuccessful();
                                    mContext.runOnUiThread(() -> {
                                        if (maps.get(position).mId == RecordingMedium
                                            .getWorkMapId()) {
                                            PreferencesUtils.putInt(SynopsisObj.getAppContext(),
                                                Constants.CURRENT_USER_MAP,
                                                Constants.STATUS_DEFAULT_NUM);
                                        }
                                        maps.remove(position);
                                        notifyDataSetChanged();
                                        mContext.hintDialog("删除成功.");
                                        closeAllItems();
                                    });

                                } catch (Exception e) {
                                    mContext.runOnUiThread(
                                        () -> mContext.hintDialog("删除失败."));
                                } finally {
                                    if (dbHelperDbHelper != null) {
                                        dbHelperDbHelper.endTransaction();
                                    }

                                }

                            }
                        }.start();
                    });
                    dlg.show();

                }
            );

            v.findViewById(R.id.select).setOnClickListener(view -> {
                new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("设置工作地图")
                    .setContentText("确定使用 \"" + maps.get(position).mName + "\" 此工作地图?")
                    .setConfirmText("确定")
                    .setCancelText("取消")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        PreferencesUtils
                            .putInt(mContext, Constants.CURRENT_USER_MAP,
                                maps.get(position).mId);
                        PreferencesUtils.putString(mContext, Constants.CURRENT_JWD_MAP,
                            JwdPrefix + maps.get(position).mId);
                        notifyDataSetChanged();
                        LoggerUtils.d(Constants.CURRENT_JWD_MAP,
                            "当前使用的map表：" + maps.get(position).mId + "当前使用的jwd表："
                                + JwdPrefix + maps.get(position).mId);
                        sweetAlertDialog.setContentText("设置完成").showCancelButton(false)
                            .setTitleText("")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        closeAllItems();
                    }).show();
            });
        } else {
            swipeLayout.setSwipeEnabled(false);
            v.setOnClickListener(v1 -> {
                long aLong = RecordingMedium.getWorkMapId();
                String title = "导出地图";
                new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText(title)
                    .setContentText("确定导出 \"" + maps.get(position).mName + "\" 此工作地图?")
                    .setConfirmText("确定")
                    .setCancelText("取消")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismiss();
                        mContext.export(maps.get(position).mId);
                    }).show();
            });
        }

        return v;
    }

    @Override
    public void fillValues(int position, View view) {

        int id = maps.get(position).mId;
        int workMapId = RecordingMedium.getWorkMapId();
        if (mIsSwipe){
            if (id == workMapId) {
                view.findViewById(R.id.lable).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.lable).setVisibility(View.GONE);
            }
        }

        TextView mid = (TextView) view.findViewById(R.id.tv_id);

        mid.setText(id + "");
        TextView mname = (TextView) view.findViewById(R.id.tv_name);
        mname.setText(maps.get(position).mName);

        TextView mdate = (TextView) view.findViewById(R.id.tv_date);
        mdate.setText(CommonDateParseUtils
            .date2string(new Date(Long.parseLong(maps.get(position).cjsj)),
                CommonDateParseUtils.YYYY_MM_DD_HH_MM));
    }


}
