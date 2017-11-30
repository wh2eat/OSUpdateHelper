package com.idatachina.www.osupdatehelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wanghang on 2017/11/29.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive()这个函数执行时开启
        //NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification notification=new Notification(R.drawable.ic_launcher,"用电脑时间过长了！白痴！"
        //      ,System.currentTimeMillis());
        //notification.defaults = Notification.DEFAULT_ALL;
        //manager.notify(1, notification);

        LogUtils.debug("[AlarmReceiver][][action:"+intent.getAction()+"]");

        //再次开启LongRunningService这个服务，从而可以
        Intent i = new Intent(context, MainService.class);
        i.setAction("auto");
        context.startService(i);
    }
}
