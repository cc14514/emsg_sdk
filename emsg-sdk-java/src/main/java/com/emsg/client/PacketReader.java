package com.emsg.client;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.emsg.client.beans.IPacket;
import com.emsg.client.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketReader<T> implements Define{
	
    private Thread readerThread;
    private final BlockingQueue<String> queue;
    private EmsgClient<T> client = null;
    volatile boolean done;
    
    public void kill() {
    	try {
			queue.put(KILL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void recv(String msg) throws InterruptedException{
    	queue.put(msg);
    }
    
    protected PacketReader(EmsgClient<T> client) {
    	this.client = client;
    	this.queue = new ArrayBlockingQueue<String>(500, true);
        this.init();
    }
    
    public void init(){
    	readerThread = new Thread(){
    		public void run(){
    			try {
	    			while(true){
	    				//ack answer
						/*
						{
						  "envelope":{
						    "id":"xxxx",
						    "type":3,
						    "from":"userb@test.com",
						    "to":"server_ack"
						  }
						}
						*/
						String packet = queue.take();
						if(KILL.equals(packet)){
							return ;
						}
						JsonObject jp = new JsonParser().parse(packet).getAsJsonObject();
						JsonObject envelope = jp.get("envelope").getAsJsonObject();
						if(envelope.has("ack") && JsonUtil.getAsInt(envelope, "ack", 0)==1){
							JsonObject ack = new JsonObject();
							JsonObject ack_envelope = new JsonObject();
							String id = JsonUtil.getAsString(envelope, "id");
							ack_envelope.addProperty("id", id);
							ack_envelope.addProperty("from", client.getJid());
							ack_envelope.addProperty("to", "server_ack");
							ack_envelope.addProperty("type", MSG_TYPE_STATE);
							ack.add("envelope", ack_envelope);
							client.send(ack.toString());
						}

						// 认证信息暂不处理
//						if(envelope.has("type")&&envelope.getInt("type")==0){
//							continue;
//						}
						
						// 确认信息暂不处理
						if(envelope.has("from")&&JsonUtil.getAsString(envelope, "from").equals("server_ack")){
							continue;
						}
												
						if(client.listener!=null){
							System.out.println("reader :::> "+packet);
							IPacket<T> p = client.getProvider().decode(packet);
							//所有消息都会扔到这个回调方法
							client.listener.processPacket(p);
							int type = p.getEnvelope().getType();
							if(MSG_TYPE_P2P_SOUND==type || MSG_TYPE_P2P_VIDEO==type){
								//媒体类型消息，包括语音和视频的拨号调度响应与事件
								client.listener.mediaPacket(p);
							}else{
								//文本类型的消息，包括文字、录音、图片、附件
								client.listener.textPacket(p);
							}
						}
	    			}
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				client.shutdown();
    			}
    		}
    	};
    	readerThread.setName("PacketReader__"+new Date());
    	readerThread.setDaemon(true);
    	readerThread.start();
    }
}
