package com.emsg.test.offlinemsg.p2p;

import net.sf.json.JSONObject;

import com.emsg.client.Constants;
import com.emsg.client.EmsgClient;
import com.emsg.client.PacketListener;

public class OfflineReceiver {
	public static void main(String[] args) throws Exception {
		OfflineReceiver.recv(new PacketListener() {
			@Override
			public void processPacket(String packet) {
			}
		});
	}
	
	public static void recv(final PacketListener listener) throws Exception {
		EmsgClient offline = new EmsgClient(Constants.server_host,Constants.server_port);
		offline.setPacketListener(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println(Constants.to_account + " recv ===> "+packet);
				JSONObject msg = JSONObject.fromObject(packet);
				
				// 去除server_ack和认证等返回信息
				if (msg.getJSONObject("envelope").getInt("type") != 0) {
					listener.processPacket(packet);
				}
			}
		});
		offline.auth(Constants.to_account, Constants.to_password);
	}
	
}
