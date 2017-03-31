package com.master.ui.adapter;

import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.tools.CommonUtils;
import com.master.bean.Table;
import com.master.ui.activity.MainActivity;
import com.master.ui.activity.WorkParamListActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hign on 2016/11/20.
 */

public class DatabaseInfoAdapter extends BaseAdapter {

    Context mContent = null;

    List<Table> data = null;

    public DatabaseInfoAdapter(Context context, List data) {
        this.mContent = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View convertView = View
            .inflate(SynopsisObj.getAppContext(), R.layout.att_layer_list_item, null);
        TextView tvlayername = (TextView) convertView.findViewById(R.id.tvlayername);
        tvlayername.setText(data.get(i).getTNameCHS());
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(SynopsisObj.getAppContext(), WorkParamListActivity.class);
            intent.putExtra("tname", data.get(i).getTName());
            intent.putExtra("chsname", data.get(i).getTNameCHS());
            CommonUtils.toActivity(MainActivity.S_MainActivity, intent);
            MainActivity.mtable = data.get(i);
        });
        return convertView;
    }
}
