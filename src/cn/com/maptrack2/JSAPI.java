package cn.com.maptrack2;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JSAPI {
	private DogGoActivity	m_act = null;
    private final static String TAG = JSAPI.class.getSimpleName();
	public JSAPI( DogGoActivity act){
		this.m_act = act;		
	}
	@JavascriptInterface
	public String getDeviceDataAll( ){		  

		JSONArray		json = new JSONArray( );
		
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			json.put( data.getJSONData() );
		}   
    	return json.toString();                        
	}

	@JavascriptInterface
	public String getDeviceData( String address ){ 
		String		str = new String();

		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				str = data.getJSONData().toString();
			}				 
		}
		return str;
	}
	

	@JavascriptInterface	
	public void editDevice ( String address, String name, String psd ){
		Log.w(TAG, "edit:"+address+':'+name+':' +psd);
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				data.m_name = name;
				data.m_password = psd;
				data.setDirty(true);
				break;
			}				 
		}
		
	}

	// 已链接的断开链接
	// 未链接的链接
	@JavascriptInterface
	/*public void toggleConnect( String address ){
		
		Log.w(TAG, "taggleConnect"+ address );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				Log.w(TAG, data.m_address+" status:"+ String.valueOf(data.getConnectState()));
				if( data.isConnected() ){
					data.m_autoConnected = false;
					m_act.disconnect( address );
					data.setDirty(true);
				}
				// 有信号且为断开时才链接
				else if(data.isDisConnected() && data.haveRssi() ){
					data.m_autoConnected = true;
					data.setConnectState( BluetoothLeServiceM.STATE_CONNECTING );
					m_act.connect(address);
					m_act.updateWebView("javascript:onConnecting()");
					// 有信号时限定几秒内必须完成连接
					data.setConnectedCountDown( 5 );
					data.setDirty(true);
				}
				break;
			}				 
		}
		
	}*/

	public void toggleConnect( String address ){
		
		Log.w(TAG, "taggleConnect"+ address );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				Log.w(TAG, data.m_address+" status:"+ String.valueOf(data.getConnectState()));
				if( data.isConnected() ){
					data.m_autoConnected = false;
					//m_act.disconnect( address );
					m_act.stopsocketThread( );
					m_act.disconnectwifi( address );
					data.setDirty(true);
				}
				// 有信号且为断开时才链接
				else if(data.isDisConnected() && data.haveRssi() ){
					data.m_autoConnected = true;
					data.setConnectState( BluetoothLeServiceM.STATE_CONNECTING );
					//m_act.connect(address);
					m_act.connectwifi( address );
					m_act.updateWebView("javascript:onConnecting()");
					// 有信号时限定几秒内必须完成连接
					//data.setConnectedCountDown( 5 );
					data.setDirty(true);
				}
				break;
			}				 
		}
		
	}
	// TESTNG
	@JavascriptInterface
	public void action( String action, String addr ){
		
		
		//Log.w(TAG, action+':'+addr);
		
		String[] actions = action.split( "\\|\\|\\|" );
		String[] addrs = addr.split( "\\|\\|\\|" );
		
		String	 senddata = new String();

		//Log.w(TAG, String.valueOf(actions.length)+':'+String.valueOf(addrs.length));
		
		for( int i = 0; i < addrs.length; i++){
			String[]  values;
			int	   act;
			
			senddata = "";
			for( int j = 0; j < actions.length; j++ ){
				act = Integer.parseInt(actions[j]);

				//Log.w(TAG, "action is"+String.valueOf(act) );
				// 设防丢直接完成
				if( act == 4 ){
					//this.m_act.changeDeviceMode(addrs[i], act );
	        		//String js = String.format("javascript:onDeviceDataRead('%s','%d')", addrs[i], act );

					Log.w(TAG, "action is 4, not need do anything" );
	        		//this.m_act.updateWebView( js );
				}
				else{
					values = this.m_act.getParam( addrs[i], act );
					if( senddata.length() > 0 ){
						senddata += ";";					
					}
					senddata += Protocol.Make(act, values);
				}
			}		
			if( senddata.length() > 0 ){
				Log.w(TAG, "send:"+senddata);
				this.m_act.write( addrs[i], senddata+"\r\n", 5 );			
			}
		}
	}

	@JavascriptInterface
	public void changeShock( String address, String v){
		
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_shockLvl = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void changeVibration(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_vibrationLvl = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void changeVoiceLvl(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_voiceLvl = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void changeVoiceType(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_voiceType = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void setSlientShock(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){		
				
				if( v0 == 1){
					data.setSlientShock( true );
				}
				else{
					data.setSlientShock( false );
				}
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void setSlientVibration(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){		
				
				if( v0 == 1){
					data.setSlientVibration( true );
				}
				else{
					data.setSlientVibration( false );
				}
				data.setDirty(true);
				break;
			}				 
		}
	}	
	@JavascriptInterface
	public void changeSlientNoiseI(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_slientNoiseI = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void changeSlientVibrationI(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_slientVibrationI = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	
	@JavascriptInterface
	public void changeSlientShockO(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_slientShockO = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void changeslientVibrationO(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_slientVibrationO = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void changeAntiLostDistance(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_antiLostDistLvl = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void changeAntiLostMusic(String address, String v){
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){				
				data.m_antiLostMusic = Integer.parseInt( v );
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	public void setToAntiLostMode(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){		
				
				if( v0 == 1){
					data.setAntiMode( true );
				}
				else{
					data.setAntiMode( false );
				}
				data.setDirty(true);
				break;
			}				 
		}
	}
	@JavascriptInterface
	/*public void scan( ){		// scan wifi
		this.m_act.scanLeDevice();	
		this.m_act.setAllRssiZero();
		this.m_act.updateWebView("javascript:initView()");
	}*/
	public void scan( ){		// scan wifi
		//this.m_act.setAllRssiZero();
		this.m_act.scanWifiDevice();
		this.m_act.updateWebView("javascript:initView()");
	}

	@JavascriptInterface
	public String getMusicList( ){
		JSONArray json = MyRingTone.getRingtoneTitleListJson( RingtoneManager.TYPE_ALARM );
		return json.toString();	
	}

	@JavascriptInterface
	public void playMusic( String v ){
		int n = Integer.parseInt( v );
		this.m_act.playMusic(n);
	}

	@JavascriptInterface
	public void stopMusic( ){
		this.m_act.stopMusic( );		
	}
	@JavascriptInterface
	public void stopVibrate( ){
		this.m_act.stopVibrate( );		
	}
	@JavascriptInterface
	public void setToSleepMode(String address, String v ){

		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){		
				
				if( v0 == 1){
					data.m_sleepmode = 1;
				}
				else{
					data.m_sleepmode = 0;
				}
				data.setDirty(true);
				break;
			}				 
		}
		
	}

	@JavascriptInterface
	public void setToSleepStart(String address, String v ){

		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){		
				
				data.m_sleepstart = v;
				data.setDirty(true);
				break;
			}				 
		}
		
	}

	@JavascriptInterface
	public void setToSleepEnd(String address, String v ){

		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){
				data.m_sleepend = v;
				data.setDirty(true);
				break;
			}				 
		}
		
	}
	
	@JavascriptInterface
	public void setToMobileAttMode(String address, String v ){

		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){		
				
				if( v0 == 1){
					data.setAntiLostMobileAttn( true );
				}
				else{
					data.setAntiLostMobileAttn( false );
				}
				data.setDirty(true);
				break;
			}				 
		}
		
	}
	@JavascriptInterface
	public void setToDogCtrMode(String address, String v ){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){		
				
				if( v0 == 1){
					data.setAntiLostDeviceCtrl( true );
				}
				else{
					data.setAntiLostDeviceCtrl( false );
				}
				data.setDirty(true);
				break;
			}				 
		}
		
	}
	@JavascriptInterface
	public void setToMobileVibration(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){						
				data.m_antiLostMobileVib = v0;
				data.setDirty(true);
				break;
			}				 
		}		
		
	}
	@JavascriptInterface
	public void setToAntiLostShockCtrl(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){	
				if( v0 == 1){
					data.setAntiLostShockCtrl(true);
				}
				else{
					data.setAntiLostShockCtrl(false);
				}
				data.setDirty(true);
				break;
			}				 
		}		
		
	}
	@JavascriptInterface
	public void setToAntiLostVibrationCtrl(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){	
				if( v0 == 1){
					data.setAntiLostVibrationCtrl(true);
				}
				else{
					data.setAntiLostVibrationCtrl(false);
				}
				data.setDirty(true);
				break;
			}				 
		}		
	}
	@JavascriptInterface
	public void setToAntiLostVoiceCtrl(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){	
				if( v0 == 1){
					data.setAntiLostVoiceCtrl(true);
				}
				else{
					data.setAntiLostVoiceCtrl(false);
				}
				data.setDirty(true);
				break;
			}				 
		}		
	}

	@JavascriptInterface
	public void changeAntiLostShockO(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){						
				data.m_antiLostShockO = v0;
				data.setDirty(true);
				break;
			}				 
		}		
	}
	@JavascriptInterface
	public void changeAntiLostVibrationO(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){						
				data.m_antiLostVibrationO = v0;
				data.setDirty(true);
				break;
			}				 
		}		
	}
	@JavascriptInterface
	public void changeAntiLostVoiceTypeO(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){						
				data.m_antiLostVoiceTypeO = v0;
				data.setDirty(true);
				break;
			}				 
		}				
	}
	@JavascriptInterface
	public void changeAntiLostVoiceLvlO(String address, String v){
		int			v0 = Integer.parseInt( v );
		for( DeviceData data : DogGoActivity.getAllDeviceData() ){
			if( data.m_address.compareToIgnoreCase(address) == 0 ){						
				data.m_antiLostVoiceLvlO = v0;
				data.setDirty(true);
				break;
			}				 
		}		
		
	}
	
	
}
