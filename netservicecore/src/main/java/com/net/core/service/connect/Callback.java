package com.net.core.service.connect;

import java.io.IOException;

import okhttp3.Call;

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
