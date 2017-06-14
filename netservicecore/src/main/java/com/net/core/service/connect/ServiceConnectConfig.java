package com.net.core.service.connect;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.net.core.BuildConfig;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;

/**
 * 服务器请求配置信息
 * <p>
 * 整个应用对于每一个server请求有缓存需要的会生成，有仅只有一个 Config对像存储在存储在
 * ${@link ServiceConnectInstance mCacheFetchData 中
 * </p>
 */
public class ServiceConnectConfig implements Callback, Serializable {

    //请求连接
    public String url;
    //对当前请求设置的缓存时间 单位 毫秒
    public long cacheTime = 24 * 3600 * 1000;
    //上一次从服务器fetch的时间。
    public long mLastFetchTime;
    //服务器响应数据
    private String responseData;
    private static final String TAG = "ServiceConnectConfig";
    //请求链接和参数组合而成的字符串，用来作为一次请求缓存数据的KEY。 如果，同一个链接，但是两次请求参数不一样，那么会构建不同的 ServiceConnectConfig的对象。
    public String urlParamsKey;

    public ServiceConnectConfig(String url, long cacheTime) {
        this.url = url;
        this.cacheTime = cacheTime;
    }

    /**
     * 构造函数
     *
     * @param url          请求的url
     * @param cacheTime    缓存时间
     * @param urlParamsKey 请求参数和url生成的Key
     */
    public ServiceConnectConfig(String url, long cacheTime, String urlParamsKey) {
        this.url = url;
        this.urlParamsKey = urlParamsKey;
        this.cacheTime = cacheTime;
    }

    public void fetchValueWithURLSingle(final Callback callback, String url, Map<String, String> params, long cacheTime, Context context) throws ServiceConnectException, IOException {
        this.cacheTime = cacheTime;

        // 没有超时，本地数据
        if (!isTimeOut()) {
            if (callback == null) {
                throw new ServiceConnectException("callback is null");
            }
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "get data from cache!");
            }
            callback.onResponse(null, responseData);
            return;
        }
        //超时重新获取数据
        else {
            ArrayList<Callback> callbacks = new ArrayList<>();
            //TODO call back的问题。
            callbacks.add(this);
            callbacks.add(callback);
            //TODO fetchvalueWithUrl
            ServiceConnectInstance.getInstance(context).fetchValueWithURL(callbacks, url, params);

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "time out  get data from server !");
            }
        }
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
        result = (System.currentTimeMillis() - mLastFetchTime) > cacheTime;

        //TODO 这里加上标准的时间会更好
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "url is :\t" + url + "\t" + "  it is time out :\t" + result);
        }
        return result;
    }

    public void onFailure(Call call, IOException e) {

    }

    public void onResponse(Call call, String result) throws IOException {
        //TODO 这里有一个风格，数据格式的校验,不然会出现异常数据返回到客户端
        if (!TextUtils.isEmpty(result)) {
            this.responseData = result;
            this.mLastFetchTime = System.currentTimeMillis();
        }
    }

    /**
     * @return
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("{");
        sb.append("url:");
        sb.append(url);
        sb.append(",");
        sb.append("\t");

        sb.append("cacheTime:");
        sb.append(cacheTime);
        sb.append(",");
        sb.append("\t");

        sb.append("mLastFetchTime:");
        sb.append(mLastFetchTime);
        sb.append(",");
        sb.append("\t");

        sb.append("urlParamsKey:");
        sb.append(urlParamsKey);
        sb.append(",");
        sb.append("\t");
        sb.append("}");


        return sb.toString();
    }
}
