package com.master.app.view;

import com.master.R;
import com.master.app.inter.JsonApi;

import android.support.v7.widget.TintContextWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;


/**
 * editext 监听器。  设置监听规则
 */
public class GenericTextWatcher implements TextWatcher {

    private View mView;

    private String mStepName;

    public GenericTextWatcher(String stepName, View view) {
        mView = view;
        mStepName = stepName;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        JsonApi api = null;
        if (mView.getContext() instanceof JsonApi) {
            api = (JsonApi) mView.getContext();
        } else if (mView.getContext() instanceof TintContextWrapper) {
            TintContextWrapper tintContextWrapper = (TintContextWrapper) mView.getContext();
            api = (JsonApi) tintContextWrapper.getBaseContext();
        } else {
            throw new RuntimeException("Could not fetch context");
        }

        String key = (String) mView.getTag(R.id.key);
        try {
//            api.writeValue(mStepName, key, text);
        } catch (Exception e) {
            // TODO- handle
            e.printStackTrace();
        }
    }
}