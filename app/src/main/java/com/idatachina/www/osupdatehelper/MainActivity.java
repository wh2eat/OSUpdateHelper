package com.idatachina.www.osupdatehelper;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.GetDeviceInfo;

public class MainActivity extends AppCompatActivity {

    private GetDeviceInfo deviceInfo;

    String osDirPath="/osupdate";

    public final static String REQUEST = "extra.mdm.request";
    public final static String RESPONE = "extra.mdm.respone";

    private Integer updateOsStatus = 0;//-1：没有任务；1：等待下载；2：下载中；3：下载成功

    private Integer chooseDelayStatus = 0;//0：没有选择；1:正在选择

    int parseType = 1;

    private String deviceSn;
    private String deviceType;
    private String deviceSystemVersion;

    private String updatePackageName=null;
    private String updatePackageVersion=null;

    private static  MainActivity _this = null;

    public static  MainActivity getInstance(){
        return  _this;
    }

    Button updateButton = null;

    TextView noTaskInfoView = null;

    View updatePackageInfoSplitLineView = null;

    TextView updatePackageInfoView = null;

    TextView updateLogTitleView= null;
    TextView updateLogView = null;

    LinearLayout fileNameLayout = null;
    TextView fileNameView = null;
    LinearLayout targetVersionLayout = null;
    TextView targetVersionView = null;
    LinearLayout fileSizeLayout = null;
    TextView fileSizeView = null;

    TextView deviceTypeView = null;
    TextView osVersionView =null;
    TextView deviceSnView =null;

    LinearLayout updateTipLayout = null;
    TextView updateTipView = null;
    LinearLayout updateLayout = null;

    Button goUpdateButton = null;
    Button cancelButton = null;

    AlertDialog.Builder alertDialogBuilder=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MainActivity._this = this;

        alertDialogBuilder = new AlertDialog.Builder(this);

        setContentView(R.layout.activity_main);

        deviceTypeView = (TextView)findViewById(R.id.deviceType);
        osVersionView =  (TextView)findViewById(R.id.osVersion);
        deviceSnView = (TextView)findViewById(R.id.deviceSn);
        noTaskInfoView = (TextView)findViewById(R.id.noTaskInfo);
        updatePackageInfoSplitLineView = findViewById(R.id.updatePackageSplitLine);
        updatePackageInfoView = (TextView)findViewById(R.id.updatePackageInfo) ;
        fileNameLayout = (LinearLayout)findViewById(R.id.fileNameLayout) ;
        fileNameView = (TextView) findViewById(R.id.fileName);
        fileSizeLayout = (LinearLayout)findViewById(R.id.fileSizeLayout);
        fileSizeView = (TextView) findViewById(R.id.fileSize);
        targetVersionLayout=(LinearLayout)findViewById(R.id.targetVersionLayout);
        targetVersionView = (TextView) findViewById(R.id.targetVersion);
        updateLogTitleView= (TextView)findViewById(R.id.updateLogTitle);
        updateLogView = (TextView) findViewById(R.id.updateLog);

        updateTipLayout = (LinearLayout)findViewById(R.id.updateTipLayout);
        updateTipLayout.setVisibility(View.GONE);
        updateTipView = (TextView)findViewById(R.id.updateTimeCountTip);

        updateLayout = (LinearLayout)findViewById(R.id.updateLayout);
        updateLayout.setVisibility(View.GONE);
        goUpdateButton = (Button)findViewById(R.id.nowUpdateButton);
        cancelButton = (Button)findViewById(R.id.cancelButton);

        if (null==deviceInfo){
            try {
                LogUtils.debug("deviceInfo init start.");
                deviceInfo = new GetDeviceInfo(this);
                LogUtils.debug("deviceInfo init success.");
                deviceSn = deviceInfo.getsn();
                LogUtils.debug("sn:"+deviceSn);
                deviceType = deviceInfo.getdevice_type();
                LogUtils.debug("device_type:"+deviceType);
                LogUtils.debug("product:"+deviceInfo.getProduct());
                LogUtils.debug("device:"+deviceInfo.getDevice());
                LogUtils.debug("devicecode:"+deviceInfo.getDevicecode());
                deviceSystemVersion = deviceInfo.getDeviceVersion();
                LogUtils.debug("deviceVersion:"+deviceSystemVersion);
            }catch (Exception e){
                LogUtils.error("deviceInfo init failed,case:"+e.getMessage(),e);
            }
        }

