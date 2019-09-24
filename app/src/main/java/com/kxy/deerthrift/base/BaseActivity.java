package com.kxy.deerthrift.base;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.kxy.deerthrift.utils.AndroidWorkaround;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import io.reactivex.Flowable;


/**
 * @author peng.
 * @time 2019/7/1.
 * @description
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);

        //适配虚拟按键
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
        }

        initView();
        initData();
    }

    /**
     * 布局文件ID
     *
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 控件初始化
     */
    public abstract void initView();

    /**
     * 数据适配
     */
    public abstract void initData();


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Rx生命周期
     */
    public <T> Flowable<T> rxDestroy(Flowable<T> observable) {
        return observable.compose(this.<T>bindUntilEvent(ActivityEvent.DESTROY));
    }

}
