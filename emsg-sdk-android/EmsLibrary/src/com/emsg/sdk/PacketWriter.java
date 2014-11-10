package com.emsg.sdk;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PacketWriter implements Define{

    private final BlockingQueue<String> queue;
    
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
        this.queue = new ArrayBlockingQueue<String>(500, true);
    }

}
