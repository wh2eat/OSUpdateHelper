package com.idatachina.www.osupdatehelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import api.GetDeviceInfo;

/**
 * Created by wanghang on 2017/11/27.
 */

public class MainService extends Service {

    String osDirPath="/osupdate";

    public static  String fileName="";
    public  static  long fileSize = 0l;
    public  static  long downloadFileSize = 0l;
    public  static  String targetVersion = "";
    public  static  String updateLog = "";

    public final static String REQUEST = "extra.mdm.request";
    public final static String RESPONE = "extra.mdm.respone";

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
                String updateLogFilePath = Environment.getExternalStorageDirectory().getPath()+osDirPath+"/update.txt";
                File updateLogFile = new File(updateLogFilePath);
                if (updateLogFile.exists()){
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(updateLogFile),"gbk"));
                        String line = reader.readLine();
                        if (null !=line&&line.length()>0){
                            try {
                                JSONObject jsonObject = new JSONObject(line);
                                MainService.fileName=jsonObject.getString("fileName");
                                MainService.fileSize = jsonObject.getLong("fileSize");
                                MainService.targetVersion = jsonObject.getString("targetVersion");
                                MainService.updateLog = jsonObject.getString("updateLog");
                                LogUtils.debug("[][onStartCommand][update package info,fileName:"+MainService.fileName
                                        +",fileSize:"+MainService.fileSize+";targetVersion:"+MainService.targetVersion+",updateLog:"+MainService.updateLog+"]");

                                String updateFilePath = Environment.getExternalStorageDirectory().getPath()+osDirPath+"/"+fileName;
                                File updateFile = new File(updateFilePath);
                                if (updateFile.exists()){
                                    downloadFileSize = updateFile.length();
                                    LogUtils.debug("[][][update file download size:"+downloadFileSize+".]");
                                }else{
                                    LogUtils.debug("[][][not found update file:"+updateFilePath+"]");
                                }

                                if (fileSize>0&&fileSize!=downloadFileSize){
                                    LogUtils.debug("[][][update file not finish download,start auto check.]");
                                    AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    int five = 60*1000; // 这是1min
                                    long triggerAtTime = SystemClock.elapsedRealtime() + five;
                                    Intent i = new Intent();
                                    i.setClass(_this,AlarmReceiver.class);
                                    PendingIntent pi = PendingIntent.getBroadcast(_this, 0, i, 0);
                                    manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
                                }

                                if ("auto".equals(action)){
                                    if(downloadFileSize>0&&downloadFileSize==fileSize){
                                        Intent startActivity = new Intent(_this,MainActivity.class);
                                        startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(startActivity);
                                    }else{
                                        LogUtils.debug("[][][downloadFileSize:"+downloadFileSize+";fileSize:"+fileSize+"]");
                                    }
                                }else{
                                    LogUtils.debug("[][][set update info]");
                                    if (null!=MainActivity.getInstance()){
                                        MainActivity.getInstance().setUpdateInfo(fileName,fileSize,downloadFileSize,targetVersion,updateLog);
                                    }
                                }
                            }catch (JSONException jsonException) {
                                LogUtils.error("", jsonException);
                            }
                        }
                    }catch (IOException e)
                    {
                        LogUtils.error("",e);
                    }finally {
                        if (null!=reader){
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    LogUtils.debug("[MainService][onStartCommand][not found desc file:"+updateLogFilePath+"]");
                }
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
