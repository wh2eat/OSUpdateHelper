package com.idatachina.www.osupdatehelper.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.idatachina.www.osupdatehelper.receiver.AlarmReceiver;

/**
 * Created by wanghang on 2017/11/30.
 */

public class AlarmServiceUtils {

    public static  void addRunMainServiceIntent(Context context,long startMillis){

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(AlarmReceiver.RUN_MAIN_SERVICE_COMMAND);
        alarmIntent.setClass(context,AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        cancelRunMainServiceIntent(context,manager,pi);

        PreferencesUtils.setMainServiceRunMillis(context,startMillis);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,startMillis,pi);
        LogUtils.info("[][addRunMainServiceIntent][add run MainService Intener success,run millis:"+startMillis+"]");
    }

    public static  void cancelRunMainServiceIntent(Context context){
        cancelRunMainServiceIntent(context,null,null);
    }

    private static  void cancelRunMainServiceIntent(Context context,AlarmManager manager,PendingIntent pi){
        long nextRunTimeMillis = PreferencesUtils.getMainServiceRunMillis(context);
        if (nextRunTimeMillis!=-1){
            LogUtils.debug("[][][nextRunTimeMillis:"+nextRunTimeMillis+"]");
            if (null==manager){
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            }
            if (null==pi){
                Intent alarmIntent = new Intent();
                alarmIntent.setAction(AlarmReceiver.RUN_MAIN_SERVICE_COMMAND);
                alarmIntent.setClass(context,AlarmReceiver.class);
                pi = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            }
            manager.cancel(pi);
        }
    }
}
