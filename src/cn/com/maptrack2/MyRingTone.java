package cn.com.maptrack2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class MyRingTone {
	
	public static final String TAG = "MyRingTone";
	public static Context	m_ctx;
		
	public static void init( Context ctx ){		
		m_ctx = ctx;
	}
	
	public static Ringtone getDefaultRingtone(int type){ 

	    return RingtoneManager.getRingtone( m_ctx, RingtoneManager.getActualDefaultRingtoneUri(m_ctx, type) ); 

	} 
	
	public static Uri getDefaultRingtoneUri(int type){ 
	    return RingtoneManager.getActualDefaultRingtoneUri( m_ctx, type ); 
	} 
	// not test
	public static List<Ringtone> getRingtoneList(int type){ 

	    List<Ringtone> resArr = new ArrayList<Ringtone>(); 

	    RingtoneManager manager = new RingtoneManager( m_ctx ); 
	    manager.setType(type); 
	    
	    Cursor cursor = (Cursor) manager.getCursor();
	    if( cursor.moveToFirst() ){
		    do{ 
	            resArr.add(manager.getRingtone(cursor.getPosition())); 
	        }while(cursor.moveToNext()); 
	    }
	    cursor.close();
	    return resArr; 
	}
	
	public static Ringtone getRingtone(int type, int pos){ 
		Ringtone rt = null;
	    RingtoneManager manager = new RingtoneManager( m_ctx ); 
	    manager.setType(type); 
	    Cursor cursor = manager.getCursor();
	    
	    if( cursor.moveToFirst() ){
	    	cursor.move(pos);    
		    rt = manager.getRingtone(  cursor.getPosition() ); 
	    }
	    cursor.close();
	    return rt;

	} 

	public static Uri getRingtoneUri(int type,int pos){ 
		Uri uri = null;
	    RingtoneManager manager = new RingtoneManager( m_ctx ); 
	    manager.setType(type); 	
	    Cursor cursor = manager.getCursor();
	    
	    if( cursor.moveToFirst() ){
	    	cursor.move(pos);
		    uri = manager.getRingtoneUri(cursor.getPosition());
	    }
	    cursor.close();
	    return uri;
	} 
	
	public static List<String> getRingtoneTitleList(int type){ 

	    List<String> resArr = new ArrayList<String>(); 

	    RingtoneManager manager = new RingtoneManager(m_ctx); 
	    manager.setType(type); 

	    Cursor cursor = manager.getCursor();
		if(cursor.moveToFirst()){ 
	        do{ 
	            resArr.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)); 
	        }while(cursor.moveToNext()); 
	    } 
		cursor.close();
	    return resArr; 
	}
	

	public static JSONArray getRingtoneTitleListJson(int type){ 

		JSONArray		jsonArray = new JSONArray( );

	    RingtoneManager manager = new RingtoneManager(m_ctx); 
	    manager.setType(type); 

	    Cursor cursor = manager.getCursor();
		if(cursor.moveToFirst()){ 
			JSONObject		json = null;
			int				i = 0;

	        do{ 
	    		try {
	    			json = new JSONObject();
	    			json.put("v", i++ );
					json.put("n", cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX) );
		    		jsonArray.put( json );
				} catch (JSONException e) {
					Log.w( "MyRingTone", e.getMessage() );
				}
	        }while(cursor.moveToNext()); 

	    } 
		cursor.close();
	    return jsonArray; 
	}
	
	public static String getRingtoneUriPath(int type,int pos ){ 

		Uri		uri = null;
		String	str = new String("");
	    RingtoneManager manager = new RingtoneManager( m_ctx ); 
	    manager.setType(type); 
	    

	    Cursor cursor = manager.getCursor();
	    if( cursor.moveToFirst() ){
	    	cursor.move(pos);
	    	
		    uri = manager.getRingtoneUri( cursor.getPosition() );
		    if( uri != null ){
		    	str= uri.toString(); 
		    }
	    }
	    cursor.close();
	    return str;

	} 

	 

	public static Ringtone getRingtoneByUriPath(int type ,String uriPath){ 
	    RingtoneManager manager = new RingtoneManager( m_ctx );
	    manager.setType(type); 
	    Uri uri = Uri.parse(uriPath); 
	    return manager.getRingtone(m_ctx, uri); 
	}   
	
	
}
