
package com.emsg.sdk;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;

public class HeartBeatReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        // 执行心跳连接 并且当检测到连接中断要建立新的连接
        EmsgClient.getInstance().getHeartBeatManager().sendHeartBeat();
    }

}
