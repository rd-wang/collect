package com.master.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.master.R;

/**
 * Created by rd on 2017/3/28.
 */

public class LdHolder extends RecyclerView.ViewHolder{

    public TextView ldmc;
    public TextView ldbm;

    public LdHolder(View itemView) {
        super(itemView);
        ldmc = (TextView) itemView.findViewById(R.id.ld_name);
        ldbm = (TextView) itemView.findViewById(R.id.ld_bm);
    }
}
