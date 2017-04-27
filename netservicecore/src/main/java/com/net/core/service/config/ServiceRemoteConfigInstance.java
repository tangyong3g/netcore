package com.net.core.service.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.net.core.BuildConfig;
import com.net.core.unit.AESUtil;
import com.net.core.unit.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tyler.tang on 2017/4/13.
 * <p>
 * <p>
 * {@link ServiceRemoteConfigInstance} 主要用来获取远程服务器配置内容
 * <p>
 * 使用方法:
 * <p>
 * 1 : ${@link ServiceRemoteConfigInstance#getInstance(Context)} 实始化实例
 * 2:  ${@link ServiceRemoteConfigInstance#setDefaultValue(String)} 设置默认值
 * 3:   ${@link ServiceRemoteConfigInstance#getString(String)} 获取应的值
 * <p>
 * <p>
 * * <blockquote>
 * <pre>
 *               ServiceRemoteConfigInstance.getInstance(getApplicationContext()).setDefaultValue("default_value.xml");
 *                 ServiceRemoteConfigInstance.getInstance(getApplicationContext()).getString("joy_v2_is_show_hot_game");
 *      </pre>
 * </blockquote>
 * <p>
 * <p>
 * <p>
 * 注意事项
 * 数据缓存默认是 24小时，意味着服务器配置的数据，在客户端生效的时间会在 0-24小时。如果值更新的时间期望更快，那么可以调用如下方法调整
 * <p>
 * ${@link ServiceRemoteConfigInstance#setCacheTime(long)}
 * <p>
 * <p>
 * 如果对数据的时效性要求比较高【一定要获取到当前服务器配置的数据】
 * ${@link ServiceRemoteConfigInstance#fetchValue(com.net.core.service.config.Callback)}
 * 那么数据结果会在CallBack回调接口中，以Map的形式得到
 * <p>
 * <p>
 * <p>
 * style looks like the following:
 * <p><hr><blockquote><pre>
 *     ServiceRemoteConfigInstance.getInstance(getApplicationContext()).fetchValue(new Callback() {
 *              @Override
 *               public void onFailure(Call call, IOException e) {
 *               }
 * <p>
 *              @Override
 *               public void onResponse(Call call, Map<String, String> values) throws IOException {
 *                  //return values store by Map<String,String>
 *                  String rs = showRemoteValue(values);
 *               }
 *            });
 * <p>
 * <p>
 * </pre></blockquote><hr>
 * <p>
 * </p>
 */
public class ServiceRemoteConfigInstance {

    //单例
    private static ServiceRemoteConfigInstance mInstance;
    //存储默认值
    private Map<String, String> mDefaultValue;
    //存储从服务器得到的数据
    private Map<String, String> mServerValue;
    //上下文
    private Context mContext;
    //数据解密 Key值
    private static final String AES_KEY = "cqgf971sp394@!#0";
    //日志 Tag
    private static final String TAG = "ServiceConfig";
    //数据缓存的时间 默认12个小时
    private long mCacheTime = 3600 * 1000 * 12;
    //服务器接口URL
    private static final String CONFIGURATION_URL = BuildConfig.configuration;
    //本地数据是否超时
    private boolean mLocalValueTimeOut = false;
    //最后一次fetch时间
    private long mLastFetchTime = 0L;
    //存储用的key
    private static final String LAST_FETCHTIME_KEY = "lastFetchTime";
    //存储默认值文件默认名称
    public static final String DEFAULT_FILENAME = "default_value.xml";

    //对外公开的接口
    public static ServiceRemoteConfigInstance getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServiceRemoteConfigInstance(context);
        }
        return mInstance;
    }


    /**
     * 设置缓存时间
     *
     * @param cacheTime
     */
    public void setCacheTime(long cacheTime) {
        mCacheTime = cacheTime;
    }

    /**
     * 初始化  ServiceRemoteConfigInstance
     */
    private ServiceRemoteConfigInstance(Context context) {

        mContext = context;
        //初始化存储默认值的Map
        if (mDefaultValue == null) {
            mDefaultValue = new HashMap<String, String>();
        }
        if (mServerValue == null) {
            mServerValue = new HashMap<String, String>();
        }
        mLastFetchTime = (Long) SPUtils.get(mContext, LAST_FETCHTIME_KEY, 0L);

        try {
            setDefaultValue(DEFAULT_FILENAME);
        } catch (XmlPullParserException xmlEx) {
            xmlEx.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "init ServiceRemoteConfigInstance finish");
            Log.i(TAG, "last fetch time is :\t" + mLastFetchTime);
        }
    }

    /**
     * 读取默认值到本地,对于应用只会初台化一次,
     *
     * @param fileName
     * @throws XmlPullParserException
     * @throws IOException
     */
    public void setDefaultValue(String fileName) throws XmlPullParserException, IOException {

        if (mDefaultValue != null && mDefaultValue.size() > 0) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "remote default has initialized :\n" + showRemoteDefaultValue());
            }
            return;
        } else {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "remote default init start");
            }
        }

        ServiceRemoteDefaultValue defaultValue = new ServiceRemoteDefaultValue();
        List defValueList = defaultValue.setDefaultValueFromFile(fileName, 0, mContext);

        if (defValueList != null && defValueList.size() > 0) {
            for (Object temp : defValueList) {
                ServiceRemoteDefaultValue.Entry entry = (ServiceRemoteDefaultValue.Entry) temp;
                mDefaultValue.put(entry.mKey, entry.mValue);
            }
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "init Service Config finish!");
            Log.i(TAG, "remote default value:\n" + showRemoteDefaultValue());
        }
    }

    /**
     * 根据Key值获取对应的值
     * <p>
     * TODO 实时性的问题还需要解决 ,这样设计有缺陷，第一次使用的是服务器上一次值 ，那么到下一次才能够有得到
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        String result = null;

        //从服务器缓存得到 没有超时的时候
        if (isTimeOut()) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "remote value is timeout fetch value !");
            }
            // 每次get的时候，如果数据超时会重新请求，但是会为下一次使用。当次使用的是上一次的数值,如果没有网络，获取失败会是默认值
            fetchValue();
        }

        //优先服务器数据，如果没有使用默认数据
        if (mServerValue != null) {
            result = mServerValue.get(key);

            if (!TextUtils.isEmpty(result)) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "the key " + key + "of  value " + result + " is from server local !");
                }
                return result;
            }
        }

        result = mDefaultValue.get(key);

        if (TextUtils.isEmpty(result)) {
            Log.i(TAG, "the key " + key + "has not initialized in file " + DEFAULT_FILENAME);
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "the key " + key + "of  value " + result + " is from default !");
        }
        return result;


    }

    private String getReturnDataFromJson(String returnData, String key) {
        String result = null;
        if (TextUtils.isEmpty(returnData)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(returnData);
            result = jsonObject.getString(key);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return result;
    }


    /**
     * 打印调试信息
     *
     * @return
     */
    private String showRemoteDefaultValue() {
        StringBuffer sb = new StringBuffer();

        if (mDefaultValue != null && mDefaultValue.size() > 0) {
            for (String key : mDefaultValue.keySet()) {
                sb.append(key);
                sb.append(":\t");
                sb.append(mDefaultValue.get(key));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 获取所有服务器的配置数据【非实时，会考虑数据缓存】
     */
    public void fetchValue() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "url is " + BuildConfig.configuration);
        }
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建Request 跟据URL  Request的Build可以创建Request对象
        final Request request = new Request.Builder().url(BuildConfig.configuration).build();
        //创建Call 对象，实际是上执行任务
        Call call = okHttpClient.newCall(request);

        //把请求加入到队列之中
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "fetch data failure!！" + BuildConfig.configuration);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "fetch data success!");
                }
                //处理数据 【解密以及获取 configuration 】
                String value = resolveServerData(response);
                //把服务器的数据存储到本地来 mem
                Map<String, String> values = storeJsonDataToLocal(value);
                long currentTime = System.currentTimeMillis();
                //存储获取时间
                SPUtils.put(mContext, LAST_FETCHTIME_KEY, currentTime);
                mLastFetchTime = currentTime;
            }
        });
    }

    /**
     * 獲取服务器数据，【实时，非缓存】
     *
     * @param callback 回调接口，返回服务器数据
     */
    public void fetchValue(final com.net.core.service.config.Callback callback) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "url is " + BuildConfig.configuration);
        }
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建Request 跟据URL  Request的Build可以创建Request对象
        final Request request = new Request.Builder().url(BuildConfig.configuration).build();
        //创建Call 对象，实际是上执行任务
        Call call = okHttpClient.newCall(request);

        //把请求加入到队列之中
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "fetch data failure!！" + BuildConfig.configuration);
                }
                if (callback != null) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "fetch data success!");
                }
                //处理数据 【解密以及获取 configuration 】
                String value = resolveServerData(response);
                //把服务器的数据存储到本地来 mem
                Map<String, String> values = storeJsonDataToLocal(value);
                long currentTime = System.currentTimeMillis();
                //存储获取时间
                SPUtils.put(mContext, LAST_FETCHTIME_KEY, currentTime);
                mLastFetchTime = currentTime;

                if (callback != null) {
                    callback.onResponse(call, values);
                }
            }
        });
    }

    /**
     * 判断数据是否超时
     *
     * @return boolean
     */
    private boolean isTimeOut() {

        boolean result = false;

        //没有初始化，在软件生命周期还没有fetch,所以不存在超时的概念。
        if (mLastFetchTime == 0) {
            return true;
        }
        result = (System.currentTimeMillis() - mLastFetchTime) > mCacheTime;

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "locadata is time out :\t" + result);
        }
        return result;
    }

    /**
     * 处理服务器数据 【解密以及获取 configration值】
     *
     * @return
     */
    private String resolveServerData(Response response) throws IOException {

        String bodyStr = response.body().string();

        String dataStr = getReturnDataFromJson(bodyStr, "data");
        String decodeStr = null;
        String value = null;

        try {
            decodeStr = AESUtil.decryptUncompress(dataStr, AES_KEY, false);
            JSONObject json = new JSONObject(decodeStr);
            value = json.getString("configuration");
        } catch (JSONException jx) {
            jx.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "响应的原始数据:+\n" + bodyStr);
            Log.i(TAG, "得到Data数据:\n" + dataStr);
            Log.i(TAG, "解密后的数据:\n" + decodeStr);
            Log.i(TAG, "value的值:\n" + value);
        }

        return value;
    }


    /**
     * 把服务器的数据存储到本地来
     *
     * @param jsonArrayDataStr
     * @return
     */
    private Map<String, String> storeJsonDataToLocal(String jsonArrayDataStr) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "start storeJsonDataToLocal\n");
        }

        JSONArray jsonArray = null;

        //转化json数据成 JsonArray
        if (!TextUtils.isEmpty(jsonArrayDataStr)) {
            try {
                jsonArray = new JSONArray(jsonArrayDataStr);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
                Log.i(TAG, " server json Data formate error !");
            }
        }

        /* 把数据存储到 Map中去*/

        if (jsonArray != null && jsonArray.length() > 0) {
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    Object obj = jsonArray.get(i);
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    String keyTemp = jsonObject.getString("key");
                    String valueTemp = jsonObject.getString("value");
                    mServerValue.put(keyTemp, valueTemp);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "存储Json到本地数据出现异常");
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "storeJsonDataToLocal :\t" + showRemoteValue());
        }
        return mServerValue;
    }

    /**
     * 查看从服务器fetch下来的内容，是否正常 调试用
     *
     * @return
     */
    private String showRemoteValue() {
        StringBuffer sb = new StringBuffer("");

        if (mServerValue != null && mServerValue.size() > 0) {
            for (String keyTemp : mServerValue.keySet()) {
                sb.append(keyTemp);
                sb.append(":\t");
                sb.append(mServerValue.get(keyTemp));
                sb.append("\n");
            }
        }
        return sb.toString();
    }


}
