package com.net.core.service.connect;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.net.core.BuildConfig;
import com.net.core.unit.SPUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tyler.tang on 2017/5/5.
 * <p>
 * 以OkHttp为基础进入网络请求，结果以回调的形式返回
 * <p>
 * * 1 : ${@link ServiceConnect#fetchValueWithURL(Callback, String, Map)}
 * <p>
 * <p>
 * <p>
 * TODO fetchTime没有处理好，这里对像需要序列化和反序列化.
 */
public class ServiceConnect {

    //volatile防止编译器修改指令次序
    private volatile static ServiceConnect singleton;
    // TAG
    private static final String TAG = "ServiceConnect";
    //缓存服务器请求数据,同时封装了请求信息
    private List<ServiceConnectConfig> mCacheFetchData = null;
    //上下文
    private Context mContext;


    //私有构造函数
    private ServiceConnect(Context context) {
        if (mCacheFetchData == null) {
            mCacheFetchData = new ArrayList<ServiceConnectConfig>();
        }
        //全局上下文，以防应用拿住Activity引用，导致内存泄露
        if (context != null) {
            mContext = context.getApplicationContext();
        }
    }

    /**
     * @return
     */
    public static ServiceConnect getInstance(Context context) {
        if (singleton == null) {
            synchronized (ServiceConnect.class) {
                if (singleton == null) {
                    singleton = new ServiceConnect(context);
                }
            }
        }
        return singleton;
    }


    /**
     *
     * 获取服务器数据，会处理缓存逻辑
     *
     * @param callback 回调函数
     * @param url      请求URl
     * @param params   参数的Key Map
     */
    public void fetchValueWithURLWithCa(final Callback callback, String url, Map<String, String> params, long cacheTime) throws IOException, ServiceConnectException {

        ServiceConnectConfig config = getConnectConfigFromCache(url);

        if (config != null) {
            config.fetchValueWithURLSingle(callback, url, params, cacheTime);
        } else {
            ServiceConnectConfig configCp = new ServiceConnectConfig(url, cacheTime);
            configCp.fetchValueWithURLSingle(callback, url, params, cacheTime);

            mCacheFetchData.add(configCp);
        }

        if (BuildConfig.DEBUG) {
            showCacheState();
        }

    }


    /**
     * 获取用户请求，支持多个回调
     *
     * @param callbacks 集合 回调函数
     * @param url       请求URl
     * @param params    参数的Key Map
     */
    public void fetchValueWithURL(final List<Callback> callbacks, String url, Map<String, String> params) {

        OkHttpClient client = new OkHttpClient();
        //构建带参数的请求BODY
        MultipartBody body = null;
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();

        if (params != null && params.size() > 0) {
            for (String keyTemp : params.keySet()) {
                String value = params.get(keyTemp);
                multipartBodyBuilder.addPart(MultipartBody.Part.createFormData(keyTemp, value));
            }
            body = multipartBodyBuilder.build();
        }
        Request request = null;
        //参数和URL构成了 Request
        Request.Builder builder = new Request.Builder().url(url);
        if (body != null) {
            request = builder.post(body).build();
        } else {
            request = builder.build();
        }

        //用Client创建Call，call 和Request形成请求
        Call call = client.newCall(request);

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "resolveServerConfig\t" + "onFailure");
                if (callbacks != null) {
                    for (Callback callback : callbacks) {
                        callback.onFailure(call, e);
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bodyStr = response.body().string();
                Log.i(TAG, "返回的数据是:\t" + bodyStr);

                if (callbacks != null && !TextUtils.isEmpty(bodyStr)) {
                    try {
                        for (Callback callBack : callbacks) {
                            callBack.onResponse(call, bodyStr);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (response != null && response.body() != null) {
                            response.body().close();
                        }
                    }
                }
            }
        });
    }


    /**
     * <p>
     * <p>
     * 获取服务器数据，
     *
     * @param callback 回调函数
     * @param url      请求URl
     * @param params   参数的Key Map
     */
    public void fetchValueWithURL(final Callback callback, String url, Map<String, String> params) {

        OkHttpClient client = new OkHttpClient();
        //构建带参数的请求BODY
        MultipartBody body = null;
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();

        if (params != null && params.size() > 0) {
            for (String keyTemp : params.keySet()) {
                String value = params.get(keyTemp);
                multipartBodyBuilder.addPart(MultipartBody.Part.createFormData(keyTemp, value));
            }
            body = multipartBodyBuilder.build();
        }
        Request request = null;
        //参数和URL构成了 Request
        Request.Builder builder = new Request.Builder().url(url);
        if (body != null) {
            request = builder.post(body).build();
        } else {
            request = builder.build();
        }

        //用Client创建Call，call 和Request形成请求
        Call call = client.newCall(request);

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "resolveServerConfig\t" + "onFailure");
                if (callback != null) {
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bodyStr = response.body().string();
                Log.i(TAG, "返回的数据是:\t" + bodyStr);
                if (callback != null && !TextUtils.isEmpty(bodyStr)) {
                    try {
                        callback.onResponse(call, bodyStr);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (response != null && response.body() != null) {
                            response.body().close();
                        }
                    }
                }
            }
        });
    }


    /**
     * 回调接口
     * <p>
     * 请求数据会在onRespose里面参数
     */
    public interface Callback {
        /**
         * Called when the request could not be executed due to cancellation, a connectivity problem or
         * timeout. Because networks can fail during an exchange, it is possible that the remote server
         * accepted the request before the failure.
         */
        void onFailure(Call call, IOException e);


        /**
         * Called when the HTTP response was successfully returned by the remote server. The callback may
         * proceed to read the response body with {@link Response#body}. The response is still live until
         * its response body is closed with {@code response.body().close()}. The recipient of the callback
         * may even consume the response body on another thread.
         * <p>
         * result 为响应后的结果，以String形式返回
         */
        void onResponse(Call call, String result) throws IOException;
    }


    /**
     * 服务器请求配置信息
     * <p>
     * 整个应用对于每一个server请求有需要的会生成有仅一个 Config对像存储在，url和请求是1-1的关系。
     * ${@link ServiceConnect mCacheFetchData 中
     * </p>
     */
    class ServiceConnectConfig implements Callback {

        //请求连接
        private String url;
        //对当前请求设置的缓存时间 单位 毫秒
        private long cacheTime = 24 * 3600 * 1000;
        //上一次从服务器fetch的时间。
        private long mLastFetchTime;
        //服务器响应数据
        private String responseData;

        public ServiceConnectConfig(String url, long cacheTime) {
            this.url = url;
            this.cacheTime = cacheTime;
        }

        public void fetchValueWithURLSingle(final Callback callback, String url, Map<String, String> params, long cacheTime) throws ServiceConnectException, IOException {
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
                callbacks.add(this);
                callbacks.add(callback);

                fetchValueWithURL(callbacks, url, params);
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

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "locadata is time out :\t" + result);
            }

            //TODO 这里加上标准的时间会更好
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "url is :\t" + url + "\t" + "  it is time out :\t" + result);
            }
            return result;
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, String result) throws IOException {
            //TODO 这里有一个风格，数据格式的校验
            if (!TextUtils.isEmpty(result)) {
                this.responseData = result;
                this.mLastFetchTime = System.currentTimeMillis();

                //支持化获取时间    获取时间需要初始化
                SPUtils.put(mContext, url, mLastFetchTime);
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
            sb.append("}");
            return sb.toString();
        }

    }


    /**
     * 添加服务配置信息到缓存
     *
     * @param connectConfig
     */
    private boolean addServiceConnectConfigToCache(ServiceConnectConfig connectConfig) throws ServiceConnectException {

        if (connectConfig == null) {
            return false;
        }

        String url = connectConfig.url;

        if (isExistServerConnectConfig(url)) {
            return false;
        }
        mCacheFetchData.add(connectConfig);
        return true;
    }


    /**
     * 缓存是否存在
     *
     * @return boolean
     */
    private boolean isExistServerConnectConfig(String url) throws ServiceConnectException {

        if (TextUtils.isEmpty(url)) {
            throw new ServiceConnectException("ulr is null  invalid !");
        }

        boolean result = false;

        for (ServiceConnectConfig temp : mCacheFetchData) {

            String tempUrl = temp.url;
            if (url.equals(tempUrl)) {
                return true;
            }
        }
        return result;
    }


    /**
     * 获取 ConnectConfig
     *
     * @param url
     * @return
     */
    private ServiceConnectConfig getConnectConfigFromCache(String url) {
        ServiceConnectConfig config = null;

        if (TextUtils.isEmpty(url)) {
            return config;
        }
        for (ServiceConnectConfig temp : mCacheFetchData) {
            if (url.equals(temp.url)) {
                config = temp;
                return config;
            }
        }
        return config;
    }


    /**
     * 测试数据
     */
    private static final String RETURN_DATA = "{\n" +
            "    \"status\": \"0\",\n" +
            "    \"msg\": \"Success\",\n" +
            "    \"data\": {\n" +
            "        \"list\": [\n" +
            "            {\n" +
            "                \"id\": \"id-1\",\n" +
            "                 \"name\":\"hi Launcher\",\n" +
            "                 \"packageName\":\"com.tcl.launcherpro\",\n" +
            "                \"icon\": \"http://tcl-icloudcdn.tclclouds.com/tlauncher/20161101/09/36/25/96a3fdf5624d4c5fbe81b12e0ce857f8.png\",\n" +
            "                \"url\": \"http:...\",\n" +
            "                \"timestamp\": 1481526650839\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"compress\": 0\n" +
            "}\n";


    private void showCacheState() {

        if (mCacheFetchData != null && mCacheFetchData.size() > 0) {
            Log.i(TAG, "Cache data size :\t" + mCacheFetchData.size());
            Log.i(TAG, "Cache data is :\t" + mCacheFetchData.size());

            for (ServiceConnectConfig config : mCacheFetchData) {
                Log.i(TAG, config.toString());
            }
        }
    }


}
