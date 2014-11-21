
package com.emsg.sdk.client.android;

import android.app.Application;

import android.util.Log;

import com.emsg.sdk.EmsgClient;

public class DemoApplication extends Application {
    private static final String TAG = Application.class.getSimpleName();

    private EmsgClient mEmsgClient;

    public EmsgClient getEmsgClient() {
        return mEmsgClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mEmsgClient = EmsgClient.getInstance();
        mEmsgClient.init(this);
    }

    @Override
    public void onTerminate() {
        try {
            mEmsgClient.close();
        } catch (Exception ex) {
            Log.e(TAG, "尝试关闭client异常." + ex.getMessage(), ex);
        }
        super.onTerminate();
    }

}
