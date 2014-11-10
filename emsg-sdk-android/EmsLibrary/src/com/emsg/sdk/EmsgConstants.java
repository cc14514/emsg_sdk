
package com.emsg.sdk;

public class EmsgConstants {

    // 线程的�?出信号，在队列中传�?
    public final static String KILL = "\01\02\03";
    /**
     * 结束�?
     */
    public static final String END_TAG = "\01";
    /**
     * 心跳�?
     */
    public static final String HEART_BEAT = "\02";
    /**
     * 心跳频率
     */
    public static final int HEART_BEAT_FREQ = 1000 * 50;

    // %% 0 打开session
    // ~ | 20 %% 1 普�?聊天，文�?
    // ~ | 21 %% 2 群聊，文�?
    // ~ | 22 %% 3 状�?同步
    // ~ | 23 %% 4 系统消息
    public static final int MSG_TYPE_OPEN_SESSION = 0;
    public static final int MSG_TYPE_CHAT = 1;
    public static final int MSG_TYPE_GROUP_CHAT = 2;
    public static final int MSG_TYPE_STATE = 3;
    public static final int MSG_TYPE_SYSTEM = 4;

    // 保存离线消息的数�?
    public static final int OFFLINE_MSG_COUNT = 10;

    public static String from_account = "liang@test.com/123";
    public static String from_password = "123123";

    public static String to_account = "cai@test.com/123";
    public static String to_password = "123123";

    public static String server_host = "192.168.1.11";
    public static int server_port = 4222;

    public static final String MSG_ACTION_RECDATA = "action.emsg.reciverdata";
    public static final String MSG_ACTION_RECOFFLINEDATA = "action.msg.reciverofflinedata";
    public static final String MSG_ACTION_SESSONOPENED = "action.msg.sessonopened";
    public static final String MSG_TYPE_FILEAUDIO= "audio";
    public static final String MSG_TYPE_FILEIMG = "image";
    public static final String MSG_TYPE_FILETEXT = "text";
    
    public static final int MSG_FILETYPE_IMAGE =1;
    public static final int MSG_FILETYPE_AUDIO =2;
    
    public static final int MSG_ERRORCODE_INIT = 101;
    
    public static final int MSG_ERRORCODE_NET = 102;
    
    public static final int MSG_ERRORCODE_INTRRU = 103;
    
    public static final int MSG_ERRORCODE_OTHER = 106;
    
    public static final int MSG_ERRORCODE_APPKEY = 104;
    
    public static final int MSG_ERRORCODE_LOGIN = 105;
}
