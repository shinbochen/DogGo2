package cn.com.maptrack2;

import android.util.Log;

public class Protocol {
	
	public static final String key[] = new String[]{
		"a",		// shock
		"b",		// vibration
		"c",		// voice	
		"d",		// slient
		"e",		// not used anti-lost
		"s",		// sleep  s=0,start,endtime,curtime
		"z"			// battery
	};
	
	public Protocol( ){	
	}
	// 请求一个ACK
	public static String MakeAck( ){
		return "x=1";		
	}
	
	public static String Make( int action, int[] value){
		
		String 		result = new String();
		
		
		if( (action < key.length) && (value != null) ){		
			result = key[action]+"=";
			
			for( int i = 0; i< value.length; i++){
				if( i > 0 ){
					result += ",";
				}
				result += Integer.toString(value[i]);
			}
		}
		return result;
	}
	

	public static String Make( int action, String[] value){
		
		String 		result = new String();
		
		
		if( (action < key.length) && (value != null) ){		
			result = key[action]+"=";
			
			for( int i = 0; i< value.length; i++){
				if( i > 0 ){
					result += ",";
				}
				result += value[i];
			}
		}
		return result;
	}

	// 分析出action类型
	public static int[] Parse( String data ){
		
		int[]		result = null;
		String[] 	ps = null;
		
		if( data.indexOf(";") != -1 ){
			ps = data.split(";");			
		}
		else{
			ps = new String[1];
			ps[0] = data;
		}
			
		result = new int[ps.length];         
		
		
		for( int i = 0; i < ps.length; i++ ){
			result[i] = -1;
			for( int j = 0; j < key.length; j++ ){
				if( ps[i].indexOf( key[j]+"=" ) != -1 ){
					result[i] = j;
				} 
			}  
		}  
		return result;
	}

	public static int GetBattery( String data ){
		
		int			result = 0;
		String[] 	ps = null;
		
		if( data.indexOf(";") != -1 ){
			ps = data.split(";");			
		}
		else{
			ps = new String[1];
			ps[0] = data;
		}

		
		for( int i = 0; i < ps.length; i++ ){
			int k = ps[i].indexOf(key[key.length-1]);

			if( k != -1 ){	
				result = Integer.parseInt( ps[i].substring(k+2).trim() );
			}
		}
		return result;
	}
}
