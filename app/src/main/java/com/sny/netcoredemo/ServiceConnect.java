package com.sny.netcoredemo;

import android.os.Bundle;

import com.sny.netcoredemo.connect.AdvertisementActivity;
import com.sny.netcoredemo.connect.HotWActivity;

import java.util.ArrayList;

public class ServiceConnect extends BaseListActivity {


    public ServiceConnect() {
        initListItems();
    }

    public void initListItems() {

        mItemsInfo = new ArrayList<ItemComponentInfo>();

        ItemComponentInfo info;

        info = new ItemComponentInfo("Advertisment", AdvertisementActivity.class);
        mItemsInfo.add(info);


        info = new ItemComponentInfo("HotW", HotWActivity.class);
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
