package com.idatachina.www.osupdatehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.idatachina.www.osupdatehelper.MainService;
import com.idatachina.www.osupdatehelper.util.LogUtils;

/**
 * Created by wanghang on 2017/11/29.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public final static String RUN_MAIN_SERVICE_COMMAND="RunMainService";

    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive()这个函数执行时开启
        //NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification notification=new Notification(R.drawable.ic_launcher,"用电脑时间过长了！白痴！"
        //      ,System.currentTimeMillis());
        //notification.defaults = Notification.DEFAULT_ALL;
        //manager.notify(1, notification);

        String action = intent.getAction();
        LogUtils.debug("[AlarmReceiver][][action:"+action+"]");
        if (RUN_MAIN_SERVICE_COMMAND.equals(action)){
            //再次开启这个服务，从而可以
            Intent i = new Intent(context, MainService.class);
            i.setAction(MainService.AUTO_START_COMMAND);
            context.startService(i);
        }
    }
}
