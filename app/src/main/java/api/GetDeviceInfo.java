package api;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Get device info
 * @author chaizsz
 *
 */
public class GetDeviceInfo {

	private Context mcontext;

	public GetDeviceInfo(Context context){
		
		mcontext = context;
	}

	static
	{
		System.loadLibrary("DeviceJni");
	}
	
	private native String getProperties(String name);
	
	/*
	 *  函数介绍：获取设备MDM API LEVEL
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getapilevel(){

		return getProperties("persist.idata.mdm.api.level");
	}
	
	/*
	 * 函数介绍：获取设备ROM_UID
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getrom_uid(){

		return getProperties("persist.idata.rom.uid");
	}
	
	/*
	 *函数介绍：获取设备ROM_VER
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getrom_ver(){

		return getProperties("persist.idata.rom.ver");
	}
	
	public String getdevice_type(){
		
		return getProperties("persist.idata.device.type");
	}
	
	/*
	 * not used
	 * 函数介绍：获取设备SN码
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getsn(){

		//SN码为IMEI码后7位
		//1.从imei中截取？
		//2.反射获取？
//		return android.os.SystemProperties.get("sn");
		return "7654321";
		//3.
		//参照http://blog.csdn.net/hpccn/article/details/22684953
		
		//SIM卡序列码
//		TelephonyManager telephonyManager = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
//		return telephonyManager.getSimSerialNumber();
		
		// TODO 获取到01234567ABCDEF
//		String v = "";
//		String []propertys = {"ro.boot.serialno", "ro.serialno"};   
//        for (String key : propertys){
////          String v = android.os.SystemProperties.get(key);  
//            v = getAndroidOsSystemProperties(key);  
//        }
//        return v;
		
	}
	
	static Method systemProperties_get = null;
	static String getAndroidOsSystemProperties(String key) {
	    String ret;
	    try {
	    	systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);  
	        if ((ret = (String) systemProperties_get.invoke(null, key)) != null)
	        return ret;  
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return "";  
	    }
	    return "";  
	}

	/*
	 * 函数介绍：获取设备IMEI
	 * 输入3hnj参数：void
	 * 输出参数：void
	 */
	public String getimei(){
		
		//一般为15位，飞行模式下部分设备会变成14位，待测试
		TelephonyManager telephonyManager = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	/*
	 * 函数介绍：获取wifi的mac地址
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getwifimac(){

		WifiManager wifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if( null == wifiInfo){
			return "";
		}
		return wifiInfo.getMacAddress();
	}
	
	/*
	 * 函数介绍：获取wifi的ssid
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getssid(){
		
		WifiManager wifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getSSID();
	}

	/*
	 * 函数介绍：获取wifi的bssid
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getbssid(){
		
		WifiManager wifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getBSSID();
	}
	
	/*
	 * 函数介绍：获取蓝牙mac地址
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getbtmac(){
		
		String result = "";
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null){
			return "";
		}
//		if (!bluetoothAdapter.isEnabled()){
//			bluetoothAdapter.enable();
//			result = bluetoothAdapter.getAddress();
//			bluetoothAdapter.disable();
//		}else{
//			result = bluetoothAdapter.getAddress();
//		}
		result = bluetoothAdapter.getAddress();
		if(result == null){
			result = "";
		}
		return result;
	}

	/*
	 * 函数介绍：获取androidid
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getandroidid(){
		
		//恢复出厂设置会重置此ID，待测试
		return android.provider.Settings.Secure.getString(mcontext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	}
	
	/*
	 * 函数介绍：获取platform型号
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getplateformtype(){
		
		return System.getProperty("ro.hw_platform");
	}
	
	/*
	 * 函数介绍：屏幕分辨率
	 * 输入参数：void
	 * 输出参数：分辨率字符串
	 */
	public String getscreenresolution(){
		
//		仅Activity中可用
		
//		1.
//		WindowManager windowManager = getWindowManager();
//		Display display = windowManager.getDefaultDisplay();
//		int width = display.getWidth();
//		int height = display.getHeight();
		
//		2.
//		WindowManager windowManager = getWindowManager();
//		DisplayMetrics displayMetrics = windowManager.getDefaultDisplay().getMetrics(displayMetrics);
//		int width = displayMetrics.widthPixels;
//		int height = displayMetrics.heightPixels;
		
//		非Activity亦可用
		
//		3.
		DisplayMetrics displayMetrics = mcontext.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		
		return String.valueOf(width) + "*" + String.valueOf(height); 
	}
	
	/*
	 * 函数介绍：获取SIM卡ISMI
	 * 输入参数：void
	 * 输出参数：void
	 */
	public String getimsi(){
//		需导入layoutlib库，TelephonyProperties找不到
//		return SystemProperties.get(android.telephony.TelephonyProperties.PROPERTY_IMSI);
		
		//双卡双待处理，原生SDK不支持双卡双待，用一般的方法只能读取到主卡号
		//或者看framework层修改后提供的方法或者其他特殊处理
		//ISMI
//		需要权限
//	    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
		TelephonyManager telephonyManager = (TelephonyManager) mcontext.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}
	
	public String getModel(){
		
		return android.os.Build.MODEL;
	}
	
	

	public String getManufacturer(){
		
		return android.os.Build.MANUFACTURER;
	}

	public String getDevice(){
		
		return android.os.Build.DEVICE;
	}

	public String getBoard(){
		
		return android.os.Build.BOARD;
	}

	public String getHardware(){
		
		return android.os.Build.HARDWARE;
	}

	public String getCpuabi(){
		
		return android.os.Build.CPU_ABI;
	}

	public String getId(){
		
		return android.os.Build.ID;
	}

	public String getDisplay(){
		
		return android.os.Build.DISPLAY;
	}

	public String getFingerprint(){
		
		return android.os.Build.FINGERPRINT;
	}

	public String getRelease(){
		
		return android.os.Build.VERSION.RELEASE;
	}
	
	/*
	 * 函数介绍：获取硬件平台
	 * 输入参数：void
	 * 输出参数：
	 */
	public String getPlatform(){

		return getProperties("ro.hw_platform");
	}
	
	public String getProduct(){
		
		return getProperties("ro.build.product");
	}

	/*
	 * 函数介绍：获取版本
	 * 输入参数：void
	 * 输出参数：
	 */
	public String getVersion(){
		
		return getProperties("ro.build.sw.version");
	}

	
	/*
	 * 函数介绍：获取主机名
	 * 输入参数：void
	 * 输出参数：
	 */
	public String getNethostname(){

		return getProperties("net.hostname");
	}

	
	/*
	 * 函数介绍：获取设备码
	 * 输入参数：void
	 * 输出参数：
	 */
	public String getDevicecode(){
		
		return getProperties("persist.idata.device.code");
	}
	
	/*
	 * 获取自定义版本号
	 */
	public String getDeviceVersion(){
		String deviceVersion = getProperties("ro.custom.build.version");
		if(null == deviceVersion || ("").equals(deviceVersion)){
			//A7专用
			deviceVersion = getProperties("ro.idata.version.release");
		}
		return deviceVersion;
	}
	
	
	
}
