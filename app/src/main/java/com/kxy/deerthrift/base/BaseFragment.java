package com.kxy.deerthrift.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import io.reactivex.Observable;


/**
 * @author peng.
 * @time 2019/7/1.
 * @description
 */
public abstract class BaseFragment extends RxFragment {
    public Activity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getContentViewId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mContext = this.getActivity();
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
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Rx生命周期
     */
    public <T> Observable<T> rxDestroy(Observable<T> observable) {
        return observable.compose(bindUntilEvent(FragmentEvent.DESTROY));
    }
}
