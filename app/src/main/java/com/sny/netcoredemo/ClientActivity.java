package com.sny.netcoredemo;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

public class ClientActivity extends BaseListActivity {


    public ClientActivity() {
        initListItems();
    }

    public void initListItems() {

        mItemsInfo = new ArrayList<ItemComponentInfo>();

        ItemComponentInfo info;

        info = new ItemComponentInfo("ServiceConfig", ServiceConfig.class);
        mItemsInfo.add(info);

        info = new ItemComponentInfo("ServiceConnect", ServiceConnect.class);
        mItemsInfo.add(info);

        info = new ItemComponentInfo("FireBaseConfig", FireBaseActivity.class);
        mItemsInfo.add(info);

        initDisplayList();
    }

    private void initDisplayList() {
        if (mItemsInfo != null && mItemsInfo.size() > 0) {
            mUnits = new String[mItemsInfo.size()];

            for (int i = 0; i < mItemsInfo.size(); i++) {
                mUnits[i] = mItemsInfo.get(i).mDisplayName;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActionBar();

    }
}
