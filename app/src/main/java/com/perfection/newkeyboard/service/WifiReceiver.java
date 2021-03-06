package com.perfection.newkeyboard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.perfection.newkeyboard.utils.PrefUtils;

/**
 * Created by Mayank on 10/14/2017.
 */

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info != null && info.isConnected()) {
            // Do your work.

            // e.g. To check the Network Name or other info:
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            PrefUtils.setSharedPrefBooleanData(context, PrefUtils.WIFI_ENABLE, true);
        } else {
            PrefUtils.setSharedPrefBooleanData(context, PrefUtils.WIFI_ENABLE, false);
        }
    }
}
