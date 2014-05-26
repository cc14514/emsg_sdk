package com.emsg.client;

import net.sf.json.JSONObject;


public class UserB implements Define{
	
	static final String auth_service = "http://127.0.0.1:8080/emsg_auth_service/auth.html";
	
	public static void main(String[] args) throws Exception {
		final EmsgClient client = EmsgClient.newInstance(auth_service);
    	client.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("bbb recv ===> "+packet);
				JSONObject jp = JSONObject.fromObject(packet);
				JSONObject envelope = jp.getJSONObject("envelope");
				if(envelope.has("ack")&&envelope.getInt("ack")==1){
					JSONObject ack = new JSONObject();
					JSONObject ack_envelope = new JSONObject();
					if(1==envelope.getInt("ack")){
						//answer
						String id = envelope.getString("id");
						String from = envelope.getString("from");
						String to = envelope.getString("to");
						/*{
							  "envelope":{
							    "id":"xxxx",
							    "type":3,
							    "from":"userb@test.com",
							    "to":"usera@test.com",
							    "ct":"1368410111254"
							  },
							  "payload":"received"
						}*/
						ack_envelope.put("id", id);
						ack_envelope.put("from", to);
						ack_envelope.put("to", from);
						ack_envelope.put("type", MSG_TYPE_STATE);
						
						ack.put("envelope", ack_envelope);
						ack.put("payload", "received");
						
						System.out.println("send ack :::>"+ack.toString());
						try {
							client.send(ack.toString());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		});
    	client.auth("userb@test.com","123123");
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
