/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.maptrack2;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.maptrack2.R;

public class DeviceControlActivity extends Activity 
implements View.OnClickListener{
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_RSSI = "DEVICE_RSSI";

    private TextView mConnectionState;
    private TextView mRecvDataField;
    private TextView mUiidField;
    private TextView mSendDataField;
    private TextView mRSSIDataField;
    private Button	 mReadRSSIBtn;
    private Button	 mSendBtn;
    private String 	 mDeviceName;
    private String 	 mDeviceAddress;
    
    private Boolean	 mKeepConnect;
    
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =  new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mTargetCharacteristic;
    
    private Bundle	mBundle;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect( );
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	mBluetoothLeService.disconnect();
        	mBluetoothLeService.close();
            mBluetoothLeService = null;
        }
    };

    
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            Toast.makeText(context, action, 0).show();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
                // 延时后重连
                if( mKeepConnect ){
	                new Handler().postDelayed(new Runnable(){   
	                    public void run() {  
	                        mBluetoothLeService.connect( );
	                    }   
	                 }, 500);
                }
                
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_RSSI_AVAILABLE.equals(action)) {
            	displayRSSI( intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_WRITE_COMPLETE.equals(action)) {
                
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void displayData(String data) {
        if (data != null) {
        	mRecvDataField.setText(data);
        }
    }

    private void displayRSSI(String data) {
        if (data != null) {
        	mRSSIDataField.setText(data);
        }
    }
    public void playMusic( int n ){    	

		Uri uri = MyRingTone.getRingtoneUri( RingtoneManager.TYPE_RINGTONE, n );
		
		Log.w(TAG, String.valueOf(n)+':'+uri.toString() );
		Intent intent = new Intent(this,SoundService.class);
        intent.putExtra("playing", true);
        intent.putExtra("path", uri.toString() );
        startService(intent);
    }

    private void updateConnectionState(final int resourceId) {
        //runOnUiThread(new Runnable() {
        //    @Override
        //    public void run() {
                mConnectionState.setText(resourceId);
        //    }
        //});
    }
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
    	

        if (gattServices == null) return;
        
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        // 服务数据包含名字与UIID
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        // 相当于二维数组
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
        	
        	// 每个服务的UUID与名字 -》gattServiceData list
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =   new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            // 找到每个服务下面拥有的特征码
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
	new ExpandableListView.OnChildClickListener() {
    	@Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (mGattCharacteristics != null) {
                final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                final int charaProp = characteristic.getProperties();
                mTargetCharacteristic = characteristic;
                //if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                //    if (mTargetCharacteristic != null) {
                //        mBluetoothLeService.setCharacteristicNotification( mTargetCharacteristic, false);
               //         mTargetCharacteristic = null;
                //    }
                //    mBluetoothLeService.readCharacteristic(characteristic);
                //}
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                    

                    mUiidField.setText(characteristic.getUuid().toString());
                    //Intent intent = new Intent();
                    
                    //mBundle.putBoolean("CONNET_SATE", mConnected);
                    //mBundle.putString("UUID", characteristic.getUuid().toString());
                    //intent.putExtras( mBundle );
                    //intent.setClass(DeviceControlActivity.this, FunctionActivity.class);
                    //startActivity(intent);
                }
                return true;
            }
            return false;            
            
        }
    };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mRecvDataField.setText(R.string.no_data);
    }
    

    public void write( String str ){
      if ((BluetoothGattCharacteristic.PROPERTY_WRITE | mTargetCharacteristic.getProperties()) > 0)
      {
    	  mTargetCharacteristic.setValue(str);
    	  mBluetoothLeService.writeCharacteristic(mTargetCharacteristic);
      }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        
        mBundle = intent.getExtras();
        
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mRecvDataField = (TextView) findViewById(R.id.recv_data);
        mUiidField = (TextView) findViewById(R.id.target_uuid);
                
        mRSSIDataField = (TextView) findViewById(R.id.rssi_data);
        mSendDataField = (EditText) findViewById(R.id.send_data);
        mSendBtn = (Button)findViewById(R.id.btn_send);
        mReadRSSIBtn = (Button)findViewById(R.id.btn_read_rssi);
        
        mSendBtn.setOnClickListener( this );
        mReadRSSIBtn.setOnClickListener(this);

        mKeepConnect = true;
        
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void onClick(View view)
    {
    	switch( view.getId() ){
    	case R.id.btn_send:
    		String str = mSendDataField.getText().toString();
    		write(str);
    		break;
    		
    	case R.id.btn_read_rssi:
    		mBluetoothLeService.readRssi();
    		break;
    	
    	}
    	
    }
    // 该方法在 onCreate() 方法之后被调用，或者在 Activity 从 Stop 状态转换为 Active 状态时被调用，一般执行了onStart()后就执行onResume()。 
    // onResume() 在 Activity 从 Pause 状态转换到 Active 状态时被调用。
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect( );
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
            	connect( );
                return true;
                
            case R.id.menu_disconnect:            	
                disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void connect( ){
    	// 保持链接设为真
    	mKeepConnect = true;
    	mBluetoothLeService.connect( );	
    }    
    public void disconnect(){
    	mKeepConnect = false;
    	mBluetoothLeService.disconnect();	    	
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_RSSI_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_COMPLETE);
        return intentFilter;
    }
}
