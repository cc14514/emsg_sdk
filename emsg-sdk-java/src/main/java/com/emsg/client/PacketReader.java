package com.emsg.client;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.sf.json.JSONObject;

import com.emsg.client.beans.IPacket;

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
						JSONObject jp = JSONObject.fromObject(packet);
						JSONObject envelope = jp.getJSONObject("envelope");
						if(envelope.has("ack")&&envelope.getInt("ack")==1){
							JSONObject ack = new JSONObject();
							JSONObject ack_envelope = new JSONObject();
							String id = envelope.getString("id");
							ack_envelope.put("id", id);
							ack_envelope.put("from", client.getJid());
							ack_envelope.put("to", "server_ack");
							ack_envelope.put("type", MSG_TYPE_STATE);
							ack.put("envelope", ack_envelope);
							client.send(ack.toString());
						}

						// 认证信息暂不处理
//						if(envelope.has("type")&&envelope.getInt("type")==0){
//							continue;
//						}
						
						// 确认信息暂不处理
						if(envelope.has("from")&&envelope.getString("from").equals("server_ack")){
							continue;
						}
												
						if(client.listener!=null){
							System.out.println("reader :::> "+packet);
							IPacket<T> p = client.getProvider().decode(packet);
							client.listener.processPacket(p);
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
