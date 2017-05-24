package com.net.core.service.connect;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.net.core.BuildConfig;
import com.net.core.unit.AESUtil;
import com.net.core.unit.Base64Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
 * <p>
 * 以OkHttp为基础进入网络请求，结果以回调的形式返回
 * <p>
 * * 1 : ${@link ServiceConnectInstance#fetchValueWithURL(Callback, String, Map)}
 * <p>
 * 以OkHttp为基础进入网络请求，结果以CallBack形式返回，同学会缓存在本地。
 * ${@link ServiceConnectInstance#fetchValueWithURLWithCa(Callback, String, Map, long)}
 * <p>
 * <p>
 * <p>
 */
public class ServiceConnectInstance {

    //volatile防止编译器修改指令次序
    private volatile static ServiceConnectInstance singleton;
    // TAG
    private static final String TAG = "ServiceConnect";
    //缓存服务器请求数据,同时封装了请求信息
    private ArrayList<ServiceConnectConfig> mCacheFetchData = null;
    //上下文
    private Context mContext;
    //序列化文件名
    private static final String serilizableFile = "serviceConnect.ca";

    //私有构造函数
    private ServiceConnectInstance(Context context) {

        //全局上下文，以防应用拿住Activity引用，导致内存泄露
        if (context != null) {
            mContext = context.getApplicationContext();
        }

        //从序列化文件中读出信息
        mCacheFetchData = readObj();

        if (mCacheFetchData == null) {
            mCacheFetchData = new ArrayList<ServiceConnectConfig>();
        }

    }

    /**
     * @return 获取单例
     */
    public static ServiceConnectInstance getInstance(Context context) {
        if (singleton == null) {
            synchronized (ServiceConnectInstance.class) {
                if (singleton == null) {
                    singleton = new ServiceConnectInstance(context);
                }
            }
        }
        return singleton;
    }


    /**
     * 获取服务器数据，并且会处理缓存逻辑
     *
     * @param callback 回调函数
     * @param url      请求URl
     * @param params   参数的Key Map
     */
    public void fetchValueWithURLWithCa(final Callback callback, String url, Map<String, String> params, long cacheTime) throws IOException, ServiceConnectException {

        //获取是否有本地数据
        ServiceConnectConfig config = getConnectConfigFromCache(url);

        if (config != null) {
            config.fetchValueWithURLSingle(callback, url, params, cacheTime, mContext);

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "load cache data!");
            }

        } else {
            ServiceConnectConfig configCp = new ServiceConnectConfig(url, cacheTime);
            configCp.fetchValueWithURLSingle(callback, url, params, cacheTime, mContext);

            mCacheFetchData.add(configCp);
            asnySerilizable(configCp);

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "local has not data ,get  from server and Serializable to local ");
            }
        }

        if (BuildConfig.DEBUG) {
            showCacheState();
        }
    }

    /**
     * 异步处理序列化信息
     */
    private void asnySerilizable(final ServiceConnectConfig config) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                writeObj(mCacheFetchData);
            }
        };
        thread.start();
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
                Log.i(TAG, "返回的原始数据是:\t" + bodyStr);
                showReturnData(bodyStr);

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
     * @param result
     * @return
     */
    private String showReturnData(String result) {
        try {
            JSONObject object = new JSONObject(result);
            String status = object.getString("status");
            if ("0".equals(status)) {
                String data = object.getString("data");
                byte[] base64Byte = Base64Utils.decodeBase64(data);
                byte[] decryptByte = AESUtil.decrypt2(base64Byte, AESUtil.AES_DECRYPT_KEY);

                String mAppString = new String(decryptByte);
                Log.i(TAG, "返回解密数据是:\t" + mAppString);

            }
        } catch (JSONException js) {
            js.printStackTrace();
        }

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


    /**
     * 显示cache内容状态
     */
    private void showCacheState() {

        if (mCacheFetchData != null && mCacheFetchData.size() > 0) {
            Log.i(TAG, "Cache data size :\t" + mCacheFetchData.size());
            Log.i(TAG, "Cache data is :\t" + mCacheFetchData.size());

            for (ServiceConnectConfig config : mCacheFetchData) {
                Log.i(TAG, config.toString());
            }
        }
    }


    /**
     * 序列化对象，整个List序列化
     *
     * @param configs
     */
    private void writeObj(ArrayList<ServiceConnectConfig> configs) {

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {

            File file = getInternalStorageFile();

            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(configs);

            Log.i(TAG, "serilizable success");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();

        } finally {
            try {
                oos.flush();
                oos.close();
                fos.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    /**
     * 读取序列化文件
     *
     * @return ArrayList<ServiceConnectConfig>
     */
    private ArrayList<ServiceConnectConfig> readObj() {

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "read Serializable start!");
        }

        File file = getInternalStorageFile();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        ArrayList<ServiceConnectConfig> configList = null;
        try {

            //对象反序列化过程
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);

            configList = (ArrayList<ServiceConnectConfig>) ois.readObject();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ca) {
            ca.printStackTrace();
        } catch (Exception eofEx) {
            eofEx.printStackTrace();
        } finally {
            try {
                ois.close();
                fis.close();
            } catch (IOException exi) {
                exi.printStackTrace();
            } catch (NullPointerException nullEx) {
                nullEx.printStackTrace();
            }
        }

        if (BuildConfig.DEBUG && configList != null && configList.size() > 0) {
            Log.i(TAG, "read Serializable success!" + configList.size());
        }

        return configList;
    }


    /**
     * 创建存储序列化文件
     *
     * @param
     */
    private File getInternalStorageFile() {

        File folder = mContext.getFilesDir();
        File file = new File(folder.getAbsolutePath() + File.separator + serilizableFile);

        boolean success = false;
        if (!file.exists()) {
            try {
                success = file.createNewFile();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        if (success) {
            Log.i(TAG, file.getAbsolutePath() + "create success!");
        }

        return file;
    }

}
