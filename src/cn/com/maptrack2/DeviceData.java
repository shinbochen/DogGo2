package cn.com.maptrack2;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.maptrack2.R;

import android.text.format.Time;  
import android.util.Log;  

public class DeviceData{

	
	public static final int MODE_TRAINING = 0x03;	// 训练模式 0.1.2 =shock.vibration.voice
	
	public static final int MODE_SLEEP = 0x20;		// 睡眠模式
	public static final int	MODE_SLIENT = 0x40;		// 止吠模式
	public static final int	MODE_ANTILOST = 0x80;	// 防丢模式
	
	public static final int MODE_SLIENT_SHOCK = 0x01;		// 电J止吠
	public static final int MODE_SLIENT_VIBRATION = 0x02;	// 震动止吠
	
	public static final int MODE_ANTI_MOBILE_ATTN = 0x01;
	public static final int MODE_ANTI_DEVICE_CTRL = 0x02;
	public static final int MODE_ANTI_DEVICE_SHOCK = 0x04;
	public static final int MODE_ANTI_DEVICE_VIBRATION = 0x08;
	public static final int MODE_ANTI_DEVICE_VOICE = 0x10;
	
	public static final int SOCKET_DISCONNECT = 0x00;
	public static final int SOCKET_CONNECT = 0x01;
	

	public static final int	RSSI_MIN	 = 30;
	public static final int	RSSI_MAX  = 100;
	
    private final static String TAG = DeviceData.class.getSimpleName();
    
	public static final String SQL_TABLE = "devicedata";
	
	public static final String SQL_CREATE = "create table if not exists "+SQL_TABLE+"(" +
													 "name 	   			text not null,"+ 
													 "address  			text primary key not null,"+
													 "password 			text,"+
													 "icon     			integer,"+
													 "autoconnect   	integer,"+
													 "mode     			integer,"+
													 "shocklvl 			integer,"+
													 "vibrationlvl     	integer,"+
													 "voicetype 		integer,"+
													 "voicelvl 			integer,"+

													 "slientmode		integer,"+	
													 "slientnoisei 		integer,"+
													 "slientvibrationi	integer,"+												 
													 "slientshocko 		integer,"+
													 "slientvibrationo	integer,"+

													 "antimode			integer,"+													 
													 "antilostdistlvl	integer,"+
													 
													 "antilostmobilevib	integer,"+
													 "antilostmusic		integer,"+
													 

													 "antilostshocko		integer,"+
													 "antilostvibrationo	integer,"+
													 "antilostvoicetypeo	integer,"+
													 "antilostvoicelvlo		integer,"+
													 

													 "sleepmode				integer,"+
													 "sleepstart			text,"+
													 "sleepend				text"+
													 
													 
													 ")";
	
	
	// 不存入数据库
	public	int		m_rssi;
	public	int		m_connected;
	public  int		m_socketstate;	// 0：没有连上服务器  1:表示连上服务器
	public	boolean m_dirty;		// true 表示已存过数据库
	public  int		m_antilostcnt; 
	public  int 	m_battery;
	
	// 有信号时，且人工连接时，置此标志为1
    // 置此标志为1时：如果规定时间内  没有收到 【连接成功，或连接不成功】视为再也不可能连接成功要采取措施
	// 连接倒计数，如果倒计数结束还没结果。那当前链接可能永远也连不上要另想办法
	// =-1时不需要倒计数
	// 也做为读信号倒计时用。
	public  int 	m_connctedcountdown;	
	
	// 存入数据库
	
	
	
	public  String  m_name;
	public	String  m_address;
	public	String  m_password;
	public  int		m_icon;	
	public  boolean	m_autoConnected;	

	// setup parameter
	
	public	int		m_mode;				// 	
	public 	int		m_shockLvl;			// 电击级别
	public 	int		m_vibrationLvl;		// 震动级别
	public	int		m_voiceType;		// 声音训练类别
	public	int		m_voiceLvl;			// 声音训练类别
	

	public	int		m_slientNoiseI;		// 止吠的声音灵敏度 b=0,4,0,0
	public	int		m_slientVibrationI;	// 止吠的震动灵敏度
	
