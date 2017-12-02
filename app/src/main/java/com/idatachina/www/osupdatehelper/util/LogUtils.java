package com.idatachina.www.osupdatehelper.util;

import android.util.Log;

/**
 * Created by wanghang on 2017/11/24.
 */

public class LogUtils {

    private final static String tag="OSUpdateHelper";

    private final static boolean isDebug = true;

    public  static  void debug(String log){
        if (isDebug){
            Log.d(tag, log);
        }
    }

    public  static  void info(String log){
        Log.i(tag, log);
    }

    public  static  void error(String log){
        Log.e(tag, log);
    }

    public  static  void error(String log,Exception e){
        Log.e(tag,log,e );
    }

}
