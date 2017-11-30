package com.idatachina.www.osupdatehelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wanghang on 2017/11/29.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    public  final static  String boot_action="android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(boot_action)){
            LogUtils.debug("[][][system finsih boot]");

            PreferencesUtils.clearRunUpdateMillis(context);

            //启动服务
            Intent startIntent = new Intent(context,MainService.class);
            startIntent.setAction("auto");
            context.startService(startIntent);
        }
    }
}