        deviceTypeView.setText(null==deviceType?"NA":(""==deviceType?"NA":deviceType));
        osVersionView.setText(null==deviceSystemVersion?"NA":(""==deviceSystemVersion?"NA":deviceSystemVersion));
        deviceSnView.setText(null==deviceSn?"NA":(""==deviceSn?"NA":deviceSn));

        deviceSystemVersion = deviceSystemVersion.toLowerCase();
        if (deviceSystemVersion.startsWith("a21")){
            String[] array = deviceSystemVersion.split("_");
            if (array.length>1){
                String lastStr = array[array.length-1];
                Pattern numPattern = Pattern.compile("\\d+");
                Matcher numMatcher = numPattern.matcher(lastStr);
                if (numMatcher.find()){
                    String deviceNumVersionStr = numMatcher.group();
                    LogUtils.debug("deviceNumVersionStr:"+deviceNumVersionStr);
                    int deviceNumVersion = Integer.parseInt(deviceNumVersionStr);
                    if(deviceNumVersion<=19){
                        parseType = 2;
                    }
                }
            }
        }

        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendBroadcast4UpdateOS(updatePackageList.get(0));
                if (-1==updateOsStatus){
                    //没有更新包，检测更新包
                }else if(1==updateOsStatus){
                    //等待下载
                }else if (2==updateOsStatus){
                    //下载中，请稍等
                }else if (3==updateOsStatus){
                    //下载完成。
                }else if(9==updateOsStatus){
                    //升级成功
                }
            }
        });

        goUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runUpdate();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (3==updateOsStatus){
                    delayRunUpdateTip();
                }else{
                    showErrorStatusTip("状态错误，无法执行延时升级操作！");
                }
            }
        });

        setViewDisplay(false);

        //启动服务
        Intent startIntent = new Intent(this,MainService.class);
        startService(startIntent);
    }

    private void delayRunUpdateTip(){

        chooseDelayStatus=1;//开始选择延迟

        alertDialogBuilder.setIcon(null);
        alertDialogBuilder.setTitle("请选择延长时间");
        alertDialogBuilder.setMessage(null);
        alertDialogBuilder.setItems(new String[]{"1个小时后", "2个小时后", "3个小时后", "4个小时后","5个小时后"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.debug("[][][which:"+which+"]");
                chooseDelayStatus = 0;
                delayRunUpdate(which+1);
            }
        });
        //取消ok
        alertDialogBuilder.setPositiveButton(null,null);
        //监听取消
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chooseDelayStatus = 0;
                LogUtils.debug("[][][cancel delay run update]");
            }
        });
        //设置对话框是可取消的
        alertDialogBuilder.setCancelable(false);
        AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();
    }

    private void delayRunUpdate(int times){
        if (null!=timeRunnable){
            LogUtils.info("[][][delayRunUpdate,stop auto time tip.]");
            timeRunnable.stop();
            timeRunnable = null;
        }
        LogUtils.debug("[][][update file not finish download,start auto check.]");
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent();
        i.setClass(_this,AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(_this, 0, i, 0);

        long delayMillis = PreferencesUtils.getRunUpdateMillis(getInstance());
        if (delayMillis!=-1){
            LogUtils.debug("[][][cancel old delay:"+delayMillis+"]");
            manager.cancel(pi);
        }

        long millis = SystemClock.elapsedRealtime() + times*60*1000;
        PreferencesUtils.setRunUpdateMillis(getInstance(),millis);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,millis,pi);

        updateTipView.setText(times+"小时后执行升级");

        LogUtils.info("[][][after "+times+" hours ,system to run update.]");

    }

    //显示错误提示
    private void showErrorStatusTip(String msg){

        alertDialogBuilder.setIcon(null);
        alertDialogBuilder.setTitle("提示");
        alertDialogBuilder.setMessage(msg);

        //监听下方button点击事件
        alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtils.debug("[][][click ok]");
            }
        });
        alertDialogBuilder.setNegativeButton(null,null);
        //设置对话框是可取消的
        alertDialogBuilder.setCancelable(false);
        AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();
    }

    private void runUpdate(){
        LogUtils.debug("[][][updateOsStatus:"+updateOsStatus+"]");

        if (9==updateOsStatus){
            showClearSystemTip();
            return;
        }

        if (1==updateOsStatus||2==updateOsStatus){
            showDownloadNotFinishTip();
            return;
        }
        showRunUpdateTip();
    }

    private void showClearSystemTip(){
        alertDialogBuilder.setIcon(null);
        alertDialogBuilder.setTitle("提示");
        alertDialogBuilder.setMessage("\n系统升级成功，即将重启并完成更新！\n\n");

        //监听下方button点击事件
        alertDialogBuilder.setPositiveButton(null,null);
        alertDialogBuilder.setNegativeButton(null,null);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File osFile = new File(Environment.getExternalStorageDirectory().getPath()+osDirPath+"/"+updatePackageName);
                if (osFile.exists()){
                    boolean rs = osFile.delete();
                    LogUtils.debug("[][][delete:"+osFile.getPath()+","+rs+"]");
                }
                File updateFile = new File(Environment.getExternalStorageDirectory().getPath()+osDirPath+"/update.txt");
                if (updateFile.exists()){
                    boolean rs = updateFile.delete();
                    LogUtils.debug("[][][delete:"+updateFile.getPath()+","+rs+"]");
                }
                dialog.dismiss();
                LogUtils.debug("[][][update success]");
            }
        },5*1000);
    }

    private void showRunUpdateTip(){
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder.setMessage("即将重启并执行系统升级，请确定？");

        //监听下方button点击事件
        alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtils.debug("[][][click ok]");
                startUpdate();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtils.debug("[][][click cancel]");
            }
        });
        //设置对话框是可取消的
        alertDialogBuilder.setCancelable(true);
        AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();
    }

    private void showDownloadNotFinishTip(){
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder.setTitle("系统提示");
        alertDialogBuilder.setMessage("更新包未下载完成，请稍后重试！");

        alertDialogBuilder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUtils.debug("[][][click cancel]");
            }
        });

        //设置对话框是可取消的
        alertDialogBuilder.setCancelable(true);
        AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();
    }

    private void startUpdate(){
        Toast.makeText(getApplicationContext(),"即将重启并执行更新！",Toast.LENGTH_LONG).show();
        PreferencesUtils.setUpdateOsVersion(getInstance(),updatePackageVersion);
        String updatePackageFilePath = Environment.getExternalStorageDirectory()+osDirPath+"/"+updatePackageName;
        if (2==parseType){
            updatePackageFilePath = updatePackageFilePath.replace("emulated/","sdcard");
        }
        LogUtils.debug("[][startUpdate]["+updatePackageFilePath+"]");
        sendBroadcast4UpdateOS(updatePackageFilePath);
    }

    public final static int CMD_FOTA_UPDATE_OS = 0x0101;

    private  void sendBroadcast4UpdateOS(String filePath){
        Intent _intent = new Intent(REQUEST);

        _intent.putExtra("Timestamp", System.currentTimeMillis());
        _intent.putExtra("Cmd", CMD_FOTA_UPDATE_OS);

        int osType = 1;
        if (filePath.endsWith("_diff.zip")){
            osType=2;
        }
        _intent.putExtra("Type", osType);
        _intent.putExtra("Path", filePath);

        this.sendBroadcast(_intent);
        LogUtils.debug("send update os broadcast,os path:"+filePath);
    }

    private void setViewDisplay(boolean hasTask){
        setViewDisplay(hasTask,false);
    }

    private Handler handler = new Handler();

    class TimeRunnable implements  Runnable {

        int ts = 60;

        private boolean stop=false;

        public void stop(){
            this.stop=true;
        }

        public  TimeRunnable(){

        }

        @Override
        public void run() {
            if (stop){
                LogUtils.info("[TimeRunnable][][ stop time count down.]");
                return;
            }
            if (0==chooseDelayStatus){
                updateTipView.setText(ts+"S后将重启并执行升级!");
                ts--;
                if (ts==0){
                    LogUtils.debug("[][][time is finish.]");
                    startUpdate();
                }else{
                    handler.postDelayed(this,1000);
                }
            }else{
                handler.postDelayed(this,1000);
            }
        }
    }

    private TimeRunnable timeRunnable;

    private void setViewDisplay(boolean hasTask,boolean downloadFinish){
        if (hasTask){
            updatePackageInfoSplitLineView.setVisibility(View.VISIBLE);
            noTaskInfoView.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
            updateLayout.setVisibility(View.VISIBLE);
            if (downloadFinish){
                updateTipLayout.setVisibility(View.VISIBLE);
                timeRunnable = new TimeRunnable();
                handler.postDelayed(timeRunnable,1000);
            }else {
                updateTipLayout.setVisibility(View.GONE);
            }
            updatePackageInfoView.setVisibility(View.VISIBLE);
            fileNameLayout.setVisibility(View.VISIBLE);
            fileSizeLayout.setVisibility(View.VISIBLE);
            targetVersionLayout.setVisibility(View.VISIBLE);
            updateLogTitleView.setVisibility(View.VISIBLE);
            updateLogView.setVisibility(View.VISIBLE);
        }else{
            updatePackageInfoSplitLineView.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);
            noTaskInfoView.setVisibility(View.VISIBLE);
            updateTipLayout.setVisibility(View.GONE);
            updateLayout.setVisibility(View.GONE);
            updatePackageInfoView.setVisibility(View.GONE);
            fileNameLayout.setVisibility(View.GONE);
            fileSizeLayout.setVisibility(View.GONE);
            targetVersionLayout.setVisibility(View.GONE);
            updateLogTitleView.setVisibility(View.GONE);
            updateLogView.setVisibility(View.GONE);
        }
    }

    public  void setUpdateInfo(String fileName,long fileSize,long downloadFileSize,String targetVersion,String updateLog){
        if (null==targetVersion){
            noTaskInfoView.setText("已是最新版本!");
            setViewDisplay(false);
            updateOsStatus=-1;
            updateButton.setText("检测更新");
            LogUtils.debug("[][][not update package]");
            return;
        }

        updatePackageName = fileName;
        updateButton.setText("升级");

        if (targetVersion.toLowerCase().equals(deviceSystemVersion)){
            LogUtils.debug("[][setUpdateInfo][os update success]");
            noTaskInfoView.setText("已是最新版本!");
            setViewDisplay(false);
            updateOsStatus = 9;
            showClearSystemTip();
            return ;
        }

        updatePackageVersion = targetVersion;

        String displaySize = getDisplaySize(fileSize);
        if (null!=displaySize){
            if (fileSize==downloadFileSize){
                displaySize+=" / 已下载完成";
                updateOsStatus = 3;
            }else if(downloadFileSize==0){
                displaySize+=" / 等待下载";
                updateOsStatus = 1;
            }else{
                displaySize+=" / "+getDisplaySize(downloadFileSize)+"，"+(String.format("%.2f", ((downloadFileSize*100.00)/fileSize)))+"%";
                updateOsStatus = 2;
            }
            fileSizeView.setText(displaySize);
        }

        fileNameView.setText(fileName);

        targetVersionView.setText(targetVersion);

        updateLogView.setText(updateLog);

        if (3==updateOsStatus){
            setViewDisplay(true,true);
        }else{
            setViewDisplay(true);
        }
    }

    private String getDisplaySize(long size){
        DecimalFormat formater = new DecimalFormat("####.00");
        if(size<1024){
            return size+"bytes";
        }else if(size<1024*1024){
            float kbsize = size/1024f;
            return formater.format(kbsize)+"KB";
        }else if(size<1024*1024*1024){
            float mbsize = size/1024f/1024f;
            return formater.format(mbsize)+"MB";
        }else if(size<1024*1024*1024*1024){
            float gbsize = size/1024f/1024f/1024f;
            return formater.format(gbsize)+"GB";
        }else{
            return null;
        }
    }
}