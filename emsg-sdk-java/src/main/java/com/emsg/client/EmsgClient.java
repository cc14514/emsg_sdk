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

import net.sf.json.JSONObject;

public class EmsgClient implements Define {
	
	private static EmsgClient client = null;
	
	static MyLogger logger = new MyLogger(EmsgClient.class);
	
	private String auth_service = "http://192.168.1.12:8080/emsg_auth_service/auth.html";
	private String jid = null;
	private String pwd = null;
	private String inner_token = null;
	
	private String emsg_host = "127.0.0.1";
	private int emsg_port = 4222;
	
	private Socket socket = null;
	
	protected InputStream reader = null;
    protected OutputStream writer = null;
	
    public PacketReader packetReader = null;
    public PacketWriter packetWriter = null;
    
    protected PacketListener listener = null;
    
    private boolean auth = false;
    
	private EmsgClient(String auth_service){
		this.auth_service = auth_service;
	}
    public static EmsgClient newInstance(String auth_service){
    	if(client==null){
    		client = new EmsgClient(auth_service);
    	}
    	return client;
    }
    public void auth(String jid,String pwd) throws Exception {
    	this.jid = jid;
    	this.pwd = pwd;
		Map<String,String> params = new HashMap<String,String>();
		params.put("jid", jid);
		params.put("pwd", pwd);
		String rtn = HttpUtils.http(auth_service, params);
		logger.info(rtn);
		JSONObject success = JSONObject.fromObject(rtn);
		if(success.getBoolean("success")){
			JSONObject entity = JSONObject.fromObject(success.getString("entity"));
			String host = entity.getString("host");
			//TODO split ip and port
			this.inner_token = entity.getString("inner_token");
			this.auth = true;
			initConnection();
		}else{
			new Exception("fail auth ::>"+success.getString("entity"));
		}
    }
    
    public void setPacketListener(PacketListener listener){
    	this.listener = listener;
    }
   
    private void initConnection() throws UnknownHostException, IOException, InterruptedException{
    	this.socket = new Socket(this.emsg_host,this.emsg_port);
    	initReaderAndWriter();
    	openSession();
    }
    
    private void openSession() throws InterruptedException{
		JSONObject j = new JSONObject();
		j.put("id", UUID.randomUUID().toString());
		j.put("type", 0);
		j.put("inner_token", this.inner_token);
		String open_session_packet = j.toString();
		logger.info("open_session ::> "+open_session_packet);
		packetWriter.write(open_session_packet);
	}
    
    private synchronized void reconnection(){
    	logger.info("======== reconnection ========");
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
		Thread readThread = null;
		Thread writeThread = null;
		IOListener(){
			listenerRead();	
			listenerWriter();
		}
		void listenerRead(){
			readThread = new Thread(){
				public void run(){
					try{
						byte[] buff = new byte[1024];
						int len = 0;
						StringBuffer sb = new StringBuffer();
						while((len=reader.read(buff))!=-1){
							String packet = new String(buff,0,len);
							String[] arr = packet.split(END_TAG);
							for(int i=0 ; i<arr.length;i++){
								if(i==arr.length-1 && !packet.endsWith(END_TAG)){
									//这个包没接全
									sb.append(arr[i]);
								}else{
									sb.append(arr[i]);
									String msg = sb.toString();
									packetReader.recv(msg);
									sb.setLength(0);
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
						shutdown();
						reconnection();
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
							writer.write(msg.getBytes());
							writer.flush();
						}
					}catch(Exception e){
						e.printStackTrace();
						shutdown();
						reconnection();
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
			if(!socket.isClosed()){
				socket.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
    
    public static void main(String[] args) throws Exception {
    	String auth_service = "http://192.168.1.12:8080/emsg_auth_service/auth.html";
    	EmsgClient client = EmsgClient.newInstance(auth_service);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("recv ===> "+packet);
			}
		});
    	client.auth("liangc@test.com","123123");
    	
    	Thread.sleep(1000*60*3);
    	JSONObject packet = new JSONObject();
		packet.put("id", UUID.randomUUID().toString());
		packet.put("type", 1);
		packet.put("from", "cc@test.com");
		packet.put("to", "liangc@test.com");
		packet.put("body", "hello world");
		packet.put("ack", 1);
		
    	client.send(packet.toString());
    }
    
}
