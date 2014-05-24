package com.emsg.client;

import java.util.UUID;

import net.sf.json.JSONObject;

public class UserA {
	
	static final String auth_service = "http://127.0.0.1:8080/emsg_auth_service/auth.html";
	
	public static void main(String[] args) throws Exception {
		EmsgClient client = EmsgClient.newInstance(auth_service);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("aaa recv ===> "+packet);
			}
		});
    	client.auth("usera@test.com","123123");
    	JSONObject packet = new JSONObject();
		packet.put("id", UUID.randomUUID().toString());
		packet.put("type", 1);
		packet.put("from", "usera@test.com");
		packet.put("to", "userb@test.com");
		packet.put("body", "fuckyou");
		packet.put("ack", 1);
		String msg = packet.toString();
		
		System.out.println("aaa send ===> "+msg);
    	client.send(msg);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
