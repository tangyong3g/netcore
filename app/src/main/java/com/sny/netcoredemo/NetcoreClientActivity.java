package com.sny.netcoredemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.net.core.service.config.ServiceRemoteConfigInstance;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

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
     *
     */
    public void fetchRemoteValue() {
        try {
            ServiceRemoteConfigInstance.getInstance(getApplicationContext()).setDefaultValue("default_value.xml");
        } catch (XmlPullParserException xmlEx) {
            xmlEx.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
        ServiceRemoteConfigInstance.getInstance(getApplicationContext()).getString("JRD_AD_COUNT");
    }


}
