package com.sny.netcoredemo.connect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.net.core.service.connect.Callback;
import com.net.core.service.connect.ServiceConnectInstance;
import com.sny.netcoredemo.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class HotWActivity extends AppCompatActivity {

    //数据解密 Key值
    private static final String AES_KEY = "cqgf971sp394@!#0";
    TextView mTxResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_w);

        initComponent();
    }

    private void initComponent() {

        /**
         * 实时获取
         */
        findViewById(R.id.btn_fetch_hot_online).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchHotOnline();
            }
        });

        mTxResult = (TextView) findViewById(R.id.tx_hot_result);

    }


    /**
     * 在线获取
     */
    private void fetchHotOnline() {

//        final String url = "http://trends.mobitech-search.xyz/v1/trends/TCLLN8675D348";
        final String url_get = "http://trends.mobitech-search.xyz/v1/trends/TCLLN8675D348?user_id=MzU4MDk5MDYwNjI2NzAyIzhjOjk5OmU2OjQzOjFiOjdmIzNiOTMyMzgxYTVjOWUzN2Y&c=zh_CN";
        final Map<String, String> params = new HashMap<String, String>();

//        params.put("user_id", "MzU4MDk5MDYwNjI2NzAyIzhjOjk5OmU2OjQzOjFiOjdmIzNiOTMyMzgxYTVjOWUzN2Y");
//        params.put("c", "zh_CN");
//        params.put("num", "4");
//        params.put("country", "all_cou");
//        params.put("version", "all_ver");
//        params.put("language", "all_lan");


        ServiceConnectInstance.getInstance(getApplicationContext()).fetchValueWithURL(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, String result) throws IOException {

                try {
//                    result = getReturnDataFromJson(result, "data");
//                    String rs = AESUtil.decryptUncompress(result, AES_KEY, false);
                    displayResult(result);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, url_get, params);

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
}
