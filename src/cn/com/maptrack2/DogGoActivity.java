package cn.com.maptrack2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.maptrack2.SQLDataHelper;
import cn.com.maptrack2.WifiUtils;

import cn.com.maptrack2.R;


public class DogGoActivity extends Activity 
/*implements OnTouchListener*/ {
	
	private static final long 	SCAN_PERIOD = 3000L;
	private static final int 	REQUEST_ENABLE_BT = 1;
	private static final int	REQUEST_SCAN_BT = 2;
	private static final int 	REQUEST_ENABLE_WIFI = 3;

	private final static String DBNAME = "DoGoDB";
	private final static String MAPURL = "file:///android_asset/JSData/main.html";	
    private final static String TAG = DogGoActivity.class.getSimpleName();
    private final static String SERVERIP = "192.168.4.1";
    private final static int	SERVERPORT = 8888;
    private final static String WIFIPSD = "doggo001";
    
    
    
    


	private BluetoothAdapter 					m_BluetoothAdapter;
    private BluetoothLeServiceM					m_BluetoothLeService = null;
	private GestureDetector 					m_GestureDetector = null;
	
	private SQLDataHelper						m_db;    
	private Handler 							m_Handler;  
	
	    
    public  static ArrayList<DeviceData>		m_lstDeviceData;
    private MyTimer								m_timer;
    private WebView								m_webView;
    private	boolean								m_pageLoaded = false;
    public int									m_BackPressCnt = 0;	
    private Intent								m_musicIntent;
    private int									m_connectcnt = 0;

	private WifiUtils 							m_wifiUtils;
    public  ClientThread 						m_clientThread;  
    String								m_prevBSSID;		// 记录前一个连接的BSSID
    boolean										m_bActive;
    private int									m_nFailedCnt;		// 记录wifi连续失败的次数
    int											m_nSendFailedCnt;	// 记录发送后没收到数据的记数
    
    /*
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        	runOnUiThread(new Runnable() {
				@Override
                public void run() {
					
					Log.i(TAG, "LeScan find:"+device.getName()+'@'+device.getAddress()+"rssi:"+String.valueOf(rssi) );
					if( DogGoActivity.AddDeviceData(device, rssi ) ){
			            String js = String.format("javascript:onNewDeviceFind('%s')", device.getAddress() );
			    		updateWebView(js);  	
			    	}
			    	else{   
			    		Log.i(TAG, "New Device Find but page not load or dont need update!");
			    	}
                }
            });
        }
    };    
    */
    /*
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            Log.w(TAG, "onServiceConnected Start:");
            m_BluetoothLeService = ((BluetoothLeServiceM.LocalBinder)service).getService();
            if (!m_BluetoothLeService.initialize()) {
                Log.w(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.w(TAG, "onServiceConnected: initialize ok");
            //Log.w(TAG, "wait scan end!");
           // m_Handler.postDelayed(new Runnable() {
    	   //     @Override
    	   //     public void run() {
    	        	connectall();
    	    //    }
    	    //}, SCAN_PERIOD);
        }
		
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w(TAG, "onServiceDisconnected:"+componentName);
        	m_BluetoothLeService.disconnect();
        	m_BluetoothLeService.close();
            m_BluetoothLeService = null;
        }
    };
    */
    // 接受来自于socket的信息
    private final Handler socketRecvhandler = new Handler() { 
        @Override  
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
            // 如果消息来自子线程  
        	switch( msg.what){
        	case ClientThread.RECV_SOCKET_DATA:
        		String addr;
        		String data;
        		String obj;
        		int 	len;
        		
        		obj = msg.obj.toString();
        		
        		Log.e( TAG, "recv data:"+ obj ); 

        		m_nSendFailedCnt = 0;
        		if( (len= obj.indexOf(':')) != -1 ){
        			
        			addr = obj.substring(0,len);
        			data = obj.substring(len+1);   
        			          		
        			onRead( addr, data);
        		}
        		break;
        		
        	case ClientThread.SOCKET_DISCONNECT:
        		Log.e(TAG, "socket disconnect:" + msg.obj.toString());
        		if( msg.obj != null){
            		changeSocketState( msg.obj.toString(), DeviceData.SOCKET_DISCONNECT );
    	            String js = String.format("javascript:onSocketDisConnected('%s')", msg.obj.toString());
    	            updateWebView( js );
        			changeDeviceBattery(msg.obj.toString(), 0 );
        		}
        		break;
        		
        	case ClientThread.SOCKET_CONNECT:   
        		Log.e(TAG, "socket connect:" + msg.obj.toString());
        		if( msg.obj != null){
        			changeSocketState( msg.obj.toString(), DeviceData.SOCKET_CONNECT );
    	            String js = String.format("javascript:onSocketConnected('%s')", msg.obj.toString());
    	            updateWebView( js );
    				write( msg.obj.toString(), Protocol.MakeAck()+"\r\n", 3 );		
        		}        		
        		break;
        	
        	} 
        }  
    };  
     /*
    private final BroadcastReceiver BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
            final String action = intent.getAction();
            final String address = intent.getStringExtra( BluetoothLeServiceM.DEVICE_ADDRESS );
            String js = null;
            
           
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals( action )) {
                Log.w( TAG, address+':'+action);
                // 防止误触发。实测时发现连接信号与断开信号会重复发送
                if( isConnected(address) ){
                	Log.w(TAG, address+':'+"single repeat!" );
                }
                else{
	            	changeConnectState( address, BluetoothLeServiceM.STATE_CONNECTED);
	            	js = String.format("javascript:onDeviceConnected('%s')", address); 
	            	updateWebView( js );
                }
                // 停止倒计数
                setConnectCountDown( address, -1 );
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.w( TAG, address+':'+action);

                // 防止误触发。实测时发现连接信号与断开信号会重复发送
                if( isConnected(address) ){                
	            	changeConnectState( address, BluetoothLeServiceM.STATE_DISCONNECTED);
	            	changeRssi( address, "0" );
	            	
	            	if( isNeedAutoConnect( address ) ){
	            		m_Handler.postDelayed(new Runnable(){public void run(){  connect( address );} }, 1000);
	            	}
	            	js = String.format("javascript:onDeviceDisConnected('%s')", address);
	            	updateWebView( js );
                }
                else{
                	Log.w(TAG, address+':'+"single repeat!" );
                }
                // 停止倒计数
                setConnectCountDown( address, -1 );
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.w( TAG, address+':'+action);
            	enableRS232IO( address );
                //displayGattServices(m_BluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_RSSI_AVAILABLE.equals(action)) {
            	Log.w(TAG, address+":rssi=" + intent.getStringExtra(BluetoothLeService.EXTRA_DATA) );

                // 停止倒计数
                setConnectCountDown( address, -1 );
                
            	changeRssi( address, intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            	//
            	js = String.format("javascript:onDeviceRSSIChange('%s','%s', '%s')", address, getDeviceData(address).m_name, intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            	updateWebView( js );
            } else if (BluetoothLeService.ACTION_WRITE_COMPLETE.equals(action)) {
                Log.w( TAG, address+':'+action);
            	js = String.format("javascript:onDeviceDataWrite('%s')", address);
            	updateWebView( js );
                
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.w( TAG, address+':'+action);
                // 停止倒计数
                setConnectCountDown( address, -1 );
                onRead( address, intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
            else if( BluetoothLeServiceM.ACTION_ASK_CONNECT.equals( action ) ){
                Log.w( TAG, address+':'+action);
            	connect( address );
            }
            else if( BluetoothLeServiceM.ACTION_ASK_DISCONNECT.equals( action ) ){
                Log.w( TAG, address+':'+action);
            	disconnect( address );
            }
        }
    };*/
    public static ArrayList<DeviceData> getAllDeviceData( ){
    	return m_lstDeviceData;
    }
    public static void changeDeviceMode( String address, int action ){
    	
    	for( DeviceData data : m_lstDeviceData ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				data.changeMode( action );
			}				 
		}
    	return;
    	
    }
    public void  setConnectCountDown( String address, int cnt ){
    	for( DeviceData data : m_lstDeviceData ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				data.setConnectedCountDown(cnt);
			}				 
		}
    	return;
    }
    
    public void showMsg( String str, boolean longtime ){
    	if( longtime ){
    		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    		
    	}
    	else{
    		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    	}
    }
    /*
    public void restartBluetooth(  boolean off ){
    	boolean  flag = false;
    	
    	if( off ){
    		m_BluetoothLeService.RemoveAllBluetoothGatt();
	    	flag = turnOffBluetooth();
	    	showMsg( "Bluetooth failed, Restarting...!", true );
	    	Log.w(TAG, "turnOffBlurTooth result:"+String.valueOf(flag) );

			m_Handler.postDelayed(
				new Runnable(){
					public void run(){
						restartBluetooth( false );
					} 
				}, 
				1000
			);
    	}
    	else{
    		flag = turnOnBluetooth();
    		Log.w(TAG, "turnOnBlurTooth result:"+String.valueOf(flag) );
    		setAllRssiZero( );
    		Log.w(TAG, "set all rssi to zero" );

	    	showMsg( "Please Refresh and connect again！", true );

			//m_Handler.postDelayed(
			//	new Runnable(){
			//		public void run(){
			//			m_connectcnt = 0;
			//			connectall( );
			//		} 
			//	}, 
			//	6000
			//);
    	}
    }
    */
    /*
    // 每秒调用一次
    public void  calcConnectCountDown( ){
    	String js = null;
    	for( DeviceData data : m_lstDeviceData ){
			if( data.calcConnectCountDown() ){
				Log.w( TAG, data.m_address+": connect failed! need do something!" );
				if( data.isConnected() ){					
					Intent intent = new Intent( BluetoothLeServiceM.ACTION_GATT_DISCONNECTED );
					intent.putExtra(BluetoothLeServiceM.DEVICE_ADDRESS, data.m_address);
					sendBroadcast( intent );	            	
				}  
				m_Handler.postDelayed(
					new Runnable(){
						public void run(){
							restartBluetooth( true );
						} 
					}, 500
				);
			}				 
		}    	
    }
    */
    public static void changeDeviceBattery( String address, int battery ){
    	
    	for( DeviceData data : m_lstDeviceData ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				data.changeBattery( battery );
			}				 
		}
    	return;
    	
    }
    
    public static DeviceData getDeviceData( String address){
    	for( DeviceData data : m_lstDeviceData ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				return data;
			}				 
		}
    	return null;
    }
    // 得到参数
    public String[] getParam( String address, int action ){  
    	
    	String[]	result = null;
    	
		for( DeviceData data : m_lstDeviceData ){
		  if( data.m_address.compareToIgnoreCase(address) == 0 ){
			  result = data.getPara( action );
		  }    		  
		}
    	return result;
    	
    }
    /*
    // 如果需要更新返回真
    public static boolean AddDeviceData( BluetoothDevice device, int rssi ){  
  	  boolean	find = false;
  	  boolean 	bUpdateDisplay = false;
	  
  	  //Log.w(TAG, "AddDeviceData:"+device.getName()+':'+device.getAddress() );
  	  for( DeviceData data : m_lstDeviceData ){
  		  
  		  if( data.m_address.compareTo( device.getAddress() )  == 0 ){
  			  find = true;
  			  if( data.m_rssi == 0 ){ 
  				  Log.w(TAG, device.getName()+':'+ "rssi got it!");
  				  bUpdateDisplay = true;
  			  }
  			  data.m_rssi = rssi;
  			  break;
  		  }
  	  }    	  
  	  if( !find ){
		Log.w(TAG, device.getName()+':'+ "new device find!");
  		m_lstDeviceData.add( new DeviceData( device.getName(), device.getAddress(), rssi ) );
  		bUpdateDisplay = true;
  	  }
  	  return bUpdateDisplay;
    }
    */
    // 如果需要更新返回真
    public static boolean AddWifiData( ScanResult device ){  
  	  boolean	find = false;
  	  boolean 	bUpdateDisplay = false;
	  
  	  for( DeviceData data : m_lstDeviceData ){
  		  
  		  if( data.m_address.compareTo( device.BSSID.replace(":", "-") )  == 0 ){
  			  find = true;
  			  if( data.m_rssi == 0 ){ 
  				  bUpdateDisplay = true;
  			  }
  			  data.m_rssi = device.level;
  			  break;
  		  } 
  	  }    	  
  	  if( !find ){
		if( device.SSID.indexOf("DogGo") != -1 ){
			DeviceData d  = new DeviceData( device.SSID, device.BSSID.replace(":", "-"), device.level );
			// 设定自动重连
			d.m_autoConnected = true;   
			m_lstDeviceData.add( d );
			bUpdateDisplay = true;
		}
  	  }
  	  return bUpdateDisplay;
    }
    
    public boolean isConnected( String address ){
    	boolean	flag = false;
    	for( DeviceData data : m_lstDeviceData ){
    		  if( data.m_address.compareTo( address )  == 0 ){
    			  flag = data.isConnected();
    			  break;
    		  }
    	}    	  
		return flag;    	
    }
    public boolean isNotConnected( String address ){
    	boolean	flag = false;
    	for( DeviceData data : m_lstDeviceData ){
    		  if( data.m_address.compareTo( address )  == 0 ){
    			  flag = data.isDisConnected();
    			  break;
    		  }
    	}    	  
		return flag;    	
    }
    
    public boolean isNeedAutoConnect( String address ){
    	boolean	need = false;
    	for( DeviceData data : m_lstDeviceData ){
    		  if( data.m_address.compareTo( address )  == 0 ){
    			  need = data.isAutoConnect();
    			  break;
    		  }
    	}    	  
		return need;    	
    }
    /*
	public boolean turnOnBluetooth(){
         if (m_BluetoothAdapter != null)
         {
             return m_BluetoothAdapter.enable();
         }
         return false;
	}
	public boolean turnOffBluetooth(){
	   	if (m_BluetoothAdapter != null){
	   		return m_BluetoothAdapter.disable();
	   	}
	   	return false;
	}*/
	
	public void scanWifiDevice( ){

		// 无条件打开wifi
		m_wifiUtils.WifiOpen();
		m_wifiUtils.WifiStartScan();

		m_Handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {

	        	List<ScanResult> listScan = m_wifiUtils.getScanResults();
	    		// 得到已有的配置
	    		m_wifiUtils.getConfiguration();
	    			    		
	    		for(int i = 0; i <listScan.size(); i++){
	    			ScanResult wifi = listScan.get(i);
	    				    			
	    			if( DogGoActivity.AddWifiData(wifi ) ){
			            String js = String.format("javascript:onNewDeviceFind('%s')", wifi.BSSID.replace(":", "-") );
			    		updateWebView(js);  	     
			    	}
			    	else{   
			    		//Log.i(TAG, "New Device Find but page not load or dont need update!");
			    	}
	    		}
	        }
	    }, 1000);        		
		
	}
	/*
	public void scanLeDevice( ){

		String js = null;
		if( m_BluetoothAdapter != null && m_BluetoothAdapter.isEnabled()){
			Log.i( TAG, "scanLeDevice begin");
			m_BluetoothAdapter.startLeScan( mLeScanCallback );		
			m_Handler.postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	stopScan();
		        }
		    }, SCAN_PERIOD);
  
	    	js = String.format("javascript:onStartScanLe( )");
            updateWebView( js );
		}
	}
	public void stopScan( ){
		String js = null;
		if( m_BluetoothAdapter != null && m_BluetoothAdapter.isEnabled()){
			Log.w( TAG, "scanLeDevice stop");
			
			m_BluetoothAdapter.stopLeScan(mLeScanCallback);
		}    
    	js = String.format("javascript:onStopScanLe( )");
        updateWebView( js );
	}*/
	/*
    public  void  buildService( ){
    	Log.w( TAG, "buildService start!");
        Intent intent = new Intent(this, BluetoothLeServiceM.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);    	
    }
    */
	/*
    public void connectall( ){    
		Log.w( TAG, "connect all start");
    	if( m_BluetoothLeService == null ){
    		Log.w( TAG, "no connect service!");
    		return;
    	}
    	if( m_connectcnt < m_lstDeviceData.size() ){
    		DeviceData data = m_lstDeviceData.get(m_connectcnt++);
    		if( data.isAutoConnect() ){
    			Log.w( TAG, "connect:"+data.m_name);
    			if( m_BluetoothLeService.connect( data.m_address )){
    				Log.w( TAG, "connecting:" + data.m_address );	
    			}
    			else{
    				Log.w( TAG, "connect failed:" + data.m_address );				
    			}
    		}  

        	if( m_connectcnt < m_lstDeviceData.size() ){
        		m_Handler.postDelayed(new Runnable() {
    		        @Override
    		        public void run() {
    		        	connectall();
    		        }
    		    }, 500);        		
        	}
        	else{
        		Log.w( TAG, "connect all end");
        		
        	}
    	}   	
    }
    */

	// 只要程序运行就不断连接wifi. 用来唤醒终端
    public void connectwifi_all( ){  
    	Log.w( TAG, "connect wifi all begin");
    	for( DeviceData data : m_lstDeviceData ){
        	Log.w( TAG, "connect wifi loop");
    		// 如果发现一个已经存在的链接不再连接
    		if( m_wifiUtils.ifWifiEnabledbyBSSID(data.m_address) ){
    			Log.i(TAG, data.m_address + "have connect ok. end!");
    			break;    			
    		}    	
        	Log.w( TAG, "connect wifi loop1");	
    		// 遇到第一个连接且有信号就链第一个
    		if( data.isAutoConnect() && (data.m_rssi != 0) ){

    			
				connectwifi( data.m_address );
				break;
    		} 
    	}
    }
    
	
	/*
	public void connect( String address ){		
    	if( m_BluetoothLeService == null ){
    		return;
    	}		
    	// 纠正有时系统会状态乱的情况
		if( isConnected( address) ){
			Log.w(TAG, "need connect:"+address+" but has connected!");
		}
		else{		
			Log.w( TAG, "connect:" + address );
			if( m_BluetoothLeService.connect( address )){	
				Log.w( TAG, "connecting:" + address );	
			}
			else{
				Log.w( TAG, "connect failed:" + address );				
			}
		}
	}
	*/
	public void connectwifi( String address  ){	
		
    	// 纠正有时系统会状态乱的情况
		if( isConnected( address) ){
			Log.w(TAG, "need connect:"+address+" but has connected!");
		}
		else if( isNotConnected( address ) ){						
			DeviceData data = getDeviceData( address );

			m_wifiUtils.DisconnectWifi( );
			
			//try {
			//	Thread.sleep(100);
			//} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			Log.e( TAG, "connect " + data.m_name +":" + WIFIPSD );
			int netId = m_wifiUtils.AddWifiConfig( data.m_name, WIFIPSD);	
			Log.e( TAG, "connect hello" + data.m_name +":" + WIFIPSD );			
			if(netId != -1){      
				Log.e( TAG, "Disconnect wifi end");
				m_wifiUtils.getConfiguration();
				if(m_wifiUtils.ConnectWifi(netId)){
					changeConnectState( address, BluetoothLeServiceM.STATE_CONNECTING );

	            	//String js = String.format("javascript:onDeviceConnected('%s')", address); 
	            	//updateWebView( js );
	            	//Log.e(TAG, js);
	            	
					//Log.w( TAG, "connect " + address + "success!");
				}
				else{			
					//changeConnectState( address, BluetoothLeServiceM.STATE_DISCONNECTED );

					//String js = String.format("javascript:onDeviceDisConnected('%s')", address);
	            	//updateWebView( js );
	            	
					Log.e( TAG, "connect " + address + "failed!" );		
				}
			}
			else{
				Log.e( TAG, "AddWifiConfig " + address + "failed!" );		
				Toast.makeText(this, "save config failed!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/*
	public void disconnect( String address ){		
    	if( m_BluetoothLeService == null ){
    		return;
    	}
		Log.w( TAG, "disconnect:" + address );
		m_BluetoothLeService.disconnect( address );
		scanLeDevice( );
	}*/
	
	public void disconnectwifi( String address ){	
		m_wifiUtils.DisconnectWifi( address );   
		
		changeConnectState( address, BluetoothLeServiceM.STATE_DISCONNECTED );
		String js = String.format("javascript:onDeviceDisConnected('%s')", address);
    	updateWebView( js );
    	
		return;
	}
	/*
	public void enableRS232IO( String address ){
    	if( m_BluetoothLeService == null ){
    		return;
    	}
		m_BluetoothLeService.enableRS232IO(address, true);
		Log.w(TAG, address+":enable RS232 end");
	}*/
	/*
	public void write( String address, String data ){
    	if( m_BluetoothLeService == null ){
    		return;
    	}
		m_BluetoothLeService.writeRS232Data(address, data);
    }
	*/
	public void write( String address, String data, int nCnt){	
		
		if( m_clientThread.isConnected() ){
			m_nSendFailedCnt = nCnt;    
			Log.w(TAG, "send data->"+ address +":" + data );
			m_clientThread.sendData(data);
		}
	} 
	
	
    public void onRead(String address, String data) {
    	
        if (data != null) {
        	Log.w(TAG, "Read Data:"+data );
        	int[]	result = Protocol.Parse( data );
        	String	actions = new String();
        	for( int i = 0; i < result.length; i++){
        		if( actions.length() >0 ){
        			actions += "|||";
        		}
        		actions += Integer.toString(result[i]);
        		// battery
        		if( result[i] == 6){
        			m_nSendFailedCnt = 6;
        			changeDeviceBattery(address, Protocol.GetBattery(data));
        		}
        		else{
        			changeDeviceMode( address, result[i]);
        		}
        	}
        	String js = String.format("javascript:onDeviceDataRead('%s','%s')", address, actions );
        	updateWebView(js);
        }
    } 
    
    public void updateWebView( final String js ){
    	if( m_pageLoaded ){ 
    		runOnUiThread(new Runnable() {
				@Override
                public void run() {
					m_webView.loadUrl( js );
				}
    		});  
    	}    	
    }
    
    /*
    public void readRssi( ){ 
    	m_Handler.post(new Runnable(){

			@Override
			public void run() {
				if( m_BluetoothLeService == null ){ 
		    		return;
		    	}
		    	for( DeviceData data : m_lstDeviceData ){
		    		if( data.isConnected() ){
		    			if( m_BluetoothLeService.readRssi( data.m_address ) == false ){
		    				Log.w(TAG, data.m_address+"connected failed" );
		    				data.m_connected = BluetoothLeServiceM.STATE_DISCONNECTED;
		    			}
		    			// 不要重复设
		    			if( data.getConnectedCountDown( ) == -1 ){
		    				data.setConnectedCountDown( 5 );
		    			}
		    		}
		    	} 
			}    		
    	});	
    }*/
    
    public void readRssi( ){ 
    	
    	boolean  flag;
    	long t = System.currentTimeMillis()/1000;
		// 每两秒扫一次
		if( t%2 == 0 ){
			m_wifiUtils.WifiStartScan();
		}
		//try {
		//	Thread.sleep(1000);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}   
		
    	List<ScanResult> listScan = m_wifiUtils.getScanResults();
    	
    	// 如果有结果先把所有信号清0.等待更新
    	// 先清0信号
    	//if( listScan.size() > 0 ){	        		
    	for( DeviceData data : m_lstDeviceData){
    		data.m_rssi = 0;
    	}   	        		
    	//}
    	
    	if( listScan.size() > 0 ){
			for(int i = 0; i <listScan.size(); i++){
				ScanResult wifi = listScan.get(i);	    			
				
				String BSSID = wifi.BSSID.replace(":", "-");
				
				if( DogGoActivity.AddWifiData(wifi ) ){
					// 统一到下面更新
		            //String js = String.format("javascript:onNewDeviceFind('%s')", BSSID );
		    		//updateWebView(js); 
		            Log.w(TAG, "readrssi->:" + wifi.BSSID +':'+ wifi.level  ); 	     
		    	}
				else{
	    			if( changeRssi( BSSID, wifi.level )){
		            	String js = String.format("javascript:onDeviceRSSIChange('%s','%s', '%d')", BSSID, getDeviceData(BSSID).m_name, wifi.level);
		            	updateWebView( js );
		            	Log.w(TAG, "readrssi:"+js );
	    			}
				}
			}
    	}
    	
    	String js = String.format("javascript:reloadDeviceList( )");
    	updateWebView( js );   

    	/*
    	m_Handler.post(new Runnable(){
			@Override
			public void run() {
				

				long t = System.currentTimeMillis()/1000;
				// 每两秒扫一次
				if( t%2 == 0 ){
					m_wifiUtils.WifiStartScan();
				}
				//try {
				//	Thread.sleep(1000);
				//} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}   
				
	        	List<ScanResult> listScan = m_wifiUtils.getScanResults();
	        	
	        	// 如果有结果先把所有信号清0.等待更新
	        	///if( listScan.size() > 0 ){	        		
	            //	for( DeviceData data : m_lstDeviceData){
	            //		data.m_rssi = 0;
	            //	}   	        		
	        	//}
	        	
	    		for(int i = 0; i <listScan.size(); i++){
	    			ScanResult wifi = listScan.get(i);	    			
	    			
	    			String BSSID = wifi.BSSID.replace(":", "-");
	    			
	    			if( DogGoActivity.AddWifiData(wifi ) ){
			            String js = String.format("javascript:onNewDeviceFind('%s')", BSSID );
			    		updateWebView(js); 
			            Log.w(TAG, "readrssi:"+js ); 	     
			    	}
	    			else{
		    			if( changeRssi( BSSID, wifi.level )){
			            	String js = String.format("javascript:onDeviceRSSIChange('%s','%s', '%d')", BSSID, getDeviceData(BSSID).m_name, wifi.level);
			            	updateWebView( js );
			            	Log.w(TAG, "readrssi:"+js );
			            	
		    			}
	    			}
	    		}
	    		
	    		
			}    		
    	});	*/
    }
    /*
    private void changeRssi( String address, String rssi ){
    	int			nMusic;
    	if( m_BluetoothLeService == null ){
    		return;
    	}    	
		
    	for( DeviceData data : m_lstDeviceData){
    		if( data.m_address.equals( address ) ){
    			data.m_rssi = Integer.parseInt(rssi);
    		}    		
    	}    	
    }*/

    private boolean changeRssi( String address, int rssi ){
    	int			nMusic;
		
    	for( DeviceData data : m_lstDeviceData){
    		if( data.m_address.equals( address ) ){
    			data.m_rssi = rssi;
    			return true;
    		}    		
    	}    	
    	return false;
    }
    public void setAllRssiZero(  ){
    	for( DeviceData data : m_lstDeviceData){
    		if( data.isConnected() == false ){
    			data.m_rssi = 0;
    			data.setConnectState( BluetoothLeServiceM.STATE_DISCONNECTED );
    		}  		
    	}   
    }
    

    private void changeSocketState( String address, int flag ){
    	
    	Log.w(TAG, "socket state change:"+ address+":"+String.valueOf(flag) ) ;
    	//if( m_BluetoothLeService == null ){
    	//	return;
    	//}    	
    	for( DeviceData data : m_lstDeviceData){
    		if( data.m_address.equals( address ) ){
    			data.m_socketstate = flag;
    			break;
    		}    		
    	}    	
    }  
    
    private void changeConnectState( String address, int flag ){
    	
    	Log.w(TAG, "connect state change:"+ address+":"+String.valueOf(flag) ) ;
    	//if( m_BluetoothLeService == null ){
    	//	return;
    	//}    	
    	for( DeviceData data : m_lstDeviceData){
    		if( data.m_address.equals( address ) ){
    			data.m_connected = flag;
    			// 连接上后存入数据库(如果第一次)
    			if( flag == BluetoothLeServiceM.STATE_CONNECTED ){
    				data.m_autoConnected = true;    
    			}
    			else{
    				// 不能置为0，这样的话。后面再得到信号时会频繁的变换
    				data.m_rssi = 0;
    			}				
				data.setDirty(true);
    			break;
    		}    		
    	}    	
    }  
    
    // 存储数据
    public void writeSQLData( ){

    	for( DeviceData data : m_lstDeviceData ){
    		if( data.isDirty() ){
    			data.setDirty(false);    			
    			if( m_db.searchRecord(DeviceData.SQL_TABLE, data.getSqlClause(), null) ){
    				Log.w(TAG, "update record:" + data.m_name);
    				m_db.updateRecord(DeviceData.SQL_TABLE, data.getHashMap(), data.getSqlClause(), null);
    			}
    			else{
    				Log.w(TAG, "add record:" + data.m_name );
    				m_db.addRecord(DeviceData.SQL_TABLE, data.getHashMap() );
    			}
    		}
    		
    	}
    }
    private void readSQLData( ){
        //SharedPreferences	sp = this.getPreferences(Context.MODE_PRIVATE);
    	ArrayList<HashMap<String, String>> records = m_db.queryRecord(DeviceData.SQL_TABLE, null, "", null );    	
         if( records != null ){
	    	for( HashMap<String, String> record : records ){  
	    		DeviceData 	d = new DeviceData( record );
	    		Log.w(TAG,"readSQLData:"+d.m_name + '@'+ d.m_address );
	    		m_lstDeviceData.add( d );
	    	}
        }	
    }    
    public void initWebView( ){
        m_pageLoaded = true;    	
    }    
    public boolean isTwice( ){
    	if( m_BackPressCnt == 0 ){
    		m_BackPressCnt++;
    		showMsg( "press again to exit!", false );
    		return false;
    	}
    	else{   
    		return true;   
    	}
    }
    
    public void stopMusic( ){
		m_musicIntent.putExtra("playing", false);
        startService(m_musicIntent);    	
    }
    
    public void playMusic( int n ){    	

		String path = MyRingTone.getRingtoneUriPath( RingtoneManager.TYPE_ALARM, n );
		m_musicIntent.putExtra("playing", true);
		m_musicIntent.putExtra("path", path );
        startService(m_musicIntent);        
    }
    
    public void startVibrate( ){
		VibratorUtil.Vibrate(this, new long[]{500,1000,500,1000}, true);
		m_Handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	        	stopVibrate();
	        }
	    }, 30*1000);
    }
    
    public void stopVibrate( ){
    	VibratorUtil.stop(this);    	
    }
    public void judgeAntiLostAlarm( ){
    	
    	int			music = -1;
    	boolean 	vibration = false;
    	String		names = new String("");
    	
    	for( DeviceData data : m_lstDeviceData ){
    		
    		// 只触发一次
    		if( data.isAntiLostedAlarm() ){
    			if( data.isAntiLostMobileAttn( ) ){
    				music = data.m_antiLostMusic;
    				if( data.m_antiLostMobileVib != 0 ){
    					vibration = true;
    				}
    				if( names.length() > 0 ){
    					names += ",";
    				}
    				names += data.m_name+'@'+data.m_address;
    			}
    			if( data.isAntiLostDeviceCtrl( ) ){
    				String 		senddata = new String("");
    				
    				if( data.isAntiLostShockCtrl() ){
    					if( senddata.length() > 0 ){
    						senddata += ";";					
    					}
    					senddata += Protocol.Make(0, data.getPara(10));		
    				}
    				if( data.isAntiLostVibrationCtrl() ){
    					
    					if( senddata.length() > 0 ){
    						senddata += ";";					
    					}
    					senddata += Protocol.Make(1, data.getPara(11));			
    				}
    				if( data.isAntiLostVoiceCtrl() ){
    					if( senddata.length() > 0 ){
    						senddata += ";";					
    					}
    					senddata += Protocol.Make( 2, data.getPara(12) );	
    				}

    				if( senddata.length() > 0 ){
    					Log.w(TAG, "anti lost control send:"+senddata);
    					write( data.m_address, senddata, 5 );			
    				}
    			}
    		}    		
    	}
    	if( music != -1 ){
    		playMusic( music );
    	}
    	if( vibration ){
    		startVibrate( );
    	}
    	if( names.length() > 0 ){

    		String js = String.format("javascript:onAlarm('%s')", names );
    		updateWebView(js);  
    	}
    	
    }
    // 启动手势识别
    /*
    public void enableGesture( ){
        
        m_GestureDetector = new GestureDetector(this.getApplicationContext(), new OnGestureListener(){

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				
			}
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,	float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				
			}
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// 参数解释： 
		        // e1：第1个ACTION_DOWN MotionEvent 
		        // e2：最后一个ACTION_MOVE MotionEvent 
		        // velocityX：X轴上的移动速度，像素/秒 
		        // velocityY：Y轴上的移动速度，像素/秒 
		     
		        // 触发条件 ： 
		        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒  
		        Log.w(TAG, "onFling");    
		        
		        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200; 
		        
		        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) { 
		            // Fling left  
		            Log.w("MyGesture", "Fling left"); 
		            Toast.makeText(DogGoActivity.this, "Fling Left", Toast.LENGTH_SHORT).show(); 
		            
		        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) { 
		            // Fling right  
		            Log.w("MyGesture", "Fling right"); 
		            Toast.makeText(DogGoActivity.this, "Fling Right", Toast.LENGTH_SHORT).show(); 
		        } else if(e2.getY()-e1.getY()>FLING_MIN_DISTANCE && Math.abs(velocityY)>FLING_MIN_VELOCITY) {
		            // Fling down  
		            Log.i("MyGesture", "Fling down"); 
		            Toast.makeText(DogGoActivity.this, "Fling down", Toast.LENGTH_SHORT).show();
		        } else if(e1.getY()-e2.getY()>FLING_MIN_DISTANCE && Math.abs(velocityY)>FLING_MIN_VELOCITY) {
		            // Fling up  
		            Log.i("MyGesture", "Fling up"); 
		            Toast.makeText(DogGoActivity.this, "Fling up", Toast.LENGTH_SHORT).show();
		        } 
				return false;
			}        	
        });
        m_GestureDetector.setIsLongpressEnabled(true);  
    }*/
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState); 

        m_wifiUtils = new WifiUtils( this );
        m_prevBSSID = "";
        m_bActive = true;
        m_nFailedCnt = 0;
        m_nSendFailedCnt = 0;

    	Log.w(TAG, "on create");
    	// FORTEST
        //if (!getPackageManager().hasSystemFeature("android.hardware.bluetooth_le"))
        //{
        //  showMsg( "your phone not support BLE, please upgrade your version to Android 4.3 or above!", false);
        //  finish();
        //}
        m_pageLoaded = false;
        m_Handler = new Handler();
        m_timer = new MyTimer( this );
        setContentView(R.layout.main1);    
        
        
        MyRingTone.init( this.getApplicationContext() );

        m_lstDeviceData = new ArrayList<DeviceData>( );    
        ArrayList<String> 	lstSql = new ArrayList<String>();
        lstSql.add( DeviceData.SQL_CREATE );

        // String name, String addr, String psd, int icon, int rssi, int flag
        try{
        	Log.w(TAG, "init SQL lite!");
        	m_db = new SQLDataHelper( this.getApplicationContext(), DogGoActivity.DBNAME, null, 3, lstSql  );
        }catch (Exception ex) {   
    		Log.e(TAG , ex.getMessage()); 
        }

    	Log.w(TAG, "init SQL lite end!");

        m_webView = (WebView) this.findViewById(R.id.main_webview);

        m_webView.getSettings().setJavaScriptEnabled( true );  
        m_webView.addJavascriptInterface( new JSAPI( this ), "App" );
		m_webView.setWebChromeClient(new WebChromeClient() {});
		m_webView.setWebViewClient(new WebViewClient() {

	          @Override
	          public void onPageFinished(WebView view, String url) {
	              super.onPageFinished(view, url);
	              //Log.w( TAG, "page finished:"+url );
	              initWebView( );
	          }

		});

    	Log.w(TAG, "load"+MAPURL);  
        m_webView.loadUrl( MAPURL );   
    	Log.w(TAG, "read sql Data");  
        readSQLData( );
    	Log.w(TAG, "load URL end!");  
        
    	//FORTEST
        //m_BluetoothAdapter = ((BluetoothManager)getSystemService("bluetooth")).getAdapter();
        //if ((m_BluetoothAdapter == null) || (!m_BluetoothAdapter.isEnabled())){
        //    startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_ENABLE_BT);
        //}
        //else{
    	//	scanLeDevice( );
        //	buildService( );
        //}
    	if( !m_wifiUtils.isWifiOpened() ){
    		showMsg( "your phone need enable wifi first!", false);
    		startActivityForResult( new Intent(Settings.ACTION_WIFI_SETTINGS), REQUEST_ENABLE_WIFI);
    	}
    	else{
    		scanWifiDevice();    		
    	}
    	m_musicIntent = new Intent( this,SoundService.class);
        new Timer().schedule( m_timer, 200, 1000 );
        
        //playMusic( 1 );
        //playMusic( 3 );
    }
	
	
	// 该方法在 onCreate() 方法之后被调用，
    // 或者在 Activity 从 Stop 状态转换为 Active 状态时被调用，
    // 一般执行了onStart()后就执行onResume()。 
    // onResume() 在 Activity 从 Pause 状态转换到 Active 状态时被调用。
    @Override
    protected void onResume() {
    	Log.w(TAG, "onResume"); 
    	m_bActive = true;
        super.onResume();
        //registerReceiver(BroadcastReceiver, makeGattUpdateIntentFilter());
        //if (m_BluetoothLeService != null) {
        //    m_BluetoothLeService.connect( );
        //}
    }
    @Override
    protected void onPause() {
    	Log.w(TAG, "onPause"); 
    	m_bActive = false;
        super.onPause();
        //unregisterReceiver(BroadcastReceiver);
    }
    @Override
    protected void onDestroy() {
    	Log.w(TAG, "onDestroy"); 
    	m_bActive = false;
    	m_timer.cancel();
        //unbindService(mServiceConnection);
        stopService( m_musicIntent );
        //m_BluetoothLeService = null;

        super.onDestroy();
    }
    /*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch( item.getItemId() ){
		case R.id.menu_add:
			Intent intent = new Intent( this, DeviceList.class);		
			startActivityForResult( intent, REQUEST_SCAN_BT );
			return true;
			
		default:
			break;		
		}
		return super.onMenuItemSelected(featureId, item);
	}*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch( requestCode ){
		case REQUEST_SCAN_BT:
			break;
		case REQUEST_ENABLE_BT:
			//if (resultCode == RESULT_OK) {  
            //    Toast.makeText(this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();  
            //} else if (resultCode == RESULT_CANCELED) {  
            //    Toast.makeText(this, "不允许蓝牙开启", Toast.LENGTH_SHORT).show();  
            //    finish();  
            //}  
			/*
	        if ((m_BluetoothAdapter != null) && (m_BluetoothAdapter.isEnabled())){
				scanLeDevice( );
				buildService( );
	        }
	        else{
	        	Toast.makeText(this, "you must enable bluetooth before ussed this!", Toast.LENGTH_SHORT).show();
	        	finish();	        	
	        }*/
			break;
			
		case REQUEST_ENABLE_WIFI:
			if( resultCode == RESULT_OK ){
				Toast.makeText(this, "wifi enabled!", Toast.LENGTH_SHORT).show();
				scanWifiDevice( );
			}
			else{
				Toast.makeText(this, "you must enable wifi before ussed this!", Toast.LENGTH_SHORT).show();
				finish( );
			}
			break;
			
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {	
		if( isTwice() ){	
			writeSQLData();
			//finish();
			super.onBackPressed();
		}
	}
	public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeServiceM.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeServiceM.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeServiceM.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeServiceM.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeServiceM.ACTION_RSSI_AVAILABLE);
        intentFilter.addAction(BluetoothLeServiceM.ACTION_WRITE_COMPLETE);
        intentFilter.addAction(BluetoothLeServiceM.ACTION_ASK_CONNECT);
        intentFilter.addAction(BluetoothLeServiceM.ACTION_ASK_DISCONNECT);
        return intentFilter;
    }
	
	public void stopsocketThread( ){
		
		Log.w(TAG, "stop socketThread" );
		if( m_clientThread != null ){
			m_clientThread.stop( );
			m_clientThread = null;
		}
	}
	
	public void connectdevice( ){
		
		boolean bNeedStart = false;
		boolean bNeedStop = false;
		String connectBBSID = "";
		
		connectBBSID = m_wifiUtils.getWifiConnectBSSID();
		
		// Log.w(TAG, "check connectdevice ok bssid is:" + connectBBSID ); 
		// 为空白或为全0时表示没有连接成功
		// 连续测5次为失败。表明现有的连接已经失败
		if( connectBBSID.isEmpty() || (connectBBSID.indexOf("00-00-00") != -1) ){
			if( m_nFailedCnt++ > 1 ){
				Log.e(TAG, "BSSID is null!" );  
				for( DeviceData data : m_lstDeviceData ){					
					if( data.isDisConnected() == false ){ 
						m_prevBSSID = "";
						changeConnectState( data.m_address, BluetoothLeServiceM.STATE_DISCONNECTED);
			            String js = String.format("javascript:onDeviceDisConnected('%s')", data.m_address);
			            updateWebView( js );
			            bNeedStop = true;
			            m_prevBSSID = "";
					}
				}	
				Log.e(TAG, "BSSID is null end!" );  			
			}
		}
		else{
			m_nFailedCnt = 0;
			for( DeviceData data : m_lstDeviceData ){
				Log.w(TAG, "check device" + data.m_address + ':' + data.m_name + "current bssid:" + connectBBSID); 
				if( data.isConnected()  ){ 
					if( data.m_address.equalsIgnoreCase(connectBBSID) == false ){
						changeConnectState( data.m_address, BluetoothLeServiceM.STATE_DISCONNECTED);
			            String js = String.format("javascript:onDeviceDisConnected('%s')", data.m_address);
			            updateWebView( js );
			            bNeedStop = true;
			            m_prevBSSID = "";
					}
					else{					
						if( m_prevBSSID.compareToIgnoreCase(connectBBSID) != 0 ){
							m_prevBSSID = connectBBSID;
							bNeedStart = true;
						}
					}
				}
				else{
					if( data.m_address.compareToIgnoreCase(connectBBSID) == 0 ){
						
						Log.e(TAG, "connect BSSID is" + connectBBSID );
						changeConnectState( data.m_address, BluetoothLeServiceM.STATE_CONNECTED);
			            String js = String.format("javascript:onDeviceConnected('%s')", data.m_address);
			            updateWebView( js );		
			            bNeedStart = true;
			            m_prevBSSID = connectBBSID;	
						break;
					}	
				}
			}
		}
		if( bNeedStop ){
			Log.e(TAG, "need stop clientthread");
			if( m_clientThread != null ){
				m_clientThread.stop( );
				m_clientThread = null;
			}
		}
		if( bNeedStart ){ 
			Log.e(TAG, "need start clientthread");  
			if( m_clientThread != null ){
				m_clientThread.stop( );    
			}
			m_clientThread = new  ClientThread( socketRecvhandler, SERVERIP, SERVERPORT, m_prevBSSID );
			new Thread(m_clientThread ).start(); 
		}
	}
