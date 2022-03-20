package com.feasycom.feasyblue.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

/**
 * Copyright 2017 Shenzhen Feasycom Technology co.,Ltd
 */
public class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        super(context);
        /**
         * remove the system dialog title and border background
         */
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public BaseDialog(Context context, int styles) {
        super(context, styles);
        /**
         * remove the system dialog title and border background
         */
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
