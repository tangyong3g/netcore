package com.sny.netcoredemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.net.core.service.config.ServiceRemoteConfigInstance;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;

public class ServiceConfig extends Activity {

    Button mBtn;
    TextView mTx;
    EditText mETx;

    Button mBtnFetchAll;
    TextView mTxCaRs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_config);
        initComponent();
        //加载配置数据
        fetchAllCconfig();
    }


    private String fetchKeyValue() {

        ServiceRemoteConfigInstance instance = ServiceRemoteConfigInstance.getInstance(getApplicationContext());
        instance.getInstance(getApplicationContext()).fetchValue(new com.net.core.service.config.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Map<String, String> values) throws IOException {

                showFetchKeyValue(values);
            }
        });
        return null;
    }


    private void showFetchKeyValue(final Map<String, String> values) {

        mETx.post(new Runnable() {
            @Override
            public void run() {
                String key = mETx.getText().toString();
                Log.i("tyler.tang", "key is:" + key);
                String value = values.get(key);

                StringBuffer sb = new StringBuffer("");
                sb.append("the key is :\t" + key);
                sb.append("\t");
                sb.append("value is:\t" + value);

                if (mTx != null) {
                    mTx.setText(sb.toString());
                }
            }
        });

    }


    private void initComponent() {

        mBtn = (Button) findViewById(R.id.btn_fetch_key);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRemoteValue(null);
            }
        });

        mTx = (TextView) findViewById(R.id.tx_fetch_key);
        mETx = (EditText) findViewById(R.id.editText);


        findViewById(R.id.btn_fetch_key).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchKeyValue();
            }
        });

        mTxCaRs = (TextView) findViewById(R.id.tx_ca_rs);
        mBtnFetchAll = (Button) findViewById(R.id.btn_fetch_all);
        mBtnFetchAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRemoteValueFromCa();
            }
        });

    }


    private void fetchRemoteValueFromCa() {

        String key = mETx.getText().toString();
        String value = ServiceRemoteConfigInstance.getInstance(getApplicationContext()).getString(key.trim());

        StringBuffer sb = new StringBuffer("");

        sb.append("the key is :\t" + key);
        sb.append("\t");
        sb.append("value is:\t" + value);

        mTxCaRs.setText(sb.toString());

    }


    private void fetchAllCconfig() {
        setDefault();
        ServiceRemoteConfigInstance.getInstance(getApplicationContext()).fetchValue();
    }


    /**
     * 添加按钮
     *
     * @param view
     */
    public void fetchRemoteValue(View view) {

        try {
            ServiceRemoteConfigInstance.getInstance(getApplicationContext()).setDefaultValue("default_value.xml");
        } catch (XmlPullParserException xmlEx) {
            xmlEx.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
        ServiceRemoteConfigInstance.getInstance(getApplicationContext()).getString("hi_launcher_show_hot_game");
    }


    private void setDefault() {
        try {
            ServiceRemoteConfigInstance.getInstance(getApplicationContext()).setDefaultValue("default_value.xml");
        } catch (XmlPullParserException xmlEx) {
            xmlEx.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


}
