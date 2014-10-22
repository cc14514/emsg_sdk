package com.emsg.client;

import java.util.List;

import com.emsg.client.beans.DefPacket;
import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;
import com.emsg.client.beans.Pubsub;

public class UserB implements Define {

	public static void main(String[] args) throws Exception {
		EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.2.11", 4222);
		client.setProvider(new DefProvider());
		client.setHeartBeat(15000);
		client.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("process ===> " + packet);
			}
			@Override
			public void mediaPacket(IPacket<DefPayload> packet) {
			}
			@Override
			public void sessionPacket(IPacket<DefPayload> packet) {
				System.out.println("session ===> " + packet);
			}
			@Override
			public void offlinePacket(List<IPacket<DefPayload>> packets) {
				for(IPacket<DefPayload> packet : packets){
					System.out.println(packets.size()+"__offline__message=="+packet.toString());
				}
			}
			@Override
			public void pubsubPacket(Pubsub pubsub) {
				System.out.println(pubsub);
			}
			
		});
		client.auth("userb@test.com", "123123");
//		for(int i=0;i<1;i++){
//			client.send(new DefPacket("you@helloemsg.com","FFFFFFFFFFFFFFFFFFFF____"+i,Define.MSG_TYPE_CHAT));
//		}
		Thread.sleep(Integer.MAX_VALUE);
	}

}
