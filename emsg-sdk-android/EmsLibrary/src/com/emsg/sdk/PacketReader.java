package com.emsg.sdk;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.emsg.sdk.beans.DefPayload;
import com.emsg.sdk.beans.IPacket;
import com.emsg.sdk.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketReader<T> implements Define{
	
    private Thread readerThread;
    private final BlockingQueue<String> queue;
    private EmsgClient client = null;
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
    
    protected PacketReader(EmsgClient client) {
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
						String id = JsonUtil.getAsString(envelope, "id");
						if(envelope.has("ack") && JsonUtil.getAsInt(envelope, "ack", 0)==1){
							sendAck(id);
						}
						// 认证信息
						if(envelope.has("type")&&JsonUtil.getAsInt(envelope, "type", 0)==0){
							JsonObject entity = jp.get("entity").getAsJsonObject();
							String result = JsonUtil.getAsString(entity, "result");
							if("ok".equals(result)){
								client.setAuth(true);
								sendAck(id);
							}
						}
						// 确认信息暂不处理
//						if(envelope.has("from")&&JsonUtil.getAsString(envelope, "from").equals("server_ack")){
//							continue;
//						}
												
						if(client.listener!=null){
							System.out.println("reader :::> "+packet);
							IPacket<DefPayload> p = client.getProvider().decode(packet);
							//�?��消息都会扔到这个回调方法
							int type = p.getEnvelope().getType();
							if(MSG_TYPE_P2P_SOUND==type || MSG_TYPE_P2P_VIDEO==type){
								//媒体类型消息，包括语音和视频的拨号调度响应与事件
								client.listener.mediaPacket(p);
							}if(MSG_TYPE_OPEN_SESSION==type){
							    
								//打开session的包中包含了离线消息，在 delay 字段中，将其分析出来并拆分到两个回调方法�?
								List<IPacket<DefPayload>> packets = null;
								if(p.getDelay()!=null){
									if(p.getDelay().getTotal()>0){
										packets = p.getDelay().getPackets();
										p.setDelay(null);
									}
								}
								//�?delay �?session 的响应中去除
								client.listener.sessionPacket(p);
								if(packets!=null)
									//将离线消息打包并回调
									client.listener.offlinePacket(packets);
							}else if(MSG_TYPE_PUBSUB==type){
								//订阅信息
								client.listener.pubsubPacket(p.getPubsub());
							}else{
								//文本类型的消息，包括文字、录音�?图片、附�?
								//序列化以后的�?
								client.listener.processPacket(p);
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

    private void sendAck(String id) throws InterruptedException{
		JsonObject ack = new JsonObject();
		JsonObject ack_envelope = new JsonObject();
		ack_envelope.addProperty("id", id);
		ack_envelope.addProperty("from", client.getJid());
		ack_envelope.addProperty("to", "server_ack");
		ack_envelope.addProperty("type", MSG_TYPE_STATE);
		ack.add("envelope", ack_envelope);
		client.send(ack.toString());
    }
}
