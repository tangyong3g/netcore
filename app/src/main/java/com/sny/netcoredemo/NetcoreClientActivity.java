package com.sny.netcoredemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.net.core.service.config.ServiceRemoteConfigInstance;
import com.net.core.service.connect.Callback;
import com.net.core.service.connect.ServiceConnectException;
import com.net.core.service.connect.ServiceConnectInstance;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class NetcoreClientActivity extends AppCompatActivity {

    Button mBtn;
    TextView mTx;
    EditText mETx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netcore_client);
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

//        mBtn = (Button) findViewById(R.id.btn_fetch);
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

        final Map<String, String> params = new HashMap<String, String>();

        params.put("inner_package_name", "com.tcl.launcherpro");
        params.put("posision", "1");
        params.put("num", "4");
        params.put("country", "all_cou");
        params.put("version", "all_ver");
        params.put("language", "all_lan");

//        Button btn = (Button) findViewById(R.id.btn_fetch_ads);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    ServiceConnectInstance.getInstance(NetcoreClientActivity.this).fetchValueWithURLWithCa(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                        }
//
//                        @Override
//                        public void onResponse(Call call, String result) throws IOException {
//                            Log.i("tyler.tang", "获取到的数据:\t" + result);
//                        }
//                    }, url, params, 3600 * 1000 * 24);
//
//                } catch (ServiceConnectException ex) {
//
//                } catch (IOException io) {
//
//                }
//            }
//        });


//        findViewById(R.id.btn_un_ser).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                ArrayList<ServiceConnectConfig> config = ServiceConnectInstance.getInstance(NetcoreClientActivity.this).readObj();
////                Log.i("tyler.tang", config.toString());
//
//            }
//        });

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


    private void initComponentddd() {

        final String url = "http://launcher-test.tclclouds.com/tlauncher-api/advertising/list";

//        mBtn = (Button) findViewById(R.id.btn_fetch);
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

        final Map<String, String> params = new HashMap<String, String>();

        params.put("inner_package_name", "com.tcl.launcherpro");
        params.put("posision", "1");
        params.put("num", "4");
        params.put("country", "all_cou");
        params.put("version", "all_ver");
        params.put("language", "all_lan");

        Button btn = null;
//        (Button) findViewById(R.id.btn_fetch_ads);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ServiceConnectInstance.getInstance(NetcoreClientActivity.this).fetchValueWithURLWithCa(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, String result) throws IOException {
                            Log.i("tyler.tang", "获取到的数据:\t" + result);
                        }
                    }, url, params, 3600 * 1000 * 24);

                } catch (ServiceConnectException ex) {

                } catch (IOException io) {

                }
            }
        });


    }


    /**
     * 添加按钮
     *
     * @param view
     */
    public void fetchRemoteValue_(View view) {

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
