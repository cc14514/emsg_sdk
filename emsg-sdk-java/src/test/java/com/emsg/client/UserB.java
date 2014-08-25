package com.emsg.client;

import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;

public class UserB implements Define {

	public static void main(String[] args) throws Exception {
		EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.2.11", 4222);
		client.setProvider(new DefProvider());
		client.setHeartBeat(50000);
		client.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("liangc___packet__recv ===> " + packet);
			}

			@Override
			public void mediaPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
			}

			@Override
			public void sessionPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void textPacket(String packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void objectPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}
		});
		client.auth("liangc@test.com", "123123");

		Thread.sleep(Integer.MAX_VALUE);
	}

}
