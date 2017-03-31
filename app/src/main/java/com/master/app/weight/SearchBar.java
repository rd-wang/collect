package com.master.app.weight;

import com.master.R;
import com.master.app.inter.OnSearchOperateListener;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class SearchBar extends LinearLayout
    implements TextView.OnEditorActionListener, View.OnClickListener, TextWatcher {


    private TextView etSearch;

    private ImageView mClearBtn;

    public SearchBar(Context context) {
        super(context);
        init();
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /***
     * 界面初始化
     **/
    private void init() {

        //计算屏幕的宽度
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();

        View view = inflate(getContext(), R.layout.bt_header_recommend_search, this);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.recommend_search_layout);
        mClearBtn = (ImageView) view.findViewById(R.id.search_clear);
        ListView.LayoutParams layoutParams = new ListView.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        try {
            LayoutParams params = (LayoutParams) layout.getLayoutParams();
            params.setMargins(20, 20, 20, 20);
            layout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        etSearch = (TextView) findViewById(R.id.drawer_search);
        etSearch.setOnEditorActionListener(this);
        mClearBtn.setOnClickListener(this);
        etSearch.addTextChangedListener(this);
    }

    /**
     * 计算view的宽高
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, lp.width);
        int lpHeight = lp.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


    /***
     * 设置搜索框是否可以编辑
     */
    public void setTextEditable() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) etSearch.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String temp = v.getText().toString();
        setTextEditable();
        if (actionId == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(temp)) {
            if (onSearchOperateListener != null) {
                onSearchOperateListener.onSearchOperate(temp);
            }
        }

        return true;
    }

    private OnSearchOperateListener onSearchOperateListener;

    public void setOnSearchOperateListener(OnSearchOperateListener onSearchOperateListener) {
        this.onSearchOperateListener = onSearchOperateListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mClearBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        if (etSearch.length() > 0) {
            etSearch.setText("");
            mClearBtn.setVisibility(View.INVISIBLE);
            if (onSearchOperateListener != null) {
                onSearchOperateListener.onClearOperate();
            }
        }
    }

    public void setText(String msg) {
        etSearch.setHint(msg);
    }
}
