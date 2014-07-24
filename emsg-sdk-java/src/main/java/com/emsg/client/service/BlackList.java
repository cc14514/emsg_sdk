package com.emsg.client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.emsg.client.HttpUtils;
import com.emsg.client.MyLogger;


/**
 * 黑名单操作
 * @author liangc
 */
public class BlackList {
	
	
	static MyLogger logger = new MyLogger(BlackList.class);
	
	private String INF_URL = "http://192.168.1.11:4280";
	private final static String INF_SERVICE = "emsg_black_list";
	private final static String INF_METHOD_APPEND = "append";
	private final static String INF_METHOD_REMOVE = "remove";
	private final static String INF_METHOD_FETCH = "fetch";
	
	public BlackList(){}
	
	public BlackList(String INF_URL){
		setInfUrl(INF_URL);
	}
	public void setInfUrl(String INF_URL){
		this.INF_URL = INF_URL;
	}
	
	private String buildArgs(String method,Map<String,String> args){
		return buildArgs(INF_SERVICE,method,args);
	}
	private String buildArgs(String service,String method,Map<String,String> args){
		JSONObject params = new JSONObject();
		params.putAll(args);
		JSONObject json = new JSONObject();
		json.put("sn", UUID.randomUUID().toString());
		json.put("service", service);
		json.put("method", method);
		json.put("params", params);
		return json.toString();
	}
	
	/**
	 * 添加黑名单；<br>
	 * 例如要将 test.com 域的 usera 添加到 userb 的黑名单中
	 * @param from_jid = usera@test.com
	 * @param to_jid = userb@test.com
	 * @param pwd = 登录 emsg 时用的 pwd，应该是个 token
	 * @throws Exception 添加失败时，会返回异常
	 */
	public void append(String from_jid,String to_jid,String pwd) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
		params.put("pwd", pwd);
		params.put("from", from_jid);
		params.put("to", to_jid);
		String body = buildArgs(INF_METHOD_APPEND,params);
		logger.debug("blacklist_append_body="+body);
		Map<String,String> form = new HashMap<String,String>();
		form.put("body", body);
		String rtn = HttpUtils.http(INF_URL, form);
		JSONObject json = JSONObject.fromObject(rtn);
		logger.debug(json.toString());
		if(!json.getBoolean("success")){
			throw new Exception(json.getString("entity"));
		}
	} 
	/**
	 * 移除黑名单；<br>
	 * 例如要将 test.com 域的 usera 在 userb 的黑名单中移出
	 * @param from_jid = usera@test.com
	 * @param to_jid = userb@test.com
	 * @param pwd = 登录 emsg 时用的 pwd，应该是个 token
	 * @throws Exception 删除失败时，会返回异常
	 */
	public void remove(String from_jid,String to_jid,String pwd) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
		params.put("pwd", pwd);
		params.put("from", from_jid);
		params.put("to", to_jid);
		String body = buildArgs(INF_METHOD_REMOVE,params);
		logger.debug("blacklist_remove_body="+body);
		Map<String,String> form = new HashMap<String,String>();
		form.put("body", body);
		String rtn = HttpUtils.http(INF_URL, form);
		JSONObject json = JSONObject.fromObject(rtn);
		logger.debug(json.toString());
		if(!json.getBoolean("success")){
			throw new Exception(json.getString("entity"));
		}
	}
	
	public List<Map<String,String>> fetch(String to_jid,String pwd) throws Exception {
		Map<String,String> params = new HashMap<String,String>();
		params.put("pwd", pwd);
		params.put("to", to_jid);
		String body = buildArgs(INF_METHOD_FETCH,params);
		logger.debug("blacklist_fetch_body="+body);
		Map<String,String> form = new HashMap<String,String>();
		form.put("body", body);
		String rtn = HttpUtils.http(INF_URL, form);
		JSONObject json = JSONObject.fromObject(rtn);
		logger.debug(json.toString());
		if(!json.getBoolean("success")){
			throw new Exception(json.getString("entity"));
		}else{
			List<Map<String,String>> result = new ArrayList<Map<String,String>>();
			JSONArray arr = json.getJSONArray("entity");
			if(arr!=null&&arr.size()>0)
				for(int i=0;i<arr.size();i++){
					JSONObject u = arr.getJSONObject(i);
					Map<String,String> um = new HashMap<String,String>();
					um.put("jid", u.getString("jid"));
					um.put("ct", u.getString("ct"));
					logger.debug(um);
					result.add(um);
				}
			return result;
		}
	}
	
	public static void main(String[] args) throws Exception {
		BlackList b = new BlackList();
		b.fetch("userb@test.com", "123123");
	}
	
}
