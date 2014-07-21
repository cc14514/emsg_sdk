package com.emsg.test.offlinemsg.customtype;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.emsg.client.Constants;
import com.emsg.client.EmsgClient;
import com.emsg.client.PacketListener;
import com.emsg.client.beans.DefPacket;
import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.DefProvider;
import com.emsg.client.beans.IPacket;

/**
 * 
 * 类型101离线消息存储数量（只存储最后条），用户<-->用户场景：
 * 用户liang（liang@test.com/123）给用户cai（cai@test.com/123）发消息，
 * 用户cai不在线，用户liang发了10条消息，用户cai上线后只能收到最后一条。
 * 
 * 类型100离线消息存储数量（只存储第一条），用户<-->用户场景：
 * 用户liang（liang@test.com/123）给用户cai（cai@test.com/123）发消息，
 * 用户cai不在线，用户liang发了10条消息，用户cai上线后只能收到第一条。
 * 
 * @author tiger
 *
 */
public class OfflineMsgTester {

	// 消息计数器
	public static int counter = 0;
	private EmsgClient<DefPayload> sender;
	private EmsgClient<DefPayload> receiver;
	final String message = "Test message";
	
    @Before
    public void setUp() throws Exception {
    	counter = 0;
    	sender = new EmsgClient<DefPayload>(Constants.server_host,Constants.server_port);
    	receiver = new EmsgClient<DefPayload>(Constants.server_host,Constants.server_port);
    }

    @After
    public void clearup() throws Exception {
    	sender.close();
    	receiver.close();
    }
    
    @Test
    public void testOfflineMsgType100() throws Exception {
    	sender.setProvider(new DefProvider());
    	sender.setHeartBeat(30000);
    	sender.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println(Constants.from_account + " packet__recv ===> "+packet);
			}
		});
    	sender.auth(Constants.from_account, Constants.from_password);

    	
    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<9; i++) {
    		sender.send(new DefPacket(Constants.to_account, message + i, 100));
    	}

    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	// 离线用户上线，接收离线消息
    	receiver.setProvider(new DefProvider());
    	receiver.setHeartBeat(30000);
    	receiver.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println(Constants.to_account + " packet__recv ===> "+packet);
				if (packet.getEnvelope().getType() == 100) {
					Assert.assertTrue(packet.getPayload().getContent().equals(message + 0));
					
					// 使用计数器计算离线消息个数
					counter++;
				}
			}
		});
    	receiver.auth(Constants.to_account, Constants.to_password);
    	
    	// 等待离线消息被完全接收
    	Thread.sleep(2000);
    	
    	// 判断离线消息数量
    	Assert.assertTrue(counter == 1);
    }
    
    @Test
    public void testOfflineMsgType101() throws Exception {
    	sender.setProvider(new DefProvider());
    	sender.setHeartBeat(30000);
    	sender.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println(Constants.from_account + " packet__recv ===> "+packet);
			}
		});
    	sender.auth(Constants.from_account, Constants.from_password);

    	
    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<10; i++) {
    		sender.send(new DefPacket(Constants.to_account, message + i, 101));
    	}

    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	// 离线用户上线，接收离线消息
    	receiver.setProvider(new DefProvider());
    	receiver.setHeartBeat(30000);
    	receiver.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println(Constants.to_account + " packet__recv ===> "+packet);
				if (packet.getEnvelope().getType() == 101) {
					Assert.assertTrue(packet.getPayload().getContent().equals(message + 9));
					
					// 使用计数器计算离线消息个数
					counter++;
				}
			}
		});
    	receiver.auth(Constants.to_account, Constants.to_password);
    	
    	// 等待离线消息被完全接收
    	Thread.sleep(2000);
    	
    	// 判断离线消息数量
    	Assert.assertTrue(counter == 1);
    }
    
}