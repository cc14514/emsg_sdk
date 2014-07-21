package com.emsg.client;

import java.util.UUID;

import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.IPacket;

import net.sf.json.JSONObject;



public class UserG implements Define{
	
	
	public static void main(String[] args) throws Exception {
		EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.1.11",4222);
    	client.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("1001 recv ===> "+packet);
			}
		});
    	client.auth("1001@test.com","123123");
    	
    	JSONObject packet = new JSONObject();
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", 2);
		envelope.put("from", "1001@test.com");
		envelope.put("gid", "1");
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		payload.put("content", "hi boys");
		// http://192.168.1.11:4280/?body={"sn":"SN_123456","service":"emsg_group","method":"reload","params":{"domain":"test.com","license":"123456","gid":"1"}}
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");
		String msg = packet.toString();
		
		System.out.println("1001 send ===> "+msg);
    	client.send(msg);
    	
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
