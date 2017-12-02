package com.idatachina.www.osupdatehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.idatachina.www.osupdatehelper.MainService;
import com.idatachina.www.osupdatehelper.util.LogUtils;
import com.idatachina.www.osupdatehelper.util.PreferencesUtils;

/**
 * Created by wanghang on 2017/11/29.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    public  final static  String boot_action="android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(boot_action)){
            LogUtils.debug("[][][system finsih boot]");

            PreferencesUtils.clearMainServiceRunMillis(context);

            //启动服务
            Intent startIntent = new Intent(context,MainService.class);
            startIntent.setAction(MainService.AUTO_START_COMMAND);
            context.startService(startIntent);
        }
    }
}
