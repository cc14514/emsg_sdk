package com.emsg.client;

import java.io.Serializable;

import org.json.JSONObject;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
System.out.println(JSONObject.wrap(new Serializable() {
	private int m;
	private String s;
	
	public int getM() {
		return m;
	}
	
	public String getS() {
		return s;
	}
}));
	}

}
