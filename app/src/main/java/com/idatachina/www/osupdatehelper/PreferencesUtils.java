package com.idatachina.www.osupdatehelper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wanghang on 2017/11/29.
 */

public class PreferencesUtils {

    private final static String pref_name="OSUpdatePrefsFile";

    public static void setUpdateOsVersion(Context context,String version){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("osUpdateVersion", version);
        editor.commit();
    }

    public static String getUpdateOsVersion(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        return settings.getString("osUpdateVersion",null);
    }

    public static void clearFirstStart(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("isFirstStart");
        editor.commit();
    }

    public static void saveFirstStart(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isFirstStart", true);
        editor.commit();
    }

    public static boolean isFirstStart(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        return settings.getBoolean("isFirstStart", false);
    }

    public static void clearRunUpdateMillis(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("runUpdateMillis");
        editor.commit();
    }

    public static long getRunUpdateMillis(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        return settings.getLong("runUpdateMillis", -1l);
    }

    public static void setRunUpdateMillis(Context context,long millis){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("runUpdateMillis",millis);
        editor.commit();
    }

}
