package com.net.core.unit;

import android.content.Context;
import android.os.ParcelFormatException;

import com.net.core.service.config.ServiceRemoteConfigInstance;
import com.net.core.serviceconfig.SerCfgCons;

/**
 * <br>类描述:云控开关工具类
 * <br>详细描述:注意调用该类的相关方法时,确保{@link #initContext(Context)}被提前调用,即确保Context被初始化
 * <br><b>Author sichard</b>
 * <br><b>Date 17-10-17</b>
 */

public class CloudControlUtil {
    private static Context sContext;

    public static void initContext(Context context) {
        sContext = context;
    }
    /**
     * 获取云控广告是否实时加载
     * @return true,广告实时加载;false,广告采用提前缓存方式加载
     */
    public static boolean isAdRealTime() {
        String key = SerCfgCons.initFinalKey(SerCfgCons.LSCREEN_IS_ADS_REAL_TIME, sContext);
        final String value = ServiceRemoteConfigInstance.getInstance(sContext).getString(key);
        boolean isAdRealTime = true;
        try{
            isAdRealTime = Boolean.parseBoolean(value);
        }catch (ParcelFormatException ex){
            ex.printStackTrace();
        }
        return isAdRealTime;
    }


    /**
     * 获取云控key对应的boolean值,如果云端获取失败,默认返回true
     * @param couldKey 配置项的key值,参看@{@link SerCfgCons}的静态变量值
     * @param defaultValue 云端获取失败返回的默认值
     * @return
     */
    public static boolean getBooleanByCloudKey(String couldKey, boolean defaultValue) {
        String key = SerCfgCons.initFinalKey(couldKey, sContext);
        final String value = ServiceRemoteConfigInstance.getInstance(sContext).getString(key);
        try{
            defaultValue = Boolean.parseBoolean(value);
        }catch (ParcelFormatException ex){
            ex.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取云控key对应的Int值,如果云端获取失败
     * @param couldKey 配置项的key值,参看@{@link SerCfgCons}的静态变量值
     * @param defaultValue 云端获取失败返回的默认值
     * @return
     */
    public static int getIntByCloudKey(String couldKey, int defaultValue) {
        String key = SerCfgCons.initFinalKey(couldKey, sContext);
        final String value = ServiceRemoteConfigInstance.getInstance(sContext).getString(key);
        try{
            defaultValue = Integer.valueOf(value);
        }catch (ParcelFormatException ex){
            ex.printStackTrace();
        }
        return defaultValue;
    }


}
