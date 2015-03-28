package com.singhdd.dftpclient.common.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

/**
 * Created by damandeep on 29/3/15.
 */
public class CustomProgessDialog extends ProgressDialog {

    private Context mContext;

    public CustomProgessDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CustomProgessDialog(Context context, int theme) {
        super(context,theme);
        mContext = context;
    }

    public void show() {
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.show();
    }

    public void dismiss() {
        super.dismiss();
        ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}
