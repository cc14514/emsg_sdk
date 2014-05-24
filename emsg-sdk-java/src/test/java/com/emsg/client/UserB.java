package com.emsg.client;

import net.sf.json.JSONObject;


public class UserB {
	
	static final String auth_service = "http://127.0.0.1:8080/emsg_auth_service/auth.html";
	
	public static void main(String[] args) throws Exception {
		final EmsgClient client = EmsgClient.newInstance(auth_service);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("bbb recv ===> "+packet);
				JSONObject json = JSONObject.fromObject(packet);
				if(json.has("ack")&&json.getInt("ack")==1){
					//ack message
					/*{
						  "id":"xxxx",
						  "type":3,
						  "from":"userB@test.com",
						  "to":"userA@test.com",
						  "body":"to_ack",
						  "ct":"1368410111254"
					}*/
					JSONObject ack = new JSONObject();
					ack.put("id", json.get("id"));
					ack.put("type", 3);
					ack.put("from", json.get("to"));
					ack.put("to", json.get("from"));
					ack.put("body", "recevied");
					System.out.println("send ack :::>"+ack.toString());
					try {
						client.send(ack.toString());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
    	client.auth("userb@test.com","123123");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
