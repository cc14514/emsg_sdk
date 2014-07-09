package com.emsg.client;

import java.util.UUID;

import net.sf.json.JSONObject;


public class SimpleClient {
	protected String user;
	protected String password;
	protected EmsgClient client;
	
	public SimpleClient(String user, String password) {
		this.user = user;
		this.password = password;
		
	}
	
	public void init(PacketListener listener) throws Exception {
		client = new EmsgClient(Constants.server_host,Constants.server_port);
		client.setPacketListener(listener);
		client.auth(user, password);
	}
	
	public void send(String user, String message) throws Exception {
		JSONObject packet = new JSONObject();
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", 1);
		envelope.put("from", this.user);
		envelope.put("to", user);
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		
		JSONObject attrs = new JSONObject();
		attrs.put("msgtype", "normalchat");
		attrs.put("type", "chat");
		
		payload.put("attrs", attrs);
		payload.put("content", message);
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");

		String msg = packet.toString();
    	client.send(msg);
	}
	
	public void sendToGroup(String group, String message) throws Exception {
		JSONObject packet = new JSONObject();
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", 2);
		envelope.put("from", this.user);
		envelope.put("gid", group);
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		
		JSONObject attrs = new JSONObject();
		attrs.put("msgtype", "normalchat");
		attrs.put("type", "chat");
		
		payload.put("attrs", attrs);
		payload.put("content", message);
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");

		String msg = packet.toString();
    	client.send(msg);
	}
	
	public void sendTypedMessage(String user, int type, String message) throws Exception {
		JSONObject packet = new JSONObject();
		JSONObject envelope = new JSONObject();
		envelope.put("id", UUID.randomUUID().toString());
		envelope.put("type", type);
		envelope.put("from", this.user);
		envelope.put("to", user);
		envelope.put("ack", 1);
		
		JSONObject payload = new JSONObject();
		
		JSONObject attrs = new JSONObject();
		attrs.put("msgtype", "normalchat");
		attrs.put("type", "chat");
		
		payload.put("attrs", attrs);
		payload.put("content", message);
		
		packet.put("envelope",envelope);
		packet.put("payload",payload);
		packet.put("vsn","0.0.1");

		String msg = packet.toString();
    	client.send(msg);
	}
	
	public void shutdown() {
		client.shutdown();
	}
	
}
