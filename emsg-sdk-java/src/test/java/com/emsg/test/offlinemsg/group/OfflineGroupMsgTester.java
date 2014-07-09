package com.emsg.test.offlinemsg.group;

import junit.framework.Assert;

import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.emsg.client.Constants;
import com.emsg.client.PacketListener;
import com.emsg.client.SimpleClient;

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
	private SimpleClient sender;
	private SimpleClient receiver;
	final String message = "Test message";
	
    @Before
    public void setUp() throws Exception {
    	counter = 0;
    	sender = new SimpleClient("1001@test.com/123", Constants.from_password);
    	receiver = new SimpleClient("1002@test.com/123", Constants.to_password);
    }

    @After
    public void clearup() throws Exception {
    	sender.shutdown();
    	receiver.shutdown();
    }
    
    @Test
    public void testOfflineMsgCount() throws Exception {
    	// 在线用户给离线用户发送消息
    	sender.init( new PacketListener() {
			@Override
			public void processPacket(String packet) {
				// 发送者不应该收到自己发送的群消息
				Assert.fail();
			}
		});

    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<20; i++) {
    		sender.sendToGroup("1", message+i);
    	}

    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	receiver.init(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("aaa recv ===> "+counter + "  "+packet);

				JSONObject jp = JSONObject.fromObject(packet);
				JSONObject payload = jp.getJSONObject("payload");
				String content = payload.getString("content");
				Assert.assertTrue(content.equals(message + (counter + 10)));
				
				// 使用计数器计算离线消息个数
				counter++;
			}
		});
    	
    	// 等待离线消息被完全接收
    	Thread.sleep(2000);
    	
    	// 判断离线消息数量
    	Assert.assertTrue(counter == Constants.OFFLINE_MSG_COUNT);
    }
    
    @Test
    public void testOfflineMsgCountLessThan() throws Exception {
    	// 在线用户给离线用户发送消息
    	sender.init( new PacketListener() {
			@Override
			public void processPacket(String packet) {
				// 发送者不应该收到自己发送的群消息
				Assert.fail();
			}
		});

    	// 在线用户给离线用户发送消息
    	Thread.sleep(1000);
    	for (int i=0; i<9; i++) {
    		sender.sendToGroup("1", message+i);
    	}

    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	receiver.init(new PacketListener() {
			@Override
			public void processPacket(String packet) {
				System.out.println("aaa recv ===> "+counter + "  "+packet);

				JSONObject jp = JSONObject.fromObject(packet);
				JSONObject payload = jp.getJSONObject("payload");
				String content = payload.getString("content");
				Assert.assertTrue(content.equals(message + counter));
				
				// 使用计数器计算离线消息个数
				counter++;
			}
		});
    	
    	// 等待离线消息被完全接收
    	Thread.sleep(2000);
    	
    	// 判断离线消息数量
    	Assert.assertTrue(counter == 9);
    }


}