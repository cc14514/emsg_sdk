package com.emsg.client.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtil {
	
	public static JsonObject parse(String json) {
		try {
			return new JsonParser().parse(json).getAsJsonObject();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static JsonObject getAsJsonObject(JsonObject json, String key) {
		try {
			return json.get(key).getAsJsonObject();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static JsonArray getAsJsonArray(JsonObject json, String key) {
		return json.get(key).getAsJsonArray();
	}

	public static String getAsString(JsonObject json, String key) {
		try {
			return json.get(key).getAsString();
		} catch (Exception e) {
			return null;
		}
	}

	public static int getAsInt(JsonObject json, String key) {
		return json.get(key).getAsInt();
	}

	public static int getAsInt(JsonObject json, String key, int def) {
		try {
			return json.get(key).getAsInt();
		} catch (Exception e) {
			return def;
		}
	}

	public static boolean getAsBoolean(JsonObject json, String key) {
		return json.get(key).getAsBoolean();
	}

	public static boolean getAsBoolean(JsonObject json, String key, boolean def) {
		try {
			return json.get(key).getAsBoolean();
		} catch (Exception e) {
			return def;
		}
	}
	
	

}
