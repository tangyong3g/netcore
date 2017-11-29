package com.net.core.unit;

import android.content.Context;
import android.os.ParcelFormatException;
import android.telephony.TelephonyManager;

import com.net.core.service.config.ServiceRemoteConfigInstance;
import com.net.core.serviceconfig.SerCfgCons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
        final String value = getStringByCloudKey(couldKey);
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
        final String value = getStringByCloudKey(couldKey);
        try{
            defaultValue = Integer.valueOf(value);
        }catch (ParcelFormatException ex){
            ex.printStackTrace();
        }
        return defaultValue;
    }


    public static String getStringByCloudKey(String couldKey){
        String key = SerCfgCons.initFinalKey(couldKey, sContext);
        String value = ServiceRemoteConfigInstance.getInstance(sContext).getString(key);
        return value;
    }

    public static String getCountry() {
        TelephonyManager manager = (TelephonyManager) sContext.getSystemService(Context.TELEPHONY_SERVICE);
        String country=manager.getSimCountryIso();
        String c ;
        if (country==null || country.isEmpty()){
            c = Locale.getDefault().getCountry();
        }else{
            c = country;
        }
        return c;
    }

    public static boolean isLoadAd() {
        String country = getCountry();
        if (country != null && !country.isEmpty()) {
            String c = country.toLowerCase();
            String cloudCountry =getStringByCloudKey(SerCfgCons.NO_ADS_COUNTRY);
            List<String> list = getSplitList(cloudCountry);
            for (String s : list) {
                if (s.equals(c)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<String> getSplitList(String str){
        List countryList=new ArrayList();
        if (str!=null && !str.isEmpty()){
            countryList = Arrays.asList(str.split(","));
        }
        return countryList;
    }
}
