package com.master.app.weight;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.master.R;
import com.master.app.Constants;
import com.master.app.manager.AcquisitionPara;
import com.master.app.tools.AppManager;
import com.master.app.tools.CommonUtils;
import com.master.app.tools.DataUtils;
import com.master.app.tools.LoggerUtils;
import com.master.bean.Fields;
import com.master.bean.Table;
import com.master.constant.Const;
import com.master.ui.activity.MainActivity;
import com.master.ui.activity.map.CollectListActivity;
import com.master.ui.fragment.map.LayerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Litao-pc on 2016/11/16.
 *         ~
 */

public class LableGradView extends GridView {

    private List<Table> dataList;

    private Context context;

    private int datasId;

    public LableGradView(Context context) {
        this(context, null);
    }


    public LableGradView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lableGride);
            try {
                datasId = a.getResourceId(R.styleable.lableGride_datas, 0);

            } finally {

                a.recycle();
            }
        }
        this.context = context;
        if (datasId == R.array.m_line_label) {
            // TODO: 2017/3/2  上面两个选项是写死的跟配置文件没关系，存储的还是一样的类型所以copy一份即可
            // 伪造数据
            List<Table> lineList = AcquisitionPara.getLineList();
            if (DataUtils.isNotEmpty(lineList)) {
                Table lxTable = lineList.get(0);
                lxTable.setTNameCHS("新路线");
                Table copy = lxTable.copy();
                copy.setTNameCHS("续采集");
                lineList.add(copy);
            }
            dataList = lineList;
        } else {
            dataList = AcquisitionPara.getPointList();
        }

        init();

    }

    private void init() {
//        setLayoutAnimation(getAnimationController());

        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return dataList.size();
            }

            @Override
            public Object getItem(int i) {
                return dataList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                CustomTextView lable;

                if (view == null) {
                    view = View.inflate(context, R.layout.label, null);
                    lable = (CustomTextView) view.findViewById(R.id.ctv_lable);
                    view.setTag(lable);
                } else {
                    lable = (CustomTextView) view.getTag();
                }

                lable.setOnClickListener(view1 -> {
                    MainActivity.S_MainActivity.hideExtra();
                    //采集时移除search画的路线
                    LayerFragment.S_LayerFragment.localCenter();
                    LayerFragment.S_LayerFragment.clearSearchLd();
                    if (datasId == R.array.m_line_label) {
                        MainActivity.mCJDX = "T_ld";
                        Intent intent = new Intent(MainActivity.S_MainActivity,
                                CollectListActivity.class);
                        Bundle b = new Bundle();
                        Table xlTable = dataList.get(i);
                        b.putSerializable(Constants.SELECT_COLLECT_LABE, xlTable);
                        b.putInt(Const.COLLECT_LINE_TYPE,
                                "新路线".equals(xlTable.getTNameCHS()) ?
                                        Const.LineType.NEWLINE : Const.LineType.OLDLINE);
                        intent.putExtras(b);
                        MainActivity.mtable = xlTable;
                        CommonUtils.toActivity(AppManager.getAppManager().currentActivity(), intent);
                    }
                    if (datasId == R.array.m_point_label) {

                        LoggerUtils.d("LableGradView", dataList.get(i).getTNameCHS());
                        //获取tname对应的所有字段
                        List<Fields> arguments = MainActivity.S_MainActivity
                                .getArguments(dataList.get(i).getTName(), new ArrayList<>());
                        if (arguments == null) {
                            return;
                        }

                        MainActivity.mtable = dataList.get(i);
                        MainActivity.mCJDX = dataList.get(i).getTName();
                        //生产view
                        MainActivity.S_MainActivity.builderSheet(arguments);
                        MainActivity.S_MainActivity.show(MainActivity.S_MainActivity.findViewById(R.id.main_scroll));

                    }
                });
                lable.setText(dataList.get(i).getTNameCHS());
                return view;
            }
        };
        setAdapter(adapter);
    }

    protected LayoutAnimationController getAnimationController() {
        int duration = 60;
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }


    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
