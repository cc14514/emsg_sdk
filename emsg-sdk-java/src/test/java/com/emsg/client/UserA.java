package com.emsg.client;

import java.util.ArrayList;
import java.util.List;

import com.emsg.client.beans.DefPacket;
import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;
import com.emsg.client.beans.Pubsub;

public class UserA implements Define {
//	<<"{\"cb\":\"cc@test.com\",\"node\":\"hello\",\"title\":\"good job!\",\"summary\":\"what's app ?\"}">>
	public static void main(String[] args) throws Exception {
		List<EmsgClient<DefPayload>> list = new ArrayList<EmsgClient<DefPayload>>();
		
		for (int i=0; i<2000; i++) {
			EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.2.11", 5222);
			list.add(client);
			client.setHeartBeat(100000-1);
			client.setProvider(new DefProvider());
			client.setPacketListener(new PacketListener<DefPayload>() {
				@Override
				public void processPacket(IPacket<DefPayload> packet) {
					System.err.println("liangc___packet__recv ===> " + packet);
				}
				@Override
				public void mediaPacket(IPacket<DefPayload> packet) {
				}
				@Override
				public void sessionPacket(IPacket<DefPayload> packet) {
				}
				@Override
				public void offlinePacket(List<IPacket<DefPayload>> packets) {
					for(IPacket<DefPayload> packet : packets){
						System.err.println(packets.size()+"__offline__message=="+packet.toString());
					}
				}
				@Override
				public void pubsubPacket(Pubsub pubsub) {
					System.err.println(pubsub);
				}
				
			});
			client.auth("wangxxxx" + i + "@test.com", "123123");
		}
		System.out.println("wait");
		
		Thread.sleep(10000);
		StringBuilder sb = new StringBuilder(256);
		try {
			int k = 0;
			int count = 0;
		while(true) {
			for (int i=0; i<list.size(); i++) {
//				if (i % 6 != k) {
//					continue;
//				}
				for (int j=0; j<1; j++) {
					
				sb.setLength(0);
				sb.append("liangchuan__aaaa");
				sb.append(System.currentTimeMillis());
				sb.append("@test.com");
				list.get(i).send(new DefPacket(sb.toString(),"new___hello___world",Define.MSG_TYPE_CHAT));
				System.err.println("count : " + count++);
				}
			}
			k++;
			k = k > 5 ? 0 : k;
			
			// Thread.sleep(250);
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
