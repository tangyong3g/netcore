package com.sny.netcoredemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.net.core.service.config.ServiceRemoteConfigInstance;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class FireBaseActivity extends Activity {

    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base);
        initComponent();
    }

    private void initComponent() {

        ServiceRemoteConfigInstance instance = ServiceRemoteConfigInstance.getInstance(this);
        instance.setIsSupportFireBase(true);

        mEdit = (EditText) findViewById(R.id.edit_key);
    }


    /**
     * 添加按钮
     *
     * @param view
     */
    public void fetchRemoteValue(View view) {

        final FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        Task tsk = config.fetch();

        tsk.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.i("tyler.tang", "onComplete");
                    config.activateFetched();
                } else {
                    Log.i("tyler.tang", "error!");
                }
            }
        });

        String key = mEdit.getText().toString();

        if (!TextUtils.isEmpty(key)) {
            String lastResult = config.getString(key);
            Log.i("tyler.tang", "value:" + lastResult);
        }
    }


    /**
     * 添加按钮
     *
     * @param view
     */
    public void fetchFromServer(View view) {

        ServiceRemoteConfigInstance instance = ServiceRemoteConfigInstance.getInstance(this);
        instance.setIsSupportFireBase(true);

        String key = mEdit.getText().toString();

        if (!TextUtils.isEmpty(key)) {
            String lastResult = instance.getString(key);
            Log.i("tyler.tang", "value:" + lastResult);
        }
    }


}
