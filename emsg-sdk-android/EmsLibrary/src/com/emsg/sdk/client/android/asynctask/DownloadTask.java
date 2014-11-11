package com.emsg.sdk.client.android.asynctask;

import android.os.AsyncTask;

import com.emsg.sdk.client.utils.DownloadUtil;

public class DownloadTask {
	
	public void download(String key, String filename, DownloadTaskCallback callback) {
		this.download(key, filename, false, callback);
	}
			
	/**
	 * @param key       文件链接
	 * @param filename  文件名
	 * @param thumbnail 是否缩略图
	 * @param callback  下载回调函数
	 */
	public void download(String key, final String filename, boolean thumbnail, final DownloadTaskCallback callback) {
		String param = thumbnail ? "?imageView2/2/w/200/h/200" : "";
		String url = "http://emsg.qiniudn.com/" + key + param;
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				try {
					DownloadUtil.get(params[0], params[1]);
					return true;
				} catch (Exception e) {
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean success) {
				if (success) {
					callback.onSuccess(filename);
				} else {
					callback.onFailure();
				}
			}
			
		}.execute(url, filename);

	}

	/**
	 * 异步下载完成/失败后通知,在主线程中,可以更新界面
	 * 
	 * @author tiger
	 *
	 */
	public interface DownloadTaskCallback {
		/**
		 * 下载成功后调用
		 */
		public void onSuccess(String filename);
		
		/**
		 * 下载失败后调用
		 */
		public void onFailure();
	}
	
	
}
