package com.master.app.view;

import com.master.R;
import com.master.app.inter.CommonListener;
import com.master.app.tools.StringUtils;
import com.master.bean.Fields;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class EditTextFactory implements FormWidgetFactory {

    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 20;

    /**
     * @param stepName
     * @param context
     * @param jsonObject
     * @param listener
     * @return
     * @throws Exception
     */
    @Override
    public List<View> getViewsFromJson(Context context, Fields fields, CommonListener listener) throws Exception {
        int minLength = MIN_LENGTH;
        int maxLength = MAX_LENGTH;
        List<View> views = new ArrayList<>(1);
        boolean bm = StringUtils.endsWithIgnoreCase(fields.getFName(), "BM");
        MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        editText.setMinCharacters(minLength);
        editText.setFloatingLabelText(fields.getFNameCHS());
        editText.setTag(R.id.fname, fields.getFName());
        editText.setHint("输入" + fields.getFNameCHS());
        editText.setSingleLine();
        if (!fields.isfShow()) {
            editText.setVisibility(View.GONE);
        }
        editText.setId(ViewUtil.generateViewId());
        editText.setMaxCharacters(maxLength);
        views.add(editText);
        return views;
    }

    public static ValidationStatus validate(MaterialEditText editText) {

        boolean validate = editText.validate();
        if (!validate) {
            return new ValidationStatus(false, editText.getError().toString());
        }
        return new ValidationStatus(true, null);
    }
}
