package com.hsf1002.sky.xljgps.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hsf1002.sky.xljgps.app.XLJGpsApplication;

import static com.hsf1002.sky.xljgps.util.Const.SHARED_PREFERENCE_NAME;

/**
 * Created by hefeng on 18-6-8.
 */

public class SharedPreUtils {
    private static final String SHARED_NAME = SHARED_PREFERENCE_NAME;
    private static SharedPreUtils sInstance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public SharedPreUtils() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(XLJGpsApplication.getAppContext());//XLJGpsApplication.getAppContext().getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS);
        editor = sharedPreferences.edit();
    }

    public static SharedPreUtils getInstance()
    {
        if (sInstance == null)
        {
            synchronized (SharedPreUtils.class)
            {
                if (sInstance == null)
                {
                    sInstance = new SharedPreUtils();
                }
            }
        }

        return  sInstance;
    }

    private void sharedPreRemove(String key) {
        editor.remove(key).apply();
    }

    private int getInt(String key, int value)
    {
        return sharedPreferences.getInt(key, value);
    }

    public void putInt(String key, int value)
    {
        editor.putInt(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key, Boolean value)
    {
        return sharedPreferences.getBoolean(key, value);
    }

    public void putBoolean(String key, Boolean value)
    {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(String key, String value)
    {
        return sharedPreferences.getString(key, value);
    }

    public void putString(String key, String value)
    {
        editor.putString(key, value);
        editor.apply();
    }
}
