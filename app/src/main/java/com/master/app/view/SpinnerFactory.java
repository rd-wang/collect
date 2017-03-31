package com.master.app.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.master.R;
import com.master.app.inter.CommonListener;
import com.master.bean.Fields;
import com.rey.material.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;


/**
 * Created by nipun on 30/05/15.
 */
public class SpinnerFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(Context context, Fields fields, CommonListener listener) {
        List<View> views = new ArrayList<>(1);
        MaterialSpinner spinner = (MaterialSpinner) LayoutInflater.from(context).inflate(R.layout.item_spinner, null);

        String hint = "测试";
        if (!TextUtils.isEmpty(hint)) {
            spinner.setHint(hint);
            spinner.setFloatingLabelText(hint);
        }

        spinner.setId(ViewUtil.generateViewId());

        spinner.setTag(R.id.key, "key");
        spinner.setTag(R.id.type, "type");


        String valueToSelect = "";
        int indexToSelect = -1;


        String[] values = {"oo1", "oo2", "oo3", "oo4", "oo5"};

        if (values != null) {
            spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, values));
            spinner.setSelection(indexToSelect + 1, true);
            spinner.setOnItemSelectedListener(listener);
        }
        views.add(spinner);
        return views;
    }

    public static ValidationStatus validate(MaterialSpinner spinner) {

        int selectedItemPosition = spinner.getSelectedItemPosition();
        if (selectedItemPosition > 1) {
            return new ValidationStatus(true, null);
        } else
            return new ValidationStatus(false, null);
    }
}
