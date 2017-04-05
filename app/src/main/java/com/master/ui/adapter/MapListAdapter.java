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

public class MapListAdapter extends BaseAdapter {
    String maptype = null;
    Context mContent = null;
    ArrayList data = null;

    public MapListAdapter(Context context, ArrayList data, String maptype) {
        this.mContent = context;
        this.data = data;
        this.maptype = maptype;
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

        View convertView = View.inflate(SynopsisObj.getAppContext(), R.layout.map_list_item, null);
        TextView tvfilename = (TextView) convertView.findViewById(R.id.tvfilename);
        TextView tvfilesize = (TextView) convertView.findViewById(R.id.tvfilesize);
        ImageView imgview_maplist = (ImageView) convertView.findViewById(R.id.imgview_maplist);
        ArrayList item = (ArrayList) getItem(i);
        if (maptype.equals("bg") || maptype.equals("gl")) {
            tvfilename.setText((String) item.get(0));
            tvfilesize.setText((String) item.get(1));
        } else if (maptype.equals("online")) {
            imgview_maplist.setImageDrawable(mContent.getResources().getDrawable((int) item.get(1)));
            tvfilename.setText((String) item.get(2));
            tvfilesize.setText((String) item.get(0));
        }
        return convertView;
    }
}
