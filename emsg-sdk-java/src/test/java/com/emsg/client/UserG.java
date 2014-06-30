package com.emsg.client;

import java.util.UUID;

import net.sf.json.JSONObject;



public class UserG implements Define{
	
	
	public static void main(String[] args) throws Exception {
		final EmsgClient client = new EmsgClient("",0);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("bbb recv ===> "+packet);
			}
		});
    	client.auth("usera@test.com","123123");
    	
    	JSONObject packet = new JSONObject();
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", 2);
		envelope.put("from", "usera@test.com");
		envelope.put("gid", "g123");
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		payload.put("content", "hi all");
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");
		String msg = packet.toString();
		
		System.out.println("aaa send ===> "+msg);
    	client.send(msg);
    	
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
