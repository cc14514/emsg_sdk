package com.emsg.client;



public class UserB implements Define{
	
	static final String auth_service = "http://127.0.0.1:8080/emsg_auth_service/auth.html";
	
	public static void main(String[] args) throws Exception {
		final EmsgClient client = EmsgClient.newInstance(auth_service);
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
