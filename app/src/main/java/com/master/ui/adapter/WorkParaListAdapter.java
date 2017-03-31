package com.master.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.orm.DbHelperDbHelper;
import com.master.app.tools.CommonUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.tools.ThreadPoolUtils;
import com.master.bean.Envelope;
import com.master.bean.LocaDate;
import com.master.constant.Const;
import com.master.ui.activity.GatherDetailActivity;
import com.master.ui.activity.collect.ModifActivity;
import com.master.ui.activity.map.LocationActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.master.app.orm.DbHelperDbHelper.MAP_NUMBER_COLUMN;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/21
 */
public class WorkParaListAdapter extends BaseAdapter {

    /**
     * xiangqing，需要查询到名称，编码。
     * <p>
     * tname 表名
     * tbm 编码值
     * TBMLM 编码列名
     * 需要通过表面获取bm列名称
     * select * from tname where tbm
     */
    private List<Map<String, String>> list;

    //表名
    private String tname;

    private Context mContext;

    public WorkParaListAdapter(String tname, Context context) {
        this.tname = tname;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(SynopsisObj.getAppContext(), R.layout.workpama_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        Map<String, String> map = list.get(position);
        String tbm = map.get("def_TBM");
        holder.tvName.setText(tbm);
        holder.btDetails.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GatherDetailActivity.class);
            Envelope<Map> e = new Envelope();
            e.setT(map);
            intent.putExtra("data", e);
            intent.putExtra("tname", tname);
            CommonUtils.toActivity((Activity) mContext, intent);
        });

        holder.ctvModif.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ModifActivity.class);
            Envelope<Map> e = new Envelope();
            e.setT(map);
            intent.putExtra("data", e);
            intent.putExtra("tname", tname);
            CommonUtils.toActivity((Activity) mContext, intent);
        });

        holder.cbOrientation.setOnClickListener(v -> {
            String MAPID = map.get(MAP_NUMBER_COLUMN);
            String lxbm = map.get("SSLX");
            ThreadPoolUtils.execute(() -> {
                if ("T_ld".equals(tname)) {
                    ArrayList<LocaDate> ldList = DbHelperDbHelper.open().queryLDByBMAndJwd(lxbm, tbm, MAPID);
                    if (ldList == null || ldList.size() <= 0) {
                        LoggerUtils.d("WorkParaListAdapter", "此路线点信息为0");
                    }
                    Intent intent = new Intent(mContext, LocationActivity.class);
                    intent.putExtra(Const.SHOW_ATTR_TYPE, Const.DrawType.POLYLINE);
                    intent.putExtra(Const.SHOW_ATTR_DATA_POINT_LIST, ldList);
                    CommonUtils.toActivity((Activity) mContext, intent);
                } else {
                    LocaDate locationData = DbHelperDbHelper.open().queryPointByBMAndJwd(tbm, MAPID);
                    Intent intent = new Intent(mContext, LocationActivity.class);
                    intent.putExtra(Const.SHOW_ATTR_TYPE, Const.DrawType.POINT);
                    intent.putExtra(Const.SHOW_ATTR_DATA_POINT, locationData);
                    CommonUtils.toActivity((Activity) mContext, intent);
                }

            });

        });
        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.bt_details)
        TextView btDetails;

        @BindView(R.id.cb_orientation)
        TextView cbOrientation;

        @BindView(R.id.ctv_modif)
        TextView ctvModif;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    //设置adapter数据
    public void deviceDataSource(List<Map<String, String>> list) {
        this.list = list;
        LoggerUtils.d("WorkParaListAdapter", "DataSize:" + list.size());
        notifyDataSetChanged();
    }
}
