package com.example.yuanping.bike;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by yuanping on 3/27/18.
 */

public class Constant {
    public static final String reg = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
    public static final String PHONE = "phone";

    public static ProgressDialog showSpinnerDialog(Activity activity, String msg) {
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage(msg);
        return dialog;
    }
}