	public	int		m_slientMode;		// b0:shock, b1:振动
	public	int		m_slientShockO;
	public	int		m_slientVibrationO;
	

	public	int		m_antimode;			// 0x01 mobile attention   
										// 0x02 device control. 
										// 0x04:shock, 
										// 0x08:vibration. 
										// 0x10:voice
	
	public 	int		m_antiLostDistLvl;		// 自动防丢距离
	//=== mobile attetion
	public 	int 	m_antiLostMobileVib;	// 0/1 disable/enable
	public	int		m_antiLostMusic;		// 丢失时音乐
	
	//=== device control
	public 	int 	m_antiLostShockO;
	public 	int 	m_antiLostVibrationO;
	public 	int 	m_antiLostVoiceTypeO;
	public 	int 	m_antiLostVoiceLvlO;
	
	//==== sleepmode
	public 	int 	m_sleepmode;		// 0/1 disable/enable
	public  String  m_sleepstart;		// 开始睡眠时间
	public  String  m_sleepend;			// 结束睡眠时间
	
	
	
	public  static final int 	m_sigicon[] = {
		R.drawable.sig0,
		R.drawable.sig1,
		R.drawable.sig2,
		R.drawable.sig3,
		R.drawable.sig4			
	};
	public  static final int 	m_dogicon[] = {
		R.drawable.dog1,
		R.drawable.dog2,
		R.drawable.dog3		
	};

	public  static final int 	m_connecticon[] = {
		R.drawable.connect,
		R.drawable.connecting,
		R.drawable.disconnect		
	};
	
	public DeviceData( HashMap<String, String> record ){
		
		m_name = record.get("name");
		m_address = record.get("address");
		m_password = record.get("password");

		m_icon = Integer.parseInt( record.get("icon"));
		
		m_antilostcnt = 0;
		m_battery = 0;
		
		m_rssi = 0;
		m_connctedcountdown = -1;
		
		m_connected = BluetoothLeServiceM.STATE_DISCONNECTED;	
		m_socketstate = SOCKET_DISCONNECT;
		m_autoConnected = Integer.parseInt(record.get( "autoconnect")) == 1 ? true :false;
		setDirty( false );
		
		
		m_mode = record.get("mode") == null ? 0: Integer.valueOf(record.get("mode"));	
		
		m_shockLvl = record.get("shocklvl") == null ? 0: Integer.parseInt(record.get("shocklvl"));
		m_vibrationLvl = record.get("vibrationlvl") == null ? 0: Integer.parseInt(record.get("vibrationlvl"));
		m_voiceType = record.get("voicetype") == null ? 0: Integer.parseInt(record.get("voicetype"));
		m_voiceLvl = record.get("voicelvl") == null ? 0: Integer.parseInt(record.get("voicelvl"));
		


		m_slientMode = record.get("slientmode") == null ? 0: Integer.parseInt(record.get("slientmode"));
		m_slientNoiseI = record.get("slientnoisei") == null ? 0: Integer.parseInt(record.get("slientnoisei"));
		m_slientVibrationI = record.get("slientvibrationi") == null ? 0: Integer.parseInt(record.get("slientvibrationi"));
		m_slientShockO = record.get("slientshocko") == null ? 0: Integer.parseInt(record.get("slientshocko"));
		m_slientVibrationO = record.get("slientvibrationo") == null ? 0: Integer.parseInt(record.get("slientvibrationo"));
		
		
		m_antimode = record.get("antimode") == null ? 0: Integer.parseInt(record.get("antimode"));
		m_antiLostDistLvl = record.get("antilostdistlvl") == null ? 0: Integer.parseInt(record.get("antilostdistlvl"));
		m_antiLostMobileVib = record.get("antilostmobilevib") == null ? 0: Integer.parseInt(record.get("antilostmobilevib"));
		m_antiLostMusic = record.get("antilostmusic") == null ? 0: Integer.parseInt(record.get("antilostmusic"));

		
		//=== device control
		m_antiLostShockO = record.get("antilostshocko") == null ? 0: Integer.parseInt(record.get("antilostshocko"));
		m_antiLostVibrationO = record.get("antilostvibrationo") == null ? 0: Integer.parseInt(record.get("antilostvibrationo"));
		m_antiLostVoiceLvlO = record.get("antilostvoicelvlo") == null ? 0: Integer.parseInt(record.get("antilostvoicelvlo"));
		m_antiLostVoiceTypeO = record.get("antilostvoicetypeo") == null ? 0: Integer.parseInt(record.get("antilostvoicetypeo"));
		
		//=== sleep control
		m_sleepmode = record.get("sleepmode") == null ? 0: Integer.parseInt(record.get("sleepmode"));
		m_sleepstart = record.get("sleepstart") == null ? "": record.get("sleepstart");
		m_sleepend = record.get("sleepend") == null ? "": record.get("sleepend");
	}
	

	
	
