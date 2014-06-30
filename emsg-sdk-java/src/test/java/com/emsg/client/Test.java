package com.emsg.client;

import java.io.OutputStream;
import java.net.Socket;

public class Test {
	
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("192.168.1.11",4222);
		OutputStream os = socket.getOutputStream();
		System.out.println("wait 6ms");
		Thread.sleep(1000*6);
		System.out.println("wait 35ms over");
		os.write("\02\01".getBytes());
		os.flush();
		System.out.println(socket.isClosed());
		System.out.println("sleep");
		Thread.sleep(1000);
		os.write("\02\01".getBytes());
		os.flush();
		Thread.sleep(1000);
		os.write("\02\01".getBytes());
		os.flush();
		
	}

}