/*
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	public View createTabContent(String tag) {

        int			h,w;
       

		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		

		w = metric.widthPixels;  
		h = m_Tabhost.getHeight() - m_Tabhost.getTabWidget().getHeight();
				        		
		if( tag.compareTo(TAB1) == 0){
			m_drawView = new DrawView( this.getApplicationContext(), this, w, h ); 
			return m_drawView;
		}
		else if (tag.compareTo(TAB2) == 0 ){
			
			WebView	view  = new WebView1(this.getApplicationContext(), this, w, h);
			WebView	view  = new WebView(this);
			view.getSettings().setJavaScriptEnabled( true );  
			view.addJavascriptInterface( new JSAPI(), "App" );
			view.setWebChromeClient(new WebChromeClient() {});
			view.setWebViewClient(new WebViewClient() {

		          @Override
		          public void onPageFinished(WebView view, String url) {
		              super.onPageFinished(view, url);
		          }

			});
			
			view.loadUrl( MAPURL );
			return view;
		}
		else{
			return null;
		}
	}
*/  /*
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if( m_GestureDetector.onTouchEvent(ev) == false ){
			return super.dispatchTouchEvent(ev);
		}
		else{
			return true;			
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		// TODO Auto-generated method stub
		//return super.onTouchEvent(event);
		//return m_GestureDetector.onTouchEvent(ev);
		return false;
	}*/

}
/*
//新建一个类继承View
class DrawView extends View{
		
	
	private Activity			m_activity = null;
	private int 				m_width; 
	private int 				m_height;	
	private Paint 				m_paint;
	private ArrayList<Object>	m_arrData;
	private Handler				m_handler;
	
	
	public DrawView(Context context, Activity activity, int w, int h) {
		super(context);
		
		m_activity = activity;
		m_width = w;  
		m_height = h;
				
		
		PathEffect effects = new DashPathEffect(new float[] { 2, 2, 2, 8}, 2);  
		m_paint=new Paint(Paint.DITHER_FLAG);
		
		m_paint.setPathEffect(effects);  
		
		m_paint.setStyle(Style.STROKE);			//设置非填充
		m_paint.setStrokeWidth(3);				
		m_paint.setColor(Color.GRAY);			
		m_paint.setAntiAlias(true);		
		m_handler = new Handler() {  
            public void handleMessage(Message msg) { 
            	
            	Toast.makeText(m_activity, msg.getData().getString("shinbo"), Toast.LENGTH_LONG );
            	
                super.handleMessage(msg);   
           }   
		};    
	 
	}
	
	
	private void drawText( int x, int y, String str, Canvas canvas ){
		
		int			nTextSize = 30;
		Rect 		rect = new Rect();	
		RectF 		rectf = null;
		Paint 		p=new Paint( );				
		int			l,r,t,b;		
		int			dx=30, dy=15; // ___ dx 指这个三角形直边的长度。 dy指这个三角形另一边的高度
								  // | /
								  // |/	
		
		p.setTextSize( nTextSize );
		p.setAntiAlias(true);	
        p.setColor ( Color.rgb(0x65,0xC0,0xEF ) ); //0x65C0EF
        p.setStyle(Paint.Style.FILL);	
		
		p.getTextBounds(str, 0, str.length(), rect);
		
		int x1,y1;
		
		x1 = x - rect.width()/2;
		y1 = y - rect.height()-dy;
		
		if( x1 < 2 ){
			x1 = 2;
		}
		
		if( x1+ rect.width() > (m_width-2) ){
			x1 = m_width - rect.width() - 2;			
		}
		
		rect.offset(x1, y1);
		
		
		if( rect.left - 10 > 0 ){
			l = rect.left - 10;
		}
		else{
			l = 2;			
		}
		if( rect.top - 10 > 0 ){
			t = rect.top - 10;
		}
		else{
			t = rect.top - 2;			
		}
		if( rect.right + 10 < m_width ){
			r = rect.right + 10;
		}
		else{
			r =  m_width -2 ;			
		}		
		if( rect.bottom + 10 < m_height ){
			b = rect.bottom + 10;
		}
		else{
			b = m_height-2;			
		}
		
		rectf = new RectF( l, t, r, b);
		canvas.drawRoundRect(rectf, 10, 10, p);
		
		Path path = new Path();  
		int l1 = (int) (l+(rectf.width()/2));
		 
        path.moveTo(l1+5, b);
        path.lineTo(l1+dx, b);  
        path.lineTo(l1, b+dy);  
        path.close(); 
	    canvas.drawPath(path, p);

		
		p.setColor(Color.WHITE);		
		canvas.drawText(str, rect.left, rect.bottom, p);
		
	}
	
	private int getRssiLevel( int level){

	    int		rssis[ ] = {-33, -57, -70, -100, -133};
	    int		result = rssis.length;
	    
	    for( int i = 0; i < rssis.length; i++ ){
	    	
	    	if( level >= rssis[i]){
	    		result = i;
	    		break;
	    	}
	    }
	    return result;		
	}
	
	//画位图
	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		
		if( m_height == 0 ){
			int h = ((DogGoActivity)m_activity).m_Tabhost.getHeight() - ((DogGoActivity)m_activity).m_Tabhost.getTabWidget().getHeight();
			m_height = h;
		}
		
		
		int 	cx = m_width/2;
		int 	cy = m_height/2;
		int		radius = m_width/12;
		
		
		
	    for( int i = 0; i < 6; i++){
			canvas.drawCircle( cx, cy, radius*(i+1), m_paint );
	    }
	    
	    
	    // 最多4个
	    int		i,cx1,cy1,rssi, icon = 0;
	    String	name;	    	    
	    Bitmap  bitmap;
	    
	    i = 0;
	    for( DeviceData data : DogGoActivity.getDeviceData() ){
	    	rssi = data.m_rssi;
	    	name = data.m_name;
	    	icon = data.getDogIcon();
	    	
	    	if( !data.isConnected() ){
	    		continue;
	    	}
	    	
	    	switch( i++ ){
	    	case 0:
	    		cx1 = cx;
	    		cy1 = cy - getRssiLevel( rssi )*radius;
	    		break;
	    	case 1:
	    		cx1 = cx + getRssiLevel( rssi )*radius;
	    		cy1 = cy;
	    		break;
	    	case 2:
	    		cx1 = cx;
	    		cy1 = cy + getRssiLevel( rssi )*radius;
	    		break;
	    	default:
	    		cx1 = cx - getRssiLevel( rssi )*radius;
	    		cy1 = cy;
	    		break;	    	
	    	}
    		bitmap = BitmapFactory.decodeResource(this.getContext().getResources(), icon );
		    drawText( cx1, cy1, name, canvas);
		    canvas.drawBitmap(bitmap, cx1-bitmap.getWidth()/2, cy1-bitmap.getHeight()/2, null);
	    }
	   
	}
	//触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
*/
/*
//新建一个类继承View
class WebView1 extends WebView{
	
	private int			m_width,m_heigth;
	private Activity	m_activity;
	
	public WebView1( Context context){
		super(context);
		
	}
	public WebView1( Context context, Activity activity, int w, int h ) {
		super(context);
		m_activity = activity;
		m_width = w;
		m_heigth = h;
	}
	@Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 		
		int wm = MeasureSpec.getMode(widthMeasureSpec);
		int hm = MeasureSpec.getMode(heightMeasureSpec);	
		setMeasuredDimension( MeasureSpec.makeMeasureSpec(m_width,wm), MeasureSpec.makeMeasureSpec(m_heigth, hm));
    }  
}
*/
class MyTimer extends java.util.TimerTask{
	private DogGoActivity	m_activity;
	private	int				m_cnt = 0;
	public MyTimer( DogGoActivity activity){
		super();
		m_activity = activity;
	}
	@Override
	public synchronized void run() {
		
		if( m_activity.m_BackPressCnt > 0 ){
			if( m_activity.m_BackPressCnt > 5 ){ 
				m_activity.m_BackPressCnt = 0;				
			}
			else{
				m_activity.m_BackPressCnt++;				
			}
		}
		
		if( m_activity.m_nSendFailedCnt > 0 ){
			m_activity.m_nSendFailedCnt--;
			
			if( m_activity.m_nSendFailedCnt == 0 ){

				if( m_activity.m_clientThread != null ){
					Log.e("DogGoActivity", "read data timeout!" );
					m_activity.m_clientThread.stop( );
					m_activity.m_prevBSSID = "";
					//m_clientThread = null;
				}
			}			
		}
		
		m_activity.writeSQLData(); 
		
		//if( ++m_cnt % 2 == 0 ){	
		
		//}
		m_activity.judgeAntiLostAlarm( );
		//m_activity.calcConnectCountDown( );
		// 主动链接wifi
		if( m_activity.m_bActive  ){
			// 自动扫描+读信号
			m_activity.readRssi();
			// 自动链接目标wifi
			Log.w("DogGo", "connect wifi all");
			m_activity.connectwifi_all( );
			Log.w("DogGo", "connect wifi allend");
			// 自动链接目标socket
			Log.w("DogGo", "connect device");
			m_activity.connectdevice( );   
			Log.w("DogGo", "connect device end");
		}
		
	}
}