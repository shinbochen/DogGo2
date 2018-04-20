package cn.com.maptrack2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class BluetoothLeServiceM extends Service {
	
	
	public static final String ACTION_DATA_AVAILABLE = "bluetooth.le.data.avaiable";
	public static final String ACTION_RSSI_AVAILABLE = "bluetooth.le.rssi.avaiable";
	public static final String ACTION_WRITE_COMPLETE = "bluetooth.le.write.complete";
	public static final String ACTION_GATT_CONNECTED = "bluetooth.le.gatt.connected";
	public static final String ACTION_GATT_DISCONNECTED = "bluetooth.le.gatt.disconnected";
	public static final String ACTION_GATT_SERVICES_DISCOVERED = "bluetooth.le.services.discovered";
	public static final String ACTION_ASK_CONNECT	= "bluetooth.ask.connect";
	public static final String ACTION_ASK_DISCONNECT	= "bluetooth.ask.disconnect";
	public static final String EXTRA_DATA = "bluetooth.le.extra.data";
	public static final String DEVICE_ADDRESS = "bluetooth.device.address";
	
	public static final UUID   UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
	
	public static final int 		STATE_CONNECTED = 0;
	public static final int 		STATE_CONNECTING = 1;
	public static final int 		STATE_DISCONNECTED = 2;
	private static final String 	TAG = BluetoothLeServiceM.class.getSimpleName();
	
	private final IBinder 			m_Binder = new LocalBinder();
	private BluetoothAdapter 		m_BluetoothAdapter;
	
	public String 					m_DeviceAddress;
	private int 					m_ConnectionState = 0;
	
	private Map<String,BluetoothGatt> 	m_mapGatt = new HashMap<String,BluetoothGatt>();
	private Map<String,Integer> 		m_mapState= new HashMap<String,Integer>();
	
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
	{
	  //数据有变化
	  public void onCharacteristicChanged(BluetoothGatt Gatt, BluetoothGattCharacteristic Characteristic)
	  {
		  broadcastUpdate( Gatt, ACTION_DATA_AVAILABLE, Characteristic);
	  }
	  // 读数据
	  public void onCharacteristicRead(BluetoothGatt Gatt, BluetoothGattCharacteristic Characteristic, int i1)
	  {
	    if (i1 == BluetoothGatt.GATT_SUCCESS) {
	      broadcastUpdate(Gatt, ACTION_DATA_AVAILABLE, Characteristic);
	    }
	  }
	  // 写数据的状态
	  public void onCharacteristicWrite(BluetoothGatt Gatt, BluetoothGattCharacteristic Characteristic, int i1)
	  {
		  if( i1 == BluetoothGatt.GATT_SUCCESS ){	
		      broadcastUpdate( Gatt, ACTION_WRITE_COMPLETE );
		  }
	  }
	  // 链接状态改变
	  public void onConnectionStateChange(BluetoothGatt Gatt, int i1, int i2)
	  {		  
		  String address = Gatt.getDevice().getAddress();
		  
		  switch( i2 ){
		  case BluetoothProfile.STATE_CONNECTED:
			  if( Gatt.discoverServices() ){
				  m_mapState.put(address, STATE_CONNECTED);
				  broadcastUpdate(Gatt, ACTION_GATT_CONNECTED);
			  }
			  else{
				  Log.e(TAG, "fault connect status" );
			  }
			  break;
		  case BluetoothProfile.STATE_DISCONNECTED:
			  onDisconnect( address, Gatt);
			  break;
	      default:
	    	  break;
		  }
	  }

	  public void onDescriptorRead(BluetoothGatt Gatt, BluetoothGattDescriptor descriptor, int i1)
	  {
	    byte[] arrayOfByte = descriptor.getValue();
	    if (arrayOfByte != null){
	      Log.w(TAG, "onDescriptorRead value: " + new String(arrayOfByte));
	    }
	  }

	  public void onDescriptorWrite(BluetoothGatt Gatt, BluetoothGattDescriptor descriptor, int i1)
	  {
		  
	  }
	  // 读信号完成
	  public void onReadRemoteRssi(BluetoothGatt Gatt, int i1, int i2)
	  {
		  if( i2 == BluetoothGatt.GATT_SUCCESS ){
			  broadcastUpdate(Gatt,ACTION_RSSI_AVAILABLE, i1);
		  }
	  }

	  public void onReliableWriteCompleted(BluetoothGatt Gatt, int i1)
	  {
		  
	  }
	  // 发现服务项目（列出所有服务项目）
	  public void onServicesDiscovered(BluetoothGatt Gatt, int i1)
	  {
	    if (i1 == BluetoothGatt.GATT_SUCCESS)
	    {
	      broadcastUpdate( Gatt, ACTION_GATT_SERVICES_DISCOVERED );
	    }
	  }
	};
	// 发送广播（只有动作类型 直接发送 )
	private void broadcastUpdate(BluetoothGatt Gatt, String action)
	{
		String address = Gatt.getDevice().getAddress();
		
		Intent intent = new Intent(action);
		intent.putExtra(DEVICE_ADDRESS, address);
		sendBroadcast( intent );
	}
	// 发送广播(有附带数据。数据为数字)
	private void broadcastUpdate(BluetoothGatt Gatt, String action, int paramInt)
	{
		String address = Gatt.getDevice().getAddress();		
		Intent intent = new Intent(action);
		intent.putExtra(DEVICE_ADDRESS, address);
		intent.putExtra(EXTRA_DATA, String.valueOf(paramInt));
	    sendBroadcast(intent);
	}
	// 发送广播(动作类型自定义。并带有相应的特征码）
	public void broadcastUpdate(BluetoothGatt Gatt, String action, BluetoothGattCharacteristic characteristic)
	{
		String address = Gatt.getDevice().getAddress();		
		Intent intent = new Intent(action);
		
	    // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            int heartRate = characteristic.getIntValue(format, 1);
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                //intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                intent.putExtra( EXTRA_DATA, new String(data) );
            }
        }
		intent.putExtra(DEVICE_ADDRESS, address);
	    sendBroadcast(intent);
	}

	public void close(){
		String			address;
		BluetoothGatt	btGatt;
		
		for (Map.Entry<String, BluetoothGatt> entry : m_mapGatt.entrySet()) {			
			address = entry.getKey();
			btGatt = entry.getValue();
			if( btGatt != null){
				btGatt.disconnect();
				btGatt.close();
			}
		}
		m_mapGatt.clear();
		m_mapState.clear();
	}

	// 供外面调用
	// 初始化blmanager and adapter
	public boolean initialize()
	{	
	    m_BluetoothAdapter = ((BluetoothManager)getSystemService("bluetooth")).getAdapter();
	    if (m_BluetoothAdapter == null){
	    	Log.w(TAG, "Unable to obtain a BluetoothAdapter.");
	    	return false;
	    }
	    else{
	    	return true;
	    }
	}
	
	
	public void onDisconnect( String address, BluetoothGatt Gatt ){		

		Log.w(TAG, "onDisconnect:"+address);
		RemoveBluetoothGatt( address );		
		//m_mapState.put(address, STATE_DISCONNECTED);
		broadcastUpdate(Gatt, ACTION_GATT_DISCONNECTED);
	}
	
	// 如果此地址已存在。找到原有的GATT重连
	// 如果此地址不存在。新建一个GATT连接
	// 修改： 连接已有的蓝牙地址时改为新建GATT连接
	
	public boolean connect( String address ){		  
		if ((m_BluetoothAdapter == null)){
			Log.w(TAG, "BluetoothAdapter not initialized.");
			return false;
		}
		
		// 
		BluetoothGatt btGatt = getBluetoothGatt(address);		
		if( btGatt != null ){
			Log.i(TAG, "Trying to used existing BluetoothGatt connection.");
			if (btGatt.connect( )){
				Log.i(TAG, "Trying to used existing BluetoothGatt connection return good.");
				m_mapState.put( address,STATE_CONNECTING );
				return true;
			}
			else{
				Log.w(TAG, "Connection failed! then create new connection");
				RemoveBluetoothGatt( address );  
			}
			
		}

		Log.i(TAG, "Trying to create new bluetoothGatt for connection.");
		BluetoothDevice device = m_BluetoothAdapter.getRemoteDevice( address );
		if (device == null)	{
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		Log.w(TAG, "connect begin:"+device.getName()+"@"+device.getAddress());
		btGatt = device.connectGatt(this, true, mGattCallback);
		m_mapGatt.put( address, btGatt );		
		m_mapState.put(address, STATE_CONNECTING);
		return true;

		
	    /*
		BluetoothGatt btGatt = getBluetoothGatt(address);
		if( btGatt == null ){
			Log.w(TAG, "Trying to create new bluetoothGatt for connection.");
			BluetoothDevice device = m_BluetoothAdapter.getRemoteDevice( address );
			if (device == null)	{
				Log.w(TAG, "Device not found.  Unable to connect.");
				return false;
			}
			Log.w(TAG, "connect begin:"+device.getName()+"@"+device.getAddress());
			btGatt = device.connectGatt(this, true, mGattCallback);
			m_mapGatt.put( address, btGatt );		
			m_mapState.put(address, STATE_CONNECTING);
			return true;
		}
		else{
			Log.w(TAG, "Trying to use an existing BluetoothGatt for connection.");
			if (btGatt.connect()){
				m_mapState.put( address,STATE_CONNECTING );
				return true;
			}
			else{
				return false;	        	
			}
		}*/
		
	}

	// 重连所有蓝牙设备
	public void connect( ){	

		String			address;
		BluetoothGatt	btGatt;
		
		if ((m_BluetoothAdapter == null)){
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return;
		}		
		for (Map.Entry<String, BluetoothGatt> entry : m_mapGatt.entrySet()) {			
			address = entry.getKey();
			btGatt = entry.getValue();			
			if( m_mapState.get(address) != STATE_CONNECTED){
				btGatt.connect();
			}
		}
	}

	public boolean disconnect( String address )
	{
		BluetoothGatt btGatt = getBluetoothGatt(address);
	    if ( btGatt == null ){
		    return false;
	    }
	    else{
		    btGatt.disconnect();
		    //RemoveBluetoothGatt( address );
		    return true;
	    }
	}

	// disconnect all
	public void disconnect( ){		

		String			address;
		BluetoothGatt	btGatt;
		
		if ((m_BluetoothAdapter == null)){
			Log.w(TAG, "BluetoothAdapter not initialized.");
			return;
		}		
		for (Map.Entry<String, BluetoothGatt> entry : m_mapGatt.entrySet()) {			
			address = entry.getKey();
			btGatt = entry.getValue();		

		    //m_mapState.put(address, STATE_DISCONNECTED);
		    btGatt.disconnect();
		    btGatt.close();	    
		    
		}
		
		m_mapState.clear();
		m_mapGatt.clear();
	}
	
	//public boolean getCharacteristicDescriptor(String address, BluetoothGattDescriptor descriptor)
	//{
	//	boolean 	flag = false;
	//	BluetoothGatt btGatt = getBluetoothGatt(address);
	//    if ( btGatt != null ){
	//	    flag = btGatt.readDescriptor(descriptor);
	//	    if( !flag ){
	//	    	Log.e(TAG, "getCharacteristicDescriptor had failed");
	//	    	onDisconnect( address, btGatt );
	//	    }
	//    }
	//    return flag;
	//}

	public List<BluetoothGattService> getSupportedGattServices( String address )
	{
		List list = null;
		BluetoothGatt btGatt = getBluetoothGatt(address);
		if (btGatt != null){
			list = btGatt.getServices();
		}
	    return list;
	}
	@Override
	public IBinder onBind(Intent intent)
	{		
		Log.w(TAG,  "onBind" );
	    return this.m_Binder;
	}

	public boolean onUnbind(Intent intent)
	{
	    Log.w(TAG, "BluetoothLeService unbind");
	    close();
	    return super.onUnbind(intent);
	}
	//public boolean readCharacteristic(String address, BluetoothGattCharacteristic Characteristic)
	//{
	//	boolean flag = false;
	//	BluetoothGatt btGatt = getBluetoothGatt(address);
	//    if ( btGatt != null ){
	//    	flag =  btGatt.readCharacteristic(Characteristic);
	//	    if( !flag ){
	//	    	Log.e(TAG, "readCharacteristic had failed");
	//	    	onDisconnect( address, btGatt );
	//	    }
	//    }	   
	//    return flag;
	//}

	public boolean readRssi( String address )
	{
		boolean 		flag = false;
		BluetoothGatt 	btGatt = getBluetoothGatt(address);

	    Log.i(TAG, address+":read rssi");
	    
	    if ( btGatt != null ){
	    	flag = btGatt.readRemoteRssi();
		    if( !flag ){
		    	Log.e(TAG, "readrssi had failed");
		    	//onDisconnect( address, btGatt );
		    }	    
	    }	
	   
	    return flag;
	}
	
/*
	public boolean setCharacteristicNotification(String address, BluetoothGattCharacteristic characteristic, boolean flag1)
	{
		boolean flag = false;
		BluetoothGatt btGatt = getBluetoothGatt(address);
	    if ( btGatt != null ){
	    
		    flag = btGatt.setCharacteristicNotification(characteristic, flag1);
		    if( flag ){
			    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString( SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			    if (flag1){
			      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			    }else{
			      descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
			    }
			    flag = btGatt.writeDescriptor(descriptor);
		    }
		    if( !flag ){
		    	Log.e(TAG, "setCharacteristicNotification failed");
		    	//onDisconnect( address, btGatt );
		    }	 
		}
	    return flag;
	}*/
/*
	public boolean writeCharacteristic(String address, BluetoothGattCharacteristic characteristic)
	{
		boolean 		flag = false;
		BluetoothGatt 	btGatt = getBluetoothGatt(address);
		
    	Log.i(TAG, address+":write data");
	    if ( btGatt != null ){
	    	flag = btGatt.writeCharacteristic(characteristic);
		    if( !flag ){
		    	Log.e(TAG, "writeCharacteristic failed");
		    	//onDisconnect( address, btGatt );
		    }	
	    }
	    return flag;
	}	*/
	private void displayGattServices( String address) {
		

		BluetoothGatt 	btGatt = getBluetoothGatt(address);
	    if ( btGatt == null ){
	    	Log.w( TAG, "not find btGATT");
		    return;
	    }
		
		List<BluetoothGattService> gattServices = btGatt.getServices();
        if (gattServices == null){
        	Log.w( TAG, "not find services");
        	return;
        }

    	Log.w( TAG, "had find services"+gattServices.size());
        String uuid = null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
        	
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            
            Log.w( TAG, "services uuid:"+uuid );

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                Log.w(TAG, "characateritic uiid:" + uuid );
            }
        }
    }
	
	public boolean enableRS232IO( String address, boolean flag ){

		//displayGattServices( address );
		

		boolean			flags = false;
		int 			charaProp;
		BluetoothGatt 	btGatt = getBluetoothGatt(address);
	    if ( btGatt == null ){
		    return false;
	    }
	    BluetoothGattCharacteristic	rs232charc = null;
	    BluetoothGattService 		service = btGatt.getService(UUID.fromString(SampleGattAttributes.USER_DEFINE_SERVICE));
	    
	    if( service == null ){
	    	Log.e(TAG, "no RS232 Service");
	    	return false;
	    }
	    rs232charc = service.getCharacteristic(UUID.fromString(SampleGattAttributes.RS232_CHARACTERISTIC));

	    if( rs232charc == null ){
	    	Log.e(TAG, "no RS232 characteristic");
	    	return false;
	    }
		charaProp = rs232charc.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            
		    btGatt.setCharacteristicNotification(rs232charc, flag);
		    BluetoothGattDescriptor descriptor = rs232charc.getDescriptor(UUID.fromString( SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		    if (flag){
		      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		    }else{
		      descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		    }
		    flags = btGatt.writeDescriptor(descriptor);
		    if( !flags ){
		    	Log.e(TAG, "enableRS232IO failed");
		    	onDisconnect( address, btGatt );
		    }
        }
        return flags;
	}
	public boolean writeRS232Data(String address, String data )
	{
		boolean		flag = false;
		BluetoothGatt btGatt = getBluetoothGatt(address);
	    if ( btGatt == null ){
		    return false;
	    }
	    BluetoothGattCharacteristic	rs232charc = null;
	    BluetoothGattService 		service = btGatt.getService(UUID.fromString(SampleGattAttributes.USER_DEFINE_SERVICE));
	    
	    if( service == null ){
	    	Log.w(TAG, "no RS232 Service");
	    	return false;
	    }
	    rs232charc = service.getCharacteristic(UUID.fromString(SampleGattAttributes.RS232_CHARACTERISTIC));

	    if( rs232charc == null ){
	    	Log.e(TAG, "no RS232 characteristic");
	    	return false;
	    }
		if ((BluetoothGattCharacteristic.PROPERTY_WRITE | rs232charc.getProperties()) > 0){
			rs232charc.setValue(data);
		    flag = btGatt.writeCharacteristic(rs232charc);
		    if( !flag ){
		    	Log.e(TAG, "writeRS232Data failed");
		    	//onDisconnect( address, btGatt );
		    }
		}
		return flag;
	}
	public BluetoothGatt getBluetoothGatt( String address){
		BluetoothGatt btGatt = m_mapGatt.get(address);
	    if ( m_BluetoothAdapter == null ){
	      Log.w(TAG, "BluetoothAdapter not initialized!");
	    }	
	    if (( btGatt == null )){
	      Log.w(TAG, address+" still not have Gatt!");
	    }else{
		   // Log.w(TAG, address+" have Gatt!");
	    }
	    return btGatt;
	}
	public void RemoveBluetoothGatt( String address){

		BluetoothGatt btGatt = m_mapGatt.get(address);
		if( btGatt != null ){			
			btGatt.disconnect();
			btGatt.close();
		}
		m_mapGatt.remove(address);	
		m_mapState.remove(address);
	}
	public void RemoveAllBluetoothGatt( ){
		String			address;
		BluetoothGatt	btGatt;
		
		for (Map.Entry<String, BluetoothGatt> entry : m_mapGatt.entrySet()) {			
			address = entry.getKey();
			btGatt = entry.getValue();
			if( btGatt != null){
				btGatt.disconnect();
				btGatt.close();
			}
		}
		m_mapGatt.clear();
		m_mapState.clear();
	}
	public class LocalBinder extends Binder{
		public LocalBinder(){			
		}
		BluetoothLeServiceM getService(){
			return BluetoothLeServiceM.this;
		}
    }	
}
