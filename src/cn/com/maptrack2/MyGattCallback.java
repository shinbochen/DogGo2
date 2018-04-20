package cn.com.maptrack2;

import java.util.Iterator;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;


public class MyGattCallback extends BluetoothGattCallback {

	  public void onCharacteristicChanged(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
	  {
	    super.onCharacteristicChanged(paramBluetoothGatt, paramBluetoothGattCharacteristic);
	  }
	
	  public void onCharacteristicRead(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt)
	  {
	    super.onCharacteristicRead(paramBluetoothGatt, paramBluetoothGattCharacteristic, paramInt);
	  }
	
	  public void onCharacteristicWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt)
	  {
	    super.onCharacteristicWrite(paramBluetoothGatt, paramBluetoothGattCharacteristic, paramInt);
	  }
	
	  public void onConnectionStateChange(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2)
	  {
	    super.onConnectionStateChange(paramBluetoothGatt, paramInt1, paramInt2);
	  }
	
	  public void onDescriptorRead(BluetoothGatt paramBluetoothGatt, BluetoothGattDescriptor paramBluetoothGattDescriptor, int paramInt)
	  {
	    super.onDescriptorRead(paramBluetoothGatt, paramBluetoothGattDescriptor, paramInt);
	  }
	
	  public void onDescriptorWrite(BluetoothGatt paramBluetoothGatt, BluetoothGattDescriptor paramBluetoothGattDescriptor, int paramInt)
	  {
	    super.onDescriptorWrite(paramBluetoothGatt, paramBluetoothGattDescriptor, paramInt);
	  }
	
	  public void onReadRemoteRssi(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2)
	  {
	    super.onReadRemoteRssi(paramBluetoothGatt, paramInt1, paramInt2);
	  }
	
	  public void onReliableWriteCompleted(BluetoothGatt paramBluetoothGatt, int paramInt)
	  {
	    super.onReliableWriteCompleted(paramBluetoothGatt, paramInt);
	  }
	
	  public void onServicesDiscovered(BluetoothGatt paramBluetoothGatt, int paramInt)
	  {
	    super.onServicesDiscovered(paramBluetoothGatt, paramInt);
	    Iterator<BluetoothGattService> localIterator = paramBluetoothGatt.getServices().iterator();
	    while (true)
	    {
	      if (!localIterator.hasNext())
	        return;
	      BluetoothGattService localBluetoothGattService = (BluetoothGattService)localIterator.next();
	      System.out.println(localBluetoothGattService.getUuid().toString());
	    }
	  }
	
}
