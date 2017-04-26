package com.net.core.unit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by liwu.shu on 2016/7/12.
 */
public class ConfigDataProvider extends ConfigDataBase {
    private final static String IMEI = "imei";
    private final static String USER_INFO_ID = "user_info_id";
    private final static String REGION = "region";
    private final static String VERSION_NAME = "version_name";
    private final static String NETWORK = "network";
    private final static String SCREEN_SIZE = "screen_size";
    private final static String LAUNCHER = "language";
    private final static String OS_VERSION = "os_version";
    private final static String IMSI = "imsi";
    private final static String OS_VERSION_CODE = "os_version_code";
    private final static String MODEL = "model ";

    /**
     * MAC地址缓存
     */
    public final static String SPARE_MAC = "spare_mac";

    static Context appContext;

    private ConfigDataProvider(Context mc) {
        super(mc);
    }

    public static ConfigDataProvider getInstance(Context mc) {
        appContext = mc.getApplicationContext();
        return ConfigDataProviderHolder.INSTANCE;
    }

    private static class ConfigDataProviderHolder {
        private static ConfigDataProvider INSTANCE = new ConfigDataProvider(appContext);
    }

    public String getIMEI() {
        return TelephonyManagerUtil.getInstance(appContext).getDeviceId();
    }

    public String getUserInfoId() {
        return getStringData(USER_INFO_ID);
    }

    public String getRegion() {
        return getStringData(REGION);
    }

    public String getVersionName() {
        try {
            return appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.getTypeName().toUpperCase();
        }
        return "UNKNOW";
    }

    public String getScreenSize() {
        StringBuilder sb = new StringBuilder();
        Display display = ((WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        sb.append(point.x);
        sb.append("#");
        sb.append(point.y);
        return sb.toString();
    }

    public String getLauncher() {
        return appContext.getResources().getConfiguration().locale.toString();

    }

    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getIMSI() {
        return TelephonyManagerUtil.getInstance(appContext).getSubscriberId(appContext);
    }

    public String getOsVersionCode() {
        System.out.println("Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public String getModel() {
        return Build.MODEL;
    }

    public void setSpareMacData(String data) {
        addStringData(SPARE_MAC, data);
    }

    public String getSpareMacData() {
        return getStringData(SPARE_MAC);
    }

    public String getCountry() {
        return TelephonyManagerUtil.getInstance(appContext).getSimCountryIso();
    }

}
