package com.idatachina.www.osupdatehelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.idatachina.www.osupdatehelper.util.LogUtils;
import com.idatachina.www.osupdatehelper.util.MdmCommandUtils;

/**
 * Created by wanghang on 2017/12/11.
 */

public class MainServiceBroadcastReceiver extends BroadcastReceiver {

    private static MainServiceBroadcastReceiver broadcastReceiver;

    private MainServiceBroadcastReceiver(){

    }

    public  static MainServiceBroadcastReceiver getInstance(){
        synchronized (MainServiceBroadcastReceiver.class){
            if (null==broadcastReceiver){
                broadcastReceiver= new MainServiceBroadcastReceiver();
            }
            return broadcastReceiver;
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MdmCommandUtils.RESPONE)){

            String log = "Timestamp:"+intent.getLongExtra("Timestamp", -1)
                    +";Cmd:"+intent.getIntExtra("Cmd", -1)
                    +";Result:"+intent.getIntExtra("Result", -1)
                    +";Status:"+intent.getIntExtra("Status", -1)
                    +";Data:"+intent.getStringExtra("Data")
                    +";Errorcode:"+intent.getIntExtra("Errorcode", -1)
                    +";Errormessage:"+intent.getStringExtra("Errormessage");
            LogUtils.debug("[][onReceive][RESPONE:"+log+"]");
        }
    }
}
