package com.net.core.unit;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author flying
 * @version 1.0
 * @description restore config data
 * @crated on 2016/7/12.
 */
class ConfigDataBase {
    private SharedPreferences shared;

    public static final String FILE_NAME = "config";
    public static final int MODE = Context.MODE_APPEND;

    public ConfigDataBase(Context context) {
        shared = context.getSharedPreferences(FILE_NAME, MODE);
    }

    public ConfigDataBase(Context context, String fileName) {
        shared = context.getSharedPreferences(fileName, MODE);
    }

    public void addStringData(String name, String value) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public String getStringData(String name) {
        return shared.getString(name, "");
    }

    public void addIntData(String name, int value) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public int getIntData(String name) {
        return shared.getInt(name, -1);
    }

    public int getIntData(String name, int defaultvalue) {
        return shared.getInt(name, defaultvalue);
    }

    public void addLongData(String name, long value) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putLong(name, value);
        editor.commit();
    }

    public long getLongData(String name) {
        return shared.getLong(name, 0);
    }

    public void addBooleanData(String name, boolean value) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public boolean getBooleanData(String name) {
        return shared.getBoolean(name, false);
    }

    public boolean getBooleanData(String name, boolean defaultvalue) {
        return shared.getBoolean(name, defaultvalue);
    }
}