	public DeviceData( String name, String addr, int rssi){
		m_name = name;
		m_address = addr;
		m_rssi = rssi;

		m_connctedcountdown = -1;
		m_antilostcnt = 0;
		m_battery = 0;
		
		m_password = "";
		m_icon	= 0;
		m_connected = BluetoothLeServiceM.STATE_DISCONNECTED;	
		m_socketstate = SOCKET_DISCONNECT;
		m_autoConnected = false;		
		setDirty( false );
		
		

		m_mode = 0;				// 	
		m_shockLvl = 0;			// 电击级别
		m_vibrationLvl = 0;		// 震动级别
		m_voiceType = 0;		// 声音训练类别
		m_voiceLvl = 0;			// 声音训练类别
		
		m_slientMode = 0;
		m_slientNoiseI = 0;		// 止吠的震动级别
		m_slientVibrationI = 0;	// 止吠的声音级别
		m_slientShockO = 0;
		m_slientVibrationO = 0;
		
		

		m_antimode = 0;
		m_antiLostDistLvl = 0; 		// 自动防丢距离
		m_antiLostMobileVib = 0;	
		m_antiLostMusic = 0;		// 丢失时音乐

		
		//=== device control
		m_antiLostShockO = 0;
		m_antiLostVibrationO = 0;
		m_antiLostVoiceLvlO = 0;
		m_antiLostVoiceTypeO = 0;		
		

		m_sleepmode = 0;
		m_sleepstart = "18:00:00";
		m_sleepend = "9:00:00";
		
	}
	
	public void setConnectedCountDown( int cnt ){
		m_connctedcountdown = cnt;
	}
	public int getConnectedCountDown( ){
		return m_connctedcountdown;
	}
	
	public boolean calcConnectCountDown( ){
		if(m_connctedcountdown == -1){
			return false;
		}		
		if( --m_connctedcountdown == 0 ){
			m_connctedcountdown = -1;
			return true;
		}
		return false;
	}
	
	public void setSlientShock( boolean flag ){
		if( flag ){
			m_slientMode |= MODE_SLIENT_SHOCK;
		}
		else{
			m_slientMode &= ~MODE_SLIENT_SHOCK;
		}		
	}
	public void setSlientVibration( boolean flag ){
		if( flag ){
			m_slientMode |= MODE_SLIENT_VIBRATION;
		}
		else{
			m_slientMode &= ~MODE_SLIENT_VIBRATION;
		}
	}
	
	public void setAntiMode( boolean flag ){
		if( flag ){
			m_mode |= MODE_ANTILOST;
		}
		else{
			m_mode &= ~MODE_ANTILOST;
		}
	}
	
