package com.emsg.client.beans;

import org.json.JSONException;
import org.json.JSONObject;

import com.emsg.client.JSONUtil;

public class Entity {
	
	private String result = null;
	
	private String reason = null;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "Entity [result=" + result + ", reason=" + reason + "]";
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("reuslt", this.result);
			json.put("reason", this.reason);
			return json;
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static Entity fromJSON(JSONObject json) {
		Entity entity = new Entity();
		entity.setResult(JSONUtil.getString(json, "result"));
		entity.setReason(JSONUtil.getString(json, "reason"));
		return entity;
	}
}
