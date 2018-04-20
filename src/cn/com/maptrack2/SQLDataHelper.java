package cn.com.maptrack2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDataHelper extends SQLiteOpenHelper {

    private final static String TAG = SQLDataHelper.class.getSimpleName();
	private	String		m_sDBName;
	private int			m_iVersion;
	ArrayList<String>	m_arrTableSql;	// 执行创建TABLE语句
	

	public SQLDataHelper(Context context, String name, CursorFactory factory, int version, ArrayList<String> tablesql) {

		super(context, name, factory, version);

		m_sDBName = name;
		m_iVersion = version;	
		m_arrTableSql = (ArrayList<String>) tablesql.clone();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		Log.w(TAG, "onCreate");
		for( int i = 0; i < m_arrTableSql.size(); i++ ){
			Log.w(TAG, m_arrTableSql.get(i) );
			db.execSQL( m_arrTableSql.get(i) );
		}
	}
	
	// 版本不同时调用此函数。
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.w(TAG, "onupdate"+oldVersion+newVersion );
		// TODO Auto-generated method stub
	}
	
	// 查询数据。返回数据集
	public ArrayList<HashMap<String, String>> queryRecord( String table, String[] columns, String select, String[] selectargs  ){

		SQLiteDatabase  						sqliteDB = null;	
		ArrayList<HashMap<String, String>> 		data = null;
		
		try{		
			sqliteDB = getReadableDatabase();
			Cursor cursor = sqliteDB.query( table, columns, select, selectargs, null, null, null );
			cursor.moveToFirst();
			
			if( cursor.getCount() > 0 ){			
				String[] cols = cursor.getColumnNames();			
				data = new ArrayList<HashMap<String,String>>(cursor.getCount());
				for( int i = 0; i < cursor.getCount(); i++){
					HashMap<String, String> it = new HashMap<String, String>();				
					for( String col : cols){					
						it.put(col, cursor.getString( cursor.getColumnIndex(col)));
					}
					data.add(it);
					cursor.moveToNext();
				}
			}
			cursor.close();
			sqliteDB.close();	
    	} 
		catch (Exception ex) {   
    		Log.e(TAG , ex.getMessage()); 
    		if( sqliteDB != null ){
    			sqliteDB.close();	
    		}
    	}  	
		return data;	
	}
	public Boolean addRecord( String table, HashMap<String, String> data){

		Boolean			bResult = false;
		SQLiteDatabase  sqliteDB = null;		
		ContentValues 	vals = new ContentValues();	
		
		
		for(Map.Entry<String, String> set: data.entrySet()) {
			
			Log.w(TAG, set.getKey()+':'+set.getValue());
			vals.put( set.getKey(), set.getValue() );
		}
		try{		
			sqliteDB = getWritableDatabase();
			sqliteDB.insert( table, null, vals );
			bResult = true;
			sqliteDB.close();	
    	} 
		catch (Exception ex) {   
    		Log.e(TAG , ex.getMessage()); 
    		if( sqliteDB != null ){
    			sqliteDB.close();	
    		}
    	} 
		return bResult;	
	}

	public Boolean updateRecord( String table, HashMap<String, String> data, String clause, String[] args){

		Boolean			bResult = false;
		SQLiteDatabase  sqliteDB = null;		
		ContentValues 	vals = new ContentValues();	
		
		for(Map.Entry<String, String> set: data.entrySet()) {
			vals.put( set.getKey(), set.getValue() );
		}
		try{		
			sqliteDB = getWritableDatabase();
			sqliteDB.update( table, vals, clause, args );
			bResult = true;
			sqliteDB.close();	
    	} 
		catch (Exception ex) {   
    		Log.e(TAG , ex.getMessage()); 
    		if( sqliteDB != null ){
    			sqliteDB.close();	
    		}
    	}  	
		return bResult;	
	}
	public Boolean deleteRecord( String table, String clause, String[] args){

		Boolean			bResult = false;
		SQLiteDatabase  sqliteDB = null;		
		
		try{		
			sqliteDB = getWritableDatabase();
			sqliteDB.delete( table, clause, args );
			bResult = true;
			sqliteDB.close();	
    	} 
		catch (Exception ex) {   
    		Log.e(TAG , ex.getMessage()); 
    		if( sqliteDB != null ){
    			sqliteDB.close();	
    		}
    	}  	
		return bResult;	
	}
	
	public Boolean searchRecord( String table, String clause, String[] args ){

		Boolean			bResult = false;
		SQLiteDatabase  sqliteDB = null;		
		
		try{		
			sqliteDB = this.getReadableDatabase();
			Cursor cursor = sqliteDB.query( table, null, clause, args, null, null, null );
			//cursor.moveToFirst();
			if( cursor.getCount() > 0 ){
				bResult = true;				
			}
			cursor.close();
			sqliteDB.close();	
    	} 
		catch (Exception ex) {   
    		Log.e(TAG , ex.getMessage()); 
    		if( sqliteDB != null ){
    			sqliteDB.close();	
    		}
    	}  	
		return bResult;			
	}
}
