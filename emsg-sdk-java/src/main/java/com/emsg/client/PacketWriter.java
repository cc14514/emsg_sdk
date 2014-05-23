package com.emsg.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class PacketWriter {

    private Thread writerThread;
    private final BlockingQueue<String> queue;
    volatile boolean done;
    
    public void write(String packet) throws InterruptedException{
    	queue.put(packet);
    }

    public String take() throws InterruptedException{
    	return queue.take();
    }

    protected PacketWriter() {
        this.queue = new ArrayBlockingQueue<String>(500, true);
        init();
    }

    protected void init() {
        
    }

}
