package com.emsg.sdk.client.android.asynctask.qiniu;

import android.content.Context;

import com.emsg.sdk.client.android.asynctask.AbsFileServerTarget;
public class QiNiuFileServerTarget extends AbsFileServerTarget{

    public QiNiuFileServerTarget(Context mContext) {
        super(mContext);
    }

    @Override
    public void setDownLoadTask() {
        this.mDownLoadTask = new DownloadTask();
    }

    @Override
    public void setUpLoadTask() {
       this.mUpLoadTask = new UploadTask(mContext);
    }
    
   

}
