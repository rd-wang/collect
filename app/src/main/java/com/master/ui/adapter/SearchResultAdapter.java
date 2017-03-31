package com.master.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.master.R;
import com.master.bean.LdData;
import com.master.ui.holder.LdHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rd on 2017/3/28.
 */

public class SearchResultAdapter extends RecyclerView.Adapter {

    private List<LdData> ldDatas = new ArrayList<>();

    public SearchResultAdapter() {
    }

    public void setData(List<LdData> list) {
        if (list == null) {
            ldDatas = new ArrayList<>();
        } else {
            ldDatas = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        return new LdHolder(mLayoutInflater.inflate(R.layout.search_result_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        LdHolder ldHolder = (LdHolder) holder;
        LdData ldData = ldDatas.get(position);
        ldHolder.ldbm.setText(ldData.getLDBM());
        ldHolder.ldmc.setText(ldData.getLDMC());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                }
            }
        });

    }


    public LdData getItemData(int position) {
        return ldDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return ldDatas.size();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
