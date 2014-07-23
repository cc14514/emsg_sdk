package com.emsg.client;

public class Constants {

	//线程的退出信号，在队列中传递
	public final static String KILL = "\01\02\03";
	/**
	 * 结束符
	 */
	public static final String END_TAG = "\01";
	/**
	 * 心跳包
	 */
	public static final String HEART_BEAT = "\02";
	/**
	 * 心跳频率
	 */
	public static final int HEART_BEAT_FREQ = 1000*50;
	
//	 									 %% 0    打开session                                                                                                                      
//	 ~                              | 20 %% 1    普通聊天，文本                                                                                                                   
//	 ~                              | 21 %% 2    群聊，文本                                                                                                                       
//	 ~                              | 22 %% 3    状态同步                                                                                                                         
//	 ~                              | 23 %% 4    系统消息                                                                                                                         
	public static final int MSG_TYPE_OPEN_SESSION = 0;
	public static final int MSG_TYPE_CHAT = 1;
	public static final int MSG_TYPE_GROUP_CHAT = 2;
	public static final int MSG_TYPE_STATE = 3;
	public static final int MSG_TYPE_SYSTEM = 4;
	
	// 保存离线消息的数量
	public static final int OFFLINE_MSG_COUNT = 10;
	
	public static String from_account = "liang@test.com/123";
	public static String from_password = "123123";
	
	public static String to_account = "cai@test.com/123";
	public static String to_password = "123123";
	
	public static String server_host = "192.168.1.11";
	public static int server_port = 4222;
	
}