	public void setAntiLostMobileAttn( boolean flag ){
		if( flag ){
			m_antimode |= MODE_ANTI_MOBILE_ATTN;
		}
		else{
			m_antimode &= ~MODE_ANTI_MOBILE_ATTN;
		}
	}
	public boolean isAntiLostMobileAttn(   ){		
		return (m_antimode&MODE_ANTI_MOBILE_ATTN) > 0 ? true :false;
	}
	public void setAntiLostDeviceCtrl( boolean flag ){
		if( flag ){
			m_antimode |= MODE_ANTI_DEVICE_CTRL;
		}
		else{
			m_antimode &= ~MODE_ANTI_DEVICE_CTRL;
		}
	}
	public boolean isAntiLostDeviceCtrl(  ){
		return (m_antimode&MODE_ANTI_DEVICE_CTRL) > 0 ? true :false;
	}
	public void setAntiLostShockCtrl( boolean flag ){
		if( flag ){
			m_antimode |= MODE_ANTI_DEVICE_SHOCK;
		}
		else{
			m_antimode &= ~MODE_ANTI_DEVICE_SHOCK;
		}
	}
	public boolean isAntiLostShockCtrl(  ){
		return (m_antimode&MODE_ANTI_DEVICE_SHOCK) > 0 ? true :false;
	}
	public void setAntiLostVibrationCtrl( boolean flag ){
		if( flag ){
			m_antimode |= MODE_ANTI_DEVICE_VIBRATION;
		}
		else{
			m_antimode &= ~MODE_ANTI_DEVICE_VIBRATION;
		}
	}
	public boolean isAntiLostVibrationCtrl(  ){
		return (m_antimode&MODE_ANTI_DEVICE_VIBRATION) > 0 ? true :false;
	}
	public void setAntiLostVoiceCtrl( boolean flag ){
		if( flag ){
			m_antimode |= MODE_ANTI_DEVICE_VOICE;
		}
		else{
			m_antimode &= ~MODE_ANTI_DEVICE_VOICE;
		}
	}
	public boolean isAntiLostVoiceCtrl(  ){
		return (m_antimode&MODE_ANTI_DEVICE_VOICE) > 0 ? true :false;
	}
	
	public boolean isDirty(){
		return m_dirty;
	}
	public void setDirty( boolean flag){
		m_dirty = flag;		
	}
	public void setAutoConnect( boolean flag ){
		m_autoConnected =flag;
	}

	public void setConnectState( int flag ){
		m_connected =flag;
	}
	public int getConnectState( ){
		return m_connected;
	}
	public boolean isConnected( ){
		return (m_connected == BluetoothLeServiceM.STATE_CONNECTED) ? true : false;
	}
	public boolean isDisConnected( ){
		return (m_connected == BluetoothLeServiceM.STATE_DISCONNECTED) ? true : false;
	}
	public boolean isSocketConnected( ){
		return (m_socketstate == SOCKET_CONNECT) ? true : false;
	}
	public boolean haveRssi( ){
		return (m_rssi != 0) ? true : false;
	}
	public boolean isAutoConnect( ){
		return m_autoConnected;
	}
	
	public void changeMode( int action ){
		if( (action  < 3) && (action >0) ){
			m_mode &= ~MODE_SLIENT;
			m_mode &= ~MODE_TRAINING;
			m_mode |= action;
		}
		else if(action == 3 ){
			m_mode |= MODE_SLIENT;
		}
		else if(action == 4 ){
			m_mode |= MODE_ANTILOST;
		}
		else if( action == 5 ){		// 收到sleep回复时看以前的设置来决定On or off			
			if( m_sleepmode > 0 ){
				m_mode |= MODE_SLEEP;				
			}
			else{
				m_mode &= ~MODE_SLEEP;					
			}
		}
		setDirty( true );
	}
	public void changeBattery( int battery ){
		m_battery = battery;
	}
	
