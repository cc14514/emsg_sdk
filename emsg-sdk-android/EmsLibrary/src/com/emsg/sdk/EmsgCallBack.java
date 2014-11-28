package com.emsg.sdk;

public abstract class  EmsgCallBack {
    
    public abstract void onSuccess();

    public abstract void onError(TypeError mErrorType);
    
    public void onProgress(int current ,int max){
        
    }
    public enum TypeError{
        TIMEOUT,NETERROR,SOCKETERROR,AUTHERROR,FILEUPLOADERROR,SESSIONCLOSED
    }
    public long mCallBackTime;
}
