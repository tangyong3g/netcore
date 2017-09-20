package com.net.core.unit;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by liwu.shu on 2016/7/12.
 */
public class TelephonyManagerUtil {

    private static final String DEVICEID = "deviceId";
    private static final String MACADDRESS = "mac";
    private static final String ANDROIDID = "androidID";
    private static final String SYMBOL = "#";
    private static final String SUBSCRIBERID = "subscriberId";
    private static final String COUNTRYCODE = "countryCode";
    private TelephonyManager tm;
    private static TelephonyManagerUtil telMUtil;
    private Context appContext;

    private TelephonyManagerUtil(Context mContext) {
        appContext = mContext.getApplicationContext();
        if (PermissionUtil.hasReadPhonePermission(appContext))
            tm = (TelephonyManager) appContext
                    .getSystemService(mContext.TELEPHONY_SERVICE);
    }

    private TelephonyManager getTelephonyManager() {
        return tm;
    }

    public static TelephonyManagerUtil getInstance(Context mContext) {
        if (telMUtil == null || telMUtil.getTelephonyManager() == null) {
            telMUtil = new TelephonyManagerUtil(mContext);
        }
        return telMUtil;
    }

    /*
     * 电话状态： 1.tm.CALL_STATE_IDLE=0 无活动 2.tm.CALL_STATE_RINGING=1 响铃
     * 3.tm.CALL_STATE_OFFHOOK=2 摘机
     */
    public int getCallState() {
        return tm.getCallState();//
    }

    /*
     * 电话方位：
     */
    public CellLocation getCellLocation() {
        return tm.getCellLocation();//
    }

    /*
     * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
     * available.
     */
    public String getDeviceId() {
        String deviceId = getDeviceSingleId();
        String mac = getMacaddress();
        String androidId = getAndroidId();
        StringBuffer sb = new StringBuffer();
        sb.append(deviceId);
        sb.append(SYMBOL);
        sb.append(mac);
        sb.append(SYMBOL);
        sb.append(androidId);
        return sb.toString();
    }

    private String getDeviceSingleId() {
        String deviceId;
        ConfigDataProvider configDataProvider = ConfigDataProvider.getInstance(appContext);

        if (tm != null) {
            deviceId = tm.getDeviceId();
            configDataProvider.addStringData(DEVICEID, deviceId);
        } else {
            deviceId = configDataProvider.getStringData(DEVICEID);
        }
        return deviceId;
    }

    private String getMacaddress() {
        ConfigDataProvider configDataProvider = ConfigDataProvider.getInstance(appContext);
        String mac = configDataProvider.getStringData(MACADDRESS);
        if (!TextUtils.isEmpty(mac)) {
            return mac;
        }
        WifiManager wifi = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();

        if (wifiInfo != null) {
            mac = wifiInfo.getMacAddress();
            configDataProvider.addStringData(MACADDRESS, mac);
        }
        return mac;
    }

