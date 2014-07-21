package com.emsg.client;

import java.util.ArrayList;
import java.util.List;

import com.emsg.client.beans.DefPayload;
import com.emsg.client.beans.IPacket;


public class Many {
	
	public static void main(String[] args) throws Exception {
		s(10000);
	}
	
	private static void u() throws Exception {
		for(int i=0;i<10000;i++){
			Thread.sleep(100);
			final int n = i;
			new Thread(){
				public void run(){
					try{
						EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.1.11",4222);
				    	client.setPacketListener(new PacketListener<DefPayload>() {
							@Override
							public void processPacket(IPacket<DefPayload> packet) {
								System.err.println("recv ===> "+packet);
							}
						});
						String u = n+"@test.com";
						int j=0;
						while(true){
					    	try {
								client.auth(u,"123123");
								Thread.sleep(Integer.MAX_VALUE);
					    	} catch (Exception e) {
								j++;
								if(j>=3){
									e.printStackTrace();
									break;
								}else{
									j++;
									Thread.sleep(1000);
									System.out.println("reconn------------>"+j+" ; u="+u);
								}
							}
						}						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	private static void s(int m) throws Exception {
		List<EmsgClient<DefPayload>> list = new ArrayList<EmsgClient<DefPayload>>();
		for(int i=0;i<m;i++){
			final int j = i;
			try{
				EmsgClient<DefPayload> client = new EmsgClient<DefPayload>("192.168.1.11",4222);
		    	client.setPacketListener(new PacketListener<DefPayload>() {
					@Override
					public void processPacket(IPacket<DefPayload> packet) {
						System.err.println("["+j+"] recv ===> "+packet);
					}
				});
				String u = i+"@test.com";
				client.auth(u,"123123");
				list.add(client);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
