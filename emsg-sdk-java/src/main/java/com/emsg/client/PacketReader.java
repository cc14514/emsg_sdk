package com.emsg.client;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class PacketReader {
	
    private Thread readerThread;
    private ExecutorService listenerExecutor;
    private final BlockingQueue<String> queue;
    private EmsgClient client = null;
    
    volatile boolean done;

    private String connectionID = null;
    
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
						String packet = queue.take();
						if(client.listener!=null){
							//TODO dispach heart beat and message
							client.listener.processPacket(packet);
						}
	    			}
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				client.shutdown();
    			}
    		}
    	};
    	readerThread.setName("readerThread_"+new Date());
    	readerThread.setDaemon(true);
    	readerThread.start();
    }
    
}
