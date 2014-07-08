package com.emsg.test.offlinemsg.p2p;

import java.util.UUID;

import com.emsg.client.Constants;
import com.emsg.client.EmsgClient;
import com.emsg.client.PacketListener;

import net.sf.json.JSONObject;

public class OnlineSender {
	
	public static void main(String[] args) throws Exception {
		OnlineSender.send(10);
	}
	
	public static void send(int times) throws Exception {
		
		EmsgClient online = new EmsgClient(Constants.server_host,Constants.server_port);
    	online.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println(Constants.from_account + " recv ===> "+packet);
			}
		});
    	online.auth(Constants.from_account, Constants.from_password);

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
		for (int i=0; i<times; i++) {
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", 1);
		envelope.put("from", Constants.from_account);
		envelope.put("to", Constants.to_account);
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		
		JSONObject attrs = new JSONObject();
		attrs.put("msgtype", "normalchat");
		attrs.put("type", "chat");
		
		payload.put("attrs", attrs);
		payload.put("content", "消息"+i);
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");

		String msg = packet.toString();
    	online.send(msg);
		}
	}
	
}
