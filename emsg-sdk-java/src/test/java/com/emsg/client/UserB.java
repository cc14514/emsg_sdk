package com.emsg.client;

import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;



public class UserB implements Define{
	
	public static void main(String[] args) throws Exception {
		EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("222.128.11.38",4222);
		client.setProvider(new DefProvider());
		client.setHeartBeat(50000);
    	client.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("0000___packet__recv ===> "+packet);
			}
		});
    	client.auth("0000@test.com","123123");
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
