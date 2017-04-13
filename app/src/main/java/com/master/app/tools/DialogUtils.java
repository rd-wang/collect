package com.master.app.tools;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.master.R;
import com.master.app.inter.CallBack;

/**
 * <p>Title:${type_inName}<p/>
 * <p>Description:<p/>
 * <p>Company: </p>
 *
 * @author litao
 * @mail llsmpsvn@gmail.com
 * @date on 2017/1/6
 */
public class DialogUtils {


    private AlertDialog mAlertDialog;

    private final Button mSubmit;

    private DialogUtils(Builder builder, Activity c) {
        View view = View.inflate(c, R.layout.common_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView msg = (TextView) view.findViewById(R.id.msg);
        mSubmit = (Button) view.findViewById(R.id.submit);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        title.setText(builder.title);
        msg.setText(builder.msg);
        mSubmit.setText(builder.submitText);
        cancel.setText(builder.cancel);
        AlertDialog.Builder dlg = new AlertDialog.Builder(c);
        dlg.setView(view);
        dlg.setCancelable(false);
        mAlertDialog = dlg.create();
        cancel.setOnClickListener(v ->
            mAlertDialog.dismiss()
        );
    }


    public void showDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.show();
        }
    }
    public DialogUtils setCancelable() {
        mAlertDialog.setCancelable(false);
        return this;
    }

    public DialogUtils initSubmit(CallBack callBack) {
        mSubmit.setVisibility(View.VISIBLE);
        mSubmit.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.call(mAlertDialog);
            }
        });
        return this;
    }


    public static class Builder {

        private CharSequence title = "提示";

        private CharSequence msg = "";

        private CharSequence submitText = "确定";

        private CharSequence cancel = "取消";

        private Activity context = null;

        public Builder(Activity context) {
            this.context = context;
        }

        public Builder addTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder addMsg(CharSequence msg) {
            this.msg = msg;
            return this;
        }

        public Builder addCancel(CharSequence cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder addSubmittext(CharSequence submitText) {
            this.submitText = submitText;
            return this;
        }

        public DialogUtils build() {
            return new DialogUtils(this, context);
        }
    }
}
