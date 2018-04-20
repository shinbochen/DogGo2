package cn.com.maptrack2;

import java.util.ArrayList;

import cn.com.maptrack2.R;

import android.R.integer;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
 
public class DeviceList extends Activity implements TextWatcher  {

	public  final static String DEVICE_ADDRESS = "bluetooth.device.address";
	
    private final static String TAG = DeviceList.class.getSimpleName();
    
	private final static int 	REQUEST_ENABLE_BT = 1;
		
	private BluetoothAdapter 			m_BluetoothAdapter;
	private Handler 					m_Handler; 
    private ListView 					m_lv; 
	private ListDeviceAdapter 			m_lstAdapter;
	private TableLayout					m_groupEdit;
	private	Spinner 					m_spin;   

	private	boolean						m_flag = false;		//数据设置是否完成
	private EditText					m_editname; 
	private EditText					m_editpsd; 
	private Spinner						m_editicon; 
	private Button						m_btnConnect; 
	private Button						m_btnDisConnect;
	
	private ArrayList<DeviceData>		m_lstDeviceData;
	
	private final BroadcastReceiver BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals( action ) || 
            	BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action) || 
            	BluetoothLeService.ACTION_RSSI_AVAILABLE.equals(action) ) {
                refreshData( );
            }
        }
	};
	
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
        	runOnUiThread(new Runnable() {
				@Override
                public void run() {
                	m_lstAdapter.addDevice( device, rssi );
                	m_lstAdapter.notifyDataSetChanged();
                }
            });
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);   
        
    	m_lstDeviceData = DogGoActivity.m_lstDeviceData;       
        m_lv = (ListView)this.findViewById( R.id.lst_unsaved_device);
        m_groupEdit = (TableLayout)this.findViewById( R.id.group_edit );
        m_spin = (Spinner)findViewById(R.id.id_icon_select); 
        
        m_editname = (EditText)findViewById(R.id.id_dogname); 
        m_editpsd = (EditText)findViewById(R.id.id_password); 
        m_editicon = (Spinner)findViewById(R.id.id_icon_select); 
        m_btnConnect = (Button)findViewById(R.id.id_btn_connect); 
        m_btnDisConnect = (Button)findViewById(R.id.id_btn_disconnect); 
        
        m_btnConnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				DeviceData 	data = (DeviceData)m_lstAdapter.getCurSelItem();
				data.setAutoConnect( true );
				data.setConnectState( BluetoothLeServiceM.STATE_CONNECTING );
				m_lstAdapter.notifyDataSetChanged();
				
				Intent		intent = new Intent( BluetoothLeServiceM.ACTION_ASK_CONNECT );
				intent.putExtra(BluetoothLeServiceM.DEVICE_ADDRESS, data.m_address);
				sendBroadcast( intent );
			}
        });
        
        m_btnDisConnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {				
				DeviceData 	data = (DeviceData)m_lstAdapter.getCurSelItem();
				data.setAutoConnect( false );
				Intent		intent = new Intent( BluetoothLeServiceM.ACTION_ASK_DISCONNECT );
				intent.putExtra(BluetoothLeServiceM.DEVICE_ADDRESS, data.m_address);
				sendBroadcast( intent );
			}
        	
        	
        });
                
        m_editname.addTextChangedListener( this );
        m_editpsd.addTextChangedListener( this );
        
       	m_lstAdapter = new ListDeviceAdapter( m_lstDeviceData );

        m_lv.setAdapter(  m_lstAdapter );
        m_spin.setAdapter( new IconAdapter( this, DeviceData.m_dogicon) );
        m_lv.setOnItemClickListener( new OnItemClickListener(){

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			
    			DeviceData data = (DeviceData)parent.getAdapter().getItem(position);
    			((ListDeviceAdapter)parent.getAdapter()).setCurSel( position );
    			displayData( data );
    		}
        	
        } );
        
        m_spin.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
        });
        m_Handler = new Handler();
        
    	m_lstAdapter.notifyDataSetChanged();
    	
        m_BluetoothAdapter = ((BluetoothManager)getSystemService("bluetooth")).getAdapter();
        if ((m_BluetoothAdapter == null) || (!m_BluetoothAdapter.isEnabled())){
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_ENABLE_BT);
        }      
        m_groupEdit.setVisibility(View.INVISIBLE);
        
	}
	
	public void scanLeDevice( ){
		if( m_BluetoothAdapter != null && m_BluetoothAdapter.isEnabled()){
			m_BluetoothAdapter.startLeScan( mLeScanCallback );		
		}
	}
	public void stopScan( ){
		if( m_BluetoothAdapter != null && m_BluetoothAdapter.isEnabled()){
			m_BluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();	
		Log.w(TAG, "onBackPressed");
        finish();  	
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.w(TAG, "onResume");
		scanLeDevice( );
        registerReceiver(BroadcastReceiver, DogGoActivity.makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.w(TAG, "onpause");
		stopScan( );
        unregisterReceiver(BroadcastReceiver);
	}

	private void refreshData( ){
		
		m_lstAdapter.notifyDataSetInvalidated();
		displayData( (DeviceData)m_lstAdapter.getCurSelItem() );
	}
	
	private void  displayData( DeviceData data ){

		m_flag = false;
        m_groupEdit.setVisibility(View.VISIBLE);
		m_editicon.setSelection( data.m_icon, true);	
		

		m_editname.setText( data.m_name );
		m_editpsd.setText( data.m_password );

		m_btnConnect.setEnabled( !data.isConnected() );
		m_btnDisConnect.setEnabled( data.isConnected() );
		
		m_editname.setEnabled( !data.isConnected() );
		m_editpsd.setEnabled( !data.isConnected() );
		
		m_flag = true;
	}
	
	private void  updateData( ){
		// 避免LOOP更新
		if( !m_flag ){
			return;
		}
		DeviceData data = (DeviceData)m_lstAdapter.getCurSelItem();
		
		data.m_name = m_editname.getText().toString();
		data.m_password = m_editpsd.getText().toString();
		data.m_icon = m_editicon.getSelectedItemPosition();
		
		m_lstAdapter.notifyDataSetChanged();
	}
	
	private class IconAdapter extends BaseAdapter{

		private ArrayList				mItems;
		private Activity				mActivity;
	      
		public IconAdapter( Activity activity, int[] items ){
			mActivity = activity;
			mItems = new ArrayList();
			for( int i = 0; i < items.length; i++ ){
				mItems.add( items[i] );	
			}			
		}
		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return (Integer)mItems.get( position );			
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LinearLayout ll = new LinearLayout( mActivity );
			ll.setOrientation(LinearLayout.HORIZONTAL);

			ImageView ii = new ImageView( mActivity );
			ii.setImageResource( (Integer) mItems.get(position) );
			ll.addView(ii);
			return ll;
		}
		
	}
	
    private class ListDeviceAdapter extends BaseAdapter
    {
      private LayoutInflater 				mInflator;
      private ArrayList<DeviceData> 		mItems;
      private int							mCurSel;

      public ListDeviceAdapter( ArrayList<DeviceData> items )
      {
    	  mItems = items;
    	  mInflator = getLayoutInflater();
    	  mCurSel = -1;    	  
      }
      
      public void setCurSel( int n ){
    	  mCurSel = n;
      }
      
      public Object getCurSelItem( ){
    	  if( mCurSel != -1 ){
    		  return mItems.get(mCurSel);
    	  }
    	  else{
    		  return null;
    	  }
      }
      
      public void addDevice( DeviceData data){
    	  mItems.add( data );    	  
      }
      
      public void addDevice(BluetoothDevice device, int rssi ){  
    	  boolean	find = false;
    	  
    	  for( DeviceData item : mItems ){
    		  
    		  if( item.m_address.compareTo( device.getAddress() )  == 0 ){
    			  find = true;
    			  item.m_rssi = rssi;
    			  break;
    		  }
    	  }    	  
    	  if( !find ){
        	  mItems.add( new DeviceData( device.getName(), device.getAddress(), rssi) );
    	  }
      }
      
      public void clear()
      {
    	  mItems.clear();
      }

      public int getCount()
      {
    	  return mItems.size();
      }

      public Object getItem(int cnt)
      {
        return mItems.get(cnt);
      }

	  public View getView(int cnt, View view, ViewGroup group)
	  {
		  String		str = new String();
		  view = mInflator.inflate(R.layout.device_list_item, null );
		  DeviceData item = mItems.get(cnt);

		  TextView name = (TextView)(view.findViewById(R.id.id_lst_name));	
		  TextView addr = (TextView)(view.findViewById(R.id.id_lst_addr));		
		  TextView rssn = (TextView)(view.findViewById(R.id.id_lst_rssin));		
		  
		  ImageView icon = (ImageView)(view.findViewById(R.id.id_lst_icon));	
		  ImageView rssi = (ImageView)(view.findViewById(R.id.id_lst_rssi));
		  ImageView state = (ImageView)(view.findViewById(R.id.id_lst_connected));		
		  
		  rssn.setText( Integer.toString(item.m_rssi ) );
		  
		  
		  name.setText( item.m_name );
		  addr.setText( item.m_address );	
		  
		  icon.setImageResource( item.getDogIcon() );
		  rssi.setImageResource( item.getSigIcon() );
		  state.setImageResource(item.getConnectIcon() );
		  
		  return view;
	  }

	  @Override
	  public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	  }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch( requestCode ){
		case REQUEST_ENABLE_BT:
			scanLeDevice( );
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	@Override
	public void afterTextChanged(Editable s) {
		updateData( );
	}



}
