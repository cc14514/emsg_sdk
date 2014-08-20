package com.emsg.test.offlinemsg.p2p;

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
 * 测试离线消息存储数量（如10条），用户<-->用户场景：
 * 用户liang（liang@test.com/123）给用户cai（cai@test.com/123）发消息，
 * 用户cai不在线，用户liang发了20条消息，用户cai上线后只能收到10条，
 * 且是最后收到的10条
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
    public void testOfflineMsgCountLessThan() throws Exception {
    	sender.setProvider(new DefProvider());
    	sender.setHeartBeat(30000);
    	sender.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println(Constants.from_account + " packet__recv ===> "+packet);
			}

			@Override
			public void textPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mediaPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}
		});
    	sender.auth(Constants.from_account, Constants.from_password);

    	
    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<9; i++) {
    		sender.send(new DefPacket(Constants.to_account, message + i, Constants.MSG_TYPE_CHAT));
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
				if (packet.getEnvelope().getType() == Constants.MSG_TYPE_CHAT) {
					Assert.assertTrue(packet.getPayload().getContent().equals(message + (counter)));
					
					// 使用计数器计算离线消息个数
					counter++;
				}
			}

			@Override
			public void textPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mediaPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}
		});
    	receiver.auth(Constants.to_account, Constants.to_password);
    	
    	// 等待离线消息被完全接收
    	Thread.sleep(2000);
    	
    	// 判断离线消息数量
    	Assert.assertTrue(counter == 9);
    }


    @Test
    public void testOfflineMsgCount() throws Exception {
    	sender.setProvider(new DefProvider());
    	sender.setHeartBeat(30000);
    	sender.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println(Constants.from_account + " packet__recv ===> "+packet);
			}

			@Override
			public void textPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mediaPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}
		});
    	sender.auth(Constants.from_account, Constants.from_password);

    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<20; i++) {
    		sender.send(new DefPacket(Constants.to_account, message + i, Constants.MSG_TYPE_CHAT));
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
				if (packet.getEnvelope().getType() == Constants.MSG_TYPE_CHAT) {
					Assert.assertTrue(packet.getPayload().getContent().equals(message + (counter + 10)));
					
					// 使用计数器计算离线消息个数
					counter++;
				}
			}

			@Override
			public void textPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mediaPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}
		});
    	receiver.auth(Constants.to_account, Constants.to_password);
    	
    	// 等待离线消息被完全接收
    	Thread.sleep(2000);
    	
    	// 判断离线消息数量
    	Assert.assertTrue(counter == Constants.OFFLINE_MSG_COUNT);
    }


}