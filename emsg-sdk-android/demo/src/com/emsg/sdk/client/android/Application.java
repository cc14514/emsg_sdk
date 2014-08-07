package com.emsg.sdk.client.android;

import android.util.Log;

import com.emsg.client.EmsgClient;
import com.emsg.client.beans.DefPayload;

public class Application extends android.app.Application {
	private static final String TAG = Application.class.getSimpleName();
	
	private EmsgClient<DefPayload> emsgClient;

	public EmsgClient<DefPayload> getEmsgClient() {
		return emsgClient;
	}

	public void setEmsgClient(EmsgClient<DefPayload> emsgClient) {
		this.emsgClient = emsgClient;
	}

	@Override  
	public void onCreate() { 
		super.onCreate();  
	}

	@Override
	public void onTerminate() {
		try{
			emsgClient.close();
		}catch(Exception ex){
			Log.e(TAG, "尝试关闭client异常."+ex.getMessage(),ex);
		}
		super.onTerminate();
	} 
	
}
