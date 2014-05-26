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
		
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", 1);
		envelope.put("from", "usera@test.com");
		envelope.put("to", "userb@test.com");
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		payload.put("content", "jjjjjjjjjjjjjjjjjjjjj");
		payload.put("lat", "1123123");
		payload.put("lng", "3423423");
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		
		String msg = packet.toString();
		
		System.out.println("aaa send ===> "+msg);
    	client.send(msg);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
