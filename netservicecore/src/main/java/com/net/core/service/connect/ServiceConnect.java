package com.net.core.service.connect;

import android.text.TextUtils;
import android.util.Log;


import com.net.core.BuildConfig;

import java.io.IOException;
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
 * 。
 */
public class ServiceConnect {

    private static final String TAG = "ServiceConnect";

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

}
