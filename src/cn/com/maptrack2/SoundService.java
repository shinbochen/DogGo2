package cn.com.maptrack2;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class SoundService extends Service implements OnCompletionListener {

    private final static String 	TAG = SoundService.class.getSimpleName();
	private static MediaPlayer 		m_mp = null;
	
	@Override
    public void onCreate() {
        super.onCreate();
        m_mp = new MediaPlayer();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
        if( m_mp != null){
        	m_mp.release();
        }
        stopSelf();
    }
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	Log.w(TAG, "music on star");
        boolean playing = intent.getBooleanExtra("playing", false);     
        if (playing) {  
        	Log.w(TAG, "music on play");
			String  path = intent.getStringExtra( "path" );
        	Uri mediaUri = Uri.parse( path );
			playMusic( mediaUri );
        } else {

        	Log.w(TAG, "music  on stop");
        	if( m_mp != null ){
            	if( m_mp.isPlaying() ){
	        		m_mp.pause();
            	}
	        	m_mp.release();
        		m_mp = null;
        	}
        }
        return super.onStartCommand(intent, flags, startId);
    }
	
    private void playMusic(Uri mediaUri)   
    {   
        if( m_mp != null ){
			//m_mp.reset();    // 重置MediaPlayer
			m_mp.release();
			m_mp = null;
		}
		Log.w(TAG, "play music being....");
        try{
			m_mp = MediaPlayer.create(this, mediaUri);
			m_mp.setOnCompletionListener(this );
			m_mp.setLooping(false);
			m_mp.start();   
        }catch( Exception e ){
        	Log.w( TAG, e.getMessage() );
        }
    }   
    
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.w(TAG, "play music complete");
		//m_mp.reset();
		m_mp.release();
		m_mp = null;
	}

}
