package com.emsg.client;



public class UserB implements Define{
	
	static final String auth_service = "http://192.168.1.12/emsg_auth_service/auth.html";
	
	public static void main(String[] args) throws Exception {
		final EmsgClient client = new EmsgClient("192.168.1.11",4223);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("bbb recv ===> "+packet);
			}
		});
    	client.auth("userb@test.com","123123");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
