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
    public static final String IS_SHOW_LOCK_SCREEN_ADS = "is_show_lock_screen_ads";
    public static final String INTERVAL_TIME_KEY = "rating_interval_time_day";
    public static final String ENGINE_GOOGLE_URL="engine_google_url";
    public static final String ENGINE_BAIDU_URL="engine_baidu_url";
    public static final String ENGINE_BING_URL="engine_bing_url";
    public static final String ENGINE_YANDEX_URL="engine_yandex_url";
    public static final String ENGINE_YAHOO_URL="engine_yahoo_url";
    public static final String SHOW_YANDEX_KEYWORD="show_yandex_keyword";
    public static final String LSREEN_CARD_ADS_LIST_INDEX="lsreen_card_ads_list_index";
    public static final String LSREEN_CARD_ADS_TYPE="lsreen_card_ads_type";
    public static final String LSREEN_DETAIL_ADS_LOCATION="lsreen_detail_ads_location";
    public static final String LSREEN_DETAIL_ADS_TYPE="lsreen_detail_ads_type";
    public static final String LSREEN_ADS_STYLE_SCHEME="lsreen_ads_style_scheme";
    public static final String SETTING_IS_SHOW_LOCK_ITEM="setting_is_show_lock_item";
    public static final String SHOW_LOCK_SCREEN_V_1_3="show_lock_screen_v_1_3";
    public static final String LSCREEN_IS_ADS_REAL_TIME="lscreen_is_ads_real_time";


    /**
     *  note :
     *
     *    SerConf  里面不再增加任何 常量的Key，全都让客户端传入进来，这里不耦合业务。
     *
     */

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
