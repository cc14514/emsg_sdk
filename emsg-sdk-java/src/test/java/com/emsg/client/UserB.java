package com.emsg.client;



public class UserB implements Define{
	
	public static void main(String[] args) throws Exception {
		final EmsgClient client = new EmsgClient("192.168.1.11",4222);
		client.setHeartBeat(1000*10);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("111 recv ===> "+packet);
			}
		});
    	client.auth("bbb@test.com/222","123123");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
