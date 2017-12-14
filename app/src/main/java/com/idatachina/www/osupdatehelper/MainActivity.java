package com.idatachina.www.osupdatehelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idatachina.www.osupdatehelper.receiver.MainServiceBroadcastReceiver;
import com.idatachina.www.osupdatehelper.util.AlarmServiceUtils;
import com.idatachina.www.osupdatehelper.util.FileUtils;
import com.idatachina.www.osupdatehelper.util.LogUtils;
import com.idatachina.www.osupdatehelper.util.MdmCommandUtils;
import com.idatachina.www.osupdatehelper.util.OsUtils;
import com.idatachina.www.osupdatehelper.util.PreferencesUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.GetDeviceInfo;

public class MainActivity extends AppCompatActivity {

    private GetDeviceInfo deviceInfo;


    private Integer updateOsStatus = 0;//-1：没有任务；1：等待下载；2：下载中；3：下载成功

    private Integer chooseDelayStatus = 0;//0：没有选择；1:正在选择

    private Integer checkUpdate = 0;

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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MdmCommandUtils.RESPONE);
        registerReceiver(MainServiceBroadcastReceiver.getInstance(),intentFilter);

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
                String lastStr = array[1];
                LogUtils.debug("[][][version:"+lastStr+"]");
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
                checkUpdate = 1;
                LogUtils.debug("[][][checkUpdate:"+checkUpdate+"]");
                updateButton.setText(R.string.checking);
                updateButton.setEnabled(false);
                LogUtils.debug(updateButton.getText().toString());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OsUpdateChecker.getInstance().check(getInstance(),null);
                    }
                },3*1000);
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
        alertDialogBuilder.setItems(new String[]{"1个小时后", "2个小时后", "3个小时后", "4个小时后"}, new DialogInterface.OnClickListener() {
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

    private void delayRunUpdate(int hours){
        if (null!=timeRunnable){
            LogUtils.info("[][][delayRunUpdate,stop auto time tip.]");
            stopTimeDown = true;
            handler.removeCallbacks(timeRunnable);
            timeRunnable = null;
        }
        LogUtils.debug("[][][update file not finish download,start auto check.]");
        long millis = SystemClock.elapsedRealtime() + hours*OsUpdateChecker.TIME_UNIT_HOUR;

        AlarmServiceUtils.addRunMainServiceIntent(_this,millis);
        updateTipView.setText(hours+"小时后执行升级");
        LogUtils.info("[][][after "+hours+" hours ,system to run update.]");
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
                OsUpdateChecker.getInstance().check(_this,null);
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

        LogUtils.debug("[][showClearSystemTip][start]");

        alertDialogBuilder.setIcon(null);
        alertDialogBuilder.setTitle("提示");
        alertDialogBuilder.setMessage("\n系统升级成功，即将重启并完成更新！\n\n");

        //监听下方button点击事件
        alertDialogBuilder.setPositiveButton(null,null);
        alertDialogBuilder.setNegativeButton(null,null);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();

        LogUtils.debug("[][showClearSystemTip][clean thread start]");

        new Thread(new Runnable() {
            @Override
            public void run() {

                LogUtils.debug("[][showClearSystemTip][clean thread run]");

                int  status = PreferencesUtils.getExecuteStatus(getInstance());
                if (2==status){

                    PreferencesUtils.setExecuteStatus(getInstance(),3);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            dialog.setMessage("正在清除iScan数据!\n\n");
                        }
                    });

                    String iScanPackageName="com.android.auto.iscan";
                    MdmCommandUtils.cleanAppData(getInstance(),iScanPackageName);
                    LogUtils.debug("[][showClearSystemTip][clean iscan data]");

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            dialog.setMessage("iScan数据清除完成，即将重启!\n\n");
                        }
                    });


                    try {
                        Thread.currentThread().sleep(3*1000);
                    }catch (Exception e){
                        LogUtils.error("",e);
                    }

                    MdmCommandUtils.reboot(getInstance());
                }else if (3==status){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            dialog.setMessage("正在清除历史数据!");
                        }
                    });

                    OsUpdateChecker.getInstance().deleteUpdateDescriberFile();
                    OsUpdateChecker.getInstance().deleteUpdatePackageFile(updatePackageName);
                    LogUtils.debug("[][showClearSystemTip][clean update package&descfile]");

                    try {
                        Thread.currentThread().sleep(3*1000);
                    }catch (Exception e){
                        LogUtils.error("",e);
                    }

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            dialog.setMessage("即将自卸载并完成更新!\n\n");
                        }
                    });

                    try {
                        Thread.currentThread().sleep(3*1000);
                    }catch (Exception e){
                        LogUtils.error("",e);
                    }

                    MdmCommandUtils.uninstallApplication(getInstance(),getPackageName());
                    LogUtils.debug("[][showClearSystemTip][uninstall myself]");

                    dialog.dismiss();
                    LogUtils.debug("[][][update success]");
                }else{
                    LogUtils.debug("[][][status error]");
                    //PreferencesUtils.setExecuteStatus(getInstance(),-1);
                }
            }
        }).start();
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
                OsUpdateChecker.getInstance().check(_this,null);
            }
        });

        //设置对话框是可取消的
        alertDialogBuilder.setCancelable(true);
        AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();
    }

    private void startUpdate(){

        if (!PreferencesUtils.hasReceiveBootStatus(getInstance())){
            LogUtils.debug("[][][app not receive boot broadcat,now reboot.]");
            Toast.makeText(getApplicationContext(),"系统即将重启，重启后开始升级！",Toast.LENGTH_LONG).show();
            PreferencesUtils.setExecuteStatus(getInstance(),1);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MdmCommandUtils.reboot(getInstance());
                }
            },3*1000);
        }else{
            LogUtils.debug("[][][app not receive boot broadcat,now reboot.]");
            PreferencesUtils.setExecuteStatus(getInstance(),2);
            Toast.makeText(getApplicationContext(),"即将重启并执行更新！",Toast.LENGTH_LONG).show();
            PreferencesUtils.setUpdateOsVersion(getInstance(),updatePackageVersion);
            String updatePackageFilePath = Environment.getExternalStorageDirectory()+"/"+OsUpdateChecker.OS_STORE_DIR_NAME+"/"+updatePackageName;
            if (2==parseType){
                updatePackageFilePath = updatePackageFilePath.replace("emulated/","sdcard");
            }
            OsUtils.sendBroadcast4UpdateOS(getInstance(),updatePackageFilePath);
        }
    }

    private void setViewDisplay(boolean hasTask){
        setViewDisplay(hasTask,false);
    }

    private Handler handler = new Handler();

    private boolean stopTimeDown = false;

    class TimeRunnable implements  Runnable {

        int ts = 180;


        public  TimeRunnable(){

        }

        @Override
        public void run() {
            if (stopTimeDown){
                LogUtils.info("[TimeRunnable][][ "+Thread.currentThread().getName()+",stop time count down.]");
                return;
            }
            LogUtils.info("[TimeRunnable][][ "+Thread.currentThread().getName()+",running,times:"+ts+";stopTimeDown:"+stopTimeDown+"]");
            if (0==chooseDelayStatus){
                updateTipView.setText(ts+"S后将重启并执行升级!");
                ts--;
                if (ts==0){
                    LogUtils.debug("[][][time is finish.]");
                    if (stopTimeDown){
                        LogUtils.info("[TimeRunnable][][ stop time count down.]");
                        return;
                    }
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

                int executeStatus = PreferencesUtils.getExecuteStatus(getInstance());
                if (1==executeStatus){
                    updateTipView.setText("即将重启并开始升级！");
                    startUpdate();
                }else{
                    boolean hasDelayExecute = false;

                    long nextRunMillis = PreferencesUtils.getMainServiceRunMillis(getInstance());
                    if (nextRunMillis>0){
                        long diffMillis = nextRunMillis - SystemClock.elapsedRealtime();
                        LogUtils.debug("[][setViewDisplay][diffMillis:"+diffMillis+"]");
                        if (diffMillis>0){

                        /*long nowMillis = System.currentTimeMillis();
                        Date date = new Date(nowMillis+diffMillis);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String dataStr = dateFormat.format(date);
                        updateTipView.setText("更新将会于："+dataStr+" 重新开始");*/

                            hasDelayExecute = true;

                            int hours =(int)(diffMillis/OsUpdateChecker.TIME_UNIT_HOUR);
                            LogUtils.debug("[][][hours:"+hours+"]");
                            long lessHours = diffMillis%OsUpdateChecker.TIME_UNIT_HOUR;
                            LogUtils.debug("[][][lessHours:"+lessHours+"]");
                            int minutes =(int) (lessHours/OsUpdateChecker.TIME_UNIT_MINUTE);
                            LogUtils.debug("[][][minutes:"+minutes+"]");

                            String tips ="";
                            if (hours>0){
                                tips += hours+"小时";
                            }
                            if (minutes>0){
                                tips += minutes+"分钟";
                            }

                            if (tips!=""){
                                tips+="后将重新开始!";
                            }else{
                                tips+="即将重新开始!";
                            }
                            updateTipView.setText(tips);
                            //LogUtils.debug("[][setViewDisplay]["+tips+"]");
                        }
                    }

                    LogUtils.debug("[][setViewDisplay][hasDelayExecute:"+hasDelayExecute+"]");

                    if (!hasDelayExecute){
                        startTimeCountDown();
                    }
                }
                updateTipLayout.setVisibility(View.VISIBLE);
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

    public  void setUpdateInfo(){
        setUpdateInfo(null,-1,-1,null,null);
    }

    public  void setUpdateInfo(String fileName,long fileSize,long downloadFileSize,String targetVersion,String updateLog){
        if (1==checkUpdate){
            checkUpdate = 0;
            updateButton.setText("检测更新");
            updateButton.setEnabled(true);
        }

        if (null==targetVersion){
            noTaskInfoView.setText("已是最新版本!");
            setViewDisplay(false);
            updateOsStatus=-1;
            LogUtils.debug("[][][not update package]");
            return;
        }

        updatePackageName = fileName;

        if (targetVersion.toLowerCase().equals(deviceSystemVersion)){
            LogUtils.debug("[][setUpdateInfo][os update success]");
            noTaskInfoView.setText("已是最新版本!");
            setViewDisplay(false);
            updateOsStatus = 9;
            showClearSystemTip();
            return ;
        }

        updatePackageVersion = targetVersion;

        String displaySize = FileUtils.getDisplaySize(fileSize);
        if (null!=displaySize){
            if (fileSize==downloadFileSize){
                displaySize+=" / 已下载完成";
                updateOsStatus = 3;
            }else if(downloadFileSize==0||downloadFileSize==-1){
                displaySize+=" / 等待下载";
                updateOsStatus = 1;
            }else{
                displaySize+=" / "+ FileUtils.getDisplaySize(downloadFileSize)+"，"+(String.format("%.2f", ((downloadFileSize*100.00)/fileSize)))+"%";
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


    private void startTimeCountDown(){
        if (null!=timeRunnable){
            stopTimeDown = true;
            handler.removeCallbacks(timeRunnable);
            timeRunnable = null;
        }

        stopTimeDown = false;
        timeRunnable = new TimeRunnable();
        handler.postDelayed(timeRunnable,1000);

        LogUtils.info("[][][activity startTimeCountDown]");
    }


    private void stopTimeCountDown(){
        if (null!=timeRunnable){
            stopTimeDown = true;
            handler.removeCallbacks(timeRunnable);
            timeRunnable = null;

            long startMilis = SystemClock.elapsedRealtime()+4*OsUpdateChecker.TIME_UNIT_HOUR;
            AlarmServiceUtils.addRunMainServiceIntent(getInstance(),startMilis);

            LogUtils.info("[][][activity stop,stop count down]");
        }
    }

    @Override
    protected void onStop() {
        LogUtils.debug("[MainActivity][onStop][]");
        stopTimeCountDown();
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LogUtils.debug("[MainActivity][onPostResume][]");
        OsUpdateChecker.getInstance().check(_this,null);
    }

    @Override
    protected void onDestroy() {
        LogUtils.debug("[MainActivity][onDestroy][]");
        stopTimeCountDown();
        super.onDestroy();
    }

}