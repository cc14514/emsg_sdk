package com.emsg.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.sf.json.JSONObject;

import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;
import com.emsg.client.beans.IProvider;

public class EmsgClient<T> implements Define {
	
	private BlockingQueue<String> heart_beat_ack = null;
	
	static MyLogger logger = new MyLogger(EmsgClient.class);
	
	private String emsg_host = "127.0.0.1";
	private int emsg_port = 4222;

	private String jid = null;
	private String pwd = null;
	private String heart = null;
	private int heartBeat = 5000;
	
	private Socket socket = null;
	
	protected InputStream reader = null;
    protected OutputStream writer = null;
	
    public PacketReader<T> packetReader = null;
    public PacketWriter packetWriter = null;
    
    protected PacketListener<T> listener = null;
    private IProvider<T> provider = null;
    
	private boolean auth = false;
    private boolean isClose = false;
    private String reconnectSN = null;
    
//    public EmsgClient(String auth_service){
//    	this.auth_service = auth_service;
//    }  
    public EmsgClient(String host,int port){
    	this.emsg_host = host;
    	this.emsg_port = port;
    	// 默认的 包解析器
    	System.setProperty("emsg.packet.provider", DefProvider.class.getName());
    }
    
    public void auth(String jid,String pwd) throws Exception {
    	this.jid = jid;
    	this.pwd = pwd;
		this.auth = true;
		initConnection();
		//XXX 启动重连线程
		Thread reconnect = new Thread(new Runnable(){
			@Override
			public void run() {
				loop();
			}
		});
		reconnect.setName("reconnect__main"+new Date());
		reconnect.setDaemon(true);
    	reconnect.start();
    }
    
    public void setPacketListener(PacketListener<T> listener){
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
   
    public void close(){
		loop_queue.add(KILL);
		shutdown();
    }
    private void reconnection(String reconnectSN) {
    	if(this.reconnectSN==null){
    		this.reconnectSN = reconnectSN;
    		try {
    			logger.debug("reconnect_do_at_"+reconnectSN);
				loop_queue.put("do");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}else{
    		logger.info("======== reconnection_skip ========"+reconnectSN);
    	}
    }
    
    private final BlockingQueue<String> loop_queue = new ArrayBlockingQueue<String>(2, true);;
    
    private static int reconnect_counter = 0;
    
    private void loop(){
    	String cmd = null;
    	try {
    		// TODO 当重连被触发时，应当检测网络是否通畅
    		cmd = loop_queue.take();
    		if("do".equals(cmd)){
    			++reconnect_counter;
    			logger.info("======== reconnection_loop_start ======== "+cmd);
    			initConnection();
    		}
    		reconnectSN=null;
    		reconnect_counter=0;
		} catch (Exception e) {
			try {
				logger.info("loop---->"+e.getMessage());
				// TODO 这里的等待时间应该是会变化的，比如第一分钟，每5秒检查一次，
				// 超过1分钟，就阻塞到网络是否链接的事件上
				// 而且应该是根据一些事件来阻塞和解除阻塞的
				int sleep = 0;
				if(reconnect_counter<=10){
					sleep = 3000;
				}else if(reconnect_counter>10 && reconnect_counter<=20){
					sleep = 1000*10;
				}else{
					sleep = 1000*30;
				}
				Thread.sleep(sleep);
				loop_queue.put("do");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}finally{
			if(Define.KILL.equals(cmd)){
				logger.info("reconnect_thread_shutdown");
			}else{
    			loop();
    		}
		}
    }
    
	private void initReaderAndWriter() throws UnsupportedEncodingException, IOException{
		reader = socket.getInputStream();
        writer = socket.getOutputStream();
        //TODO if first new reader
        packetReader = new PacketReader<T>(this);
        packetWriter = new PacketWriter();
        heart_beat_ack = new ArrayBlockingQueue<String>(2, true);
        this.heart = UUID.randomUUID().toString();
        new IOListener(heart);
	}
	
    public boolean isAuth() {
		return auth;
	}
    
	class IOListener extends PacketDecoder {
		Thread heartbeatThread = null;
		Thread readThread = null;
		Thread writeThread = null;
		String _heart = null;
		IOListener(String _heart){
			listenerRead();	
			listenerWriter();
			heartbeat();
			this._heart = _heart;
		}
		void heartbeat(){
			heartbeatThread = new Thread(){
				public void run(){
					try{
						while(true){
							Thread.sleep(getHeartBeat());
							if(!heart.equals(_heart)){
								return ;
							}
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
						shutdown();
						reconnection("heart_beat");
					}
				}
			};
			heartbeatThread.setName("IOListener__heart_beat__"+new Date());
			heartbeatThread.setDaemon(true);
			heartbeatThread.start();
		}
		void listenerRead(){
			readThread = new Thread(){
				public void run(){
					try{
						byte[] buff = new byte[1024];
						int len = 0;
						List<Byte> part = new ArrayList<Byte>();
						while((len=reader.read(buff))!=0&&len!=-1){//当远程流断开时，会返回 0
							List<Byte> list = parseBinaryList(buff,len);
							List<String> packetList = new ArrayList<String>();
							List<Byte> new_part = new ArrayList<Byte>();
							splitByteArray(list,END_TAG,packetList,new_part,part);
							for(int i=0;i<packetList.size();i++){
								String packet = packetList.get(i);
								System.out.println("--> packet = "+packet);
								// dispach heart beat and message
								if(HEART_BEAT.equals(packet)){
									//心跳单独处理
									heart_beat_ack.poll();
								}else if(SERVER_KILL.equals(packet)){
									logger.debug("server_kill="+packet);
									close();
								}else{
									packetReader.recv(packet);
								}
								//将新的片段赋给中间变量
								part.clear();
								if(new_part!=null&&new_part.size()>0){
									for(byte pb : new_part){
										part.add(pb);
									}
								}
							}
						}
						throw new Exception("emsg_retome_socket_closed");
					}catch(Exception e){
						shutdown();
						reconnection("listenerRead");
					}
				}
			};
			readThread.setName("IOListener__read__"+new Date());
			readThread.setDaemon(true);
			readThread.start();
		}

		void listenerWriter(){
			writeThread = new Thread(){
				public void run(){
					try{
						while(true){
							String msg = packetWriter.take();
							if(KILL.equals(msg)){
								return ;
							}
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
			writeThread.setName("IOListener__writer__"+new Date());
			writeThread.setDaemon(true);
			writeThread.start();
		}
	}
	
	public void send(IPacket<T> packet) throws InterruptedException{
		if(packet.getEnvelope().getFrom()==null){
			packet.getEnvelope().setFrom(this.jid);
		}
		String encode_message = getProvider().encode(packet);
		packetWriter.write(encode_message);
	}
	
	public void send(String message) throws InterruptedException{
		packetWriter.write(message);
	}
	
	public void shutdown(){
		try {
			isClose = true;
			if (packetReader != null) {
				packetReader.kill();
				packetReader = null;
			}
			if (packetWriter != null) {
				packetWriter.kill();
				packetWriter = null;
			}
			heart_beat_ack = null;
			if(!socket.isClosed()){
				socket.close();
			}
			logger.info("shutdown...");
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

	public String getJid() {
		return jid;
	}
    
    public IProvider<T> getProvider() {
    	return provider;
	}

	public void setProvider(IProvider<T> provider) {
		this.provider = provider;
	}
	
	/**
	 * true 服务器已断开
	 * false 服务器已链接
	 * @return
	 */
	public boolean isClose(){
		return isClose;
	}
	
}
