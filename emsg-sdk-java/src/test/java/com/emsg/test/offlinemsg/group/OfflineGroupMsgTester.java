package com.emsg.test.offlinemsg.group;

import java.util.List;
import java.util.UUID;

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
import com.emsg.client.beans.Envelope;
import com.emsg.client.beans.IEnvelope;
import com.emsg.client.beans.IPacket;

/**
 * 测试离线消息存储数量（如10条），用户<-->群场景：
 * 用户liang（1001）给群丰简（群号1）发消息，
 * 群中用户cai（1002）不在线，用户liang发了20条消息，
 * 用户cai上线后只能收到10条，用户liang自己不会收到群消息。
 * 
 * @author tiger
 *
 */
public class OfflineGroupMsgTester {

	// 消息计数器
	public static int counter = 0;
	private EmsgClient<DefPayload> sender;
	private EmsgClient<DefPayload> receiver;
	final String groupMemberSender = "1001@test.com/123";
	final String groupMemberReceiver = "1002@test.com/123";
	
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
				System.out.println(groupMemberSender + " packet__recv ===> "+packet);
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
			public void offlinePacket(List<IPacket<DefPayload>> packets) {
				// TODO Auto-generated method stub
				
			}
		});
    	sender.auth(groupMemberSender, Constants.from_password);

    	IPacket<DefPayload> packet = new DefPacket();
    	IEnvelope envelope = new Envelope();
    	DefPayload payload = new DefPayload();
    	packet.setEnvelope(envelope);
    	packet.setPayload(payload);
    	packet.setVsn("0.0.1");
    	envelope.setType(Constants.MSG_TYPE_GROUP_CHAT);
    	envelope.setAck(1);
    	envelope.setFrom(groupMemberSender);
    	envelope.setGid("1");
    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<9; i++) {
    		envelope.setId(UUID.randomUUID().toString());
        	payload.setContent(message + i);
        	sender.send(packet);
    	}
    	
    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	// 离线用户上线，接收离线消息
    	receiver.setProvider(new DefProvider());
    	receiver.setHeartBeat(30000);
    	receiver.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("packet__recv ===> "+packet);
				if (packet.getEnvelope().getType() == Constants.MSG_TYPE_GROUP_CHAT) {
					Assert.assertTrue(packet.getPayload().getContent().equals(message + (counter)));
					
					// 使用计数器计算离线消息个数
					counter++;
				}
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
			public void offlinePacket(List<IPacket<DefPayload>> packets) {
				// TODO Auto-generated method stub
				
			}
		});
    	receiver.auth(groupMemberReceiver, Constants.to_password);
    	
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
				System.out.println(groupMemberSender + " packet__recv ===> "+packet);
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
			public void offlinePacket(List<IPacket<DefPayload>> packets) {
				// TODO Auto-generated method stub
				
			}
		});
    	sender.auth(groupMemberSender, Constants.from_password);

    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	IPacket<DefPayload> packet = new DefPacket();
    	IEnvelope envelope = new Envelope();
    	DefPayload payload = new DefPayload();
    	packet.setEnvelope(envelope);
    	packet.setPayload(payload);
    	packet.setVsn("0.0.1");
    	envelope.setType(Constants.MSG_TYPE_GROUP_CHAT);
    	envelope.setAck(1);
    	envelope.setFrom(groupMemberSender);
    	envelope.setGid("1");
    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<20; i++) {
    		envelope.setId(UUID.randomUUID().toString());
        	payload.setContent(message + i);
        	sender.send(packet);
    	}

    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	// 离线用户上线，接收离线消息
    	receiver.setProvider(new DefProvider());
    	receiver.setHeartBeat(30000);
    	receiver.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("packet__recv ===> "+packet);
				if (packet.getEnvelope().getType() == Constants.MSG_TYPE_GROUP_CHAT) {
					Assert.assertTrue(packet.getPayload().getContent().equals(message + (counter + 10)));
					
					// 使用计数器计算离线消息个数
					counter++;
				}
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
			public void offlinePacket(List<IPacket<DefPayload>> packets) {
				// TODO Auto-generated method stub
				
			}
		});
    	receiver.auth(groupMemberReceiver, Constants.to_password);
    	
    	// 等待离线消息被完全接收
    	Thread.sleep(2000);
    	
    	// 判断离线消息数量
    	Assert.assertTrue(counter == Constants.OFFLINE_MSG_COUNT);
    }
}