package com.kxy.deerthrift.utils.net;

import android.text.TextUtils;
import android.util.Log;

/**
 * Debug日志
 */

public class LogUtils {

    public static boolean flag = true;
    public static String tag = "source";

    public static void logi(String msg) {
        if (flag && !TextUtils.isEmpty(msg)) {
            Log.i(tag, msg);
        }
    }

    public static void loge(String msg) {
        if (flag && !TextUtils.isEmpty(msg)) {
            Log.e(tag, msg);
        }
    }


}
