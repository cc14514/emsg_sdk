
package com.emsg.sdk.client.android.asynctask;

import android.content.Context;

public abstract class AbsFileServerTarget {
    protected IDownLoadTask mDownLoadTask;
    protected IUpLoadTask mUpLoadTask;
    protected Context mContext;

    public AbsFileServerTarget(Context mContext) {
        this.mContext = mContext;
        setDownLoadTask();
        setUpLoadTask();
    }

    public abstract void setDownLoadTask();

    public abstract void setUpLoadTask();

    public IDownLoadTask getDownLoadTask() {
        return mDownLoadTask;
    };

    public IUpLoadTask getUpLoadTask() {
        return mUpLoadTask;
    }
}