	public int getSigIcon(){
		int		i = 0;
		if( m_rssi == 0 ){
			i = 0;
		}
		else if( m_rssi < -120 ){
			i = 0;
		}
		else if( m_rssi < -80 ){
			i = 1;
			
		}
		else if( m_rssi < -50 ){
			i = 2;
			
		}
		else if( m_rssi < -30 ){
			i = 3;			
		}
		else{
			i = 4;
		}
		return m_sigicon[i];
	}
	public int getDogIcon(){
		if( m_icon < m_dogicon.length ){
			return m_dogicon[m_icon];
		}
		else{
			return m_dogicon[0];
		}		
	}
	public int getConnectIcon( ){
		if( m_connected < m_connecticon.length){
			return m_connecticon[m_connected];
		}
		else{
			return m_connecticon[2];			
		}
	}
	/*
	public 	int[]	getPara( int action ){
		int[]		result = null;
		
		switch( action ){
		case 0:
			result = new int[1];
			result[0] = this.m_shockLvl;
			break;
		case 1:
			result = new int[1];
			result[0] = this.m_vibrationLvl;			
			break;
		case 2:
			result = new int[2];
			result[0] = this.m_voiceType;	
			result[1] = this.m_voiceLvl;	
			break;
		
		case 3:
			result = new int[4];
			result[0] = this.m_slientNoiseI;	
			result[1] = this.m_slientVibrationI;	
			if( (m_slientMode&MODE_SLIENT_SHOCK) > 0){
				result[2] = m_slientShockO;
			}
			else{
				result[2] = 0;
			}
			if( (m_slientMode&MODE_SLIENT_VIBRATION) > 0){
				result[3] = m_slientVibrationO;
			}
			else{
				result[3] = 0;
			}
			break;	
			
		case 5:		// sleep
			
			
		case 10:
			result = new int[1];
			result[0] = this.m_antiLostShockO;
			break;

		case 11:
			result = new int[1];
			result[0] = this.m_antiLostVibrationO;
			break;

		case 12:
			result = new int[2];
			result[0] = this.m_antiLostVoiceTypeO;	
			result[1] = this.m_antiLostVoiceLvlO;	
			break;
			
		default:
			break;
		}
		return result;		
	}
	*/
	public  String 		getCurrentTime( ){  
		
		String s;
		Calendar calendar = Calendar.getInstance();
		
		s = calendar.get(Calendar.HOUR_OF_DAY) + ":"
		  + calendar.get(Calendar.MINUTE) + ":"
		  + calendar.get(Calendar.SECOND);
		return s;		
	}
	public 	String[]	getPara( int action ){
		String[]		result = null;
		
		switch( action ){
		case 0:
			result = new String[1];
			result[0] = Integer.toString ( this.m_shockLvl);
			break;
		case 1:
			result = new String[1];
			result[0] = Integer.toString ( this.m_vibrationLvl );			
			break;
		case 2:
			result = new String[2];
			result[0] = Integer.toString ( this.m_voiceType );	
			result[1] = Integer.toString ( this.m_voiceLvl );	
			break;
		
		case 3:
			result = new String[4];
			result[0] = Integer.toString (this.m_slientNoiseI);	
			result[1] = Integer.toString (this.m_slientVibrationI);	
			if( (m_slientMode&MODE_SLIENT_SHOCK) > 0){
				result[2] = Integer.toString (m_slientShockO);
			}
			else{
				result[2] = "0";
			}
			if( (m_slientMode&MODE_SLIENT_VIBRATION) > 0){
				result[3] = Integer.toString (m_slientVibrationO);
			}
			else{
				result[3] = "0";
			}
			break;	
			
		case 5:		// sleep
			result = new String[4];
			result[0] = Integer.toString (this.m_sleepmode);	
			result[1] = m_sleepstart;	
			result[2] = m_sleepend;	
			result[3] = getCurrentTime();
			break;	
			
			
		// 音独控制
		case 10:
			result = new String[1];
			result[0] = Integer.toString (this.m_antiLostShockO);
			break;

		case 11:
			result = new String[1];
			result[0] = Integer.toString (this.m_antiLostVibrationO);
			break;

		case 12:
			result = new String[2];
			result[0] = Integer.toString (this.m_antiLostVoiceTypeO);	
			result[1] = Integer.toString (this.m_antiLostVoiceLvlO);	
			break;
			
		default:
			break;
		}
		return result;		
	}
	
