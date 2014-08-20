package com.emsg.client;
/**
 * @author liangc
 */
public interface Define {
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
	 * 服务器KILL信号
	 */
	public static final String SERVER_KILL = "\03";
	/**
	 * 心跳频率
	 */
	public static final int HEART_BEAT_FREQ = 1000*50;
	
//	 									 %% 0    打开session                                                                                                                      
//	 ~                              | 20 %% 1    普通聊天，文本                                                                                                                   
//	 ~                              | 21 %% 2    群聊，文本                                                                                                                       
//	 ~                              | 22 %% 3    状态同步                                                                                                                         
//	 ~                              | 23 %% 4    系统消息                                                       
//	 ~                              | 23 %% 5    语音拨号                                                       
//	 ~                              | 23 %% 6    视频拨号                                                       
	
	public static final int MSG_TYPE_OPEN_SESSION = 0;
	public static final int MSG_TYPE_CHAT = 1;
	public static final int MSG_TYPE_GROUP_CHAT = 2;
	public static final int MSG_TYPE_STATE = 3;
	public static final int MSG_TYPE_SYSTEM = 4;
	public static final int MSG_TYPE_P2P_SOUND = 5;
	public static final int MSG_TYPE_P2P_VIDEO = 6;
	
	public static final int ACK_DISABLE = 0;
	public static final int ACK_ENABLE = 1;
	
	public static final String VSN = "0.0.1";
	
}
