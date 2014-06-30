package com.emsg.client;



public class UserC implements Define{
	
	public static void main(String[] args) throws Exception {
		final EmsgClient client = new EmsgClient("222.128.11.38",4222);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("222 recv ===> "+packet);
			}
		});
    	client.auth("bbb@test.com/222","123123");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
