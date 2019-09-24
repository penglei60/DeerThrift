package com.kxy.deerthrift.utils;


import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 判断手机是否ROOT
 */
public class IsRoot {

    public static int root() {
        int root = 0;
        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                root = 0;
            } else {
                root = 1;
            }
        } catch (Exception e) {
        }
        return root;
    }

    /**
     * 判断应用是否有root权限
     */
    public static synchronized boolean getRootAhth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    public static boolean isRootSystem() {
        if(isRootSystem1()||isRootSystem2()){
            //TODO 可加其他判断 如是否装了权限管理的apk，大多数root 权限 申请需要app配合，也有不需要的，这个需要改su源码。因为管理su权限的app太多，无法列举所有的app，特别是国外的，暂时不做判断是否有root权限管理app
            //多数只要su可执行就是root成功了，但是成功后用户如果删掉了权限管理的app，就会造成第三方app无法申请root权限，此时是用户删root权限管理app造成的。
            //市场上常用的的权限管理app的包名   com.qihoo.permmgr  com.noshufou.android.su  eu.chainfire.supersu   com.kingroot.kinguser  com.kingouser.com  com.koushikdutta.superuser
            //com.dianxinos.superuser  com.lbe.security.shuame com.geohot.towelroot 。。。。。。
            return true;
        }else{
            return false;
        }
    }
    private static boolean isRootSystem1() {
        File f = null;
        final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/" };
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()&&f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
    private static boolean isRootSystem2() {
        List<String> pros = getPath();
        File f = null;
        try {
            for (int i = 0; i < pros.size(); i++) {
                f = new File(pros.get(i),"su");
                System.out.println("f.getAbsolutePath():"+f.getAbsolutePath());
                if (f != null && f.exists()&&f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static List<String> getPath() {
        return Arrays.asList(System.getenv("PATH").split(":"));
    }

}
