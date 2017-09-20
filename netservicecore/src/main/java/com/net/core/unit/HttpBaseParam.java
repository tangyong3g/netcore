package com.net.core.unit;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by liwu.shu on 2016/7/12.
 */
public class HttpBaseParam {
    public String imei;
    public String user_info_id;
    public String region;
    public String version_name;
    public String network;
    public String screen_size;
    public String language;
    public String os_version;
    public String imsi;
    public String os_version_code;
    public String model;

    private HttpBaseParam() {

    }

    public static HashMap<String, String> getHttpBaseParam(Context mc) {
        ConfigDataProvider configDataProvider = ConfigDataProvider.getInstance(mc);
        HashMap<String, String> baseParam = new HashMap<>();
        //baseParam.put(BaseParamKey.IMEI, configDataProvider.getIMEI());
        //baseParam.put(BaseParamKey.IMIS, configDataProvider.getIMSI());
        baseParam.put(BaseParamKey.LANGUAGE, configDataProvider.getLauncher());
        baseParam.put(BaseParamKey.MODEL, configDataProvider.getModel());
        baseParam.put(BaseParamKey.OS_VERSION, configDataProvider.getOsVersion());
        baseParam.put(BaseParamKey.OS_VERSION_CODE, configDataProvider.getOsVersionCode());
        baseParam.put(BaseParamKey.SCREEN_SIZE, configDataProvider.getScreenSize());
        baseParam.put(BaseParamKey.VERSION_NAME, configDataProvider.getVersionName());
        baseParam.put(BaseParamKey.USER_INFO_ID, configDataProvider.getUserInfoId());
        baseParam.put(BaseParamKey.REGION, configDataProvider.getRegion());
        baseParam.put(BaseParamKey.NETWORK, configDataProvider.getNetwork());
        return baseParam;
    }

    public static class BaseParamKey {
        public final static String IMEI = "imei";
        public final static String USER_INFO_ID = "user_info_id";
        public final static String REGION = "region";
        public final static String VERSION_NAME = "version_name";
        public final static String NETWORK = "network";
        public final static String SCREEN_SIZE = "screen_size";
        public final static String LANGUAGE = "language";
        public final static String OS_VERSION = "os_version";
        public final static String IMIS = "imsi";
        public final static String OS_VERSION_CODE = "os_version_code";
        public final static String MODEL = "model";
    }
}
