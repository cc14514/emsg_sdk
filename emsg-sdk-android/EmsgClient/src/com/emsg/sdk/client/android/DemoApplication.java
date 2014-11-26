
package com.emsg.sdk.client.android;

import android.app.Application;

import android.util.Log;
import android.widget.Toast;

import com.emsg.sdk.EmsgClient;
import com.emsg.sdk.EmsgClient.EmsClosedCallBack;

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
        mEmsgClient.setEmsClosedCallBack(new EmsClosedCallBack() {
            @Override
            public void onAnotherClientLogin() {
                Toast.makeText(DemoApplication.this, "已掉线请重新登录", Toast.LENGTH_SHORT).show();
                //需要三方重新登录
            }
        });
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
