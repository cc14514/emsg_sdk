package com.emsg.sdk;

import java.util.Date;

public class MyLogger {
	
	private Class<?> clazz = null;
	
	public MyLogger(Class<?> c){
		clazz = c;
	}
	
	public void info(Object o){
		System.err.println("["+new Date()+"]"+clazz.getName()+" : "+o.toString());
	}
	
	public void debug(Object o){
		System.err.println("["+new Date()+"]"+clazz.getName()+" : "+o.toString());
	}
	
}
