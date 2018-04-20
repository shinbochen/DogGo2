package cn.com.maptrack2;

import java.util.ArrayList;
import java.util.List;
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
import android.widget.Toast;

public class BluetoothLeService extends Service {
	
	
	public static final String ACTION_DATA_AVAILABLE = "bluetooth.le.data.avaiable";
	public static final String ACTION_RSSI_AVAILABLE = "bluetooth.le.rssi.avaiable";
	public static final String ACTION_WRITE_COMPLETE = "bluetooth.le.write.complete";
	public static final String ACTION_GATT_CONNECTED = "bluetooth.le.gatt.connected";
	public static final String ACTION_GATT_DISCONNECTED = "bluetooth.le.gatt.disconnected";
	public static final String ACTION_GATT_SERVICES_DISCOVERED = "bluetooth.le.services.discovered";
	public static final String EXTRA_DATA = "bluetooth.le.extra.data";
	public static final String DEVICE_ADDRESS = "bluetooth.device.address";         
	 
	public static final UUID   UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
	
	private static final int 	STATE_CONNECTED = 0;
	private static final int 	STATE_CONNECTING = 1;
	private static final int 	STATE_DISCONNECTED =2;
	private static final String TAG = BluetoothLeService.class.getSimpleName();
	
	private final IBinder 			m_Binder = new LocalBinder();
	private BluetoothAdapter 		m_BluetoothAdapter;
	private BluetoothGatt 			m_BluetoothGatt;
	private BluetoothManager 		m_BluetoothManager;
	public String 					m_DeviceAddress;
	private int 					m_ConnectionState = 0;
	
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
	{
	  //数据有变化
	  public void onCharacteristicChanged(BluetoothGatt Gatt, BluetoothGattCharacteristic Characteristic)
	  {
	    broadcastUpdate(ACTION_DATA_AVAILABLE, Characteristic);
	  }
	  // 读数据
	  public void onCharacteristicRead(BluetoothGatt Gatt, BluetoothGattCharacteristic Characteristic, int i1)
	  {
	    if (i1 == BluetoothGatt.GATT_SUCCESS) {
	      broadcastUpdate(ACTION_DATA_AVAILABLE, Characteristic);
	    }
	  }
	  // 写数据的状态
	  public void onCharacteristicWrite(BluetoothGatt Gatt, BluetoothGattCharacteristic Characteristic, int i1)
	  {
		  if( i1 == BluetoothGatt.GATT_SUCCESS ){	
		      broadcastUpdate( ACTION_WRITE_COMPLETE );
		  }
	  }
	  // 链接状态改变
	  public void onConnectionStateChange(BluetoothGatt Gatt, int i1, int i2)
	  {		  
		  switch( i2 ){
		  case BluetoothProfile.STATE_CONNECTED:
			  m_ConnectionState = 2;
			  broadcastUpdate(ACTION_GATT_CONNECTED);
			  m_BluetoothGatt.discoverServices();
			  break;
		  case BluetoothProfile.STATE_DISCONNECTED:
			  m_ConnectionState = 0;
			  broadcastUpdate(ACTION_GATT_DISCONNECTED);
			  break;
	      default:
	    	  break;
		  }
	  }

	  public void onDescriptorRead(BluetoothGatt Gatt, BluetoothGattDescriptor descriptor, int i1)
	  {
	    byte[] arrayOfByte = descriptor.getValue();
	    if (arrayOfByte != null)
	      Log.w(TAG, "----onDescriptorRead value: " + new String(arrayOfByte));
	  }

	  public void onDescriptorWrite(BluetoothGatt Gatt, BluetoothGattDescriptor descriptor, int i1)
	  {
		  
	  }
	  // 读信号完成
	  public void onReadRemoteRssi(BluetoothGatt Gatt, int i1, int i2)
	  {
		  if( i2 == BluetoothGatt.GATT_SUCCESS ){
			  broadcastUpdate(ACTION_RSSI_AVAILABLE, i1);
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
	      broadcastUpdate( ACTION_GATT_SERVICES_DISCOVERED );
	    }
	  }
	};
	// 发送广播（只有动作类型 直接发送 )
	private void broadcastUpdate(String action)
	{
		Intent intent = new Intent(action);
		intent.putExtra(DEVICE_ADDRESS, m_DeviceAddress);
		sendBroadcast( intent );
	}
	// 发送广播(有附带数据。数据为数字)
	private void broadcastUpdate(String action, int paramInt)
	{
		Intent intent = new Intent(action);
		intent.putExtra(DEVICE_ADDRESS, m_DeviceAddress);
		intent.putExtra(EXTRA_DATA, String.valueOf(paramInt));
	    sendBroadcast(intent);
	}
	// 发送广播(动作类型自定义。并带有相应的特征码）
	public void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic)
	{
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
                intent.putExtra(EXTRA_DATA, stringBuilder.toString());
            }
        }
		intent.putExtra(DEVICE_ADDRESS, m_DeviceAddress);
	    sendBroadcast(intent);
	    
	}

	public void close()
	{
		if (this.m_BluetoothGatt != null){
			this.m_BluetoothGatt.close();
			this.m_BluetoothGatt = null;
		}
	}

	// 供外面调用
	// 初始化blmanager and adapter
	public boolean initialize()
	{
	    if (m_BluetoothManager == null){
	      m_BluetoothManager = ((BluetoothManager)getSystemService("bluetooth"));
	      if (m_BluetoothManager == null){
	        Log.w(TAG, "Unable to initialize BluetoothManager.");
	        return false;
	      }
	    }
	    m_BluetoothAdapter = m_BluetoothManager.getAdapter();
	    if (m_BluetoothAdapter == null){
	    	Log.w(TAG, "Unable to obtain a BluetoothAdapter.");
	    	return false;
	    }
	    else{
	    	return true;
	    }
	}
	// 供外面调用  
	// 链接蓝牙设备
	public boolean connect( )
	{		  
		if ((m_BluetoothAdapter == null))
		{
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		if ((m_DeviceAddress != null) && (m_BluetoothGatt != null))	{
			Log.d(TAG, "Trying to use an existing m_BluetoothGatt for connection.");
			if (m_BluetoothGatt.connect())
			{
				this.m_ConnectionState = STATE_CONNECTING;
				return true;
			}
			else{
				return false;	        	
			}
		}
		BluetoothDevice device = m_BluetoothAdapter.getRemoteDevice( m_DeviceAddress );
		if (device == null)	{
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		this.m_BluetoothGatt = device.connectGatt(this, true, mGattCallback);
		m_ConnectionState = STATE_CONNECTING;
		return true;
	}

	public void disconnect()
	{
		if ((m_BluetoothAdapter == null) || (m_BluetoothGatt == null)){
	      Log.w(TAG, "BluetoothAdapter not initialized");
	      return;
		}
	    m_BluetoothGatt.disconnect();
	}

	
	public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor)
	{
		if ((m_BluetoothAdapter == null) || (m_BluetoothGatt == null)){
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		m_BluetoothGatt.readDescriptor(descriptor);
	}

	public List<BluetoothGattService> getSupportedGattServices()
	{
		List list = null;
		if (m_BluetoothGatt != null){
			list = m_BluetoothGatt.getServices();
		}
	    return list;
	}
	@Override
	public IBinder onBind(Intent intent)
	{
		m_DeviceAddress = intent.getStringExtra(DEVICE_ADDRESS);
		Log.w(TAG,  "onBind"+ m_DeviceAddress);
	    return this.m_Binder;
	}

	public boolean onUnbind(Intent intent)
	{
	    Log.w(TAG, "BluetoothLeService unbind");
	    close();
	    return super.onUnbind(intent);
	}
	public void readCharacteristic(BluetoothGattCharacteristic Characteristic)
	{
	    if ((m_BluetoothAdapter == null) || (m_BluetoothGatt == null)){
	      Log.w(TAG, "BluetoothAdapter not initialized");
	      return;
	    }
	    m_BluetoothGatt.readCharacteristic(Characteristic);
	}

	public void readRssi()
	{
	    if ((m_BluetoothAdapter == null) || (m_BluetoothGatt == null)){
	      Log.w(TAG, "BluetoothAdapter not initialized");
	      return;
	    }
	    m_BluetoothGatt.readRemoteRssi();
	    Log.w(TAG, "read rssi");
	}
	

	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean flag)
	{
	    if ((m_BluetoothAdapter == null) || (m_BluetoothGatt == null)){
	      Log.w(TAG, "BluetoothAdapter not initialized");
	      return;
	    }
	    m_BluetoothGatt.setCharacteristicNotification(characteristic, flag);
	    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString( SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
	    if (flag){
	      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	    }else{
	      descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	    }
	    m_BluetoothGatt.writeDescriptor(descriptor);
	}

	public void writeCharacteristic(BluetoothGattCharacteristic characteristic)
	{
	    if (( m_BluetoothAdapter == null ) || ( m_BluetoothGatt == null )){
	      Log.w(TAG, "BluetoothAdapter not initialized");
	      return;
	    }
	    m_BluetoothGatt.writeCharacteristic(characteristic);
	    Log.w(TAG, "write data");
	}
	public class LocalBinder extends Binder
	{
		public LocalBinder()
		{
		}
		// 提供外面调用
		BluetoothLeService getService()
		{
			return BluetoothLeService.this;
		}
    }	
}
