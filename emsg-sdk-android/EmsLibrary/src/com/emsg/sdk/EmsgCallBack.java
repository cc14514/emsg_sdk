package com.emsg.sdk;

public interface  EmsgCallBack {
    
    public abstract void onSuccess(String resutMsg);

    public abstract void onError(int errorCode, String mErrorMsg);


}
