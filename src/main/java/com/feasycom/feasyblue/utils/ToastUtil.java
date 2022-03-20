package com.feasycom.feasyblue.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by York
 * Email: 329698831@qq.com
 */


public class ToastUtil {
    public static void show(Context context, String message){
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
