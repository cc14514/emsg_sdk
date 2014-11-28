
package com.emsg.sdk.client.android;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.emsg.sdk.EmsgClient;
import com.emsg.sdk.EmsgClient.EmsStateCallBack;

public class DemoApplication extends Application {
    private static final String TAG = Application.class.getSimpleName();

    private EmsgClient mEmsgClient = null;

    public EmsgClient getEmsgClient() {
        return mEmsgClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mEmsgClient = EmsgClient.getInstance();
        mEmsgClient.init(this);
        /*mEmsgClient.setEmsClosedCallBack(mEmsClosedCallback)(new EmsClosedCallBack() {
            @Override
            public void onAnotherClientLogin() {
                Toast.makeText(DemoApplication.this, "已掉线请重新登录", Toast.LENGTH_LONG).show();
                //需要三方重新登录
            }

            @Override
            public void onChannelClosed() {
                Toast.makeText(DemoApplication.this, "Emsg连接异常断开", Toast.LENGTH_LONG).show();
            }
        });*/
        mEmsgClient.setEmsStCallBack(new EmsStateCallBack() {
          
            @Override
            public void onAnotherClientLogin() {
                Toast.makeText(DemoApplication.this, "被迫下线", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onTerminate() {
        try {
            mEmsgClient.closeClient();
        } catch (Exception ex) {
            Log.e(TAG, "尝试关闭client异常." + ex.getMessage(), ex);
        }
        super.onTerminate();
    }

}
