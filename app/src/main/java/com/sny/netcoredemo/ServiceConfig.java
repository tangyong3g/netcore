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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_config);
        initComponent();
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

        final String url = "http://launcher-test.tclclouds.com/tlauncher-api/advertising/list";

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


}
