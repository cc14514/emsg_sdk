package com.emsg.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.sf.json.JSONObject;

public class EmsgClient implements Define {
	
	private BlockingQueue<String> heart_beat_ack = new ArrayBlockingQueue<String>(2, true);
	
	static MyLogger logger = new MyLogger(EmsgClient.class);
	
//	private String auth_service = "http://192.168.1.12:8080/emsg_auth_service/auth.html";
	private String emsg_host = "127.0.0.1";
	private int emsg_port = 4222;

	private String jid = null;
	private String pwd = null;
	private String inner_token = null;
	
	private int heartBeat = HEART_BEAT_FREQ;
	
	private Socket socket = null;
	
	protected InputStream reader = null;
    protected OutputStream writer = null;
	
    public PacketReader packetReader = null;
    public PacketWriter packetWriter = null;
    
    protected PacketListener listener = null;
    
    private boolean auth = false;
    private boolean isClose = false;
    private String reconnectSN = null;
    
//    public EmsgClient(String auth_service){
//    	this.auth_service = auth_service;
//    }  
    public EmsgClient(String host,int port){
    	this.emsg_host = host;
    	this.emsg_port = port;
    }
    
    public void auth(String jid,String pwd) throws Exception {
    	this.jid = jid;
    	this.pwd = pwd;
		Map<String,String> params = new HashMap<String,String>();
		params.put("jid", jid);
		params.put("pwd", pwd);
		this.auth = true;
		initConnection();
		/*String rtn = HttpUtils.http(auth_service, params);
		logger.info(rtn);
		JSONObject success = JSONObject.fromObject(rtn);
		if(success.getBoolean("success")){
			JSONObject entity = JSONObject.fromObject(success.getString("entity"));
			String host = entity.getString("host");
			this.emsg_host = host.split(":")[0];
			this.emsg_port = Integer.parseInt(host.split(":")[1]);
			this.inner_token = entity.getString("inner_token");
			this.auth = true;
			initConnection();
		}else{
			new Exception("fail auth ::>"+success.getString("entity"));
		}*/
    }
    
    public void setPacketListener(PacketListener listener){
    	this.listener = listener;
    }
   
    private void initConnection() throws UnknownHostException, IOException, InterruptedException{
    	logger.debug(this.emsg_host+" ; "+this.emsg_port);
    	this.socket = new Socket(this.emsg_host,this.emsg_port);
    	reconnectSN = null;
    	isClose = false;
    	initReaderAndWriter();
    	openSession();
    }
    
    private void openSession() throws InterruptedException{
    	//{"envelope":{"id":"1234567890","type":0,"inner_token":"abc123"}}
		JSONObject j = new JSONObject();
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", MSG_TYPE_OPEN_SESSION);
//		envelope.put("inner_token", this.inner_token);
		envelope.put("jid", this.jid);
		envelope.put("pwd", this.pwd);
		
		j.put("envelope", envelope);
		String open_session_packet = j.toString();
		logger.info("open_session ::> "+open_session_packet);
		packetWriter.write(open_session_packet);
	}
   
    private void reconnection(String reconnectSN) {
    	if(this.reconnectSN==null){
    		this.reconnectSN = reconnectSN;
    		loop(reconnectSN);
    	}else{
    		logger.info("======== reconnection_skip ========"+reconnectSN);
    	}
    }
    private void loop(String reconnectSN){
    	logger.info("======== reconnection_loop ========"+reconnectSN);
    	heart_beat_ack.clear();
    	try {
			initConnection();
		} catch (Exception e) {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
//				e1.printStackTrace();
			}
//			e.printStackTrace();
			loop(reconnectSN);
		}
    }
    
	private void initReaderAndWriter() throws UnsupportedEncodingException, IOException{
		reader = socket.getInputStream();
        writer = socket.getOutputStream();
        //TODO if first new reader
        packetReader = new PacketReader(this);
        packetWriter = new PacketWriter();
        new IOListener();
	}
	
    public boolean isAuth() {
		return auth;
	}
    
	class IOListener {
		Thread heartbeatThread = null;
		Thread readThread = null;
		Thread writeThread = null;
		IOListener(){
			listenerRead();	
			listenerWriter();
			heartbeat();
		}
		void heartbeat(){
			heartbeatThread = new Thread(){
				public void run(){
					try{
						while(true){
							Thread.sleep(getHeartBeat());
							if(isAuth()){
								heart_beat_ack.add("1");
								send(HEART_BEAT);
								logger.info("["+heart_beat_ack.size()+"]heartbeat ~~~ ");
							}
							if(isClose){
								logger.info("["+heart_beat_ack.size()+"] is_closed ~~~ ");
								return ;
							}
						}
					}catch(Exception e){
						e.printStackTrace();
						shutdown();
						reconnection(reconnectSN);
					}
				}
			};
			heartbeatThread.setName("heart_beat"+new Date());
			heartbeatThread.setDaemon(true);
			heartbeatThread.start();
		}
		void listenerRead(){
			readThread = new Thread(){
				public void run(){
					try{
						byte[] buff = new byte[1024];
						int len = 0;
						StringBuffer sb = new StringBuffer();
						while((len=reader.read(buff))!=-1){//当远程流断开时，会返回 0
							String packet = new String(buff,0,len);
							String[] arr = packet.split(END_TAG);
							for(int i=0 ; i<arr.length;i++){
								if(i==arr.length-1 && !packet.endsWith(END_TAG)){
									//这个包没接全
									sb.append(arr[i]);
								}else{
									sb.append(arr[i]);
									String msg = sb.toString();
									//TODO dispach heart beat and message
									if(HEART_BEAT.equals(msg)){
										//心跳单独处理
										heart_beat_ack.poll();
									}else{
										packetReader.recv(msg);
									}
									sb.setLength(0);
								}
							}
						}
						throw new Exception("emsg_retome_socket_closed");
					}catch(Exception e){
						e.printStackTrace();
						shutdown();
						reconnection("listenerRead");
					}
				}
			};
			readThread.setName("read"+new Date());
			readThread.setDaemon(true);
			readThread.start();
		}
		void listenerWriter(){
			writeThread = new Thread(){
				public void run(){
					try{
						while(true){
							String msg = packetWriter.take();
							if(!msg.endsWith(END_TAG)){
								msg = msg+END_TAG;
							}
							logger.debug("IOListener_writer socket_is_close="+isClose+" send_message ==> "+msg);
							writer.write(msg.getBytes());
							writer.flush();
						}
					}catch(Exception e){
						e.printStackTrace();
						shutdown();
						reconnection("listenerWriter");
					}
				}
			};
			writeThread.setName("writer"+new Date());
			writeThread.setDaemon(true);
			writeThread.start();
		}
	}
	
	public void send(String message) throws InterruptedException{
		packetWriter.write(message);
	}
	
	public void shutdown(){
		try {
			isClose = true;
			if(!socket.isClosed()){
				socket.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public int getHeartBeat() {
		return heartBeat;
	}

	public void setHeartBeat(int heartBeat) throws Exception {
		if(100*1000<heartBeat){
			throw new Exception("scope from 1000 to 100000 ");
		}
		this.heartBeat = heartBeat;
	}
    
}
