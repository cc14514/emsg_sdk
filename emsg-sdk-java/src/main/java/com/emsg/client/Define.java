package com.emsg.client;
/**
 * @author liangc
 */
public interface Define {
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
	
}
