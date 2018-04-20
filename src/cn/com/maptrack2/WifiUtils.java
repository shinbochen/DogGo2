package cn.com.maptrack2;

import java.util.List;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiUtils {

	private static final String 	TAG = WifiUtils.class.getSimpleName();
	
	private WifiManager localWifiManager;
	//private List<ScanResult> wifiScanList;
	private List<WifiConfiguration> wifiConfigList;
	private WifiInfo wifiConnectedInfo;
	private WifiLock wifiLock;
	
	public WifiUtils( Context context){
		localWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	
	public int WifiCheckState(){
		return localWifiManager.getWifiState();
	}	
	public void WifiOpen(){
		if(!localWifiManager.isWifiEnabled()){
			localWifiManager.setWifiEnabled(true);
		}
	}
	
	public boolean isWifiOpened( ){
		if( localWifiManager.getWifiState( ) == WifiManager.WIFI_STATE_ENABLED ||
				localWifiManager.getWifiState( ) == WifiManager.WIFI_STATE_ENABLED ){
			return true;
		}
		else{
			return false;			
		}
	}
		
	
	public boolean ifWifiEnabled( String targetSSID ){
		wifiConnectedInfo = localWifiManager.getConnectionInfo();
		
		if( targetSSID == wifiConnectedInfo.getSSID() ){
			return true;
		}
		else{
			return false;			
		}
	}
	public boolean ifWifiEnabledbyBSSID( String bssid ){
		wifiConnectedInfo = localWifiManager.getConnectionInfo();
		if( wifiConnectedInfo == null ){
			Log.e("DogGOWifiUtils", "getconnectinfo is null");
			return false;
		}
		String BSSID = wifiConnectedInfo.getBSSID();
		if(BSSID != null){
			BSSID = BSSID.replace(":", "-");
		}
		else{
			//Log.w(TAG, "ifWifiEnabledbyBSSID failed" + "BSSID is null");
			return false;			
		}
		
		//Log.e("DogGo", bssid+':'+BSSID );
		if( bssid.compareToIgnoreCase(BSSID) == 0 ){
			//Log.e("DogGo", "result is true");
			return true;
		}
		else{
			//Log.w(TAG, "ifWifiEnabledbyBSSID failed"+BSSID+':'+bssid);
			return false;			
		}
	}
	
	public String getWifiConnectBSSID( ){ 

		wifiConnectedInfo = localWifiManager.getConnectionInfo();
		String BSSID = wifiConnectedInfo.getBSSID();
		if(BSSID != null){
			BSSID = BSSID.replace(":", "-");		
		}
		else{
			BSSID = ""; 
		}
		return BSSID;		
	}
	
	public void WifiClose(){
		if(!localWifiManager.isWifiEnabled()){
			localWifiManager.setWifiEnabled(false);
		}
	}
	public void WifiStartScan(){
		localWifiManager.startScan();
	}
	public List<ScanResult> getScanResults(){
		return localWifiManager.getScanResults();
	}
	public List<String> scanResultToString(List<ScanResult> list){
		List<String> strReturnList = new ArrayList<String>();
		for(int i = 0; i < list.size(); i++){
			ScanResult strScan = list.get(i);
			String str = strScan.toString();
			boolean bool = strReturnList.add(str);
			if(!bool){
				Log.i("scanResultToSting","Addfail");
			}
		}
		return strReturnList;
	}
	public void getConfiguration(){ 
		wifiConfigList = localWifiManager.getConfiguredNetworks();//�õ����úõ�������Ϣ
		//for(int i =0;i<wifiConfigList.size();i++){
		//	Log.i("getConfiguration",wifiConfigList.get(i).SSID);
		//	Log.i("getConfiguration",String.valueOf(wifiConfigList.get(i).networkId));
		//}
	}
	public int IsConfiguration(String SSID){
		Log.i("IsConfiguration",String.valueOf(wifiConfigList.size()));
		for(int i = 0; i < wifiConfigList.size(); i++){
			Log.i(wifiConfigList.get(i).SSID,String.valueOf( wifiConfigList.get(i).networkId));
			if(wifiConfigList.get(i).SSID.equals(SSID)){//��ַ��ͬ
				return wifiConfigList.get(i).networkId;
			}
		}
		return -1;
	}
	public int AddWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){
		int wifiId = -1;
		for(int i = 0;i < wifiList.size(); i++){
			ScanResult wifi = wifiList.get(i);
			
			if(wifi.SSID.equals(ssid)){ 
				WifiConfiguration wifiCong = new WifiConfiguration();
				wifiCong.SSID = "\""+wifi.SSID+"\"";//\"ת���ַ���"
				wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK����
				wifiCong.hiddenSSID = false;
				wifiCong.status = WifiConfiguration.Status.ENABLED;
				wifiId = localWifiManager.addNetwork(wifiCong);//�����úõ��ض�WIFI������Ϣ���,�����ɺ�Ĭ���ǲ�����״̬���ɹ�����ID������Ϊ-1
				if(wifiId != -1){
					return wifiId;
				}
			}
		}
		return wifiId;
	}

	public int AddWifiConfig(String ssid,String pwd){
		int wifiId = -1;
		WifiConfiguration wifiCong = new WifiConfiguration();
		wifiCong.SSID = "\""+ssid+"\""; 			//"\""+wifi.SSID+"\"";
		wifiCong.preSharedKey = "\""+pwd+"\""; 	//"\""+pwd+"\"";
		wifiCong.hiddenSSID = false;
		wifiCong.status = WifiConfiguration.Status.ENABLED;
		wifiId = localWifiManager.addNetwork(wifiCong);	
		Log.e("DogGo", String.format("addnetwork result:%d", wifiId));
		return wifiId;
	}
	
	public boolean ConnectWifi(int wifiId){
		if( wifiConfigList != null ){
			for(int i = 0; i < wifiConfigList.size(); i++){
				WifiConfiguration wifi = wifiConfigList.get(i);
				if(wifi.networkId == wifiId){
					while(!(localWifiManager.enableNetwork(wifiId, true))){//�����Id����������
						Log.i("ConnectWifi",String.valueOf(wifiConfigList.get(wifiId).status));//status:0--�Ѿ����ӣ�1--�������ӣ�2--��������
					}
					return true;
				}
			}
		}
		return false;
	}

	public boolean DisconnectWifi(String address){
		String		BSSID;
		
		for(int i = 0; i < wifiConfigList.size(); i++){
			WifiConfiguration wifi = wifiConfigList.get(i);
			if(  wifi.BSSID == null ){
				continue;
			}
			BSSID = wifi.BSSID.replace(":","-");
			if(BSSID.compareToIgnoreCase(  address ) == 0){
				boolean flag;
				wifi.preSharedKey = "";
				flag = localWifiManager.disableNetwork(wifi.networkId);
				Log.w(TAG, "disableNetwork "+ wifi.networkId + " result:" + flag );
				flag = localWifiManager.removeNetwork(wifi.networkId);
				Log.w(TAG, "removeNetwork "+ wifi.networkId + " result:" + flag );
				flag = localWifiManager.disconnect();
				Log.w(TAG, "disconnect "+ wifi.networkId + " result:" + flag );
				break;
			}
		}
		return true;
	}
	
	public boolean DisconnectWifi( ){
		String		BSSID;

		wifiConnectedInfo = localWifiManager.getConnectionInfo();
		BSSID = wifiConnectedInfo.getBSSID();
		
		for(int i = 0; i < wifiConfigList.size(); i++){
			WifiConfiguration wifi = wifiConfigList.get(i);
			if(  wifi.BSSID == null ){
				continue;
			}
			if(BSSID.compareToIgnoreCase(  wifi.BSSID ) == 0){
				boolean flag;
				wifi.preSharedKey = "";
				flag = localWifiManager.disableNetwork(wifi.networkId);
				Log.w("DogGo"+TAG, "disableNetwork "+ wifi.networkId + " result:" + flag );
				//flag = localWifiManager.removeNetwork(wifi.networkId);
				//Log.w(TAG, "removeNetwork "+ wifi.networkId + " result:" + flag );
				flag = localWifiManager.disconnect();
				Log.w("DogGo"+TAG, "disconnect "+ wifi.networkId + " result:" + flag );
				break;
			}
		}
		return true;
	}
	
	public void createWifiLock(String lockName){
		wifiLock = localWifiManager.createWifiLock(lockName);
	}	
	public void acquireWifiLock(){
		wifiLock.acquire();
	}
	public void releaseWifiLock(){
		if(wifiLock.isHeld()){
			wifiLock.release();
		}
	}
	public void getConnectedInfo(){
		wifiConnectedInfo = localWifiManager.getConnectionInfo();
	}
	public String getConnectedMacAddr(){
		return (wifiConnectedInfo == null)? "NULL":wifiConnectedInfo.getMacAddress();
	}
	public String getConnectedSSID(){
		return (wifiConnectedInfo == null)? "NULL":wifiConnectedInfo.getSSID();
	}
	public int getConnectedIPAddr(){
		return (wifiConnectedInfo == null)? 0:wifiConnectedInfo.getIpAddress();
	}
	public int getConnectedID(){
		return (wifiConnectedInfo == null)? 0:wifiConnectedInfo.getNetworkId();
	}	
	public int getConnectedRssi( ){
		return (wifiConnectedInfo == null)? 0:wifiConnectedInfo.getRssi();
	}
}
