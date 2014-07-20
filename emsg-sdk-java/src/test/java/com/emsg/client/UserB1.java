package com.emsg.client;

import com.emsg.client.beans.DefPacket;
import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;


public class UserB1 implements Define{
	
	public static void main(String[] args) throws Exception {
		final EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("222.128.11.38",4222);
		client.setProvider(new DefProvider());
		//心跳
		client.setHeartBeat(5000*10);
    	client.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("1111__packet__recv ===> "+packet);
			}
		});
    	
    	client.auth("1111@test.com","123123");
    	//发送一个普通聊天消息，给 0000@test.com ，内容是 hello world
    	IPacket<DefPayload> packet = new DefPacket("0000@test.com","hello world",Define.MSG_TYPE_CHAT);
    	
		client.send(packet);
		Thread.sleep(3000);
	}
	
}
