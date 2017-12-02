package com.idatachina.www.osupdatehelper.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by wanghang on 2017/11/30.
 */

public class OsUtils {

    public final static String REQUEST = "extra.mdm.request";
    public final static String RESPONE = "extra.mdm.respone";

    public final static int CMD_FOTA_UPDATE_OS = 0x0101;

    public  static void sendBroadcast4UpdateOS(Context context, String filePath){

        Intent _intent = new Intent(REQUEST);

        _intent.putExtra("Timestamp", System.currentTimeMillis());
        _intent.putExtra("Cmd", CMD_FOTA_UPDATE_OS);

        int osType = 1;
        if (filePath.endsWith("_diff.zip")){
            osType=2;
        }
        _intent.putExtra("Type", osType);
        _intent.putExtra("Path", filePath);

        context.sendBroadcast(_intent);
        LogUtils.debug("send update os broadcast,os path:"+filePath);
    }
}
