package com.emsg.test.blacklist;

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
import com.emsg.client.beans.IPacket;
import com.emsg.client.service.BlackList;

/**
 * 黑名单功能测试：
 * 使用随机生成账号进行测试（账号A和账号B不可能在对方的黑名单）
 * 1、将A加入到B的黑名单
 * 2、A和B互相发消息
 * 3、A收到消息
 * 4、B收不到消息
 * 
 * @author tiger
 *
 */
public class BlackFunctionTester {

	private BlackList bl = new BlackList();
	private String userA; 
	private String userB; 
	private String pwd = "123123";
	private EmsgClient<DefPayload> userAClient;
	private EmsgClient<DefPayload> userBClient;
	private int counter = 0;
	
    @Before
    public void setUp() throws Exception {
    	counter = 0;
    	
    	// 产生随机账号
    	userA = UUID.randomUUID() + "@test.com";
    	userB = UUID.randomUUID() + "@test.com";
    	// 将A加入到B的黑名单
    	bl.append(userA, userB, pwd);
    	
    	userAClient = new EmsgClient<>(Constants.server_host,Constants.server_port);
    	userBClient = new EmsgClient<>(Constants.server_host,Constants.server_port);
    	
    }

    @After
    public void clearup() throws Exception {
    	
    	// 将A从B的黑名单中删除
    	bl.remove(userA, userB, pwd);
    	
    	userAClient.close();
    	userBClient.close();
    }
    
    @Test
    public void testFunctionality() throws Exception {
    	userAClient.setProvider(new DefProvider());
    	userAClient.setHeartBeat(30000);
    	userAClient.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("userA packet__recv ===> "+packet);
				if (packet.getEnvelope().getType() == 1) {
					// 用户A能收到用户B发的消息
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
			public void textPacket(String packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void objectPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}
		});
    	userAClient.auth(userA, Constants.from_password);
    	
    	
    	userBClient.setProvider(new DefProvider());
    	userBClient.setHeartBeat(30000);
    	userBClient.setPacketListener(new PacketListener<DefPayload>() {
			@Override
			public void processPacket(IPacket<DefPayload> packet) {
				System.out.println("userB packet__recv ===> "+packet);
				if (packet.getEnvelope().getType() == 1) {
					// 用户B不能收到用户A发的消息
					Assert.fail();
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
			public void textPacket(String packet) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void objectPacket(IPacket<DefPayload> packet) {
				// TODO Auto-generated method stub
				
			}
		});
    	userBClient.auth(userB, Constants.from_password);
    	
    	// 用户A给用户B发消息
    	userAClient.send(new DefPacket(userB, "test", 1));
    	// 用户B给用户A发消息
    	userBClient.send(new DefPacket(userA, "test", 1));

    	
    	Thread.sleep(1000);
    	Assert.assertEquals(counter, 1);
    }
    
    
    
}