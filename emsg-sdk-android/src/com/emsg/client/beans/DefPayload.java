package com.emsg.client.beans;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.emsg.client.JSONUtil;

/**
 * 要递送的消息内容，这是一个默认的结构，包括一个 内容，和一组 K-V 扩展属性
 * 本公司提供的所有示例，都使用此结构完成各种消息递送，用户可以根据自己的需要，随意进行扩展
 * 只要自己提供序列化和反序列化方法即可
 * @author liangc
 */
public class DefPayload {
	
	/**
	 * 扩展属性
	 */
	private Map<String,String> attrs = new HashMap<String,String>();
	
	/**
	 * 消息内容
	 */
	private String content = null;

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "DefPayload [attrs=" + attrs + ", content=" + content + "]";
	}
	
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("attrs", this.attrs);
			json.put("content", this.content);
			return json;
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static DefPayload fromJSON(JSONObject json) {
		DefPayload payload = new DefPayload();
		payload.setAttrs(JSONUtil.getMap(json, "result"));
		payload.setContent(JSONUtil.getString(json, "content"));
		return payload;
	}
	
}