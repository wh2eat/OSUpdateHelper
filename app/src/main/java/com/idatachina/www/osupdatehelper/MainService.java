package com.idatachina.www.osupdatehelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.idatachina.www.osupdatehelper.receiver.AlarmReceiver;
import com.idatachina.www.osupdatehelper.util.AlarmServiceUtils;
import com.idatachina.www.osupdatehelper.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wanghang on 2017/11/27.
 */

public class MainService extends Service {

    public final static String AUTO_START_COMMAND = "AutoStartMainService";

    public MainService(){

    }

    /**
     * 在服务创建是调用
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 在每次服务启动时调用
     * 如果我们希望服务一旦启动就去执行这个动作，就可以在这里实现
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String action = (null==intent)?"":intent.getAction();
        if (null!=intent){
            LogUtils.debug("[][][action:"+intent.getAction()+";flags:"+flags+";startId:"+startId+"]");
        }
        final Context _this = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                OsUpdateChecker.getInstance().check(_this,action);
            }
        }, 0);
        LogUtils.debug("[MainService][onStartCommand][MainService start.]");
        return START_STICKY;
    }

    /**
     * 服务销毁时调用，用于回收资源
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 抽象方法，必须实现
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
