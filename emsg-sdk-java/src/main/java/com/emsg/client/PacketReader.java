package com.emsg.client;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import net.sf.json.JSONObject;

public class PacketReader implements Define{
	
    private Thread readerThread;
    private ExecutorService listenerExecutor;
    private final BlockingQueue<String> queue;
    private EmsgClient client = null;
    volatile boolean done;

    private String connectionID = null;
    
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
						JSONObject jp = JSONObject.fromObject(packet);
						JSONObject envelope = jp.getJSONObject("envelope");
						if(envelope.has("ack")&&envelope.getInt("ack")==1){
							JSONObject ack = new JSONObject();
							JSONObject ack_envelope = new JSONObject();
							String id = envelope.getString("id");
							String to = envelope.getString("to");
							ack_envelope.put("id", id);
							ack_envelope.put("from", client.getJid());
							ack_envelope.put("to", "server_ack");
							ack_envelope.put("type", MSG_TYPE_STATE);
							ack.put("envelope", ack_envelope);
							client.send(ack.toString());
						}
						if(client.listener!=null){
							client.listener.processPacket(packet);
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
