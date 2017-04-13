package com.master.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.master.R;
import com.master.app.SynopsisObj;
import com.master.app.manager.FieldWordsDirs;
import com.master.app.tools.ActionBarManager;
import com.master.app.tools.ContextUtils;
import com.master.app.tools.LoggerUtils;
import com.master.app.weight.SearchListView;
import com.master.bean.Envelope;
import com.master.bean.Fields;
import com.master.contract.BaseActivity;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description: <p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2016/12/22
 */
public class GatherDetailActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.listView)
    SearchListView mListView;

    private Map<String, String> data;

    private LinkedList<String> keyList;

    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        this.title.setText("详情");
        ActionBarManager.initBackTitle(getSupportActionBar());
        data = (Map<String, String>) ((Envelope) getIntent().getSerializableExtra("data")).getT();

        //添加编码信息
        String def_TBM = data.get("def_TBM");
        String def_TBMLM = data.get("def_TBMLM");

        data.put(def_TBMLM, def_TBM);
        Set<String> strings = data.keySet();

        keyList = new LinkedList<>();
        for (String key : strings) {
            if (key.equals("def_TBM") || key.equals("def_TBMLM")) {
                continue;
            }
            keyList.addFirst(key);
        }
//        String tname = getIntent().getStringExtra("tname");

        mListView.addHeaderView(ContextUtils.inflate(R.layout.list_item_5));
        mListView.setAdapter(baseAdapter);
        mListView.setEnableRefresh(false);

    }

    @Override
    public int getLayoutResId() {
        return R.layout.ac_point_details;
    }

    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return keyList.size() == 0 ? 0 : keyList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(SynopsisObj.getAppContext(), R.layout.list_item_4, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            LoggerUtils.d(sClassName, keyList.get(position));
            Fields fields = FieldWordsDirs.get().fieldsFormFName(keyList.get(position));
            if(fields !=null){
                viewHolder.tvZbmc.setText(fields.getFNameCHS());
            }else{
                viewHolder.tvZbmc.setText(keyList.get(position));
            }

            viewHolder.tvZbz.setText(data.get(keyList.get(position)));
            return convertView;
        }
    };

    class ViewHolder {

        @BindView(R.id.tv_zbmc)
        TextView tvZbmc;

        @BindView(R.id.tv_zbz)
        TextView tvZbz;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
