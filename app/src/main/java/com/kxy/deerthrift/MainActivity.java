package com.kxy.deerthrift;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.kxy.deerthrift.base.BaseActivity;
import com.kxy.deerthrift.bean.BaseInfo;
import com.kxy.deerthrift.http.HostType;
import com.kxy.deerthrift.http.HttpApi;
import com.kxy.deerthrift.http.HttpApi2;
import com.kxy.deerthrift.utils.DeviceUuidFactory;
import com.kxy.deerthrift.utils.ToastUtil;
import com.kxy.deerthrift.utils.net.NetworkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.wv_view)
    WebView mWebView;

    private String url = "file:///android_asset/zyzb.html";
//    private String url = "http://ableryy.com";



    @Override
    protected int getContentViewId() {
        // 隐藏状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        getPermissions();  // 权限管理
        getUUID(); // 获取手机唯一标识码
//

        Log.e(TAG,"===00000");
        ProcessWebString();

    }

    @Override
    public void initData() {

    }

    @SuppressLint("JavascriptInterface")
    private void ProcessWebString() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        webSettings.setJavaScriptEnabled(true);                       //可执行js
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);   //设置页面默认缩放密度
        webSettings.setDefaultTextEncodingName("UTF-8");              //设置默认的文本编码名称，以便在解码html页面时使用
        webSettings.setAllowContentAccess(true);                      //启动或禁用WebView内的内容URL访问
        webSettings.setAppCacheEnabled(false);                        //设置是否应该启用应用程序缓存api
        webSettings.setBuiltInZoomControls(false);                    //设置WebView是否应该使用其内置的缩放机制
        webSettings.setUseWideViewPort(true);                         //设置WebView是否应该支持viewport
        webSettings.setLoadWithOverviewMode(true);                    //不管WebView是否在概述模式中载入页面，将内容放大适合屏幕宽度
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);          //重写缓存的使用方式
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);   //告知js自动打开窗口
        webSettings.setLoadsImagesAutomatically(true);                //设置WebView是否应该载入图像资源
        webSettings.setAllowFileAccess(true);                         //启用或禁用WebView内的文件访问
        webSettings.setDomStorageEnabled(true);                       //设置是否启用了DOM存储API,默认为false
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");

        mWebView.setWebChromeClient(new WebChromeClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        mWebView.setBackgroundColor(Color.TRANSPARENT);  //  WebView 背景透明效果，不知道为什么在xml配置中无法设置？

//        getUrlType(); // 获取url
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", "====" + url);

                // file:///android_asset/view/play.html?canshu=11&&vip=0&&id=2
                // 当加载视频界面时，强制去掉标题栏
                if (url.contains("play")) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                //H5调起微信app支付方法
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                //H5调起支付宝app支付方法
                if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    } catch (Exception e) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("未检测到支付宝客户端，请安装后重试。")
                                .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                        startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                                    }
                                }).setNegativeButton("取消", null).show();
                    }
                    return true;
                }

                //判断H5是否时http或者https
               /* if (!(url.startsWith("http") || url.startsWith("https"))) {
                    return true;
                }*/

                getUUID();
                view.loadUrl(url);
                return true;
            }
        });


        // webview下载
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Log.e("hhh","url====" + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            // goBack()表示返回WebView的上一页面
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 获取url类型
     */
    private void getUrlType() {
        if (NetworkUtils.isConnected(MainActivity.this)) {

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
                                        url = baseInfo.getData().get(0).getNewLink();
                                    }
//                                    ProcessWebString();

                                    mWebView.loadUrl(url);
                                    Log.e(TAG,"===11111");
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


    /**
     * 获取android手机唯一标识码 uuid
     */
    private void getUUID() {
        String udid = DeviceUuidFactory.getInstance(this).getDeviceUuid().toString();
        Log.e("uuid", "=====" + udid.toString());

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:setUDID('" + udid + "')");
                Log.e("udid", "=====" + udid);
            }
        }, 1000);
    }

    /**
     * 使点击回退按钮不会直接退出整个应用程序而是返回上一个页面
     */
    // 再按一次退出
    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();//返回上个页面
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showShort(getResources().getString(R.string.s_btn_exit));
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);//退出整个应用程序
    }


    /**
     * 权限管理
     */
    private void getPermissions() {
        // 权限管理（设置需要打开的所有权限）
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    //创建监听权限的接口对象（权限设置）
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
//            Toast.makeText(MainActivity.this, "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbitPermissons() {
//            finish();
//            Toast.makeText(MainActivity.this, "权限不通过!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