	// for database
	public  HashMap<String,String> getHashMap(  ){	
		
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("name", m_name);
		map.put("address", m_address);
		map.put("password", m_password);
		map.put("icon", Integer.toString( m_icon ));	
		if( m_autoConnected ){
			map.put("autoconnect", "1");				
		}
		else{
			map.put("autoconnect", "0");
		}
		map.put("mode", Integer.toString( m_mode ));	
		map.put("shocklvl", Integer.toString( m_shockLvl ));	
		map.put("vibrationlvl", Integer.toString( m_vibrationLvl ));	
		map.put("voicetype", Integer.toString( m_voiceType ));	
		map.put("voicelvl", Integer.toString( m_voiceLvl ));	
		

		
		map.put("slientmode", Integer.toString( m_slientMode));	
		map.put("slientnoisei", Integer.toString( m_slientNoiseI  ));	
		map.put("slientvibrationi", Integer.toString( m_slientVibrationI ));
		map.put("slientshocko", Integer.toString( m_slientShockO ));	
		map.put("slientvibrationo", Integer.toString( m_slientVibrationO ));
		

		map.put("antimode", Integer.toString( m_antimode ));	
		map.put("antilostdistlvl", Integer.toString( m_antiLostDistLvl ));	
		
		map.put("antilostmobilevib", Integer.toString( m_antiLostMobileVib ));	
		map.put("antilostmusic", Integer.toString( m_antiLostMusic  ));	
				
		map.put("antilostshocko", Integer.toString( m_antiLostShockO ));			
		map.put("antilostvibrationo", Integer.toString( m_antiLostVibrationO ));
		map.put("antilostvoicetypeo", Integer.toString( m_antiLostVoiceTypeO ));			
		map.put("antilostvoicelvlo", Integer.toString( m_antiLostVoiceLvlO ));		
		
		

		map.put("sleepmode", Integer.toString( m_sleepmode ));			
		map.put("sleepstart", m_sleepstart );
		map.put("sleepend", m_sleepend );	
		
		return map;
	}
	
	
	// for JS
	
 	public JSONObject getJSONData( ){

		JSONObject		json = new JSONObject();
		
		try {
			json.put("name", m_name);
			json.put("address", m_address);
			json.put("password", m_password);
			json.put("icon", m_icon);
			
			json.put("rssi", m_rssi);
			json.put("state", m_connected);
			json.put("socketstate", m_socketstate);
			json.put("mode", m_mode);
			json.put("battery", m_battery);

			json.put("shockLvl", m_shockLvl);
			json.put("vibrationLvl", m_vibrationLvl);
			json.put("voiceType", m_voiceType);
			json.put("voiceLvl", m_voiceLvl);

			json.put("slientMode", m_slientMode);
			json.put("slientNoiseI", m_slientNoiseI);
			json.put("slientVibrationI", m_slientVibrationI);
			json.put("slientShockO", m_slientShockO);
			json.put("slientVibrationO", m_slientVibrationO);
									
			json.put("antimode", m_antimode);
			json.put("antiLostDistLvl", m_antiLostDistLvl);
			
			json.put("antiLostMobileVib", m_antiLostMobileVib);
			json.put("antiLostMusic", m_antiLostMusic);
			

			json.put("antiLostShockO", m_antiLostShockO);
			json.put("antiLostVibrationO", m_antiLostVibrationO);
			json.put("antiLostVoiceTypeO", m_antiLostVoiceTypeO);
			json.put("antiLostVoiceLvlO", m_antiLostVoiceLvlO);
			
			json.put("sleepmode", m_sleepmode);
			json.put("sleepstart", m_sleepstart);
			json.put("sleepend", m_sleepend);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

 	public boolean isAntiLostedAlarm( ){
 		boolean		result = false;
 		int			rssi = m_rssi*-1;
 		float		tmp = 0;
 		
 		if( isConnected() && ((m_mode & MODE_ANTILOST) > 0) ){ 

 			
 			tmp = (rssi-DeviceData.RSSI_MIN)*100; 			
 			rssi = Math.round( tmp/(DeviceData.RSSI_MAX-DeviceData.RSSI_MIN) );
 			
 			// 只报警一次
 			if( rssi > m_antiLostDistLvl ){
 				if( m_antilostcnt == 3 ){
 					result = true;
 					m_antilostcnt++;
 				}
 				else if( m_antilostcnt < 3 ){
 					m_antilostcnt++;
 				}
 			}
 			else{
 				m_antilostcnt = 0;
 			} 
 		}
 		return result; 		
 	}
 	
 	
 	
 	public String  getSqlClause( ){
 		
 		String 	str = new String();
 		
 		str = String.format( "address='%s' ", this.m_address);
 		return str;
 	}
}
