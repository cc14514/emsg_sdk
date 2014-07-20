package com.emsg.client.beans;

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
	
}
