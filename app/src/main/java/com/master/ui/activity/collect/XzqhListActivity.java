package com.master.ui.activity.collect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.master.R;
import com.master.app.manager.ConfigManager;
import com.master.app.tools.ActionBarManager;
import com.master.bean.Xzqh;
import com.master.contract.BaseActivity;
import com.master.ui.adapter.TreeViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class XzqhListActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ExpandableListView expandableList;
    TreeViewAdapter adapter;
    ArrayList groups=new ArrayList();
    ArrayList child=new ArrayList();
    ArrayList child_code=new ArrayList();
    @Override
    public void bindView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        ActionBarManager.initBackTitle(getSupportActionBar());
        title.setText("行政区划");
        List<Xzqh> xzqhs = ConfigManager.create().getXzqhList();

        for (int i=0;i<xzqhs.size();i++) {
            Xzqh t=xzqhs.get(i);
            String code = t.getCode();
            String name = t.getName();
            String pcode = t.getPcode();
            if(pcode!=null &&code.substring(4,6).equalsIgnoreCase("00")&&!code.substring(2,4).equalsIgnoreCase("00")){
                groups.add(name+"-"+code);
                ArrayList qx1=new ArrayList();
                ArrayList qx1_code=new ArrayList();
                for(int j=0;j<xzqhs.size();j++){
                    Xzqh tt=xzqhs.get(j);
                    String tcode = tt.getCode();
                    String tname = tt.getName();
                    String tpcode = tt.getPcode();
                    if(tpcode!=null&&tcode.substring(0,4).equals(code.substring(0,4))&&!tcode.substring(4,6).equals("00")){
                        qx1.add(tname+"-"+tcode);
                        qx1_code.add(tcode);
                    }
                }
                child.add(qx1);
                child_code.add(qx1_code);
            }
        }
        adapter=new TreeViewAdapter(this,TreeViewAdapter.PaddingLeft>>1);
        expandableList=(ExpandableListView) this.findViewById(R.id.ExpandableListView01);
        adapter.RemoveAll();
        adapter.notifyDataSetChanged();
        List<TreeViewAdapter.TreeNode> treeNode = adapter.GetTreeNode();
        for(int i=0;i<groups.size();i++)
        {
            TreeViewAdapter.TreeNode node=new TreeViewAdapter.TreeNode();
            node.parent=String.valueOf(groups.get(i));
            ArrayList child_list=(ArrayList) child.get(i);
            for(int ii=0;ii<child_list.size();ii++)
            {
                node.childs.add(String.valueOf(child_list.get(ii)));
            }
            treeNode.add(node);
        }

        adapter.UpdateTreeNode(treeNode);
        expandableList.setAdapter(adapter);
        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){

            @Override
            public boolean onChildClick(ExpandableListView arg0, View arg1,
                                        int parent, int children, long arg4) {

                String str="parent id:"+String.valueOf(parent)+",children id:"+String.valueOf(children);
                Intent intent = new Intent(mContext, AddLxbmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("activity", "XzqhListActivity");
                ArrayList child_code_list=(ArrayList)child_code.get(parent);
                bundle.putString("xzqh", String.valueOf(child_code_list.get(children)));
                intent.putExtras(bundle);
                startActivity(intent);
                XzqhListActivity.this.finish();
                return false;
            }
        });
    }
    @Override
    public int getLayoutResId() {
        return R.layout.activity_xzqh_list;
    }


}
