package com.idatachina.www.osupdatehelper;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;

import com.alibaba.fastjson.JSONReader;
import com.idatachina.www.osupdatehelper.util.AlarmServiceUtils;
import com.idatachina.www.osupdatehelper.util.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wanghang on 2017/11/30.
 */

public class OsUpdateChecker {

    private OsUpdateChecker(){

    }

    private static OsUpdateChecker _this;

    public static OsUpdateChecker getInstance(){
        if (null==_this){
            synchronized (OsUpdateChecker.class){
                if(null==_this){
                    _this = new OsUpdateChecker();
                }
            }
        }
        return _this;
    }

    public final  static  String OS_STORE_DIR_NAME = "osupdate";

    public final  static  String OS_UPDATE_DESCRIBER_FILE_NAME = "update.txt";

    public final static  long TIME_UNIT = 60*60*1000;//分钟

    public void check(Context context,String action){
        OsUpdateDescriber describer = OsUpdateChecker.getInstance().checkUpdateDescriberFile();
        if (null!=describer){
            boolean downloadFinish = false;
            long downloadFileSize = OsUpdateChecker.getInstance().getUpdatePackageDownloadSize(describer.getFileName());
            long fileSize = describer.getFileSize();
            if (fileSize>0&&fileSize==downloadFileSize){
                downloadFinish = true;
            }

            if (!downloadFinish){
                LogUtils.debug("[][][update file not finish download,start auto check.]");
                autoRunCheckService(context,1);
            }

            if (MainService.AUTO_START_COMMAND.equals(action)){
                if(downloadFinish){
                    Intent startActivity = new Intent(context,MainActivity.class);
                    startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(startActivity);
                    LogUtils.debug("[][][auto start,file download success,start MainActivity]");
                }
            }else{
                LogUtils.debug("[][][set update info]");
                if (null!=MainActivity.getInstance()){
                    MainActivity.getInstance().setUpdateInfo(describer.getFileName(),describer.getFileSize(),downloadFileSize,describer.getTargetVersion(),describer.getUpdateLog());
                }
            }
        }else{
            if (null!=MainActivity.getInstance()){
                MainActivity.getInstance().setUpdateInfo();
            }
            autoRunCheckService(context,4);
        }
    }

    private void autoRunCheckService(Context context , int hour){
        long nextRunTimeMillis = SystemClock.elapsedRealtime()+hour*TIME_UNIT;
        AlarmServiceUtils.addRunMainServiceIntent(context,nextRunTimeMillis);
    }


    private OsUpdateDescriber checkUpdateDescriberFile(){
        String updateDescriberFilePath = getDescriberFilePath();
        File updateDescriberFile = new File(updateDescriberFilePath);
        if (updateDescriberFile.exists()){
            BufferedReader reader = null;
            JSONReader jsonReader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(updateDescriberFile),"gbk"));
                jsonReader = new JSONReader(reader);
                OsUpdateDescriber osUpdateDescriber = jsonReader.readObject(OsUpdateDescriber.class);
                LogUtils.debug("[][check read object]["+osUpdateDescriber+"]");
                return osUpdateDescriber;
            }catch (IOException e){
                LogUtils.error("[][][read os udpate describer file failed.]",e);
            }finally {
                if (null!=reader){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null!=jsonReader){
                    jsonReader.close();
                }
            }
        }
        return null;
    }

    private String getDescriberFilePath(){
        return Environment.getExternalStorageDirectory().getPath()+"/"+OS_STORE_DIR_NAME+"/"+OS_UPDATE_DESCRIBER_FILE_NAME;
    }

    private  String getUpdatePackageFilePath(String fileName){
        return  Environment.getExternalStorageDirectory().getPath()+"/"+OS_STORE_DIR_NAME+"/"+fileName;
    }

    public long getUpdatePackageDownloadSize(String fileName){
        String updateFilePath =getUpdatePackageFilePath(fileName);
        File updateFile = new File(updateFilePath);
        if (null!=updateFile&&updateFile.exists()){
            return updateFile.length();
        }
        return -1;
    }

    public  void deleteUpdateDescriberFile(){
        File updateFile = new File(getDescriberFilePath());
        if (updateFile.exists()){
            boolean rs = updateFile.delete();
            LogUtils.debug("[][][delete update describer file:"+updateFile.getPath()+","+rs+"]");
        }

    }

    public void deleteUpdatePackageFile(String fileName){
        File osFile = new File(getUpdatePackageFilePath(fileName));
        if (osFile.exists()){
            boolean rs = osFile.delete();
            LogUtils.debug("[][][delete update package file:"+osFile.getPath()+","+rs+"]");
        }
    }
}
