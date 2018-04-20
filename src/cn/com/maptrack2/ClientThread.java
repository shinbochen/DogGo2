package cn.com.maptrack2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class ClientThread implements Runnable {
	

    private final static String TAG = ClientThread.class.getSimpleName();
	public static final int 	RECV_SOCKET_DATA = 0x200;
	public static final int 	SEND_SOCKET_DATA = 0x201;
	public static final int 	SOCKET_DISCONNECT = 0x202;
	public static final int 	SOCKET_CONNECT = 0x203;
	
	
	private Socket 	s;
	private String  m_addr;
	private String 	m_strIP;
	private int		m_port;
    // 定义向UI线程发送消息的Handler对象  
    Handler handler;  
    // 定义接收UI线程的Handler对象  
    Handler revHandler;  
    // 该线程处理Socket所对用的输入输出流  
    //BufferedReader br = null;  
    InputStream is = null;
    OutputStream os = null;  
    private boolean	m_bStop;
    private boolean m_bRun;
    private boolean m_bConnected;
    
    
  
    public ClientThread(Handler handler, String strIP, int port, String addr) {  
        this.handler = handler;  
        m_strIP = strIP;
        m_port = port;
        m_addr = addr;
        m_bStop = false;
        m_bRun = false;
        m_bConnected = false;
    }
    public boolean isRun( ){
    	return m_bRun;    	
    }
    public boolean isConnected( ){
    	return m_bConnected;
    }
    public void stop( ){  
    	m_bStop = true;
    	return;
    }
    
    public void sendData( String data ){
		
		if( isRun() == false  || isConnected() == false ){
			Log.e(TAG, "socket write error");
			return;
		}
        try {  
           // os.write((data + "\r\n").getBytes("gbk"));  
            os.write((data + "\r\n").getBytes( ));  
        } catch (Exception e) {  
        	e.printStackTrace();
			m_bStop = true;  
        }  
        return;
    }
    
	@Override
	public void run() {
		Log.w(TAG, "clientThread run start!");
		m_bRun = true;
		if( s!= null){			
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        while( !m_bStop ){// 1秒重连

    		Log.w(TAG, "clientThread wait 1s");
        	try {
				Thread.sleep(100);    
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	try {  
        		s = new Socket();  
        		s.connect(new InetSocketAddress( m_strIP, m_port ), 3000);  
	            if( s.isConnected() ){

                	try {
						Thread.sleep(50);
					} catch (InterruptedException e) {  
						e.printStackTrace();
					}  	            	
	            	handler.obtainMessage(SOCKET_CONNECT, m_addr ).sendToTarget();
	            	
	            	m_bConnected = true;
	            	is = s.getInputStream();
	            	os = s.getOutputStream();  	
	            	
	            	String content = null;  
                    while( !m_bStop ) { 
                    	int bytes;
                    	try {
							if( (bytes = is.available()) > 0 ){
								byte[] buf = new byte[bytes];
								bytes = is.read(buf);
		                		handler.obtainMessage(RECV_SOCKET_DATA, m_addr + ':' + new String(buf)).sendToTarget();
							}
						} catch (IOException e) {
							Log.e(TAG, "read error:" + e.getMessage() );
							m_bStop = true;   
						}
                    	try {
							Thread.sleep(100);
						} catch (InterruptedException e) {  
							e.printStackTrace();
						}  
                    }
	            	
	            	
	            	/*
		            // 启动一条子线程来读取服务器相应的数据  
		            new Thread() { 
		                @Override  
		                public void run() {  
		                    String content = null;  
		                    while( !m_bStop ) {  

		                		//Log.w(TAG, "clientThread 100ms");
		                    	try {
									if( br.ready() ){
								        while ((content = br.readLine()) != null) {  
								            // 每当读取到来自服务器的数据之后，发送的消息通知程序  
								            // 界面显示该数据  
								            Message msg = new Message();  
								            msg.what = RECV_SOCKET_DATA;  
								            msg.obj = content;  
								            handler.sendMessage(msg);  
								        }  
									}
								} catch (IOException e) {
									m_bStop = true;
									Message msg = new Message();									
						            msg.what = SOCKET_DISCONNECT;  
						            msg.obj = e.getMessage();  
						            handler.sendMessage(msg);  
									
									e.printStackTrace();
								}
		                    	try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}  
		                    }
		                }  
		            }.start();  
		            // 为当前线程初始化Looper   这里如果用LOOPER,外面的主线程会停止timer
		            Looper.prepare();  
		            // 创建revHandler对象  
		            revHandler = new Handler() {  	  
		                @Override  
		                public void handleMessage(Message msg) {  
		                    // 接收到UI线程的中用户输入的数据  
		                    if (msg.what == SEND_SOCKET_DATA) {  
		                        // 将用户在文本框输入的内容写入网络  
		                        try {  
		                            os.write((msg.obj.toString() + "\r\n").getBytes("gbk"));  
		                        } catch (Exception e) {  
		                            e.printStackTrace();  

									m_bStop = true;
									Message msg1 = new Message();									
						            msg1.what = SOCKET_DISCONNECT;  
						            msg1.obj = e.getMessage();  
						            handler.sendMessage(msg1);  
		                        }  
		                    }  
		                }  
		            };   
		            // 启动Looper  
		            Looper.loop();		
		            */
	            }
	            else{
	            	Log.e("DogGo_ClientThread", "connect failed!" );	            	
	            }
        	}
	        catch (SocketTimeoutException e) {  
	        	Log.e("DogGo_ClientThread", "error timeout is" + e.getMessage() );
	        	e.printStackTrace();
            } catch (IOException e1) {
	        	Log.e("DogGo_ClientThread", "error io is" + e1.getMessage() ); 
	        	e1.printStackTrace();
			} 
        }  

		Log.w(TAG, "clientThread end!");
        if( s!= null && s.isConnected() ){
        	try {
        		Log.w(TAG, "socket close");
				s.close();
				s = null;  
        		Log.w(TAG, "socket close end");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        handler.obtainMessage(SOCKET_DISCONNECT, m_addr).sendToTarget(); 
        m_bConnected = false;
		m_bRun = false;
    } 
}
