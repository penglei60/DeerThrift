package com.kxy.deerthrift.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class CustomizeDialog extends Dialog {

	public CustomizeDialog(Context context) {
		super(context);
	}

	public CustomizeDialog(Context context, int theme) {
		super(context, theme);
	}

	public void showDialog(int layoutResID) {
		setContentView(layoutResID);
		initWindow();
		show();
	}

	@Override
	public void dismiss() {
		View view = getCurrentFocus();
		if(view instanceof TextView){
			InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
		}

		super.dismiss();
	}
  
	// 设置窗口显示
	public void initWindow() {
		Window window = getWindow(); // 得到对话框
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.alpha = 1f; //设置透明度
//		wl.gravity = Gravity.BOTTOM; //设置重力
//		wl.width = getContext().getResources().getDisplayMetrics().widthPixels;
		window.setAttributes(wl);
	}



}
