package com.master.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.master.R;
import com.master.app.SynopsisObj;

import java.util.ArrayList;

/**
 * Created by hign on 2016/11/20.
 */

public class AttributeListAdapter extends BaseAdapter {
    Context mContent = null;
    ArrayList data = null;
    public AttributeListAdapter(Context context, ArrayList data) {
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

        View convertView=View.inflate(SynopsisObj.getAppContext(), R.layout.attribute_list_item,null);
        TextView tvfieldname= (TextView)convertView.findViewById(R.id.tvfieldname);
        TextView tvfilevalue= (TextView)convertView.findViewById(R.id.tvfilevalue);
        ArrayList item = (ArrayList)getItem(i);
        tvfieldname.setText((String) item.get(0));
        tvfilevalue.setText((String) item.get(1));
        return convertView;
    }
}
