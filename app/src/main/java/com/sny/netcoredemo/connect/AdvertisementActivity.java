package com.sny.netcoredemo.connect;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.net.core.service.connect.Callback;
import com.net.core.service.connect.ServiceConnectException;
import com.net.core.service.connect.ServiceConnectInstance;
import com.net.core.unit.AESUtil;
import com.sny.netcoredemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class AdvertisementActivity extends Activity {
    //数据解密 Key值
    private static final String AES_KEY = "cqgf971sp394@!#0";
    TextView mTxResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement);

        initComponent();
    }

    private void initComponent() {

        /**
         * 实时获取
         */
        findViewById(R.id.btn_fetch_ads_online).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAdsOnline();
            }
        });

        /**
         * 考虑缓存
         */
        findViewById(R.id.btn_fetch_ads_ca).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAdsCa();
            }
        });

        mTxResult = (TextView) findViewById(R.id.tx_result);

    }


    /**
     * 在线获取
     */
    private void fetchAdsOnline() {

        final String url = "http://launcher-test.tclclouds.com/tlauncher-api/advertising/list";
        final Map<String, String> params = new HashMap<String, String>();

        params.put("inner_package_name", "com.tcl.launcherpro");
        params.put("posision", "1");
        params.put("num", "4");
        params.put("country", "all_cou");
        params.put("version", "all_ver");
        params.put("language", "all_lan");


        ServiceConnectInstance.getInstance(getApplicationContext()).fetchValueWithURL(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, String result) throws IOException {

                try {
                    result = getReturnDataFromJson(result, "data");
                    String rs = AESUtil.decryptUncompress(result, AES_KEY, false);
                    displayResult(rs);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, url, params);

    }


    /**
     * 缓存获取
     *
     * @return
     */
    private void fetchAdsCa() {

        final String url = "http://launcher-test.tclclouds.com/tlauncher-api/advertising/list";
        final Map<String, String> params = new HashMap<String, String>();

        params.put("inner_package_name", "com.tcl.launcherpro");
        params.put("posision", "1");
        params.put("num", "4");
        params.put("country", "all_cou");
        params.put("version", "all_ver");
        params.put("language", "all_lan");

        try {
            ServiceConnectInstance.getInstance(getApplicationContext()).fetchValueWithURLWithCa(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, String result) throws IOException {

                    try {
                        result = getReturnDataFromJson(result, "data");
                        String rs = AESUtil.decryptUncompress(result, AES_KEY, false);
                        displayResult(rs);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, url, params, 24 * 3600 * 1000);

        } catch (ServiceConnectException ex) {
            ex.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    /**
     * 显示结果
     *
     * @param result
     */
    private void displayResult(final String result) {

        mTxResult.post(new Runnable() {
            @Override
            public void run() {

                String original = mTxResult.getText().toString();

                StringBuffer sb = new StringBuffer("");

                sb.append(original);
                sb.append("\n");
                sb.append("------------------------");
                sb.append(result);

                mTxResult.setText(sb.toString());
            }
        });
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
}
