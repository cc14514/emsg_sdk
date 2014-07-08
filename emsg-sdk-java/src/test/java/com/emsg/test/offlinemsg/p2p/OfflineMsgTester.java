package com.emsg.test.offlinemsg.p2p;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.emsg.client.Constants;
import com.emsg.client.PacketListener;

/**
 * 测试离线消息存储数量（如10条），用户<-->用户场景：
 * 用户liang（liang@test.com/123）给用户cai（cai@test.com/123）发消息，
 * 用户cai不在线，用户liang发了20条消息，用户cai上线后只能收到10条。
 * 
 * @author tiger
 *
 */
public class OfflineMsgTester {

	// 消息计数器
	public static int counter = 0;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testOfflineMsgCount() throws Exception {
    	// 在线用户给离线用户发送消息
    	OnlineSender.send(20);

    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	// 离线用户上线，接收离线消息
    	OfflineReceiver.recv(new PacketListener() {
			@Override
			public void processPacket(String packet) {
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
    	OnlineSender.send(9);

    	// 等待消息保存为离线消息
    	Thread.sleep(1000);
    	
    	// 离线用户上线，接收离线消息
    	OfflineReceiver.recv(new PacketListener() {
			@Override
			public void processPacket(String packet) {
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