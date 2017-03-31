package com.master;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.master.app.tools.LoggerUtils;
import com.master.app.view.SpinnerFactory;
import com.master.app.view.ValidationStatus;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;

public class TestActivity extends AppCompatActivity {

    String json = "Catsic_Codes.json";

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.ll_container)
    LinearLayout ll_container;
    @BindView(R.id.text2)
    MaterialEditText text2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

    }

    private List<MaterialSpinner> list = new ArrayList<>();

    @OnClick(R.id.button)
    public void onClick() {
        SpinnerFactory sprin = new SpinnerFactory();
        List<View> viewsFromJson = sprin.getViewsFromJson(this, null, null);
        list.add((MaterialSpinner) viewsFromJson.get(0));
        ll_container.addView(viewsFromJson.get(0));
        if (list.size() > 0) {
            TextView selectedView = (TextView) list.get(0).getSelectedView();
            String s = selectedView.getText().toString();
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button2)
    public void onClick2() {
        onNextClick(ll_container);
        text2.setError("error");
    }

    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, validationStatus.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public ValidationStatus writeValuesAndValidate(LinearLayout mainView) {
        int childCount = mainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mainView.getChildAt(i);
            if (childAt instanceof MaterialSpinner) {
                MaterialSpinner spinner = (MaterialSpinner) childAt;
                ValidationStatus validationStatus = SpinnerFactory.validate(spinner);
                LoggerUtils.d("xxx", "validationStatus:" + validationStatus.isValid());
                if (!validationStatus.isValid()) {
                    spinner.setError("");
                    return validationStatus;
                }
            }
        }
        return new ValidationStatus(true, null);
    }

}
