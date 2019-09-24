package com.kxy.deerthrift.http;


import com.kxy.deerthrift.bean.BaseInfo;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiService {

    /*
    * @Query，@QueryMap，@Field，@FieldMap，@FormUrlEncoded，@Path，@Url
    * get使用@QueryMap  post使用@FieldMap
    * */

    // 获取url
    @GET("data")
    Flowable<BaseInfo> getUrlType();


//    // 登录
//    @FormUrlEncoded
//    @POST("login")
//    Flowable<LoginInfo> getLogin(@FieldMap Map<String, String> params);





}
