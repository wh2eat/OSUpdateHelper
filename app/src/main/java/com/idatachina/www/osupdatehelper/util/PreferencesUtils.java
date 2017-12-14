package com.idatachina.www.osupdatehelper.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wanghang on 2017/11/29.
 */

public class PreferencesUtils {

    private final static String pref_name="OSUpdatePrefsFile";

    private final static String MAIN_SERVICE_RUN_MILLIS="MainServiceRunMillis";

    private final static String EXECUTE_STATUS="executeStatus";

    private final static String RECEIVE_BOOT_STATUS="receiveBootStatus";

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

    public static void clearMainServiceRunMillis(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PreferencesUtils.MAIN_SERVICE_RUN_MILLIS);
        editor.commit();
    }

    public static long getMainServiceRunMillis(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        return settings.getLong(PreferencesUtils.MAIN_SERVICE_RUN_MILLIS, -1l);
    }

    public static void setMainServiceRunMillis(Context context,long millis){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PreferencesUtils.MAIN_SERVICE_RUN_MILLIS,millis);
        editor.commit();
    }

    public static int getExecuteStatus(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        return settings.getInt(PreferencesUtils.EXECUTE_STATUS,0);
    }

    public static void setExecuteStatus(Context context,int status){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PreferencesUtils.EXECUTE_STATUS,status);
        editor.commit();
    }

    public static void receiveBootStatus(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PreferencesUtils.RECEIVE_BOOT_STATUS,1);
        editor.commit();
    }

    public static boolean hasReceiveBootStatus(Context context){
        int status = getReceiveBootStatus(context);
        if(1==status){
            return true;
        }
        return false;
    }

    private static int getReceiveBootStatus(Context context){
        SharedPreferences settings = context.getSharedPreferences(pref_name, 0);
        return settings.getInt(PreferencesUtils.RECEIVE_BOOT_STATUS,0);
    }







}
