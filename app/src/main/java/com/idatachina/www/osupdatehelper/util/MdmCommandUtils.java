package com.idatachina.www.osupdatehelper.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by wanghang on 2017/12/11.
 */

public class MdmCommandUtils {

    public static final int HANDLER_WHAT_FOR_BROADCAST_RECEIVER = 120;
    public final static int CMD_SYSTEM_GET_DEVICETYPE = 0x0002;
    public final static int CMD_SYSTEM_SET_USBDEBUG = 0x0009;
    public final static int CMD_SYSTEM_GET_USBDEBUG = 0x000A;

    public final static int CMD_SYSTEM_REBOOT = 0x0004;

    public final static int CMD_SYSTEM_SET_APKINSTALLUI = 0x0012;
    //静默卸载
    public final static int UNINSTALLAPPLICATION_SILENCE=1;
    //系统默认卸载界面
    public final static int UNINSTALLAPPLICATION_DEFAULT=0;

    //复位
    public final static int CMD_SYSTEM_REST = 0x0035;

    //清除应用数据
    public final static int CMD_SYSTEM_CLEAN_APP_DATA = 0x0036;

    //卸载应用
    public final static int CMD_SYSTEM_UNAPKINSTALL = 0x0027;

    public final static String REQUEST = "extra.mdm.request";
    public final static String RESPONE = "extra.mdm.respone";

    private static  long getTimeMillis(){
        synchronized (MdmCommandUtils.class){
            return System.currentTimeMillis();
        }
    }


    private static void sendBroadcast(Context context, int cmd, Map<String,Object> params) {
        Intent _intent = new Intent(REQUEST);

        _intent.putExtra("Timestamp",getTimeMillis());
        _intent.putExtra("Cmd", cmd);

        if (null!=params&&!params.isEmpty()){
            Set<Map.Entry<String,Object>> entris = params.entrySet();
            for (Map.Entry<String,Object> entry:entris) {
                String key = entry.getKey();
                Object valueObj = entry.getValue();
                if (valueObj instanceof  Integer){
                    _intent.putExtra(key, (Integer)valueObj);
                }else if(valueObj instanceof String){
                    _intent.putExtra(key, (String) valueObj);
                }else{
                    LogUtils.debug("[][SendBroadcast][not map key convert,valueObj:"+valueObj+";valueObj class:"+valueObj.getClass()+"]");
                }
            }
        }
        LogUtils.debug("[][SendBroadcast]["+_intent.getExtras().toString()+"]");
        context.sendBroadcast(_intent);
    }

    public static  void reboot(Context content){
        LogUtils.debug("[][reboot][]");
        sendBroadcast(content,CMD_SYSTEM_REBOOT,null);
    }

    public static  void rest(Context content){
        LogUtils.debug("[][rest][]");
        sendBroadcast(content,CMD_SYSTEM_REST,null);
    }

    public static  void cleanAppData(Context content,String packageName){
        LogUtils.debug("[][cleanAppData][]");
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("package",packageName);
        sendBroadcast(content,CMD_SYSTEM_CLEAN_APP_DATA,params);
        LogUtils.debug("[][cleanAppData][send Broadcast success]");
    }

    public static  void uninstallApplication(Context content,String packageName) {
        LogUtils.debug("[][uninstallApplication][packageName:" + packageName + "]");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Path", packageName);
        params.put("Mode", UNINSTALLAPPLICATION_SILENCE);
        sendBroadcast(content, CMD_SYSTEM_UNAPKINSTALL, params);
        LogUtils.debug("[][uninstallApplication][send Broadcast success]");
    }
}
