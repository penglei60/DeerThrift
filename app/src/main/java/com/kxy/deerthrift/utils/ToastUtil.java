package com.kxy.deerthrift.utils;

import android.widget.Toast;

import com.kxy.deerthrift.App;

/**
 * Toast工具
 *
 * @author penglei
 * @date 2019/06/18
 */
public class ToastUtil {

    public static void showShort(int resId) {
        Toast.makeText(App.getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(String message) {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int resId) {
        Toast.makeText(App.getContext(), resId, Toast.LENGTH_LONG).show();
    }

    public static void showLong(String message) {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_LONG).show();
    }
}
