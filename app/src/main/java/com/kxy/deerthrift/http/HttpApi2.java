package com.kxy.deerthrift.http;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kxy.deerthrift.App;
import com.kxy.deerthrift.utils.JsonUtil;
import com.kxy.deerthrift.utils.net.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 支持多主机地址的网络请求类
 * yonbor605
 */
public class HttpApi2 {

    public static final int DEFAULT_TIMEOUT = 20;
    public Retrofit retrofit;
    public static ApiService apiService;

    private static SparseArray<HttpApi2> httpApi2SparseArray = new SparseArray<>(HostType.TYPE_COUNT);


    /*************************缓存设置*********************/
/*
   1. noCache 不使用缓存，全部走网络

    2. noStore 不使用缓存，也不存储缓存

    3. onlyIfCached 只使用缓存

    4. maxAge 设置最大失效时间，失效则不使用 需要服务器配合

    5. maxStale 设置最大失效时间，失效则不使用 需要服务器配合 感觉这两个类似 还没怎么弄清楚，清楚的同学欢迎留言

    6. minFresh 设置有效时间，依旧如上

    7. FORCE_NETWORK 只走网络

    8. FORCE_CACHE 只走缓存*/

    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";


    // 构造方法私有
    private HttpApi2(int hostType) {
        //开启Log
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            private StringBuilder mMessage = new StringBuilder();

            @Override
            public void log(String message) {
                // 请求或者响应开始
                if (message.startsWith("--> POST") || message.startsWith("--> GET")) {
                    mMessage.setLength(0);
                }
                // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
                if ((message.startsWith("{") && message.endsWith("}"))
                        || (message.startsWith("[") && message.endsWith("]"))) {
                    message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
                }
                mMessage.append(message.concat("\n"));
                // 请求或者响应结束，打印整条日志
                if (message.startsWith("<-- END HTTP")) {
                    Log.e("data","==="+mMessage.toString());
//                    LogUtil.d(mMessage.toString());
                }
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //缓存
        File cacheFile = new File(App.getContext().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        //增加头部信息
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request build = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json") //设置允许请求json数据
                        .build();
                return chain.proceed(build);
            }
        };

        //创建一个OkHttpClient并设置超时时间
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false) // 不重连
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为20s
                .connectionPool(new ConnectionPool(8, DEFAULT_TIMEOUT, TimeUnit.SECONDS))
                .addNetworkInterceptor(logInterceptor)
                .addInterceptor(mRewriteCacheControlInterceptor) // 自定义的拦截器，用于添加公共参数
                .addInterceptor(headerInterceptor)
                .cache(cache)
                .build();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson)) //请求的结果转为实体类
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //适配RxJava2.0,RxJava1.x则为RxJavaCallAdapterFactory.create()
                .baseUrl(ApiConstants.getHost(hostType))
                .build();

        apiService = retrofit.create(ApiService.class);
    }


    public static ApiService getApiService() {
        HttpApi2 httpApi2 = httpApi2SparseArray.get(0);

        if (httpApi2 == null) {
            httpApi2 = new HttpApi2(0);
            httpApi2SparseArray.put(0, httpApi2);
        }
        return httpApi2.apiService;
    }

    public static ApiService getApiService(int hostType) {
        HttpApi2 httpApi2 = httpApi2SparseArray.get(hostType);
        if (httpApi2 == null) {
            httpApi2 = new HttpApi2(hostType);
            httpApi2SparseArray.put(hostType, httpApi2);

//            Log.e("ceshi66666","======"+hostType);
        }else{
            httpApi2 = new HttpApi2(hostType);
            httpApi2SparseArray.put(hostType, httpApi2);
//            Log.e("ceshi777777","======"+hostType);
        }
        return httpApi2.apiService;
    }


    /**
     * 根据网络状况获取缓存的策略
     */
    @NonNull
    public static String getCacheControl() {
        return NetworkUtils.isNetConnected(App.getContext()) ? CACHE_CONTROL_AGE : CACHE_CONTROL_CACHE;
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String cacheControl = request.cacheControl().toString();
            if (!NetworkUtils.isNetConnected(App.getContext())) {
                request = request.newBuilder()
                        .cacheControl(TextUtils.isEmpty(cacheControl) ? CacheControl.FORCE_NETWORK : CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isNetConnected(App.getContext())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置

                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };
}