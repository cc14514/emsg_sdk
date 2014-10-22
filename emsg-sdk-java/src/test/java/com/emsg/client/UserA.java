package com.emsg.client;

import java.util.List;

import com.emsg.client.beans.DefPacket;
import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;
import com.emsg.client.beans.Pubsub;

public class UserA implements Define {
//	<<"{\"cb\":\"cc@test.com\",\"node\":\"hello\",\"title\":\"good job!\",\"summary\":\"what's app ?\"}">>
	public static void main(String[] args) throws Exception {
//		EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("182.254.210.135", 4222);
		EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.2.11", 4222);
		client.setHeartBeat(55000);
		client.setProvider(new DefProvider());
		client.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				//System.out.println("liangc___packet__recv ===> " + packet);
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
					System.out.println(packets.size()+"__offline__message=="+packet.toString());
				}
			}
			@Override
			public void pubsubPacket(Pubsub pubsub) {
				System.out.println(pubsub);
			}
			
		});
		
		client.auth("aaa@test.com", "123123");
		long s = System.currentTimeMillis();
		for(int i=0;i<3000000;i++){
			IPacket p = new DefPacket("you@test.com","kkkkkkkkkkkkkkkkkkkkkkkkkkkk",Define.MSG_TYPE_CHAT);
			p.getEnvelope().setId(i+"");
			client.send(p);
		}
		long e = System.currentTimeMillis();
		System.out.println("end__time:::> "+(e-s) );
		Thread.sleep(2000);
		client.close();
		System.out.println("closed" );
	}
}
