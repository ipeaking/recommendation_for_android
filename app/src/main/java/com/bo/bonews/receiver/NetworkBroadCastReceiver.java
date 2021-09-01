package com.bo.bonews.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;


import com.bo.bonews.event.NetworkEvent;
import com.bo.bonews.utils.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 */
public class NetworkBroadCastReceiver extends BroadcastReceiver {

    private NetworkEvent mNetworkEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            getNetworkEvent().setAvailable(Utils.isNetworkConnected(context));
            EventBus.getDefault().post(mNetworkEvent);
        }
    }

    private NetworkEvent getNetworkEvent() {
        if (mNetworkEvent == null) {
            mNetworkEvent = new NetworkEvent();
        }
        return mNetworkEvent;
    }
}