    private String getAndroidId() {
        String androidId;
        ConfigDataProvider configDataProvider = ConfigDataProvider.getInstance(appContext);
        androidId = configDataProvider.getStringData(ANDROIDID);
        if (!TextUtils.isEmpty(androidId)) {
            return androidId;
        }
        try {
            androidId = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            configDataProvider.addStringData(ANDROIDID, androidId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(androidId)) {
            androidId = "";
        }
        return androidId;
    }


    /*
     * 获取ISO标准的国家码，即国际长途区号。 注意：仅当用户已在网络注册后有效。 在CDMA网络中结果也许不可靠。
     */
    public String getNetworkCountryIso() {
        ConfigDataProvider configDataProvider = ConfigDataProvider.getInstance(appContext);
        if (tm != null) {
            String contryCode = tm.getNetworkCountryIso();//
            configDataProvider.addStringData(COUNTRYCODE, contryCode);
            return contryCode;
        } else {
            return configDataProvider.getStringData(COUNTRYCODE);
        }

    }

    /*
     * MCC+MNC(mobile country code + mobile network code) 注意：仅当用户已在网络注册时有效。
     * 在CDMA网络中结果也许不可靠。
     */
    public String getNetworkOperator() {
        if (tm == null)
            return "";
        return tm.getNetworkOperator();//
    }

    /*
     * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS GPRS网络 1
     * NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3 NETWORK_TYPE_HSDPA
     * HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9 NETWORK_TYPE_HSPA HSPA网络 10
     * NETWORK_TYPE_CDMA CDMA网络,IS95A 或 IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO网络,
     * revision 0. 5 NETWORK_TYPE_EVDO_A EVDO网络, revision A. 6
     * NETWORK_TYPE_1xRTT 1xRTT网络 7
     */
    public int getNetworkType() {
        if (tm == null)
            return 0;
        return tm.getNetworkType();//
    }

    /*
     * 手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA CDMA信号
     */
    public int getPhoneType() {
        if (tm == null) {
            return -1;
        }
        return tm.getPhoneType();//
    }

    /*
     * Returns the ISO country code equivalent for the SIM provider's country
     * code. 获取ISO国家码，相当于提供SIM卡的国家码。
     */
    public String getSimCountryIso() {
        if (tm == null)
            return "";
        return tm.getSimCountryIso();//
    }

    /*
     * Returns the MCC+MNC (mobile country code + mobile network code) of the
     * provider of the SIM. 5 or 6 decimal digits.
     * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字. SIM卡的状态必须是
     * SIM_STATE_READY(使用getSimState()判断).
     */
    public String getSimOperator() {
        if (tm == null) {
            return "";
        }
        return tm.getSimOperator();//
    }

    /*
     * 服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
     */
    public String getSimOperatorName() {
        if (tm == null)
            return "";
        return tm.getSimOperatorName();
    }

    /*
     * SIM卡的序列号： 需要权限：READ_PHONE_STATE
     */
    public String getSimSerialNumber() {
        return tm.getSimSerialNumber();//
    }

    /*
     * SIM的状态信息： SIM_STATE_UNKNOWN 未知状态 0 SIM_STATE_ABSENT 没插卡 1
     * SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2 SIM_STATE_PUK_REQUIRED
     * 锁定状态，需要用户的PUK码解锁 3 SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4
     * SIM_STATE_READY 就绪状态 5
     */
    public int getSimState() {
        if (tm == null) {
            return 0;
        }
        return tm.getSimState();
    }

    /*
     * 唯一的用户ID： 例如：IMSI(国际移动用户识别码) for a GSM phone. 需要权限：READ_PHONE_STATE
     */
    public String getSubscriberId(Context mc) {
        ConfigDataProvider configDataProvider = ConfigDataProvider.getInstance(mc);
        if (tm == null) {
            return configDataProvider.getStringData(SUBSCRIBERID);
        } else {
            String subscriberId = tm.getSubscriberId();
            // IMSI在 SAMSUNG 上可能为空后造成一直重启。
            subscriberId = subscriberId == null ? "" : subscriberId;
            configDataProvider.addStringData(SUBSCRIBERID, subscriberId);

            return subscriberId;
        }
    }

    /*
     * 取得和语音邮件相关的标签，即为识别符 需要权限：READ_PHONE_STATE
     */
    public String getVoiceMailAlphaTag() {
        return tm.getVoiceMailAlphaTag();//
    }

    /*
     * 获取语音邮件号码： 需要权限：READ_PHONE_STATE
     */
    public String getVoiceMailNumber() {
        return tm.getVoiceMailNumber();//
    }

    /*
     * ICC卡是否存在
     */
    public boolean hasIccCard() {
        return tm.hasIccCard();//
    }

    /*
     * 是否漫游: (在GSM用途下)
     */
    public boolean isNetworkRoaming() {
        if (tm == null)
            return false;
        return tm.isNetworkRoaming();//
    }

    /*
    方法不再使用
    public String getserializeNo() {
        String sqareMac = "";
        if (appContext == null)
            return sqareMac;
        if (TextUtils.isEmpty(sqareMac)) {
            WifiManager wifi = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
            sqareMac = wifi.getConnectionInfo().getMacAddress();
            if (!TextUtils.isEmpty(sqareMac)) {
                ConfigDataProvider.getInstance(appContext).setSpareMacData(sqareMac);
            } else {
                try {
                    sqareMac = System.getProperty("ro.hardware.cpuid");
                    if (TextUtils.isEmpty(sqareMac)) {
                        sqareMac = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }
        return sqareMac;
    }
    */
}
