package com.net.core.service.config;

import android.content.Context;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.net.core.BuildConfig;
import com.net.core.unit.AESUtil;
import com.net.core.unit.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private long mCacheTime = 3600 * 1000 * 24;
    //服务器接口URL
    private static final String CONFIGURATION_URL = BuildConfig.configuration;
    //本地数据是否超时
    private boolean mLocalValueTimeOut = false;
    //最后一次fetch时间
    private long mLastFetchTime = 0L;
    //存储用的key
    private static final String LAST_FETCHTIME_KEY = "lastFetchTime";
    //存储sp文件里面的值的key
    private static final String LAST_FETCH_REMOTE_VALUE_KEY = "lastFetchRemoteValueKey";
    //存储默认值文件默认名称
    public static final String DEFAULT_FILENAME = "default_value.xml";
    //是否支持 firebase Config 如果是支持的话，那么会获取Firebase的值
    private boolean isSupportFirebase = false;

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
        //恢复数据
        recoveryValueFromLocal();

        //从文件中恢复数据没有，或者不成功
        if (mServerValue.size() == 0) {
            //从文件中获取原来存储的值
            try {
                setDefaultValue(DEFAULT_FILENAME);
            } catch (XmlPullParserException xmlEx) {
                xmlEx.printStackTrace();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "init ServiceRemoteConfigInstance finish");
            Log.i(TAG, "last fetch time is :\t" + mLastFetchTime);
        }

        //处理firebase
        if (isSupportFirebase) {
            Task task = FirebaseRemoteConfig.getInstance().fetch();
            task.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        FirebaseRemoteConfig.getInstance().activateFetched();
                    }
                }
            });
        }
    }

    /**
     * 是否支持firebase
     * <p>
     * TODO 把这个监听的接口放入这个里面才行，让外面的实现可以处理
     *
     * @param isSupportFirebase
     * @return
     */
    public ServiceRemoteConfigInstance setIsSupportFireBase(boolean supportFireBase, int defaultRes, final OnFirebaseFectchComplete listener) {
        isSupportFirebase = supportFireBase;
        //处理firebase
        if (supportFireBase) {
            Task task = FirebaseRemoteConfig.getInstance().fetch();
            task.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "firebase fetch value success!");
                        }
                        FirebaseRemoteConfig.getInstance().activateFetched();
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "firebase fetch value fail!");
                        }
                    }
                    listener.onComplete(task);
                }
            });
        }
        return this;
    }


    /**
     *
     */
    public interface OnFirebaseFectchComplete {
        void onComplete(@NonNull Task task);
    }


    /**
     * 从sp文件中恢复配置数据
     */
    private void recoveryValueFromLocal() {

        String lastStore = (String) SPUtils.get(mContext, LAST_FETCH_REMOTE_VALUE_KEY, "");

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "recovery value from local start , recovery data is :" + lastStore);
        }

        if (!TextUtils.isEmpty(lastStore)) {
            storeJsonDataToLocal(lastStore);
        }

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "recovery value from local success!");
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
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        String result = null;

        if (isSupportFirebase) {
            //TODO 这里没有办法确保一定fetch过。
            result = FirebaseRemoteConfig.getInstance().getString(key);

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "get from firebase the key is:" + key + "\tthe value is  " + result);

            }
            return result;
        }

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
        Log.d("wxj", "fetchValue: " + BuildConfig.configuration);
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
            public void onResponse(Call call, final Response response) throws IOException {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "fetch data success!");
                }

                /**
                 * 解密数据，以及重新存储用时 约为 30-50ms ，又在主线程上面会造成skip frames 的问题
                 */

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        //处理数据 【解密以及获取 configuration 】
                        String value = null;
                        try {
                            value = resolveServerData(response);
                            if (!TextUtils.isEmpty(value)) {
                                //把服务器的数据存储到本地来 mem
                                Map<String, String> values = storeJsonDataToLocal(value);
                                //把解密后的数据存储
                                long currentTime = System.currentTimeMillis();
                                //存储获取时间
                                SPUtils.put(mContext, LAST_FETCHTIME_KEY, currentTime);
                                SPUtils.put(mContext, LAST_FETCH_REMOTE_VALUE_KEY, value);
                            }
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    }
                };
                thread.start();
                mLastFetchTime = System.currentTimeMillis();
            }
        });
    }

    /**
     * 獲取服务器数据，【实时，非缓存】
     *
     * @param callback 回调接口，返回服务器数据
     */
    public void fetchValue(final com.net.core.service.config.Callback callback) {
        final String url = BuildConfig.configuration;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "url is " + BuildConfig.configuration);
        }
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建Request 跟据URL  Request的Build可以创建Request对象
        final Request request = new Request.Builder().url(url).build();
        //创建Call 对象，实际是上执行任务
        Call call = okHttpClient.newCall(request);

        //把请求加入到队列之中
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "fetch data failure!！" + url);
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

        SimpleDateFormat sm = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String currentStr = sm.format(new Date());

        Date lastFetchTime = new Date(mLastFetchTime);
        String lastFetchTimeStr = sm.format(lastFetchTime);


        result = (System.currentTimeMillis() - mLastFetchTime) > mCacheTime;

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "locadata is time out :\t" + result + "\t current time is:\t" + currentStr + "\t last fechTime is :\t" + lastFetchTimeStr);
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
