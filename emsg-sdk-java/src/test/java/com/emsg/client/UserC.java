package com.emsg.client;

import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.IPacket;



public class UserC implements Define{
	
	public static void main(String[] args) throws Exception {
		EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.1.11",4222);
    	client.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("222 recv ===> "+packet);
			}
		});
    	client.auth("1002@test.com/222","123123");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
