package com.emsg.client;

import java.util.UUID;

import net.sf.json.JSONObject;

public class UserA {
	
	public static void main(String[] args) throws Exception {
		
		EmsgClient client = new EmsgClient("192.168.1.11",4222);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("aaa recv ===> "+packet);
			}
		});
    	client.auth("aaa@test.com/123","123123");

		JSONObject packet = new JSONObject();

/*		
		[{<<"envelope">>,
            [{<<"id">>,<<"d8e98cf9-147c-4853-a1cc-6c32d6da562c">>},
             {<<"type">>,1},
             {<<"ack">>,1},
             {<<"from">>,<<"aaa@test.com">>},
             {<<"to">>,<<"bbb@test.com">>}]},
           {<<"payload">>,
            [{<<"attrs">>,[{<<"msgtype">>,<<"normalchat">>},{<<"type">>,<<"chat">>}]},
             {<<"content">>,[{<<"body">>,<<"ahahahahaha_222____0">>}]}]},
           {<<"vsn">>,<<"0.0.1">>}]
*/		
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", 1);
		envelope.put("from", "aaa@test.com/123");
		envelope.put("to", "bbb@test.com");
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		
		JSONObject attrs = new JSONObject();
		attrs.put("msgtype", "normalchat");
		attrs.put("type", "chat");
		
		JSONObject content = new JSONObject();
		content.put("body", "你知不知道");
		
		payload.put("attrs", attrs);
		payload.put("content", content);
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");

		String msg = packet.toString();
    	client.send(msg);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
