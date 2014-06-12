package com.emsg.client;

import java.util.UUID;

import net.sf.json.JSONObject;

public class UserA {
	
	static final String auth_service = "http://192.168.1.12/emsg_auth_service/auth.html";
	
	public static void main(String[] args) throws Exception {
		
		EmsgClient client = new EmsgClient("127.0.0.1",4222);
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
		payload.put("content", "你心情好么");
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");

		String msg = packet.toString();
    	client.send(msg);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
