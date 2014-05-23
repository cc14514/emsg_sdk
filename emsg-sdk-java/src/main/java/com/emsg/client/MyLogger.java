package com.emsg.client;

import java.util.Date;

public class MyLogger {
	
	private Class clazz = null;
	
	public MyLogger(Class c){
		clazz = c;
	}
	
	public void info(Object o){
		System.out.println("["+new Date()+"]"+clazz.getName()+" : "+o.toString());
	}
	
	public void debug(Object o){
		System.out.println("["+new Date()+"]"+clazz.getName()+" : "+o.toString());
	}
	
}
