package com.master.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.master.R;
import com.master.app.SynopsisObj;

import java.util.ArrayList;

/**
 * Created by hign on 2016/11/20.
 */

public class MapLayerInfoAdapter extends BaseAdapter {
    Context mContent = null;
    ArrayList data = null;
    public MapLayerInfoAdapter(Context context, ArrayList data) {
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

        View convertView=View.inflate(SynopsisObj.getAppContext(), R.layout.att_layer_list_item,null);
        TextView tvlayername= (TextView)convertView.findViewById(R.id.tvlayername);
        ArrayList item = (ArrayList)getItem(i);
        tvlayername.setText((String) item.get(0)+"(共"+item.get(1)+"条记录)");
        return convertView;
    }
}
