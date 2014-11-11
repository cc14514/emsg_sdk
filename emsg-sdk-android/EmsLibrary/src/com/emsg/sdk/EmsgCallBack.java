package com.emsg.sdk;

public abstract class  EmsgCallBack {
    
    public abstract void onSuccess();

    public abstract void onError(TypeError mErrorType);
    
    public enum TypeError{
        TIMEOUT,NETERROR,SOCKETERROR,AUTHERROR,FILEUPLOADERROR
    }
    public long mCallBackTime;
}
