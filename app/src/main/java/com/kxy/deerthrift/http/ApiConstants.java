package com.kxy.deerthrift.http;

public class ApiConstants {

    // 百度url
    public static final String BASEURL = "http://www.baidu.com/";

    // 接口url
    public static final String BATHURL = "http://120.78.184.57:8092/";

    /**
     * 获取对应的host
     *
     * @param hostType host类型
     * @return host
     */
    public static String getHost(int hostType) {
        String host;
        switch (hostType) {
            case HostType.DEER_CONFIG:
                host = BATHURL;
                break;

            default:
                host = "";
                break;
        }
        return host;
    }
}
