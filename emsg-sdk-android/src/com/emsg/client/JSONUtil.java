package com.emsg.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

public class JSONUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public static int getInt(JSONObject json, String key) {
		try {
			return Integer.valueOf(json.get(key).toString());
		} catch(Exception e) {
			return 0;
		}
	}
	
	public static String getString(JSONObject json, String key) {
		try {
			return json.get(key).toString();
		} catch(Exception e) {
			return null;
		}
	}
	
	public static Map<String, String> getMap(JSONObject json, String key) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			Iterator<?> iterator = json.getJSONObject(key).keys();
			while (iterator.hasNext()) {
				String k = (String) iterator.next();
				map.put(k, getString(json, k));
			}
			return map;
		} catch(Exception e) {
			return null;
		}
	}
	

}
