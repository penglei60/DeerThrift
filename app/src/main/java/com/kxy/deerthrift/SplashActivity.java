package com.kxy.deerthrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.kxy.deerthrift.bean.BaseInfo;
import com.kxy.deerthrift.http.HostType;
import com.kxy.deerthrift.http.HttpApi2;
import com.kxy.deerthrift.utils.ToastUtil;
import com.kxy.deerthrift.utils.net.NetworkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 启动页
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏、隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        // 延时
      /*  timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, ViewActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);*/


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getUrlType(); // 获取url
            }
        }, 500);

    }

    /**
     * 获取url类型
     */
    private void getUrlType() {
        if (NetworkUtils.isConnected(SplashActivity.this)) {

            HttpApi2.getApiService(HostType.DEER_CONFIG).getUrlType().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<BaseInfo>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            s.request(Long.MAX_VALUE);
                        }

                        @Override
                        public void onNext(BaseInfo baseInfo) {
                            Log.e(TAG, "====baseInfo：" + baseInfo.toString());
                            try {
                                if (baseInfo != null && baseInfo.getData().size() > 0) {
                                    if (baseInfo.getData().get(0).getStatus() == 1) {
                                        Intent intent = new Intent(SplashActivity.this, ViewActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Throwable t) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            ToastUtil.showShort(getResources().getString(R.string.network_fial_message));
        }
    }


}
