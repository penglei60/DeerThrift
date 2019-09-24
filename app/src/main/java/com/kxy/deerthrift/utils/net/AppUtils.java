package com.kxy.deerthrift.utils.net;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author jasper
 * @since 2015-07-05 17:41
 * app版本工具类
 */

public class AppUtils {

    // 获取版本号
    public static int getVersionCode(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return 0;
    }

    // 获取版本名称
    public static String getVersionName(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return "";
    }
}
