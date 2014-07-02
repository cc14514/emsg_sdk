package com.emsg.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class PacketWriter implements Define{

    private Thread writerThread;
    private final BlockingQueue<String> queue;
    private final BlockingQueue<String> heart_beat_ack;
    volatile boolean done;
    
    public void kill() {
    	try {
    		queue.put(KILL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void write(String packet) throws InterruptedException{
    	queue.put(packet);
    }

    public String take() throws InterruptedException{
    	return queue.take();
    }

    protected PacketWriter() {
    	this.heart_beat_ack = new ArrayBlockingQueue<String>(5, true);
    	heart_beat_ack.poll();
        this.queue = new ArrayBlockingQueue<String>(500, true);
        init();
    }

    protected void init() {
        
    }

}
