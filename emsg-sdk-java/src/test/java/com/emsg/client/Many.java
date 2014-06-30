package com.emsg.client;

import java.util.ArrayList;
import java.util.List;


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
						EmsgClient client = new EmsgClient("",0);
						client.setPacketListener(new PacketListener() {
							@Override
							public void processPacket(String packet) {
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
		List<EmsgClient> list = new ArrayList<EmsgClient>();
		for(int i=0;i<m;i++){
			final int j = i;
			try{
				EmsgClient client = new EmsgClient("",0);
				client.setHeartBeat(100*1000);
				client.setPacketListener(new PacketListener() {
					@Override
					public void processPacket(String packet) {
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
