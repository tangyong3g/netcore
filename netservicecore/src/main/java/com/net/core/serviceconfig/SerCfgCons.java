package com.net.core.serviceconfig;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by tylertang on 17-6-26.
 * <p>
 * 服务器配置项常量
 * <p>
 * key的生成和項目以及渠道相關
 * <p>
 * 見　build.gradle
 * <p>
 * buildConfigField("String", "APP_SERVER_CONFIG_PRE_NAME", "\"hi\"")
 * buildConfigField("String", "channel", "\"appcenter\"")
 */

public class SerCfgCons {

    private static final String TAG = "ServiceConfig";
    private static final String PROJECT = "PROJECT";
    private static final String CHANNELKEY = "CHANNELKEY";


    public static final String SEARCH_ORDER = "search_order";
    public static final String SHOW_HOT_GAME = "show_hot_game";
    public static final String SHOW_HOT_SITE = "show_hot_site";
    public static final String SHOW_HOT_WORD = "show_hot_word";
    public static final String DEFAULT_SEARCH_ENGINE = "default_search_engine";
    public static final String DRAWER_RECOMMEND = "drawer_recommend";
    public static final String FOLDER_RECOMMEND = "folder_recommend";
    public static final String GAMEBOX_BANNER = "gamebox_banner";
    public static final String IS_SHOW_LOCK_SCREEN = "is_show_lock_screen";
    public static final String AD_COUNT = "ad_count";
    public static final String AD_FORBID_TIME = "ad_forbid_time";
    public static final String BOOST_STYLE = "boost_style";
    public static final String FEEDBACK_GOOGLE = "feedback_google";
    public static final String FEEDBACK_FACEBOOK = "feedback_facebook";
    public static final String HOT_QUESTION = "hot_question";

    /**
     * 根据渠道和项目生成最后的key
     * <p>
     * 　项目_渠道_key
     *
     * @param key
     * @return
     */
    public static String initFinalKey(String key, Context context) {

        if (TextUtils.isEmpty(key)) {
            Log.i(TAG, "the key is invalidate,can't be null");
            return null;
        }
        ApplicationInfo applicationInfo = null;

        String project = "hi";
        String channel = "google";

        try {
            applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                project = applicationInfo.metaData.getString(PROJECT);
                channel = applicationInfo.metaData.getString(CHANNELKEY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringBuffer rs = new StringBuffer("");

        rs.append(project);
        rs.append("_");
        rs.append(channel);
        rs.append("_");
        rs.append(key);

        if (true) {
            Log.i(TAG, "the key is \t" + key + " and final key is:\t" + rs.toString() + "\t project pre is:\t" + project + "\t" + "channel is :\t" + channel);
        }

        return rs.toString();
    }

}
