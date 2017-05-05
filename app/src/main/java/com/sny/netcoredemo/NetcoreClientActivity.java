package com.sny.netcoredemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.net.core.service.config.ServiceRemoteConfigInstance;
import com.net.core.service.connect.ServiceConnect;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
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

        final String url = "http://launcher-test.tclclouds.com/tlauncher-api/advertising/list";

        mBtn = (Button) findViewById(R.id.btn_fetch);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRemoteValue(null);
            }
        });


        final Map<String, String> params = new HashMap<String, String>();
        params.put("inner_package_name", "com.tcl.launcherpro");
        params.put("posision", "1");
        params.put("num", "4");

        Button btn = (Button) findViewById(R.id.btn_fetch_ads);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceConnect connect = new ServiceConnect();
                connect.fetchValueWithURL(new ServiceConnect.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, String result) throws IOException {
                        Log.i("tyler.tang", "获取到的数据:\t" + result);
                    }
                }, url, params);
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
