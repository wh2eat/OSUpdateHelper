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

    public  final static  String boot_action="android.intent.action.boot_completed";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        LogUtils.debug("[][][system finsih boot,action:"+intent.getAction()+"]");
        if (boot_action.equals(action.toLowerCase())){
            PreferencesUtils.receiveBootStatus(context);
            LogUtils.debug("[][][system finsih boot]");
            PreferencesUtils.clearMainServiceRunMillis(context);
            //启动服务
            Intent startIntent = new Intent(context,MainService.class);
            startIntent.setAction(MainService.AUTO_START_COMMAND);
            context.startService(startIntent);
        }
    }
}
