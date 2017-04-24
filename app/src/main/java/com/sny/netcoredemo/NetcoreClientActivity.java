package com.sny.netcoredemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.net.core.service.config.Callback;
import com.net.core.service.config.ServiceRemoteConfigInstance;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;

public class NetcoreClientActivity extends AppCompatActivity {

    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netcore_client);
        initComponent();

    }

    private void initComponent() {

        mBtn = (Button) findViewById(R.id.btn_fetch);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRemoteValue();
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
        ServiceRemoteConfigInstance.getInstance(getApplicationContext()).getString("JRD_AD_COUNT");
    }

    /**
     * 添加按钮
     */
    public void fetchRemoteValue() {
        try {
            ServiceRemoteConfigInstance.getInstance(getApplicationContext()).setDefaultValue("default_value.xml");
        } catch (XmlPullParserException xmlEx) {
            xmlEx.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
        ServiceRemoteConfigInstance.getInstance(getApplicationContext()).setCallBack(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Map<String, String> values) throws IOException {
                String rs = showRemoteValue(values);
                Log.i("ServiceConfig","结果是。 is :\t\n"+rs);
            }
        });
        ServiceRemoteConfigInstance.getInstance(getApplicationContext()).fetchValue();

    }


    /**
     * 查看从服务器fetch下来的内容，是否正常 调试用
     *
     * @return
     */
    private String showRemoteValue(Map<String, String> mServerValue) {
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
